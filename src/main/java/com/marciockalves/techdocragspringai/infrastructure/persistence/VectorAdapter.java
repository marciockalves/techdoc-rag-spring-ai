package com.marciockalves.techdocragspringai.infrastructure.persistence;

import com.marciockalves.techdocragspringai.domain.port.VectorPort;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class VectorAdapter implements VectorPort {

    private final VectorStore vectorStore;
    private final ResourcePatternResolver resourcePatternResolver;

    public VectorAdapter(VectorStore vectorStore, ResourcePatternResolver resourcePatternResolver) {
        this.vectorStore = vectorStore;
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public List<Resource> listManuals() throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:manuals/*.pdf");
        return Arrays.asList(resources);
    }

    @Override
    public void processAndIngest(Resource resource, int chunkSize, int chunkOverlap) throws IOException {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
        List<Document> documents = pdfReader.get();

        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(chunkSize)
                .withMinChunkSizeChars(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();

        List<Document> splitDocuments = textSplitter.apply(documents);

        for (Document doc : splitDocuments) {
            doc.getMetadata().put("source_file", resource.getFilename());
        }

        vectorStore.add(splitDocuments);
    }

    @Override
    public List<Document> similaritySearch(String query, int topK, double similarityThreshold) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .topK(topK)
                        .similarityThreshold(similarityThreshold)
                        .build()
        );
    }
}