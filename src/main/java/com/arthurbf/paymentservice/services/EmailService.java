package com.arthurbf.paymentservice.services;

import com.arthurbf.paymentservice.dtos.EmailDetailDto;
import com.arthurbf.paymentservice.models.UserModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {
    private final JavaMailSender emailSender;
    private final RabbitTemplate rabbitTemplate;
    @Value(value="${broker.queue.email.name}")
    private String routingKey;


    public EmailService(JavaMailSender emailSender, RabbitTemplate rabbitTemplate) {
        this.emailSender = emailSender;
        this.rabbitTemplate = rabbitTemplate;
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

    public void sendEmailNotification(UserModel sender, UserModel receiver, BigDecimal amount) {
        String subject = "You have received a transfer!";
        String body = String.format("Hello %s, you have received %.2f from %s(%s).", receiver.getName(), amount, sender.getName(), sender.getEmail());
        var emailDto = new EmailDetailDto(sender.getEmail(), subject, receiver.getEmail(), body);
        rabbitTemplate.convertAndSend("", routingKey, emailDto);
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
