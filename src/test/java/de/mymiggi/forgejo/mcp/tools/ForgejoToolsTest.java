package de.mymiggi.forgejo.mcp.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Repository;
import de.mymiggi.forgejo.mcp.service.ForgejoService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ForgejoToolsTest {

    @Inject
    ForgejoTools tools;

    @InjectMock
    ForgejoService service;

    @Test
    void createUserRepoCallsService() {
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
    void createOrgRepoCallsService() {
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
    void createIssueCallsService() {
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
    void listIssuesCallsService() {
        Issue issue = new Issue();
        issue.title = "Bug";
        List<Issue> expected = List.of(issue);
        when(service.listIssues("acme", "repo", "open", 1, 20)).thenReturn(expected);

        List<Issue> result = tools.forgejoListIssues("acme", "repo", "open", 1, 20);

        assertEquals(1, result.size());
        assertSame(expected, result);
        verify(service).listIssues("acme", "repo", "open", 1, 20);
    }
}
