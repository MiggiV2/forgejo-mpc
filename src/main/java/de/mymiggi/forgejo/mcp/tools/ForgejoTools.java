package de.mymiggi.forgejo.mcp.tools;

import de.mymiggi.forgejo.mcp.client.ForgejoClient;
import de.mymiggi.forgejo.mcp.model.ActionTask;
import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreatePullRequestOption;
import de.mymiggi.forgejo.mcp.model.CreateReleaseOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.EditReleaseOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.MergePullRequestOption;
import de.mymiggi.forgejo.mcp.model.PullRequest;
import de.mymiggi.forgejo.mcp.model.Release;
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

	@Tool(description = "Get a repository.")
	public Repository forgejoGetRepo(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo)
	{
		return service.getRepo(owner, repo);
	}

	@Tool(description = "List releases for a repository.")
	public List<Release> forgejoListReleases(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo)
	{
		return service.listReleases(owner, repo);
	}

	@Tool(description = "Create a release in a repository.")
	public Release forgejoCreateRelease(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "Tag name", required = true) String tagName,
		@ToolArg(description = "Target commitish") String targetCommitish,
		@ToolArg(description = "Release name") String name,
		@ToolArg(description = "Release body") String body,
		@ToolArg(description = "Is draft") Boolean draft,
		@ToolArg(description = "Is prerelease") Boolean prerelease)
	{
		CreateReleaseOption option = new CreateReleaseOption();
		option.tagName = tagName;
		option.targetCommitish = targetCommitish;
		option.name = name;
		option.body = body;
		option.draft = draft;
		option.prerelease = prerelease;
		return service.createRelease(owner, repo, option);
	}

	@Tool(description = "Update a release in a repository.")
	public Release forgejoUpdateRelease(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "Release ID", required = true) Long id,
		@ToolArg(description = "Tag name") String tagName,
		@ToolArg(description = "Target commitish") String targetCommitish,
		@ToolArg(description = "Release name") String name,
		@ToolArg(description = "Release body") String body,
		@ToolArg(description = "Is draft") Boolean draft,
		@ToolArg(description = "Is prerelease") Boolean prerelease,
		@ToolArg(description = "Hide archive links") Boolean hideArchiveLinks)
	{
		EditReleaseOption option = new EditReleaseOption();
		option.tagName = tagName;
		option.targetCommitish = targetCommitish;
		option.name = name;
		option.body = body;
		option.draft = draft;
		option.prerelease = prerelease;
		option.hideArchiveLinks = hideArchiveLinks;
		return service.updateRelease(owner, repo, id, option);
	}

	@Tool(description = "List a repository's action tasks.")
	public List<ActionTask> forgejoListActionTasks(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "Page number (1-based)") Integer page,
		@ToolArg(description = "Page size") Integer limit)
	{
		return service.listActionTasks(owner, repo, page, limit).tasks;
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

	@Tool(description = "List pull requests for a repository.")
	public List<PullRequest> forgejoListPullRequests(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "PR state: open, closed, all") String state,
		@ToolArg(description = "Sort: oldest, recentupdate, leastupdate, mostcomment, leastcomment, priority") String sort,
		@ToolArg(description = "Page number (1-based)") Integer page,
		@ToolArg(description = "Page size") Integer limit)
	{
		return service.listPullRequests(owner, repo, state, sort, page, limit);
	}

	@Tool(description = "Get a pull request by index.")
	public PullRequest forgejoGetPullRequest(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "PR index (number)", required = true) Long index)
	{
		return service.getPullRequest(owner, repo, index);
	}

	@Tool(description = "Create a pull request in a repository.")
	public PullRequest forgejoCreatePullRequest(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "PR title", required = true) String title,
		@ToolArg(description = "PR body") String body,
		@ToolArg(description = "Head branch (source)", required = true) String head,
		@ToolArg(description = "Base branch (target)", required = true) String base)
	{
		CreatePullRequestOption option = new CreatePullRequestOption();
		option.title = title;
		option.body = body;
		option.head = head;
		option.base = base;
		return service.createPullRequest(owner, repo, option);
	}

	@Tool(description = "Merge a pull request.")
	public String forgejoMergePullRequest(@ToolArg(description = "Owner name", required = true) String owner,
		@ToolArg(description = "Repository name", required = true) String repo,
		@ToolArg(description = "PR index (number)", required = true) Long index,
		@ToolArg(description = "Merge strategy: merge, rebase, rebase-merge, squash, fast-forward-only, manually-merged", required = true) String doStrategy,
		@ToolArg(description = "Merge commit title") String mergeTitle,
		@ToolArg(description = "Merge commit message") String mergeMessage,
		@ToolArg(description = "Delete head branch after merge") Boolean deleteBranchAfterMerge)
	{
		MergePullRequestOption option = new MergePullRequestOption();
		option.doStrategy = doStrategy;
		option.mergeTitle = mergeTitle;
		option.mergeMessage = mergeMessage;
		option.deleteBranchAfterMerge = deleteBranchAfterMerge;
		service.mergePullRequest(owner, repo, index, option);
		return "Pull request " + index + " merged";
	}
}
