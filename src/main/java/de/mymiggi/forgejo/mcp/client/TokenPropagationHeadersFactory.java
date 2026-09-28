package de.mymiggi.forgejo.mcp.client;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
public class TokenPropagationHeadersFactory implements ClientHeadersFactory
{

	@Inject
	HttpServerRequest request;

	@Override
	public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders)
	{
		String authorization = request.getHeader("Authorization");
		if (authorization != null && !authorization.isBlank())
		{
			clientOutgoingHeaders.putSingle("Authorization", withScheme(authorization.strip()));
		}
		return clientOutgoingHeaders;
	}

	private static String withScheme(String authorization)
	{
		return authorization.contains(" ") ? authorization : "token " + authorization;
	}
}
