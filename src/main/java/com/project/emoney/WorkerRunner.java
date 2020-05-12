package com.project.emoney;

import com.project.emoney.worker.AuthWorker;
import com.project.emoney.worker.UserWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WorkerRunner implements CommandLineRunner {

  @Autowired
  AuthWorker authWorker;

  @Autowired
  UserWorker userWorker;

  @Override
  public void run(String... args) throws Exception {
    authWorker.login();
    authWorker.login();
    userWorker.profile();
  }
}
