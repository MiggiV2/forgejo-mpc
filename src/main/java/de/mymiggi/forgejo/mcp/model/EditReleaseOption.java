package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EditReleaseOption
{
	public String body;
	public Boolean draft;
	@JsonProperty("hide_archive_links")
	public Boolean hideArchiveLinks;
	public String name;
	public Boolean prerelease;
	@JsonProperty("tag_name")
	public String tagName;
	@JsonProperty("target_commitish")
	public String targetCommitish;
}
