package com.example.demo.config;

import com.example.demo.model.MediaMessage;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue videoQueue() {
        return new Queue("videoQueue", false);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new MediaMessageConverter();
    }

    public static class MediaMessageConverter implements MessageConverter {
        @Override
        public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
            if (object instanceof MediaMessage) {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(object);
                    objectOutputStream.flush();
                    byte[] bytes = byteArrayOutputStream.toByteArray();

                    messageProperties.setContentType("application/octet-stream");
                    return new Message(bytes, messageProperties);
                } catch (IOException e) {
                    throw new MessageConversionException("Failed to serialize MediaMessage", e);
                }
            }
            throw new MessageConversionException("Invalid object type for conversion");
        }

        @Override
        public Object fromMessage(Message message) throws MessageConversionException {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBody());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                return objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new MessageConversionException("Failed to deserialize MediaMessage", e);
            }
        }
    }
}

