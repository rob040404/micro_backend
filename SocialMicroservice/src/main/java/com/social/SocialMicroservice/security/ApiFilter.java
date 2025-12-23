package com.social.SocialMicroservice.security;


import com.social.SocialMicroservice.exceptions.NoApiKeyOrJwtException;
import com.social.SocialMicroservice.services.AuthenticationService;
import com.social.SocialMicroservice.services.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//Lo mismo que JWTFilter en el micro security
@Component
@Log4j2
@RequiredArgsConstructor
public class ApiFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
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
            "/files/**"

    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        //Comprobar por si acaso, pero debería funcionar
        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        String apiKeyHeader = request.getHeader("users_apikey");
        String token = null;

        // 🔹 Dejar pasar preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // ✅ Excluir rutas públicas (correctamente)
        if (PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            if(authHeader!=null && authHeader.startsWith("Bearer ")){
                token = authHeader.substring(7);
                authenticateWithJwt(token, request);
            }else if (apiKeyHeader != null){
                APIKeyAuthentication authentication = authenticationService.getAuthentication(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                log.warn("Token or ApiKey Validation validation ware unsuccessful");
                log.warn("No authentication provided for path: {}", path);
                throw new NoApiKeyOrJwtException();
            }
        } catch (Exception e) { //We use PrintWritter and not personalized exceptions because ControllerAdvise would not get it
            log.warn("Authentication failed for path: {}", path, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try (PrintWriter writer = response.getWriter()) {
                writer.print("{\"error\": \"Invalid or missing authentication credentials\"}");
                writer.flush();
            }
            return ;
        }

        filterChain.doFilter(request, response);

    }

    private void authenticateWithJwt(String token, HttpServletRequest request){
        if(jwtUtil.isTokenValid(token) ){
            String email = jwtUtil.extractEmail(token);
            UUID userId = UUID.fromString(jwtUtil.extractId(token)) ;
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token); // nueva función

            // Convert roles into GrantedAuthority
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring espera "ROLE_XXX"
                    .collect(Collectors.toList());

            // We create a "ficticios" Authentication (Without password, only for contxt)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email, null, authorities);

            // We save userId into Details. After that we extract it with details.get("userId").
            authentication.setDetails(new CustomUserDetails(userId, username, email, authorities));

            //We set the authentication into the context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
