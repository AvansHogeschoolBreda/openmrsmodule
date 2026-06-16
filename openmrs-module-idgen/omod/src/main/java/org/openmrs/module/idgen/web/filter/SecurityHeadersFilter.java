package org.openmrs.module.idgen.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityHeadersFilter implements Filter {

	private static final String REST_PATH_MARKER = "/ws/rest/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isBlockedMethod(httpRequest.getMethod().toUpperCase(Locale.ROOT), httpRequest.getRequestURI())) {
			httpResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		applySecurityHeaders(httpResponse);

		chain.doFilter(request, new SameSiteCookieResponseWrapper(httpResponse));
	}

	private boolean isBlockedMethod(String method, String requestUri) {
		if ("TRACE".equals(method) || "CONNECT".equals(method) || "PUT".equals(method)) {
			return true;
		}
		if ("DELETE".equals(method)) {
			return !requestUri.contains(REST_PATH_MARKER);
		}
		return false;
	}

	private void applySecurityHeaders(HttpServletResponse response) {
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Content-Security-Policy",
		    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
		            + "style-src 'self' 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'none'");
		response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
		response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
		response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
		response.setHeader("Cross-Origin-Embedder-Policy", "credentialless");
		// Tomcat only adds its own "Server" header at the connector level when none is set yet,
		// so an empty value here is the only way to mask the version from a module-level filter.
		response.setHeader("Server", "");
	}

	@Override
	public void destroy() {
	}

	// Decorates cookies the application sets explicitly. Tomcat writes its own JSESSIONID
	// cookie through the raw connector response before this wrapper sees it, so that one
	// still needs a Rfc6265CookieProcessor entry in the server's context.xml.
	private static final class SameSiteCookieResponseWrapper extends HttpServletResponseWrapper {

		SameSiteCookieResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void addHeader(String name, String value) {
			super.addHeader(name, decorateIfSetCookie(name, value));
		}

		@Override
		public void setHeader(String name, String value) {
			super.setHeader(name, decorateIfSetCookie(name, value));
		}

		private String decorateIfSetCookie(String name, String value) {
			if ("Set-Cookie".equalsIgnoreCase(name) && value != null
			        && !value.toLowerCase(Locale.ROOT).contains("samesite")) {
				return value + "; SameSite=Lax";
			}
			return value;
		}
	}
}
