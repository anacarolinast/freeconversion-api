package com.example.demo.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

public class MediaMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] file; 
    private String email;

    public MediaMessage(MultipartFile multipartFile, String email) throws IOException {
        this.file = multipartFile.getBytes(); 
        this.email = email;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
