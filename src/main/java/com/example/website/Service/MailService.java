package com.example.website.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class MailService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String customerName,String password, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());


        Context context = new Context();
        context.setVariable("name", customerName);
        context.setVariable("password", password);
        String htmlContent = templateEngine.process(template, context);
        helper.setTo(to);
        helper.setSubject("Thông Tin Tài Khoản Của Bạn");
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }

    @Async
    public void sendEmailRemember(String to, String customerName,String url, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());


        Context context = new Context();
        context.setVariable("fullName", customerName);
        context.setVariable("resetPasswordLink", url);
        String htmlContent = templateEngine.process(template, context);
        helper.setTo(to);
        helper.setSubject("Thay đổi mật khẩu của bạn");
        helper.setText(htmlContent, true);

        // Gửi email
        mailSender.send(message);
    }




}