package de.mymiggi.forgejo.mcp.client;

import java.util.List;

import de.mymiggi.forgejo.mcp.model.CreateIssueOption;
import de.mymiggi.forgejo.mcp.model.CreateRepoOption;
import de.mymiggi.forgejo.mcp.model.Issue;
import de.mymiggi.forgejo.mcp.model.Repository;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;

@Path("/api/v1")
@RegisterRestClient(configKey = "forgejo")
@ClientHeaderParam(name = "Authorization", value = "token ${forgejo.token}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ForgejoClient {

    @GET
    @Path("/repos/{owner}/{repo}/issues")
    List<Issue> listIssues(@PathParam("owner") String owner,
            @PathParam("repo") String repo,
            @QueryParam("state") String state,
            @QueryParam("page") Integer page,
            @QueryParam("limit") Integer limit);

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
