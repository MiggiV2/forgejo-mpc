package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateReleaseOption
{
	@JsonProperty("tag_name")
	public String tagName;
	@JsonProperty("target_commitish")
	public String targetCommitish;
	public String name;
	public String body;
	public Boolean draft;
	public Boolean prerelease;
	@JsonProperty("hide_archive_links")
	public Boolean hideArchiveLinks;
}
