package com.desi.kart.desikart_backend.notification;

import com.desi.kart.desikart_backend.constants.SystemConstants;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("email-reset-password", context);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending email", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing the email", e);
        }
    }

    public void sendWelcomeMail( String to , String userName , String provider ) throws  Exception{

        Context context = new Context();
        context.setVariable("userName",userName);
        context.setVariable("provider",provider);
        context.setVariable("email",to);
        String htmlContent =  templateEngine.process("userRegistrationTemplate",context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");
        helper.setTo(to);
        helper.setFrom(SystemConstants.systemMail);
        helper.setSubject("Welcome To -->  desiKart ! ");
        helper.setText(htmlContent ,true);
        mailSender.send(message);
    }

    public void sendMail(String to , Map<String ,String> contexts , String subject , String templateId ) throws  Exception{
        Context context = new Context();
        for (Map.Entry<String,String> contextObj :contexts.entrySet()) {
            context.setVariable(contextObj.getKey(),contextObj.getValue());
        }
        String htmlContent =  templateEngine.process(templateId,context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");
        helper.setTo(to);
        helper.setFrom(SystemConstants.systemMail);
        helper.setSubject(subject);
        helper.setText(htmlContent ,true);
        mailSender.send(message);
    }
}
