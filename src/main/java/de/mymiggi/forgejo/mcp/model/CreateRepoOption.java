package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateRepoOption {
    public String name;
    public String description;
    @JsonProperty("private")
    public Boolean privateRepo;
    @JsonProperty("auto_init")
    public Boolean autoInit;
    @JsonProperty("default_branch")
    public String defaultBranch;
    public String license;
    public String readme;
    public Boolean template;
    @JsonProperty("trust_model")
    public String trustModel;
    @JsonProperty("object_format_name")
    public String objectFormatName;
    public String gitignores;
    @JsonProperty("issue_labels")
    public String issueLabels;
}
