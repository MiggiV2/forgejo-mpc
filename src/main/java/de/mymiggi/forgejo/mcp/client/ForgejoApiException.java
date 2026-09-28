package de.mymiggi.forgejo.mcp.client;

public class ForgejoApiException extends RuntimeException
{
	private final int status;

	public ForgejoApiException(int status, String detail)
	{
		super(describe(status, detail));
		this.status = status;
	}

	public int status()
	{
		return status;
	}

	public static ForgejoApiException of(int status, String detail)
	{
		return isTransient(status) ? new ForgejoUnavailableException(status, detail) : new ForgejoApiException(status, detail);
	}

	private static boolean isTransient(int status)
	{
		return status == 502 || status == 503 || status == 504;
	}

	private static String describe(int status, String detail)
	{
		String sentence = detail.endsWith(".") ? detail : detail + ".";
		return "Forgejo API returned HTTP " + status + ": " + sentence + " " + hint(status);
	}

	private static String hint(int status)
	{
		return switch (status)
		{
			case 400, 422 -> "Forgejo rejected the request - check the tool arguments.";
			case 401 -> "The Forgejo access token in the MCP client's Authorization header is missing, invalid or revoked - create a new token in Forgejo (Settings > Applications) and update the MCP client config.";
			case 403 -> "The token lacks the permission or scope required for this operation.";
			case 404 -> "Owner, repository or resource does not exist, or the token has no access to it.";
			case 405, 409 -> "The operation conflicts with the current state (e.g. already exists or not mergeable).";
			case 429 -> "Forgejo rate-limited the request - retry later.";
			default -> status >= 500 ? "Forgejo server error - retry later." : "Unexpected Forgejo response.";
		};
	}
}
