package com.example.demo.model;

import java.io.IOException;
import java.io.Serializable;
import org.springframework.web.multipart.MultipartFile;

public class MediaMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] file;
    private String email;
    private String format; 

    public MediaMessage(MultipartFile multipartFile, String email, String format) throws IOException {
        this.file = multipartFile.getBytes();
        this.email = email;
        this.format = format; 
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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
