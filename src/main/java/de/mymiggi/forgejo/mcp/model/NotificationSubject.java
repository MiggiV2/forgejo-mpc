package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationSubject
{
	public String title;
	public String type;
	public String state;
	public String url;
	@JsonProperty("html_url")
	public String htmlUrl;
	@JsonProperty("latest_comment_url")
	public String latestCommentUrl;
	@JsonProperty("latest_comment_html_url")
	public String latestCommentHtmlUrl;
}
