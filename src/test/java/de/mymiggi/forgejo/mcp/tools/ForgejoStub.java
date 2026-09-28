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
