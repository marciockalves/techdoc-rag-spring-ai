package com.marciockalves.techdocragspringai.infrastructure.tools;

import com.marciockalves.techdocragspringai.application.strategy.ManualSearchStrategy;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class HardwareManualSearchTool {

    private final ManualSearchStrategy searchStrategy;


    public HardwareManualSearchTool(ManualSearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;

    }

    @Tool(description = "Searches indexed technical manuals for hardware troubleshooting, specifications, assembly, and repair instructions.")
    public String searchHardwareManuals(String query) {
        return searchStrategy.executeSearch(query);
    }
}