package de.mymiggi.forgejo.mcp.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionTaskList {
    @JsonProperty("total_count")
    public Long totalCount;
    @JsonProperty("workflow_runs")
    public List<ActionTask> tasks;
}
