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
                              @RequestParam("email") String email,
                              @RequestParam("format") String format) {
        try {
            MediaMessage mediaMessage = new MediaMessage(file, email, format);
            System.out.println("Mensagem sendo enviada para a fila...");
            
            mediaService.sendToQueue(mediaMessage);
            
            System.out.println("Mensagem enviada para a fila com sucesso!");
            return "Upload iniciado com sucesso!";
        } catch (Exception e) {
            System.out.println("Erro ao iniciar o upload: " + e.getMessage());
            return "Erro ao iniciar o upload: " + e.getMessage();
        }
    }
}
