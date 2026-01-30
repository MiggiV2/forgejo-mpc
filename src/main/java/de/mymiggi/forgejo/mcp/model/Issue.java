package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Issue {
    public Long id;
    public Long number;
    public String title;
    public String state;
    public String body;
    @JsonProperty("html_url")
    public String htmlUrl;
}
