package org.openmrs.module.idgen.web.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class SecurityHeadersFilterTest {

	private final SecurityHeadersFilter filter = new SecurityHeadersFilter();

	@Test
	public void doFilter_shouldSetBaselineSecurityHeaders() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/openmrs/initialsetup");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		assertEquals("nosniff", response.getHeader("X-Content-Type-Options"));
		assertEquals("DENY", response.getHeader("X-Frame-Options"));
		assertNotNull(response.getHeader("Content-Security-Policy"));
		assertNotNull(response.getHeader("Permissions-Policy"));
		assertEquals("same-origin", response.getHeader("Cross-Origin-Opener-Policy"));
		assertEquals("same-origin", response.getHeader("Cross-Origin-Resource-Policy"));
		assertEquals("credentialless", response.getHeader("Cross-Origin-Embedder-Policy"));
		assertEquals("", response.getHeader("Server"));

		ArgumentCaptor<ServletResponse> captor = ArgumentCaptor.forClass(ServletResponse.class);
		verify(chain, times(1)).doFilter(org.mockito.Mockito.eq(request), captor.capture());
		assertNotSame(response, captor.getValue());
	}

	@Test
	public void doFilter_shouldBlockPutEverywhere() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/openmrs/ws/rest/v1/idgen/identifiersource/abc");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
		verifyZeroInteractions(chain);
	}

	@Test
	public void doFilter_shouldBlockDeleteOutsideRestApi() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/openmrs/initialsetup");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
		verifyZeroInteractions(chain);
	}

	@Test
	public void doFilter_shouldAllowDeleteOnRestApi() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("DELETE",
		        "/openmrs/ws/rest/v1/idgen/identifiersource/abc");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		assertEquals(200, response.getStatus());
		verify(chain, times(1)).doFilter(org.mockito.Mockito.eq(request), org.mockito.Mockito.any(ServletResponse.class));
	}

	@Test
	public void doFilter_shouldBlockTraceAndConnect() throws Exception {
		MockHttpServletResponse traceResponse = new MockHttpServletResponse();
		filter.doFilter(new MockHttpServletRequest("TRACE", "/openmrs/"), traceResponse, mock(FilterChain.class));
		assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, traceResponse.getStatus());

		MockHttpServletResponse connectResponse = new MockHttpServletResponse();
		filter.doFilter(new MockHttpServletRequest("CONNECT", "/openmrs/"), connectResponse, mock(FilterChain.class));
		assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, connectResponse.getStatus());
	}

	@Test
	public void wrappedResponse_shouldAppendSameSiteToSetCookie() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/openmrs/");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain chain = mock(FilterChain.class);

		filter.doFilter(request, response, chain);

		ArgumentCaptor<ServletResponse> captor = ArgumentCaptor.forClass(ServletResponse.class);
		verify(chain).doFilter(org.mockito.Mockito.eq(request), captor.capture());

		HttpServletResponse wrapped = (HttpServletResponse) captor.getValue();
		wrapped.addHeader("Set-Cookie", "JSESSIONID=abc123; Path=/openmrs");

		assertEquals("JSESSIONID=abc123; Path=/openmrs; SameSite=Lax", response.getHeader("Set-Cookie"));
	}
}
