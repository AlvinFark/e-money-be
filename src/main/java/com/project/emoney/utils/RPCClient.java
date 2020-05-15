package com.project.emoney.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.dto.MQRequestWrapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@NoArgsConstructor
public class RPCClient implements AutoCloseable {

  private Connection connection;
  private Channel channel;
  private String requestQueueName;

  public RPCClient(String requestQueueName) throws Exception {
    this.requestQueueName = requestQueueName;

    ConnectionFactory factory = new ConnectionFactory();
//    factory.setUsername("user06");
//    factory.setPassword("password06");
    factory.setHost("localhost");

    connection = factory.newConnection();
    channel = connection.createChannel();
  }

  public String call(String message) throws IOException, InterruptedException {
    final String corrId = UUID.randomUUID().toString();

    String replyQueueName = channel.queueDeclare().getQueue();
    AMQP.BasicProperties props = new AMQP.BasicProperties
        .Builder()
        .correlationId(corrId)
        .replyTo(replyQueueName)
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    MQRequestWrapper mqRequestWrapper = new MQRequestWrapper(requestQueueName,message);
    String messageWithQueue = objectMapper.writeValueAsString(mqRequestWrapper);
    channel.basicPublish("", "T6", props, messageWithQueue.getBytes(StandardCharsets.UTF_8));

    final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

    String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
      if (delivery.getProperties().getCorrelationId().equals(corrId)) {
        response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
      }
    }, consumerTag -> {
    });

    String result = response.take();
    channel.basicCancel(ctag);
    return result;
  }

  public void close() throws IOException {
    connection.close();
  }

}
