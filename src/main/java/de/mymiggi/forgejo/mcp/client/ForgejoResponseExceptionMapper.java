package de.mymiggi.forgejo.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.util.ArrayList;
import java.util.List;

public class ForgejoResponseExceptionMapper implements ResponseExceptionMapper<ForgejoApiException>
{
	private static final ObjectMapper JSON = new ObjectMapper();
	private static final int MAX_DETAIL_LENGTH = 500;

	@Override
	public ForgejoApiException toThrowable(Response response)
	{
		return ForgejoApiException.of(response.getStatus(), detail(response));
	}

	private static String detail(Response response)
	{
		String body = readBody(response);
		if (body.isBlank())
		{
			return response.getStatusInfo().getReasonPhrase();
		}
		String message = forgejoMessage(body);
		return truncate(message != null ? message : body.strip());
	}

	private static String readBody(Response response)
	{
		try
		{
			return response.hasEntity() ? response.readEntity(String.class) : "";
		}
		catch (RuntimeException e)
		{
			return "";
		}
	}

	private static String forgejoMessage(String body)
	{
		try
		{
			JsonNode json = JSON.readTree(body);
			String message = json.path("message").asText("");
			if (message.isBlank())
			{
				return null;
			}
			List<String> errors = new ArrayList<>();
			json.path("errors").forEach(error -> errors.add(error.asText()));
			return errors.isEmpty() ? message : message + " (" + String.join("; ", errors) + ")";
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static String truncate(String text)
	{
		return text.length() > MAX_DETAIL_LENGTH ? text.substring(0, MAX_DETAIL_LENGTH) + "..." : text;
	}
}
