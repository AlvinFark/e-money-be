package com.project.emoney.service.impl;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailTokenServiceImpl implements EmailTokenService {

  @Autowired
  private EmailTokenMapper emailTokenMapper;

  @Autowired
  private Generator generator;

  @Autowired
  private JavaMailSender javaMailSender;

  @Override
  public void insertByUserAndToken(User user, String token) {
    EmailToken newUserToken = new EmailToken(token, user);
    emailTokenMapper.insert(newUserToken);
  }

  @Override
  public EmailToken getByToken(String verificationToken) {
    return emailTokenMapper.getByToken(verificationToken);
  }

  @Override
  public String sendEmail(User user) {
    try {
      String token = generator.generateToken();
      insertByUserAndToken(user,token);
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setSubject("Please confirm your new e-Money App account");
      helper.setTo(user.getEmail());
      helper.setText("<a href=\""+ GlobalVariable.HOST +"/api/verify/"+token+"\">Please click here to activate your account</a> " +
          "or open this link in your browser if the link didn't work: " +GlobalVariable.HOST+"/api/verify/"+token, true);

      javaMailSender.send(message);
      return "success";
    } catch (MessagingException e) {
      return "failed";
    }
  }
}
