package com.example.demo.controller;

import com.example.demo.service.MediaService;
import com.example.demo.model.MediaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping("/upload")
    public String uploadMedia(@RequestParam("file") MultipartFile file,
                              @RequestParam("email") String email) {
        try {
            MediaMessage mediaMessage = new MediaMessage(file, email);
            
            mediaService.sendToQueue(mediaMessage);

            return "Upload iniciado com sucesso!";
        } catch (Exception e) {
            return "Erro ao iniciar o upload: " + e.getMessage();
        }
    }
}
