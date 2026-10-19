package com.marciockalves.techdocragspringai.infrastructure.controller;

import com.marciockalves.techdocragspringai.application.dto.ChatRequestDTO;
import com.marciockalves.techdocragspringai.application.usecase.ChatUseCase;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequestDTO request) {
        return chatUseCase.execute(request);
    }
}