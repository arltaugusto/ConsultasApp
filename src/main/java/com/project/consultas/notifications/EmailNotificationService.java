package com.project.consultas.notifications;

import com.project.consultas.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class EmailNotificationService {

    private Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private JavaMailSender javaMailSender;

    @Autowired
    public EmailNotificationService (JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail (EmailNotificationMessage notification) throws IOException {
        notification.buildMessage();
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(notification.getDestinationEmail());
        msg.setSubject(notification.getSubject());
        msg.setText(notification.getMessage());
        javaMailSender.send(msg);
    }

    @Async
    public void sendMassiveEmails(Set<EmailNotificationMessage> emails) {
        emails.parallelStream()
            .forEach(mail -> {
                try {
                    sendEmail(mail);
                } catch (IOException e) {
                    logger.error("Unable to send message", e);
                }
            });
    }
}
