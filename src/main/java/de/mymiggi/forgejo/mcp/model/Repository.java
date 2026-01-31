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
	@JsonProperty("private")
	public Boolean privateRepo;
}
