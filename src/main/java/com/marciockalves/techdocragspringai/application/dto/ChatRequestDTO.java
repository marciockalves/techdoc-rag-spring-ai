package com.marciockalves.techdocragspringai.application.dto;

public record ChatRequestDTO(
        String conversationId,
        String message
) {}