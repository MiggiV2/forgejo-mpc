package de.mymiggi.forgejo.mcp.tools;

import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkiverse.mcp.server.test.McpAssured.McpStreamableTestClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = ForgejoStub.class, restrictToAnnotatedClass = true)
class ForgejoToolsErrorHandlingTest
{
	@BeforeEach
	void reset()
	{
		ForgejoStub.lastAuthorization = null;
		ForgejoStub.flakyCalls.set(0);
	}

	@Test
	void rawTokenIsSentWithTokenScheme()
	{
		// given
		McpStreamableTestClient client = clientWithAuthorization("abc123");

		// when
		callGetRepo(client, "demo", response -> assertFalse(response.isError()));

		// then
		assertEquals("token abc123", ForgejoStub.lastAuthorization);
	}

	@Test
	void authorizationWithSchemeIsPassedThrough()
	{
		// given
		McpStreamableTestClient client = clientWithAuthorization("Bearer abc123");

		// when
		callGetRepo(client, "demo", response -> assertFalse(response.isError()));

		// then
		assertEquals("Bearer abc123", ForgejoStub.lastAuthorization);
	}

	@Test
	void unauthorizedReturnsForgejoMessageAndTokenHint()
	{
		callGetRepo(clientWithAuthorization("stale"), "secret", response -> {
			String text = errorText(response);
			assertTrue(text.contains("401"), text);
			assertTrue(text.contains("access token does not exist"), text);
			assertTrue(text.contains("token"), text);
		});
	}

	@Test
	void notFoundReturnsForgejoMessage()
	{
		callGetRepo(clientWithAuthorization("abc"), "missing", response -> {
			String text = errorText(response);
			assertTrue(text.contains("404"), text);
			assertTrue(text.contains("The target couldn't be found."), text);
		});
	}

	@Test
	void serverErrorWithPlainBodyReturnsBody()
	{
		callGetRepo(clientWithAuthorization("abc"), "broken", response -> {
			String text = errorText(response);
			assertTrue(text.contains("500"), text);
			assertTrue(text.contains("boom"), text);
		});
	}

	@Test
	void transientUnavailabilityIsRetried()
	{
		callGetRepo(clientWithAuthorization("abc"), "flaky", response -> assertFalse(response.isError()));

		assertEquals(2, ForgejoStub.flakyCalls.get());
	}

	@Test
	void connectionFailureReturnsReadableError()
	{
		callGetRepo(clientWithAuthorization("abc"), "hangup", response -> {
			String text = errorText(response);
			assertTrue(text.contains("Could not reach Forgejo"), text);
		});
	}

	@Test
	void optionalArgumentsAreNotRequiredInSchema()
	{
		clientWithAuthorization("abc").when()
			.toolsList(page -> {
				JsonArray required = page.findByName("forgejoListIssues").inputSchema().getJsonArray("required");
				assertEquals(new JsonArray().add("owner").add("repo"), required);
			})
			.thenAssertResults();
	}

	private static McpStreamableTestClient clientWithAuthorization(String authorization)
	{
		return McpAssured.newStreamableClient()
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", authorization))
			.build()
			.connect();
	}

	private static void callGetRepo(McpStreamableTestClient client, String repo, Consumer<ToolResponse> assertion)
	{
		client.when()
			.toolsCall("forgejoGetRepo", Map.of("owner", "acme", "repo", repo), assertion)
			.thenAssertResults();
	}

	private static String errorText(ToolResponse response)
	{
		assertTrue(response.isError(), "expected an error response");
		return response.content().getFirst().asText().text();
	}
}
