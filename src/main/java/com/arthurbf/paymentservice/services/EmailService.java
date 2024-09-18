package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.EmailDetailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String emailFrom, String emailTo, String subject, String body) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(emailTo);
        helper.setFrom(emailFrom);
        helper.setSubject(subject);
        helper.setText(body, true);
        emailSender.send(message);
    }

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void processEmailMessage(EmailDetailDto emailDetailDtO) throws MessagingException {
        String to = emailDetailDtO.emailTo();
        String subject = emailDetailDtO.subject();
        String body = emailDetailDtO.body();
        String from = emailDetailDtO.emailFrom();
        sendEmail(from, to, subject, body);
    }
}
