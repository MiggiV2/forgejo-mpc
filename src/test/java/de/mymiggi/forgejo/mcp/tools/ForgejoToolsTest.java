package de.mymiggi.forgejo.mcp.tools;

import de.mymiggi.forgejo.mcp.client.ForgejoClient;
import de.mymiggi.forgejo.mcp.model.ActionTask;
import de.mymiggi.forgejo.mcp.model.ActionTaskList;
import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.EditReleaseOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Release;
import de.mymiggi.forgejo.mcp.model.Repository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class ForgejoToolsTest
{
	@Inject
	ForgejoTools tools;

	@InjectMock
	@RestClient
	ForgejoClient service;

	@Test
	void createUserRepoCallsService()
	{
		CreateRepoOption option = new CreateRepoOption();
		option.name = "demo";
		Repository expected = new Repository();
		expected.name = "demo";
		when(service.createUserRepo(option)).thenReturn(expected);

		Repository result = tools.forgejoCreateUserRepo(option);

		assertSame(expected, result);
		verify(service).createUserRepo(option);
	}

	@Test
	void createOrgRepoCallsService()
	{
		CreateRepoOption option = new CreateRepoOption();
		option.name = "demo";
		Repository expected = new Repository();
		expected.name = "demo";
		when(service.createOrgRepo("acme", option)).thenReturn(expected);

		Repository result = tools.forgejoCreateOrgRepo("acme", option);

		assertSame(expected, result);
		verify(service).createOrgRepo("acme", option);
	}

	@Test
	void getRepoCallsService()
	{
		Repository expected = new Repository();
		expected.name = "demo";
		when(service.getRepo("acme", "demo")).thenReturn(expected);

		Repository result = tools.forgejoGetRepo("acme", "demo");

		assertSame(expected, result);
		verify(service).getRepo("acme", "demo");
	}

	@Test
	void createIssueCallsService()
	{
		CreateIssueOption option = new CreateIssueOption();
		option.title = "Bug";
		Issue expected = new Issue();
		expected.title = "Bug";
		when(service.createIssue("acme", "repo", option)).thenReturn(expected);

		Issue result = tools.forgejoCreateIssue("acme", "repo", option);

		assertSame(expected, result);
		verify(service).createIssue("acme", "repo", option);
	}

	@Test
	void listIssuesCallsService()
	{
		Issue issue = new Issue();
		issue.title = "Bug";
		List<Issue> expected = List.of(issue);
		when(service.listIssues("acme", "repo", "open", 1, 20)).thenReturn(expected);

		List<Issue> result = tools.forgejoListIssues("acme", "repo", "open", 1, 20);

		assertEquals(1, result.size());
		assertSame(expected, result);
		verify(service).listIssues("acme", "repo", "open", 1, 20);
	}

	@Test
	void listReleasesCallsService()
	{
		Release release = new Release();
		release.tagName = "v1.0.0";
		List<Release> expected = List.of(release);
		when(service.listReleases("acme", "repo")).thenReturn(expected);

		List<Release> result = tools.forgejoListReleases("acme", "repo");

		assertEquals(1, result.size());
		assertSame(expected, result);
		verify(service).listReleases("acme", "repo");
	}

	@Test
	void updateReleaseCallsService()
	{
		Release expected = new Release();
		expected.tagName = "v2.0.0";
		when(service.updateRelease(org.mockito.ArgumentMatchers.eq("acme"),
			org.mockito.ArgumentMatchers.eq("repo"),
			org.mockito.ArgumentMatchers.eq(42L),
			org.mockito.ArgumentMatchers.any(EditReleaseOption.class))).thenReturn(expected);

		Release result = tools.forgejoUpdateRelease("acme", "repo", 42L, "v2.0.0", "main", "Release 2.0",
			"notes", true, false, true);

		assertSame(expected, result);
		verify(service).updateRelease(org.mockito.ArgumentMatchers.eq("acme"),
			org.mockito.ArgumentMatchers.eq("repo"),
			org.mockito.ArgumentMatchers.eq(42L),
			org.mockito.ArgumentMatchers.any(EditReleaseOption.class));
	}

	@Test
	void listActionTasksCallsService()
	{
		ActionTask task = new ActionTask();
		task.name = "CI";
		ActionTaskList expected = new ActionTaskList();
		expected.tasks = List.of(task);
		when(service.listActionTasks("acme", "repo", 1, 20)).thenReturn(expected);

		List<ActionTask> result = tools.forgejoListActionTasks("acme", "repo", 1, 20);

		assertEquals(1, result.size());
		assertSame(expected.tasks, result);
		verify(service).listActionTasks("acme", "repo", 1, 20);
	}
}
