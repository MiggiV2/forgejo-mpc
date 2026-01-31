package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository
{
	public Long id;
	public String name;
	@JsonProperty("full_name")
	public String fullName;
	@JsonProperty("html_url")
	public String htmlUrl;
	@JsonProperty("ssh_url")
	public String sshUrl;
	@JsonProperty("clone_url")
	public String cloneUrl;
	@JsonProperty("default_branch")
	public String defaultBranch;
	public String description;
	@JsonProperty("private")
	public Boolean privateRepo;
}
