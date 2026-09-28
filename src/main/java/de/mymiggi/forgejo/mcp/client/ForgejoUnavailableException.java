package de.mymiggi.forgejo.mcp.client;

public class ForgejoUnavailableException extends ForgejoApiException
{
	public ForgejoUnavailableException(int status, String detail)
	{
		super(status, detail);
	}
}
