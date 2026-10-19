package com.marciockalves.techdocragspringai.application.usecase;

import com.marciockalves.techdocragspringai.domain.port.VectorPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestManualsUseCase {

    private final VectorPort vectorPort;

    @Value("${app.ai.chunk-size:800}")
    private int chunkSize;

    @Value("${app.ai.chunk-overlap:400}")
    private int chunkOverlap;

    public IngestManualsUseCase(VectorPort vectorPort) {
        this.vectorPort = vectorPort;
    }

    public void execute() {
        try {
            System.out.println("--> [UseCase] Checking technical manuals for ingestion...");
            List<Resource> manuals = vectorPort.listManuals();

            if (manuals.isEmpty()) {
                System.out.println("--> [UseCase] No manuals found in the resources directory.");
                return;
            }

            for (Resource manual : manuals) {
                System.out.println("--> [UseCase] Processing manual: " + manual.getFilename());
                vectorPort.processAndIngest(manual, chunkSize, chunkOverlap);
            }

            System.out.println("--> [UseCase] Manuals ingestion process completed successfully!");
        } catch (Exception e) {
            throw new RuntimeException("Critical error during the manual ingestion workflow", e);
        }
    }
}