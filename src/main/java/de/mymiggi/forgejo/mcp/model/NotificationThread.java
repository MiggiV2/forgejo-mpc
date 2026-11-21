package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationThread
{
	public Long id;
	public Boolean pinned;
	public Boolean unread;
	@JsonProperty("updated_at")
	public String updatedAt;
	public String url;
	public Repository repository;
	public NotificationSubject subject;
}
