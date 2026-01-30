# forgejo-mcp

Quarkus-based MCP server (WebSocket transport) that exposes Forgejo repository and issue operations via MCP tools.

## Features
- Create repositories for the authenticated user
- Create repositories in organizations
- Create issues in repositories
- List issues in repositories

## Configuration
Set credentials via environment variables (see `.env.example`):

- `FORGEJO_BASE_URL` – Forgejo instance base URL (e.g. `https://forgejo.example.com`)
- `FORGEJO_TOKEN` – API token (Authorization header uses `token <value>`)

The MCP WebSocket endpoint is `/mcp/ws`.

## MCP Tools
- `createUserRepo(body)` – body maps to `CreateRepoOption`
- `createOrgRepo(org, body)` – body maps to `CreateRepoOption`
- `createIssue(owner, repo, body)` – body maps to `CreateIssueOption`
- `listIssues(owner, repo, state, page, limit)`

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
