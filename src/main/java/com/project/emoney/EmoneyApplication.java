package com.project.emoney;

import com.project.emoney.worker.WorkerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmoneyApplication implements CommandLineRunner {

  @Autowired
  WorkerRunner workerRunner;

  public static void main(String[] args) {
    SpringApplication.run(EmoneyApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    workerRunner.runner();
    workerRunner.runner();
    workerRunner.runner();
    workerRunner.runner();
  }
}
