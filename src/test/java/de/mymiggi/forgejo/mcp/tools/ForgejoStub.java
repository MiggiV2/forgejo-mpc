package de.mymiggi.forgejo.mcp.tools;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ForgejoStub implements QuarkusTestResourceLifecycleManager
{
	static volatile String lastAuthorization;
	static final AtomicInteger flakyCalls = new AtomicInteger();
	static volatile String lastAssetQuery;
	static volatile String lastAssetContentType;
	static volatile byte[] lastAssetBody;
	static volatile String lastNotificationsQuery;
	static volatile String lastThreadMethod;
	static volatile String lastThreadQuery;

	private HttpServer server;

	@Override
	public Map<String, String> start()
	{
		try
		{
			server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		}
		catch (IOException e)
		{
			throw new IllegalStateException(e);
		}
		server.createContext("/api/v1/repos/acme/demo", exchange -> respond(exchange, 200, "{\"name\":\"demo\"}"));
		server.createContext("/api/v1/repos/acme/missing", exchange -> respond(exchange, 404,
			"{\"message\":\"The target couldn't be found.\",\"url\":\"https://forgejo/api/swagger\",\"errors\":[]}"));
		server.createContext("/api/v1/repos/acme/secret", exchange -> respond(exchange, 401,
			"{\"message\":\"access token does not exist [sha: abc]\",\"url\":\"https://forgejo/api/swagger\"}"));
		server.createContext("/api/v1/repos/acme/broken", exchange -> respond(exchange, 500, "boom"));
		server.createContext("/api/v1/repos/acme/flaky", exchange -> {
			if (flakyCalls.incrementAndGet() == 1)
			{
				respond(exchange, 503, "Service Unavailable");
			}
			else
			{
				respond(exchange, 200, "{\"name\":\"flaky\"}");
			}
		});
		server.createContext("/api/v1/repos/acme/hangup", HttpExchange::close);
		server.createContext("/api/v1/repos/acme/demo/releases/7/assets", exchange -> {
			lastAssetQuery = exchange.getRequestURI().getRawQuery();
			lastAssetContentType = exchange.getRequestHeaders().getFirst("Content-Type");
			lastAssetBody = exchange.getRequestBody().readAllBytes();
			respond(exchange, 201, "{\"id\":3,\"name\":\"app.zip\",\"size\":4,\"type\":\"attachment\","
				+ "\"browser_download_url\":\"https://forgejo/attachments/abc\"}");
		});
		server.createContext("/api/v1/notifications", exchange -> {
			lastNotificationsQuery = exchange.getRequestURI().getRawQuery();
			respond(exchange, 200, "[{\"id\":42,\"pinned\":false,\"unread\":true,\"updated_at\":\"2026-11-21T08:00:00Z\","
				+ "\"url\":\"https://forgejo/api/v1/notifications/threads/42\","
				+ "\"repository\":{\"id\":5,\"name\":\"easy-post\",\"full_name\":\"Miggi/easy-post\"},"
				+ "\"subject\":{\"title\":\"chore(deps): update pnpm to v11.28.13\",\"type\":\"Pull\",\"state\":\"open\","
				+ "\"url\":\"https://forgejo/api/v1/repos/Miggi/easy-post/pulls/3\","
				+ "\"html_url\":\"https://forgejo/Miggi/easy-post/pulls/3\"}}]");
		});
		server.createContext("/api/v1/notifications/threads/42", exchange -> {
			lastThreadMethod = exchange.getRequestMethod();
			lastThreadQuery = exchange.getRequestURI().getRawQuery();
			exchange.sendResponseHeaders(205, -1);
			exchange.close();
		});
		server.start();
		return Map.of("quarkus.rest-client.forgejo.url", "http://localhost:" + server.getAddress().getPort());
	}

	@Override
	public void stop()
	{
		if (server != null)
		{
			server.stop(0);
		}
	}

	private static void respond(HttpExchange exchange, int status, String body) throws IOException
	{
		lastAuthorization = exchange.getRequestHeaders().getFirst("Authorization");
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, bytes.length);
		exchange.getResponseBody().write(bytes);
		exchange.close();
	}
}
