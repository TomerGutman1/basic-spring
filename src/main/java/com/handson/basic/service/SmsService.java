package com.handson.basic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    protected final Log logger = LogFactory.getLog(getClass());
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sms4free.key}")
    private String ACCOUNT_KEY;

    @Value("${sms4free.user}")
    private String ACCOUNT_USER;

    @Value("${sms4free.password}")
    private String ACCOUNT_PASS;

    public boolean send(String text, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return false;

        String url = "https://api.sms4free.co.il/ApiSMS/v2/SendSMS";

        Map<String, String> payload = new HashMap<>();
        payload.put("key", ACCOUNT_KEY);
        payload.put("user", ACCOUNT_USER);
        payload.put("pass", ACCOUNT_PASS);
        payload.put("sender", ACCOUNT_USER); // Must match your registered number if free tier
        payload.put("recipient", phoneNumber);
        payload.put("msg", text);

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            RequestBody body = RequestBody.create(
                    jsonPayload,
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                logger.info("SMS API response for " + phoneNumber + ": " + responseBody);

                if (!response.isSuccessful()) {
                    logger.error("Failed to send SMS to " + phoneNumber + ". HTTP Status: " + response.code());
                    return false;
                }

                // Attempt to parse "status" from JSON
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                Object status = responseMap.get("status");
                if (status instanceof Integer && ((Integer) status) > 0) {
                    return true;
                } else if (status instanceof String) {
                    return Integer.parseInt((String) status) > 0;
                } else {
                    logger.error("Unexpected response format: " + responseBody);
                    return false;
                }
            }

        } catch (Exception e) {
            logger.error("Exception while sending SMS to " + phoneNumber, e);
            return false;
        }
    }
}
