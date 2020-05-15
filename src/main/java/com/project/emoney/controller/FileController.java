package com.project.emoney.controller;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
import com.project.emoney.entity.User;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.FTPBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    //get user and transaction details
    User user = userService.getUserByEmail(userDetails.getUsername());

    try {
      Transaction transaction = transactionService.getById(idTransaction);

      //validation if user upladed for other's transaction
      if (transaction.getUserId() != user.getId()) {
        return new ResponseEntity<>(new SimpleResponseWrapper(401, "transaction belong to another user"), HttpStatus.UNAUTHORIZED);
      }

      //can only upload for bank transfer method
      if (transaction.getStatus() != Status.IN_PROGRESS || transaction.getMethod() != TransactionMethod.BANK) {
        return new ResponseEntity<>(new SimpleResponseWrapper(400, "bad transaction method or status"), HttpStatus.BAD_REQUEST);
      }

      try {
        //get extension from file to be saved on db
        String extension = file.getOriginalFilename().split("\\.")[file.getOriginalFilename().split("\\.").length - 1];
        //init ftp connection
        FTPBuilder ftp = new FTPBuilder("ftp.drivehq.com","alvark", "WiUgm@Cq436AG5i");
        //check whether user already upload a file for this transaction, delete it before if yes
        if (transaction.getImagePath() != null) {
          ftp.deleteFile("/bukti-transfer/" + idTransaction + "." + transaction.getImagePath());
        }
        //upload file and save extension to db
        ftp.uploadFile(file, idTransaction + "." + extension, "/bukti-transfer/");
        transactionService.setExtensionById(idTransaction, extension);
      } catch (Exception e) {
        //response if ftp server inactive
        e.printStackTrace();
        return new ResponseEntity<>(new SimpleResponseWrapper(503, "can't connect to FTP server"), HttpStatus.SERVICE_UNAVAILABLE);
      }
      //return success
      return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.CREATED);
    } catch (NullPointerException e) {
      return new ResponseEntity<>(new SimpleResponseWrapper(404, "transaction not found"), HttpStatus.NOT_FOUND);
    }
  }
}
