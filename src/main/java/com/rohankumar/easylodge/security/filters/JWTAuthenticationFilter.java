package com.rohankumar.easylodge.security.filters;

import com.rohankumar.easylodge.exceptions.TokenExpiredException;
import com.rohankumar.easylodge.security.services.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver exceptionResolver;

    public JWTAuthenticationFilter(
            JWTService jwtService,
            UserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {

        try {

            final String header = request.getHeader(AUTHORIZATION);
            if(StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {

                filterChain.doFilter(request, response);
                return;
            }

            final String token = header.substring("Bearer ".length());
            final String userEmail = jwtService.extractUsername(token);

            if(!StringUtils.isBlank(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if(jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new TokenExpiredException("Session has been expired");
                }
            }

            filterChain.doFilter(request,response);

        } catch(ExpiredJwtException ex) {
            logger.warn("Token has expired");
            exceptionResolver.resolveException(request, response, null, new TokenExpiredException("Session has been expired"));
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid token");
            exceptionResolver.resolveException(request, response, null, ex);
        } catch(SignatureException ex) {
            logger.warn("Token signature invalid");
            exceptionResolver.resolveException(request, response, null, ex);
        } catch(Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
