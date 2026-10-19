package com.marciockalves.techdocragspringai.infrastructure.controller;

import com.marciockalves.techdocragspringai.application.usecase.IngestManualsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maintenance")
public class IngestionController {

    private final IngestManualsUseCase ingestManualsUseCase;

    public IngestionController(IngestManualsUseCase ingestManualsUseCase) {
        this.ingestManualsUseCase = ingestManualsUseCase;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> triggerIngestion() {
        ingestManualsUseCase.execute();
        return ResponseEntity.ok("Ingestion process executed successfully!");
    }
}