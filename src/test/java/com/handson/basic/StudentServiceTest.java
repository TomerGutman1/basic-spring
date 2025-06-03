package com.handson.basic;


import com.handson.basic.model.Student;
import com.handson.basic.repo.StudentRepository;
import com.handson.basic.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 * Unit-level tests: no Spring, no JPA, no H2.
 */
@ExtendWith(MockitoExtension.class)          // JUnit hooks up Mockito
class StudentServiceTest {


    /** Mock the repository dependency */
    @Mock StudentRepository repo;


    /** Class under test (CUT) with mocks injected */
    @InjectMocks StudentService service;


    Student ada;


    @BeforeEach
    void setUp() {
        ada = Student.StudentBuilder.aStudent()
                .id(1L)
                .fullname("Ada Lovelace")
                .satScore(700)
                .build();
    }


    @Test
    void findById_returnsStudentWhenPresent() {
        when(repo.findById(1L)).thenReturn(Optional.of(ada));


        Optional<Student> result = service.findById(1L);


        assertThat(result).containsSame(ada);
        verify(repo).findById(1L);
        verifyNoMoreInteractions(repo);
    }




    @Test
    void getStudentsWithHighSat_delegatesToRepo() {
        when(repo.findAllBySatScoreGreaterThan(650)).thenReturn(List.of(ada));


        List<Student> list = service.getStudentWithSatHigherThan(650);


        assertThat(list).hasSize(1).containsExactly(ada);
        verify(repo).findAllBySatScoreGreaterThan(650);
    }


    @Test
    void save_persistsViaRepo() {
        when(repo.save(ada)).thenReturn(ada);


        Student saved = service.save(ada);


        assertThat(saved).isSameAs(ada);
        verify(repo).save(ada);
    }
}
