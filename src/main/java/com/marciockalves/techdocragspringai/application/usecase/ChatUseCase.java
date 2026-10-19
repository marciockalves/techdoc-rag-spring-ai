package com.marciockalves.techdocragspringai.application.usecase;

import com.marciockalves.techdocragspringai.application.dto.ChatRequestDTO;
import com.marciockalves.techdocragspringai.infrastructure.tools.HardwareManualSearchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatUseCase {

    private final ChatClient chatClient;
   private final HardwareManualSearchTool hardwareManualSearchTool;

    @Value("classpath:templates/system-prompt.st")
    private Resource systemPromptResource;

    public ChatUseCase(ChatClient.Builder chatClientBuilder,
                       ChatMemory chatMemory,
                       HardwareManualSearchTool hardwareManualSearchTool
                      ) {


        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.hardwareManualSearchTool = hardwareManualSearchTool;
    }

    public Flux<String> execute(ChatRequestDTO request) {
        System.out.println("--> [ChatUseCase] Processing query for conversationId: " + request.conversationId());

        return this.chatClient.prompt()
                .system(promptSpec -> promptSpec.text(systemPromptResource))
                .user(request.message())
                .tools(hardwareManualSearchTool) // Registra a tool diretamente no prompt ou builder conforme sua versão
                .advisors(advisorSpec -> advisorSpec.param("chat_memory_conversation_id", request.conversationId()))
                .stream()
                .content();
    }
}