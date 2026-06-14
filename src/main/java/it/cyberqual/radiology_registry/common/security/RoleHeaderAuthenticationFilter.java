package it.cyberqual.radiology_registry.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads the {@code X-User-Role} HTTP header and populates the Spring Security context
 * with the corresponding role so that method-level or filter-level authorization rules
 * can evaluate access without a real authentication system.
 *
 * <p>Any non-blank value in the header is treated as a role (prefixed with {@code ROLE_}
 * if not already present). Requests without the header are treated as anonymous.
 */
public class RoleHeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String roleHeader = request.getHeader(ROLE_HEADER);

        if (roleHeader != null && !roleHeader.isBlank()) {
            String role = roleHeader.trim().toUpperCase();
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            role,
                            null,
                            List.of(new SimpleGrantedAuthority(authority))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}

