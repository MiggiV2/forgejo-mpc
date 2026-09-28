# forgejo-mcp

![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-3.39.5-4695EB?logo=quarkus&logoColor=white)
![MCP](https://img.shields.io/badge/MCP-Streamable%20HTTP-6E56CF)

Quarkus-based MCP server (Streamable HTTP transport) that exposes Forgejo repository and issue operations via MCP tools.

## Features
- Create repositories for the authenticated user
- Create repositories in organizations
- Get repositories
- Create issues in repositories
- List issues in repositories
- List action tasks
- List releases
- Create releases
- Update releases
- List pull requests
- Get a pull request
- Create pull requests
- Merge pull requests

## Quick start
1. Copy env file and set the Forgejo URL:
	```bash
	copy env.example .env
	```
2. Update `.env` with your Forgejo base URL.
3. Start the server:
	```bash
	./mvnw quarkus:dev
	```

## Configuration
Set the Forgejo base URL via environment variable (see `.env.example`):

- `FORGEJO_BASE_URL` – Forgejo instance base URL (e.g. `https://forgejo.example.com`)

There is no server-side token. The Forgejo API token is supplied by the MCP client via the `Authorization` header of the MCP request and forwarded as-is to Forgejo: a raw token value is sent as `token <value>`, a value that already carries a scheme (e.g. `token <value>` or `Bearer <value>`) is passed through unchanged.

The MCP Streamable HTTP endpoint is `/mcp` (SSE fallback at `/mcp/sse`).

### Docker image
The image is published as `code.mymiggi.de/miggi/forgejo-mcp` (see `quarkus.container-image.*` in `application.properties`).

### Example
```
FORGEJO_BASE_URL=https://forgejo.example.com
```

## Usage (setup example)
MCP URL (local dev):
```
http://localhost:8080/mcp
```

Example MCP client config:
```json
{
	"mcpServers": {
		"forgejo": {
			"transport": "streamable-http",
			"url": "http://localhost:8080/mcp",
			"headers": {
				"Authorization": "<forgejo-token>"
			}
		}
	}
}
```

## MCP Tools
`owner`/`repo`/ID args and `tagName`/`title`/`head`/`base`/`doStrategy` are required; `state`, `sort`, `page`, `limit` and the remaining release/PR fields are optional.
- `forgejoCreateUserRepo(body)` – body maps to `CreateRepoOption`
- `forgejoCreateOrgRepo(org, body)` – body maps to `CreateRepoOption`
- `forgejoGetRepo(owner, repo)`
- `forgejoCreateIssue(owner, repo, body)` – body maps to `CreateIssueOption`
- `forgejoListIssues(owner, repo, state, page, limit)`
- `forgejoListActionTasks(owner, repo, page, limit)`
- `forgejoListReleases(owner, repo)`
- `forgejoCreateRelease(owner, repo, tagName, targetCommitish, name, body, draft, prerelease)`
- `forgejoUpdateRelease(owner, repo, id, tagName, targetCommitish, name, body, draft, prerelease, hideArchiveLinks)`
- `forgejoListPullRequests(owner, repo, state, sort, page, limit)` – state: open/closed/all; sort: oldest/recentupdate/leastupdate/mostcomment/leastcomment/priority
- `forgejoGetPullRequest(owner, repo, index)`
- `forgejoCreatePullRequest(owner, repo, title, body, head, base)`
- `forgejoMergePullRequest(owner, repo, index, doStrategy, mergeTitle, mergeMessage, deleteBranchAfterMerge)` – doStrategy: merge/rebase/rebase-merge/squash/fast-forward-only/manually-merged

## Error handling
Forgejo API errors are returned as MCP tool errors (`isError` result) with the HTTP status, Forgejo's own error message and a hint (e.g. 401 → token missing/invalid/revoked, 404 → not found or no access) instead of a generic JSON-RPC "Internal error". Connection failures are reported as `Could not reach Forgejo: ...`. Read-only (GET) tools retry up to 2 times on 502/503/504 responses and connection errors; write operations (create/update/merge) are never retried. Timeouts: 5s connect, 30s read.

## Running
```bash
./mvnw quarkus:dev
```

## Tests
```bash
./mvnw test
```

## Reference
- MCP Server - WebSocket: https://docs.quarkiverse.io/quarkus-mcp-server/dev/index.html
- Forgejo API: see `context/swagger.v1.json`
