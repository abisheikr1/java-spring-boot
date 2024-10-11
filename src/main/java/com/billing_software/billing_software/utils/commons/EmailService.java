package com.billing_software.billing_software.utils.commons;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

@Service
public class EmailService {

    public void sendEmail(String username, String password, String to, String subject,
            String body, MultipartFile file) {
        JavaMailSender mailSender = getJavaMailSender("smtp.gmail.com", 465, username, password);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(username); // Set the from address to the user's email
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Set the body text as HTML

            // Add the attachment if the file is not null
            if (file != null && !file.isEmpty()) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private JavaMailSender getJavaMailSender(String smtpHost, int smtpPort, String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // Enable SSL for port 465
        props.put("mail.debug", "true");

        return mailSender;
    }
}