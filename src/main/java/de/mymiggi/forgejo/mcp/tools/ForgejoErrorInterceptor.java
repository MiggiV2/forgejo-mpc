package de.mymiggi.forgejo.mcp.tools;

import de.mymiggi.forgejo.mcp.client.ForgejoApiException;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ProcessingException;
import org.jboss.logging.Logger;

@TranslateForgejoErrors
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class ForgejoErrorInterceptor
{
	private static final Logger LOG = Logger.getLogger(ForgejoErrorInterceptor.class);

	@AroundInvoke
	Object translate(InvocationContext context) throws Exception
	{
		try
		{
			return context.proceed();
		}
		catch (ForgejoApiException e)
		{
			throw toolError(context, e.getMessage(), e);
		}
		catch (ProcessingException e)
		{
			throw toolError(context, "Could not reach Forgejo: " + rootCauseMessage(e) + ". The Forgejo instance may be down or unreachable - retry later.", e);
		}
	}

	private static ToolCallException toolError(InvocationContext context, String message, Exception cause)
	{
		LOG.warnf("Tool %s failed: %s", context.getMethod().getName(), message);
		return new ToolCallException(message, cause);
	}

	private static String rootCauseMessage(Throwable throwable)
	{
		Throwable root = throwable;
		while (root.getCause() != null && root.getCause() != root)
		{
			root = root.getCause();
		}
		return root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
	}
}
