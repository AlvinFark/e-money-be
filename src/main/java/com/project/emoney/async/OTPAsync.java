package com.project.emoney.async;

import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.OTPService;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.GlobalVariable;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class OTPAsync {

  @Autowired
  private OTPService otpService;

  @Autowired
  private EmailTokenService emailTokenService;

  @Autowired
  private Generator generator;

  @Autowired
  JavaMailSender javaMailSender;

  @Value("${phoneNumber}") private String myTwilioPhoneNumber;
  @Value("${twilioAccountSid}") private String twilioAccountSid;
  @Value("${twilioAuthToken}") private String twilioAuthToken;

  @Async("asyncExecutor")
  public CompletableFuture<String> sendEmail(User user) {
    //generate and save to db
    try {
      String token = generator.generateToken();
      emailTokenService.createVerificationToken(user,token);
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setSubject("Please confirm your new e-Money App account");
      helper.setTo(user.getEmail());
      helper.setText("<a href=\"https://be-emoney.herokuapp.com/api/verify/"+token+"\">Please click here to activate your account</a>", true);

      javaMailSender.send(message);
      return CompletableFuture.completedFuture("success");
    } catch (MessagingException e) {
      e.printStackTrace();
      return CompletableFuture.completedFuture("failed");
    }
  }

  @Async("asyncExecutor")
  public CompletableFuture<String> sendOtp(String phone) {
    try {
      //initialize otp details
      OTP otp = new OTP();
      otp.setEmailOrPhone(phone);
      otp.setCode(generator.generateOtp());
      //add 7 hours to calibrate it with server
      otp.setTime(LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS));
      Twilio.init(twilioAccountSid, twilioAuthToken);
      Message.creator(
          new PhoneNumber("+"+phone),
          new PhoneNumber(myTwilioPhoneNumber),
          "Kode OTP: "+otp.getCode()).create();
      otpService.create(otp);
      return CompletableFuture.completedFuture("success");
    } catch (ApiException e) {
      //twilio free can only send to verified number
      e.printStackTrace();
      return CompletableFuture.completedFuture("failed");
    }
  }

}
