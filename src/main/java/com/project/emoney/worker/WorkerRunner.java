package com.project.emoney.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.dto.MQRequestWrapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Component
public class WorkerRunner implements CommandLineRunner {

  @Autowired
  AuthWorker authWorker;

  @Autowired
  UserWorker userWorker;

  @Autowired
  OTPWorker otpWorker;

  @Autowired
  TransactionWorker transactionWorker;

  @Autowired
  EmailTokenWorker emailTokenWorker;

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  @Async("workerExecutor")
  public void runner() {
    final String QUEUE_NAME = "T6";

    ConnectionFactory factory = new ConnectionFactory();
//    factory.setUsername("user06");
//    factory.setPassword("password06");
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queuePurge(QUEUE_NAME);

      channel.basicQos(1);

      log.info("[worker]  Awaiting messages");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();

        String response = "";

        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        MQRequestWrapper mqRequest = objectMapper.readValue(message,MQRequestWrapper.class);

        switch (mqRequest.getQueue()){
          case "login":
            response = authWorker.login(mqRequest.getMessage());
            break;
          case "register":
            response = authWorker.register(mqRequest.getMessage());
            break;
          case "in-progress":
            response = transactionWorker.transactionInProgress(mqRequest.getMessage());
            break;
          case "completed":
            response = transactionWorker.transactionCompleted(mqRequest.getMessage());
            break;
          case "profile":
            response = userWorker.profile(mqRequest.getMessage());
            break;
          case "otp":
            response = otpWorker.send(mqRequest.getMessage());
            break;
          case "verify":
            response = emailTokenWorker.verify(mqRequest.getMessage());
          case "password":
            response = userWorker.password(mqRequest.getMessage());
            break;
          case "cancelTransaction":
            response = transactionWorker.cancel(mqRequest.getMessage());
            break;
          case "transaction":
            try {
              response = transactionWorker.createTransaction(mqRequest.getMessage());
            } catch (Exception e) {
              response = "e-money server unreachable";
            }
        }

        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        synchronized (monitor) {
          monitor.notify();
        }
      };

      channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
      while (true) {
        synchronized (monitor) {
          try {
            monitor.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run(String... args) {
    runner();
    runner();
    runner();
    runner();
  }
}
