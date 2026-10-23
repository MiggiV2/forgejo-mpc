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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = ForgejoStub.class, restrictToAnnotatedClass = true)
class ForgejoReleaseAssetTest
{
	@BeforeEach
	void reset()
	{
		ForgejoStub.lastAssetQuery = null;
		ForgejoStub.lastAssetContentType = null;
		ForgejoStub.lastAssetBody = null;
	}

	@Test
	void base64ContentIsUploadedAsRawBytes()
	{
		// given
		byte[] binary = { 0x50, 0x4b, 0x03, (byte) 0xff };

		// when
		upload(Map.of("name", "app.zip", "contentBase64", Base64.getEncoder().encodeToString(binary)),
			response -> {
				assertFalse(response.isError(), () -> response.content().getFirst().asText().text());
				String text = response.content().getFirst().asText().text();
				assertTrue(text.contains("https://forgejo/attachments/abc"), text);
			});

		// then
		assertEquals("name=app.zip", ForgejoStub.lastAssetQuery);
		assertTrue(ForgejoStub.lastAssetContentType.startsWith("application/octet-stream"), ForgejoStub.lastAssetContentType);
		assertArrayEquals(binary, ForgejoStub.lastAssetBody);
	}

	@Test
	void textContentIsUploadedAsUtf8()
	{
		// when
		upload(Map.of("name", "checksums.txt", "content", "abc  app.zip\n"), response -> assertFalse(response.isError()));

		// then
		assertEquals("name=checksums.txt", ForgejoStub.lastAssetQuery);
		assertArrayEquals("abc  app.zip\n".getBytes(StandardCharsets.UTF_8), ForgejoStub.lastAssetBody);
	}

	@Test
	void externalUrlIsSentAsMultipartFormField()
	{
		// when
		upload(Map.of("name", "docs", "externalUrl", "https://example.org/docs.pdf"), response -> assertFalse(response.isError()));

		// then
		assertEquals("name=docs", ForgejoStub.lastAssetQuery);
		assertTrue(ForgejoStub.lastAssetContentType.startsWith("multipart/form-data"), ForgejoStub.lastAssetContentType);
		String body = new String(ForgejoStub.lastAssetBody, StandardCharsets.UTF_8);
		assertTrue(body.contains("name=\"external_url\""), body);
		assertTrue(body.contains("https://example.org/docs.pdf"), body);
	}

	@Test
	void missingContentIsRejectedWithoutCallingForgejo()
	{
		upload(Map.of("name", "app.zip"), response -> {
			assertTrue(response.isError());
			assertTrue(response.content().getFirst().asText().text().contains("exactly one"));
		});

		assertNull(ForgejoStub.lastAssetBody);
	}

	@Test
	void multipleContentSourcesAreRejected()
	{
		upload(Map.of("name", "app.zip", "content", "x", "externalUrl", "https://example.org/x"), response -> {
			assertTrue(response.isError());
			assertTrue(response.content().getFirst().asText().text().contains("exactly one"));
		});

		assertNull(ForgejoStub.lastAssetBody);
	}

	@Test
	void invalidBase64IsRejectedWithoutCallingForgejo()
	{
		upload(Map.of("name", "app.zip", "contentBase64", "not base64!"), response -> {
			assertTrue(response.isError());
			assertTrue(response.content().getFirst().asText().text().contains("base64"));
		});

		assertNull(ForgejoStub.lastAssetBody);
	}

	@Test
	void emptyOptionalSourcesAreTreatedAsAbsent()
	{
		// when
		upload(Map.of("name", "notes.txt", "content", "x", "contentBase64", "", "externalUrl", ""),
			response -> assertFalse(response.isError(), () -> response.content().getFirst().asText().text()));

		// then
		assertArrayEquals("x".getBytes(StandardCharsets.UTF_8), ForgejoStub.lastAssetBody);
	}

	@Test
	void blankNameIsRejectedWithoutCallingForgejo()
	{
		upload(Map.of("name", "  ", "content", "x"), response -> {
			assertTrue(response.isError());
			assertTrue(response.content().getFirst().asText().text().contains("name"));
		});

		assertNull(ForgejoStub.lastAssetBody);
	}

	@Test
	void contentAboveSizeLimitIsRejectedWithoutCallingForgejo()
	{
		upload(Map.of("name", "big.bin", "contentBase64", Base64.getEncoder().encodeToString(new byte[65])), response -> {
			assertTrue(response.isError());
			assertTrue(response.content().getFirst().asText().text().contains("64 bytes"));
		});

		assertNull(ForgejoStub.lastAssetBody);
	}

	@Test
	void onlyLocatorArgumentsAreRequiredInSchema()
	{
		client().when()
			.toolsList(page -> {
				JsonArray required = page.findByName("forgejoUploadReleaseAsset").inputSchema().getJsonArray("required");
				assertEquals(new JsonArray().add("owner").add("repo").add("releaseId").add("name"), required);
			})
			.thenAssertResults();
	}

	private static void upload(Map<String, Object> arguments, Consumer<ToolResponse> assertion)
	{
		Map<String, Object> allArguments = new HashMap<>(arguments);
		allArguments.put("owner", "acme");
		allArguments.put("repo", "demo");
		allArguments.put("releaseId", 7);
		client().when()
			.toolsCall("forgejoUploadReleaseAsset", allArguments, assertion)
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
