package com.example.demo.service;

import com.example.demo.model.MediaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUEUE_NAME = "videoQueue";

    public void sendToQueue(MediaMessage videoMessage) {
        rabbitTemplate.convertAndSend(QUEUE_NAME, videoMessage);
    }
}
