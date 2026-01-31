package de.mymiggi.forgejo.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreateIssueOption
{
	public String title;
	public String body;
	public List<String> assignees;
	public List<Long> labels;
	public Long milestone;
	@JsonProperty("due_date")
	public String dueDate;
	public String ref;
	public Boolean closed;
}
