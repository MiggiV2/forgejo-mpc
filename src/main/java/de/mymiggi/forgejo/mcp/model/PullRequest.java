package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PullRequest
{
	public Long id;
	public Long number;
	public String title;
	public String state;
	public String body;
	public Boolean draft;
	public Boolean merged;
	public Boolean mergeable;
	@JsonProperty("html_url")
	public String htmlUrl;
	@JsonProperty("created_at")
	public String createdAt;
	@JsonProperty("updated_at")
	public String updatedAt;
	@JsonProperty("merged_at")
	public String mergedAt;
	public PRBranchInfo head;
	public PRBranchInfo base;
}
