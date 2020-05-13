package com.project.emoney.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.MQRequestWrapper;
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

    connection = factory.newConnection();
    channel = connection.createChannel();
  }

  public String call(String message) throws IOException, InterruptedException {
//    System.out.println(message);
//    System.out.println(requestQueueName);
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
//    System.out.println(messageWithQueue);
    channel.basicPublish("", System.getenv("userMQ"), props, messageWithQueue.getBytes(StandardCharsets.UTF_8));

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
