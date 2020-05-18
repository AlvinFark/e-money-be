package com.project.emoney.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class GmailSMTP {

  @Autowired
  private JavaMailSender javaMailSender;

  public void send(String email, String token) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setSubject("Please confirm your new e-Money App account");
    helper.setTo(email);
    helper.setText("<a href=\""+ GlobalVariable.HOST +"/api/verify/"+token+"\">Please click here to activate your account</a> " +
        "or open this link in your browser if the link didn't work: " +GlobalVariable.HOST+"/api/verify/"+token, true);

    javaMailSender.send(message);
  }
}
