package de.mymiggi.forgejo.mcp.tools;

import de.mymiggi.forgejo.mcp.client.ForgejoClient;
import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Repository;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class ForgejoTools
{
	@RestClient
	ForgejoClient service;

	@Tool(description = "Create a repository for the authenticated user.")
	public Repository forgejoCreateUserRepo(@ToolArg(description = "Repository options", required = true) CreateRepoOption body)
	{
		return service.createUserRepo(body);
	}

	@Tool(description = "Create a repository in an organization.")
	public Repository forgejoCreateOrgRepo(@ToolArg(description = "Organization name", required = true) String org,
		@ToolArg(description = "Repository options", required = true) CreateRepoOption body)
	{
		return service.createOrgRepo(org, body);
	}

	@Tool(description = "Create an issue in a repository.")
	public Issue forgejoCreateIssue(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "Issue options", required = true) CreateIssueOption body)
	{
		return service.createIssue(owner, repo, body);
	}

	@Tool(description = "List issues for a repository.")
	public List<Issue> forgejoListIssues(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "Issue state: open, closed, all") String state,
		@ToolArg(description = "Page number (1-based)") Integer page,
		@ToolArg(description = "Page size") Integer limit)
	{
		return service.listIssues(owner, repo, state, page, limit);
	}
}
