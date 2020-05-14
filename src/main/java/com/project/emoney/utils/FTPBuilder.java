package com.project.emoney.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FTPBuilder {

    FTPClient ftp;

    public FTPBuilder(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void uploadFile(MultipartFile file, String fileName, String hostDir) throws Exception {
        this.ftp.storeFile(hostDir + fileName, file.getInputStream());
    }

    public void deleteFile(String filename) throws IOException {
        ftp.deleteFile(filename);
    }
}