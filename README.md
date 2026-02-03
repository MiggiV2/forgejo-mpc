# forgejo-mcp

![Java](https://img.shields.io/badge/Java-21-007396?logo=java&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-3.31.1-4695EB?logo=quarkus&logoColor=white)
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

## Quick start
1. Copy env file and set credentials:
	```bash
	copy env.example .env
	```
2. Update `.env` with your Forgejo details.
3. Start the server:
	```bash
	./mvnw quarkus:dev
	```

## Configuration
Set credentials via environment variables (see `.env.example`):

- `FORGEJO_BASE_URL` – Forgejo instance base URL (e.g. `https://forgejo.example.com`)
- `FORGEJO_TOKEN` – API token (Authorization header uses `token <value>`)

The MCP Streamable HTTP endpoint is `/mcp` (SSE fallback at `/mcp/sse`).

### Docker image
The image is published as `code.mymiggi.de/miggi/forgejo-mcp` (see `quarkus.container-image.*` in `application.properties`).

### Example
```
FORGEJO_BASE_URL=https://forgejo.example.com
FORGEJO_TOKEN=token-value
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
			"url": "http://localhost:8080/mcp"
		}
	}
}
```

Note: currently the Forgejo token is read from `FORGEJO_TOKEN`. In a future update, the server will use the API token from the MCP client `Authorization` header.

## MCP Tools
- `forgejoCreateUserRepo(body)` – body maps to `CreateRepoOption`
- `forgejoCreateOrgRepo(org, body)` – body maps to `CreateRepoOption`
- `forgejoGetRepo(owner, repo)`
- `forgejoCreateIssue(owner, repo, body)` – body maps to `CreateIssueOption`
- `forgejoListIssues(owner, repo, state, page, limit)`
- `forgejoListActionTasks(owner, repo, page, limit)`
- `forgejoListReleases(owner, repo)`
- `forgejoCreateRelease(owner, repo, tagName, targetCommitish, name, body, draft, prerelease)`
- `forgejoUpdateRelease(owner, repo, id, tagName, targetCommitish, name, body, draft, prerelease, hideArchiveLinks)`

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
