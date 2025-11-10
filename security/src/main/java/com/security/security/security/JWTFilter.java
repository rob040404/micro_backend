package com.security.security.security;

import com.security.security.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

//Filtro personalizado JWT -> REALMENTE ES API_KEY FILTER
@Log4j2
public class JWTFilter extends GenericFilterBean {

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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // ✅ Excluir rutas públicas (correctamente)
        if (PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            APIKeyAuthentication authentication = AuthenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
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

        filterChain.doFilter(request, response);
    }

}
