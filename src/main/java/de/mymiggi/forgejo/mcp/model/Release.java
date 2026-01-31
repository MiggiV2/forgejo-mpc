package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Release {
    public Long id;
    @JsonProperty("tag_name")
    public String tagName;
    public String name;
    public String body;
    public Boolean draft;
    public Boolean prerelease;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("published_at")
    public String publishedAt;
}
