package com.handson.basic.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AWSService.
 * No Spring context—everything is mocked.
 */
@ExtendWith(MockitoExtension.class)
class AWSServiceTest {

    @Mock
    AmazonS3 s3Client;

    @InjectMocks
    AWSService awsService;

    @BeforeEach
    void setUp() {
        // 'bucket' is package-private, so this works since we're in the same package.
        awsService.bucket = "test-bucket";
    }

    //
    //──────────────────────────────────────────────────────────────────────────────────
    // 1) Tests for generateLink(...)
    //──────────────────────────────────────────────────────────────────────────────────
    //

    @Test
    void generateLink_withValidKey_returnsPresignedUrl() throws MalformedURLException {
        // Arrange: stub generatePresignedUrl(...) to return a fake URL
        URL fakeUrl = new URL("https://example.com/presigned/object.png");
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(fakeUrl);

        // Act: call the single-argument overload
        String actual = awsService.generateLink("images/photo.png");

        // Assert: it should return exactly fakeUrl.toString()
        assertThat(actual).isEqualTo(fakeUrl.toString());

        // Verify that the request used bucket="test-bucket", key="images/photo.png", method=GET
        ArgumentCaptor<GeneratePresignedUrlRequest> captor =
                ArgumentCaptor.forClass(GeneratePresignedUrlRequest.class);
        verify(s3Client, times(1)).generatePresignedUrl(captor.capture());

        GeneratePresignedUrlRequest capturedReq = captor.getValue();
        assertThat(capturedReq.getBucketName()).isEqualTo("test-bucket");
        assertThat(capturedReq.getKey()).isEqualTo("images/photo.png");
        assertThat(capturedReq.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    void generateLink_withNullKey_returnsNullWithoutCallingS3() {
        // Act
        String result = awsService.generateLink((String) null);

        // Assert: should be null, and no S3 interaction
        assertThat(result).isNull();
        verifyNoInteractions(s3Client);
    }

    @Test
    void generateLink_whenS3ThrowsException_returnsRawKey() {
        // Arrange: stub generatePresignedUrl(...) to throw
        when(s3Client.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(new RuntimeException("S3 is down"));

        // Act: explicitly call 3-arg overload
        long futureMillis = System.currentTimeMillis() + (1000L * 60 * 60);
        String fallback = awsService.generateLink("another-bucket", "some/key.png", futureMillis);

        // Assert: fallback should equal raw fileUrl
        assertThat(fallback).isEqualTo("some/key.png");

        // Verify S3 was invoked exactly once
        verify(s3Client, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    //
    //──────────────────────────────────────────────────────────────────────────────────
    // 2) Tests for putInBucket(MultipartFile, String)
    //──────────────────────────────────────────────────────────────────────────────────
    //

    @Test
    void putInBucket_validFile_invokesS3PutObject() throws Exception {
        // Arrange: mock a MultipartFile that returns a known byte-array stream
        MultipartFile fakeFile = mock(MultipartFile.class);
        byte[] dummyBytes = "hello world".getBytes();
        when(fakeFile.getInputStream()).thenReturn(new ByteArrayInputStream(dummyBytes));

        // Stub putObject(...) to return null (or a new PutObjectResult if you prefer)
        when(s3Client.putObject(any(PutObjectRequest.class)))
                .thenReturn((PutObjectResult) null);

        // Act: call putInBucket()
        awsService.putInBucket(fakeFile, "uploads/pic.png");

        // Assert: capture the PutObjectRequest passed to S3
        ArgumentCaptor<PutObjectRequest> putCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(1)).putObject(putCaptor.capture());

        PutObjectRequest actualRequest = putCaptor.getValue();
        assertThat(actualRequest.getBucketName()).isEqualTo("test-bucket");
        assertThat(actualRequest.getKey()).isEqualTo("uploads/pic.png");

        // Instead of checking file‐existence (it was deleted inside putInBucket),
        // simply assert that the File object is non-null and has a plausible temp‐file name.
        assertThat(actualRequest.getFile()).isNotNull();
        String filename = actualRequest.getFile().getName();
        assertThat(filename).startsWith("file");
        // Optionally check that it contains a dash or hex, which is typical of Files.createTempFile:
        assertThat(filename).contains("-");
    }

    @Test
    void putInBucket_whenInputStreamThrows_doesNotCallS3PutObject() throws Exception {
        // Arrange: mock a MultipartFile whose getInputStream() throws IOException
        MultipartFile brokenFile = mock(MultipartFile.class);
        when(brokenFile.getInputStream()).thenThrow(new IOException("cannot read"));

        // Act
        awsService.putInBucket(brokenFile, "some/path.png");

        // Assert: because InputStream threw, putObject() is never invoked
        verifyNoInteractions(s3Client);
    }

    @Test
    void putInBucket_whenS3PutObjectThrows_catchesException() throws Exception {
        // Arrange: mock a MultipartFile that returns a known byte-array stream
        MultipartFile fakeFile = mock(MultipartFile.class);
        byte[] dummyBytes = "hello world".getBytes();
        when(fakeFile.getInputStream()).thenReturn(new ByteArrayInputStream(dummyBytes));

        // Stub putObject(...) to throw a RuntimeException
        when(s3Client.putObject(any(PutObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 down during putObject"));

        // Act & Assert: calling putInBucket(...) should NOT propagate the exception
        assertDoesNotThrow(() -> awsService.putInBucket(fakeFile, "uploads/pic.png"));

        // Verify that putObject was indeed called exactly once, even though it threw
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }
}
