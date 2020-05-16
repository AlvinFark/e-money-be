package com.project.emoney.worker;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Worker {

  @Autowired
  private AuthWorker authWorker;

  @Autowired
  private UserWorker userWorker;

  @Autowired
  private OTPWorker otpWorker;

  @Autowired
  private TransactionWorker transactionWorker;

  @Autowired
  private EmailTokenWorker emailTokenWorker;

  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  @Async("workerExecutor")
  public void run(String QUEUE_NAME) {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("user06");
    factory.setPassword("password06");
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queuePurge(QUEUE_NAME);

      channel.basicQos(1);

      log.info("["+QUEUE_NAME+"Worker]  Awaiting messages");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();

        String response = "";

        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

        try {
          switch (QUEUE_NAME) {
            case "login":
              response = authWorker.login(message);
              break;
            case "register":
              response = authWorker.register(message);
              break;
            case "in-progress":
              response = transactionWorker.transactionInProgress(message);
              break;
            case "completed":
              response = transactionWorker.transactionCompleted(message);
              break;
            case "profile":
              response = userWorker.profile(message);
              break;
            case "otp":
              response = otpWorker.send(message);
              break;
            case "verify":
              response = emailTokenWorker.verify(message);
              break;
            case "password":
              response = userWorker.password(message);
              break;
            case "cancelTransaction":
              response = transactionWorker.cancel(message);
              break;
            case "transaction":
              response = transactionWorker.createTransaction(message);
              break;
          }
        } catch (Exception e) {
          e.printStackTrace();
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
}
