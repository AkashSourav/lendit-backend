package com.codimen.lendit.service;

import com.codimen.lendit.dto.request.EmailContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailContentBuilder mailContentBuilder;

    @Async
    public void sendForgotPasswordEmail(String userName, String userEmail, String activationLink, String subject, EmailContent emailContent) {
        log.info("Started sending forgot password email");
        MimeMessagePreparator messagePreparator = mimeMessage -> {
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        String content = mailContentBuilder.buildForgotPasswordTemplate(userName, activationLink, emailContent);
        messageHelper.setText(content, true);
        };
        javaMailSender.send(messagePreparator);
        log.info("Completed sending reset password email");
    }

    @Async
    public void sendActivationEmail(String userName, String userEmail, String activationLink, String subject, EmailContent emailContent) {
        log.info("Started sending activation Email");
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(userEmail);
            messageHelper.setSubject(subject);
            String content = mailContentBuilder.buildUserRegistrationTemplate(userName, activationLink, emailContent);
            messageHelper.setText(content, true);
        };
        javaMailSender.send(messagePreparator);
        log.info("Completed sending reset password email");
    }

}
