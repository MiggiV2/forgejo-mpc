package de.mymiggi.forgejo.mcp.client;

import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Release;
import de.mymiggi.forgejo.mcp.model.ActionTaskList;
import de.mymiggi.forgejo.mcp.model.Repository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/api/v1")
@RegisterRestClient(configKey = "forgejo")
@RegisterClientHeaders(TokenPropagationHeadersFactory.class)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ForgejoClient
{

	@GET
	@Path("/repos/{owner}/{repo}/issues")
	List<Issue> listIssues(@PathParam("owner") String owner,
		@PathParam("repo") String repo,
		@QueryParam("state") String state,
		@QueryParam("page") Integer page,
		@QueryParam("limit") Integer limit);

	@GET
	@Path("/repos/{owner}/{repo}")
	Repository getRepo(@PathParam("owner") String owner, @PathParam("repo") String repo);

	@GET
	@Path("/repos/{owner}/{repo}/actions/tasks")
	ActionTaskList listActionTasks(@PathParam("owner") String owner, @PathParam("repo") String repo,
		@QueryParam("page") Integer page, @QueryParam("limit") Integer limit);

	@GET
	@Path("/repos/{owner}/{repo}/releases")
	List<Release> listReleases(@PathParam("owner") String owner, @PathParam("repo") String repo);

	@POST
	@Path("/repos/{owner}/{repo}/issues")
	Issue createIssue(@PathParam("owner") String owner,
		@PathParam("repo") String repo,
		CreateIssueOption body);

	@POST
	@Path("/user/repos")
	Repository createUserRepo(CreateRepoOption body);

	@POST
	@Path("/orgs/{org}/repos")
	Repository createOrgRepo(@PathParam("org") String org, CreateRepoOption body);
}
