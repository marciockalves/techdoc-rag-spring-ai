package com.marciockalves.techdocragspringai.domain.port;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface VectorPort {

    List<Resource> listManuals() throws IOException;

    void processAndIngest(Resource resource, int chunkSize, int chunkOverlap) throws IOException;

    List<Document> similaritySearch(String query, int topK, double similarityThreshold);
}