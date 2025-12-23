package com.security.security.security;

import com.security.security.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;



/**
 * This class filters the api key provided in the header. If it is correct and is coming from the right header
 * the request will pass the filter. It is important for the frontend to include the header and apikey correctly
 * in the petition
 *
 * For now this microservice has only one task: to do the login. And the login route is public. So this api key filter
 * is not used. But it could be used in the future if other requests come.
 */
@Component
@Log4j2 @RequiredArgsConstructor
public class ApiKeyFilter extends GenericFilterBean {

    private final AuthenticationService authenticationService;

    //list of paths that don't need api key identification
    private static final List<String> PUBLIC_PATHS = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/javamelody/**",
            "/monitoring",
            "/actuator/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/files/**",
            "/user/auth/login"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Method that checks if the provided api key in the right headers matches the one associated with
     * this microservice.
     * <p>
     * If the requested URI matches any of the public path patterns, the request
     * is allowed to proceed without authentication. Otherwise, the filter attempts
     * to extract and validate an API key (typically from the {@code X-API-Key} header, but not in this case).
     * On successful authentication, a {@link APIKeyAuthentication} object is placed
     * into Spring Security's context. If authentication fails, the filter responds
     * immediately with HTTP 401 (Unauthorized) and a JSON error message, and the request
     * is not passed further down the filter chain.
     * </p>
     *
     * @param request  the incoming servlet request; expected to be an instance of
     *                 {@link HttpServletRequest}. Used to obtain the request URI and
     *                  authentication headers (e.g., API key).
     * @param response the outgoing servlet response; expected to be an instance of
     *                {@link HttpServletResponse}. Used to send an error response
     *                when authentication fails.
     * @param filterChain the chain of subsequent filters and the target servlet.
     *                  Must be invoked to continue normal request processing—
     *                  but only if authentication succeeds or the path is public.
     * @throws IOException if an I/O error occurs while writing the error response.
     * @throws ServletException if an unexpected error occurs during filtering.
     *
     * @see #PUBLIC_PATHS
     * @see AuthenticationService#getAuthentication(HttpServletRequest)
     * @see SecurityContextHolder
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Excluding all public paths
        if (PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            APIKeyAuthentication authentication = authenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }catch (Exception e){
            log.warn("Invalid API key", e);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = httpServletResponse.getWriter();
            writer.print(e.getMessage());
            writer.flush();
            writer.close();
        }


    }

}
