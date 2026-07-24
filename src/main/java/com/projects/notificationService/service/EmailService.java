package com.projects.notificationService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from:}")
    private String fromEmail;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body){
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                email.setFrom(fromEmail);
            }
            email.setTo(to);
            email.setSubject(subject);
            email.setText(body);
            javaMailSender.send(email);
            log.info("Email dispatched successfully to {}", to);
        } catch (Exception e){
            log.error("Exception while sending email to {}: ", to, e);
            throw new RuntimeException("Email dispatch failed", e);
        }
    }
}
