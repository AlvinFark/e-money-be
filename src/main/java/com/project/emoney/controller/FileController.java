package com.project.emoney.controller;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.TransactionService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.FTPBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
public class FileController {

  @Autowired
  UserService userService;

  @Autowired
  TransactionService transactionService;

  @PostMapping("/transaction/upload/{idTransaction}")
  public ResponseEntity<?> uploadBukti(@CurrentUser org.springframework.security.core.userdetails.User userDetails,
                                       @PathVariable long idTransaction,
                                       @RequestParam("file") MultipartFile file) {
    User user = userService.getUserByEmail(userDetails.getUsername());
    Transaction transaction = transactionService.getById(idTransaction);
    if (transaction.getUserId()!=user.getId()){
      return new ResponseEntity<>(new SimpleResponseWrapper(401, "transaction belong to another user"), HttpStatus.UNAUTHORIZED);
    }
    if (transaction.getStatus()!=Status.IN_PROGRESS||transaction.getMethod()!=TransactionMethod.BANK){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "bad transaction method or status"), HttpStatus.BAD_REQUEST);
    }
    try {
      String extension = file.getOriginalFilename().split("\\.")[file.getOriginalFilename().split("\\.").length-1];
      FTPBuilder ftp = new FTPBuilder(System.getenv("ftpserver"),
          System.getenv("ftpusername"),System.getenv("ftppassword"));
      if (transaction.getImagePath()!=null){
        ftp.deleteFile("/bukti-transfer/"+idTransaction+"."+transaction.getImagePath());
      }
      ftp.uploadFile(file, idTransaction+"."+extension, "/bukti-transfer/");
      transactionService.setExtensionById(idTransaction, extension);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(new SimpleResponseWrapper(503, "can't connect to FTP server"), HttpStatus.SERVICE_UNAVAILABLE);
    }
    return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.CREATED);
  }
}
