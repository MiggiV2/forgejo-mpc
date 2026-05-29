package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PRBranchInfo
{
	public String label;
	public String ref;
	public String sha;
	@JsonProperty("repo_id")
	public Long repoId;
}
