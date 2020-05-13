package com.project.emoney;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.MQRequestWrapper;
import com.project.emoney.worker.AuthWorker;
import com.project.emoney.worker.OTPWorker;
import com.project.emoney.worker.UserWorker;
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

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  @Async("workerExecutor")
  public void runner() {
    final String QUEUE_NAME = "worker";

    final URI rabbitMqUrl;
    try {
      rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
    factory.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
    factory.setHost(rabbitMqUrl.getHost());
    factory.setPort(rabbitMqUrl.getPort());
    factory.setVirtualHost(rabbitMqUrl.getPath().substring(1));

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
          case "profile":
            response = userWorker.profile(mqRequest.getMessage());
            break;
          case "otp":
            response = otpWorker.send(mqRequest.getMessage());
            break;
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
  public void run(String... args) throws Exception {
    runner();
  }
}
