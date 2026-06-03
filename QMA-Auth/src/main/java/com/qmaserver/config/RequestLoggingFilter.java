package com.qmaserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
        private static final Logger log = LogManager.getLogger(RequestLoggingFilter.class);

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                long startTime = System.currentTimeMillis();

                log.info(">>> REQUEST  [{} {}]",
                                request.getMethod(),
                                request.getRequestURI());

                filterChain.doFilter(request, response);

                long duration = System.currentTimeMillis() - startTime;
                log.info("<<< RESPONSE [{} {}] | Status: {} | Time: {}ms",
                                request.getMethod(),
                                request.getRequestURI(),
                                response.getStatus(),
                                duration);
        }
}
