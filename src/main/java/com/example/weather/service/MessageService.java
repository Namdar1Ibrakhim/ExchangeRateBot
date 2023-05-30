package com.example.weather.service;

import com.example.weather.models.Message;
import com.example.weather.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;
    public void saveMessage(Message message){
        repository.save(message);
    }

}
