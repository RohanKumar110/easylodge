package com.rohankumar.easylodge.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String path = request.getRequestURI();

        log.info("Incoming Request -> Method: {}, Path: {}", method, path);

        filterChain.doFilter(request, response);

        long timeTaken = System.currentTimeMillis() - start;

        log.info(
                "Completed Request -> Method: {}, Path: {}, Status: {}, Time: {} ms",
                method,
                path,
                response.getStatus(),
                timeTaken
        );
    }
}