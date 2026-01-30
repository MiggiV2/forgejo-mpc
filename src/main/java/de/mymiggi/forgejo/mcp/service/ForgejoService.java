package de.mymiggi.forgejo.mcp.service;

import java.util.List;

import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Repository;
import de.mymiggi.forgejo.mcp.repository.ForgejoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ForgejoService {

    @Inject
    ForgejoRepository repository;

    public List<Issue> listIssues(String owner, String repo, String state, Integer page, Integer limit) {
        return repository.listIssues(owner, repo, state, page, limit);
    }

    public Issue createIssue(String owner, String repo, CreateIssueOption body) {
        return repository.createIssue(owner, repo, body);
    }

    public Repository createUserRepo(CreateRepoOption body) {
        return repository.createUserRepo(body);
    }

    public Repository createOrgRepo(String org, CreateRepoOption body) {
        return repository.createOrgRepo(org, body);
    }
}
