package com.picnic.potluck.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Component
public class Logging extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger("http");

	private static final Set<String> BODY_TYPES = Set.of(
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_FORM_URLENCODED_VALUE
	);
	private static final int MAX_BODY = 8192;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String p = request.getRequestURI();
		return p.startsWith("/actuator/health") || p.startsWith("/swagger-ui") || p.startsWith("/v3/api-docs");
	}

	private boolean shouldLogBody(HttpServletRequest request) {
		String ct = request.getContentType();
		if (ct == null) return false;
		for (String allowed : BODY_TYPES) {
			if (ct.startsWith(allowed)) return true;
		}
		return false;
	}

	private String safeBody(byte[] bytes, String encoding) {
		if (bytes == null) return "";
		if (bytes.length == 0) return "";
		var cs = StringUtils.hasText(encoding) ? java.nio.charset.Charset.forName(encoding) : StandardCharsets.UTF_8;
		return new String(bytes, cs);
	}

	private String truncate(String s) {
		if (s == null) return "";
		return (s.length() > MAX_BODY) ? s.substring(0, MAX_BODY) + "...[truncated]" : s;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		long start = System.nanoTime();
		String reqId = req.getHeader("X-Request-Id");
		if (reqId == null || reqId.isBlank()) reqId = UUID.randomUUID().toString();

		MDC.put("requestId", reqId);
		MDC.put("method", req.getMethod());
		MDC.put("path", req.getRequestURI());

		// In a previous version, where we read the body no matter what,
		// certain requests did not like us consuming the body through .getInputStream().
		// This left Jackson with a mangled response and threw an HttpMessageNotReadableException.
		// Instead, we now check which requests will not get mad at us for doing this.
		boolean wantBody = shouldLogBody(req);
		HttpServletRequest request = req;
		HttpServletResponse response = res;

		if (wantBody) {
			request = new ContentCachingRequestWrapper(req);
			response = new ContentCachingResponseWrapper(res);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			int status = response.getStatus();
			long ms = (System.nanoTime() - start) / 1000000;

			if (wantBody) {
				String reqBody = safeBody(((ContentCachingRequestWrapper) request).getContentAsByteArray(), request.getCharacterEncoding());
				String resBody = safeBody(((ContentCachingResponseWrapper) response).getContentAsByteArray(), response.getCharacterEncoding());

				log.info("HTTP {} {} -> {} in {}ms reqBytes={} resBytes={} reqBody={} resBody={}",
						request.getMethod(), request.getRequestURI(), status, ms,
						reqBody.length(), resBody.length(),
						truncate(reqBody), truncate(resBody));

				// We need to do this, otherwise the client will not get anything back from the body
				// and will also scream at us as Jackson did.
				((ContentCachingResponseWrapper) response).copyBodyToResponse();
			} else {
				log.info("HTTP {} {} -> {} in {}ms", request.getMethod(), request.getRequestURI(), status, ms);
			}
			MDC.clear();
		}
	}
}