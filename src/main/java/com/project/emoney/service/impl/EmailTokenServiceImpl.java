package com.project.emoney.service.impl;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.GlobalVariable;
import com.project.emoney.utils.GmailSMTP;
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
  private GmailSMTP gmailSMTP;

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
      gmailSMTP.send(user.getEmail(),token);
      return "success";
    } catch (MessagingException e) {
      return "failed";
    }
  }
}
