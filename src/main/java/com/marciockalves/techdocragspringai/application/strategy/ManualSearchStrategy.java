package com.marciockalves.techdocragspringai.application.strategy;

import com.marciockalves.techdocragspringai.domain.port.VectorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ManualSearchStrategy {

    private static final Logger log = LoggerFactory.getLogger(ManualSearchStrategy.class);

    private final VectorPort vectorPort;

    @Value("${app.ai.search.top-k}")
    private int topK;

    @Value("${app.ai.search.threshold}")
    private double similarityThreshold;

    public ManualSearchStrategy(VectorPort vectorPort) {
        this.vectorPort = vectorPort;
    }

    public String executeSearch(String query) {
        log.info("==================================================");
        log.info("🔍 [RAG Search] Iniciando busca para a query: '{}'", query);
        log.info("⚙️ [Config] Parâmetros aplicados -> Top-K: {}, Threshold: {}", topK, similarityThreshold);

        if (query == null || query.isBlank()) {
            log.warn("⚠️ [RAG Search] A query enviada está vazia ou nula.");
            return "Please provide a valid search query.";
        }

        List<Document> documents = vectorPort.similaritySearch(query, topK, similarityThreshold);

        if (documents.isEmpty()) {
            log.warn("❌ [RAG Search] Nenhum documento relevante encontrado para a query: '{}'", query);
            return "No relevant technical information found for query: " + query;
        }

        log.info("✅ [RAG Search] Encontrados {} trecho(s) relevante(s) no banco vetorial.", documents.size());

        int index = 1;
        for (Document doc : documents) {
            String source = String.valueOf(doc.getMetadata().getOrDefault("source_file", "Unknown"));
            log.info("📄 [Documento {}] Fonte: {}", index++, source);
        }

        log.info("==================================================");

        return documents.stream()
                .map(doc -> String.format("Source: %s\nContent: %s",
                        doc.getMetadata().getOrDefault("source_file", "Unknown"),
                        doc.getText()))
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}