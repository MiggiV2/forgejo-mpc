package de.mymiggi.forgejo.mcp.repository;

import java.util.List;

import de.mymiggi.forgejo.mcp.client.ForgejoClient;
import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ForgejoRepository {

    @RestClient
    ForgejoClient client;

    public List<Issue> listIssues(String owner, String repo, String state, Integer page, Integer limit) {
        return client.listIssues(owner, repo, state, page, limit);
    }

    public Issue createIssue(String owner, String repo, CreateIssueOption body) {
        return client.createIssue(owner, repo, body);
    }

    public Repository createUserRepo(CreateRepoOption body) {
        return client.createUserRepo(body);
    }

    public Repository createOrgRepo(String org, CreateRepoOption body) {
        return client.createOrgRepo(org, body);
    }
}
