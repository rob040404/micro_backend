package com.social.SocialMicroservice.security;


import com.social.SocialMicroservice.exceptions.NoApiKeyOrJwtException;
import com.social.SocialMicroservice.services.AuthenticationService;
import com.social.SocialMicroservice.services.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class ApiFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

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
                if(jwtUtil.isTokenValid(token) ){
                    String email = jwtUtil.extractEmail(token);
                    UUID userId = UUID.fromString(jwtUtil.extractId(token)) ;
                    String username = jwtUtil.extractUsername(token);
                    List<String> roles = jwtUtil.extractRoles(token); // nueva función

                    // Convertir roles a GrantedAuthority
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring espera "ROLE_XXX"
                            .collect(Collectors.toList());

                    // Creamos un Authentication "ficticio" (sin password, solo para contexto)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email, null, authorities);

                    // Guardar userId en detalles. Luego lo extraemos (Long) details.get("userId")
                    authentication.setDetails(new CustomUserDetails(userId, username, email, authorities));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }else if (apiKeyHeader != null){
                APIKeyAuthentication authentication = AuthenticationService.getAuthentication((HttpServletRequest) request); //redundante
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                throw new NoApiKeyOrJwtException();
            }
        } catch (Exception e) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response; //redundante
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
