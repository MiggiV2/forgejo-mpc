package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionTask {
    public Long id;
    public String name;
    public String status;
    public String event;
    @JsonProperty("head_branch")
    public String headBranch;
    @JsonProperty("head_sha")
    public String headSha;
    @JsonProperty("run_number")
    public Long runNumber;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
}
