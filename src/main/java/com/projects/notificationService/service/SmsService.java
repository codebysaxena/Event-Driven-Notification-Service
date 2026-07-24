package com.projects.notificationService.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${sms.provider:TWILIO}")
    private String smsProvider;

    @Value("${sms.twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${sms.twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${sms.twilio.phone-number:}")
    private String twilioPhoneNumber;

    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
                log.warn("Recipient phone number is missing. Defaulting to testing number (+919999999999).");
                toPhoneNumber = "+919999999999";
            }

            if ("TWILIO".equalsIgnoreCase(smsProvider) && !twilioAccountSid.isBlank() && !twilioAuthToken.isBlank()) {
                log.info("[TWILIO SMS] Initializing Twilio dispatch to {}", toPhoneNumber);

                // Initialize Twilio SDK client
                Twilio.init(twilioAccountSid, twilioAuthToken);

                Message message = Message.creator(
                        new PhoneNumber(toPhoneNumber),       // To
                        new PhoneNumber(twilioPhoneNumber),    // From (Your Twilio Virtual Number)
                        messageBody                            // SMS Body
                ).create();

                log.info("Twilio SMS sent successfully. Message SID={}, status={}", message.getSid(), message.getStatus());
            } else {
                log.info("[SMS GATEWAY MOCK] Dispatching SMS to {}: {}", toPhoneNumber, messageBody);
            }
        } catch (Exception e) {
            log.error("Exception while dispatching SMS to {}: {}", toPhoneNumber, e.getMessage(), e);
            throw new RuntimeException("SMS dispatch failed", e);
        }
    }
}
