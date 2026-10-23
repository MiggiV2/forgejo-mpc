package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attachment
{
	public Long id;
	public String name;
	public Long size;
	public String type;
	public String uuid;
	@JsonProperty("download_count")
	public Long downloadCount;
	@JsonProperty("browser_download_url")
	public String browserDownloadUrl;
	@JsonProperty("created_at")
	public String createdAt;
}
