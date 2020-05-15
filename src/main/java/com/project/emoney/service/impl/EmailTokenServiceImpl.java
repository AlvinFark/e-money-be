package com.project.emoney.service.impl;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.utils.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailTokenServiceImpl implements EmailTokenService {

  @Autowired
  EmailTokenMapper emailTokenMapper;

  @Autowired
  Generator generator;

  @Autowired
  JavaMailSender javaMailSender;

  @Override
  public void createVerificationToken(User user, String token) {
    EmailToken newUserToken = new EmailToken(token, user);
    emailTokenMapper.createToken(newUserToken);
  }

  @Override
  public EmailToken getVerificationToken(String verificationToken) {
    return emailTokenMapper.findTokenByToken(verificationToken);
  }

  @Override
  public String sendEmail(User user) {
    try {
      String token = generator.generateToken();
      createVerificationToken(user,token);
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setSubject("Please confirm your new e-Money App account");
      helper.setTo(user.getEmail());
      helper.setText("<a href=\"http://ec2-3-95-185-42.compute-1.amazonaws.com:9706/api/verify/"+token+"\">Please click here to activate your account</a>", true);

      javaMailSender.send(message);
      return "success";
    } catch (MessagingException e) {
      e.printStackTrace();
      return "failed";
    }
  }
}
