package de.mymiggi.forgejo.mcp.tools;

import io.quarkiverse.mcp.server.ToolResponse;
import io.quarkiverse.mcp.server.test.McpAssured;
import io.quarkiverse.mcp.server.test.McpAssured.McpStreamableTestClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = ForgejoStub.class, restrictToAnnotatedClass = true)
class ForgejoNotificationsTest
{
	@BeforeEach
	void reset()
	{
		ForgejoStub.lastNotificationsQuery = null;
		ForgejoStub.lastThreadMethod = null;
		ForgejoStub.lastThreadQuery = null;
	}

	@Test
	void listNotificationsMapsThreads()
	{
		// when
		call("forgejoListNotifications", Map.of(), response -> {
			assertFalse(response.isError(), () -> response.content().getFirst().asText().text());
			JsonObject thread = new JsonObject(response.content().getFirst().asText().text());

			// then
			assertEquals(1, response.content().size());
			assertEquals(42, thread.getInteger("id"));
			assertTrue(thread.getBoolean("unread"));
			assertEquals("Miggi/easy-post", thread.getJsonObject("repository").getString("full_name"));
			JsonObject subject = thread.getJsonObject("subject");
			assertEquals("chore(deps): update pnpm to v11.28.13", subject.getString("title"));
			assertEquals("Pull", subject.getString("type"));
			assertEquals("open", subject.getString("state"));
			assertEquals("https://forgejo/Miggi/easy-post/pulls/3", subject.getString("html_url"));
		});
		assertNull(ForgejoStub.lastNotificationsQuery);
	}

	@Test
	void listNotificationsSendsFiltersAsRepeatedQueryParams()
	{
		// when
		call("forgejoListNotifications",
			Map.of("all", true, "statusTypes", List.of("unread", "pinned"), "subjectTypes", List.of("pull"),
				"since", "2026-11-21T00:00:00Z", "page", 2, "limit", 10),
			response -> assertFalse(response.isError(), () -> response.content().getFirst().asText().text()));

		// then
		String query = ForgejoStub.lastNotificationsQuery;
		assertTrue(query.contains("all=true"), query);
		assertTrue(query.contains("status-types=unread"), query);
		assertTrue(query.contains("status-types=pinned"), query);
		assertTrue(query.contains("subject-type=pull"), query);
		assertTrue(query.contains("since=2026-11-21T00%3A00%3A00Z") || query.contains("since=2026-11-21T00:00:00Z"), query);
		assertTrue(query.contains("page=2"), query);
		assertTrue(query.contains("limit=10"), query);
	}

	@Test
	void markNotificationReadPatchesThread()
	{
		// when
		call("forgejoMarkNotificationRead", Map.of("id", 42), response -> {
			assertFalse(response.isError(), () -> response.content().getFirst().asText().text());
			assertTrue(response.content().getFirst().asText().text().contains("42"));
		});

		// then
		assertEquals("PATCH", ForgejoStub.lastThreadMethod);
		assertEquals("to-status=read", ForgejoStub.lastThreadQuery);
	}

	@Test
	void onlyThreadIdIsRequiredInSchema()
	{
		client().when()
			.toolsList(page -> {
				JsonArray required = page.findByName("forgejoListNotifications").inputSchema().getJsonArray("required");
				assertTrue(required == null || required.isEmpty(), () -> String.valueOf(required));
				assertEquals(new JsonArray().add("id"),
					page.findByName("forgejoMarkNotificationRead").inputSchema().getJsonArray("required"));
			})
			.thenAssertResults();
	}

	private static void call(String tool, Map<String, Object> arguments, Consumer<ToolResponse> assertion)
	{
		client().when()
			.toolsCall(tool, arguments, assertion)
			.thenAssertResults();
	}

	private static McpStreamableTestClient client()
	{
		return McpAssured.newStreamableClient()
			.setAdditionalHeaders(message -> MultiMap.caseInsensitiveMultiMap().add("Authorization", "abc"))
			.build()
			.connect();
	}
}
