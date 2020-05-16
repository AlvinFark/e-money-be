package com.project.emoney;

import com.project.emoney.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmoneyApplication implements CommandLineRunner {

  @Autowired
  private Worker worker;

  public static void main(String[] args) {
    SpringApplication.run(EmoneyApplication.class, args);
  }

  @Override
  public void run(String... args) {
    worker.run("login");
    worker.run("register");
    worker.run("in-progress");
    worker.run("completed");
    worker.run("profile");
    worker.run("profile");
    worker.run("otp");
    worker.run("verify");
    worker.run("password");
    worker.run("cancelTransaction");
    worker.run("transaction");
  }
}
