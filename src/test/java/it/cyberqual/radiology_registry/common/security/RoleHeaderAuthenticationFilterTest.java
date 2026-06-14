package it.cyberqual.radiology_registry.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RoleHeaderAuthenticationFilter}.
 * Verifies that the filter correctly reads the {@code X-User-Role} header
 * and populates the Spring Security context.
 */
class RoleHeaderAuthenticationFilterTest {

    private final RoleHeaderAuthenticationFilter filter = new RoleHeaderAuthenticationFilter();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAdminRoleWhenHeaderIsAdmin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RoleHeaderAuthenticationFilter.ROLE_HEADER, "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }

    @Test
    void shouldNotSetAuthenticationWhenHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNormalizeRoleToUpperCase() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RoleHeaderAuthenticationFilter.ROLE_HEADER, "admin");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }

    @Test
    void shouldNotDuplicateRolePrefixWhenAlreadyPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RoleHeaderAuthenticationFilter.ROLE_HEADER, "ROLE_ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        long count = authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_ADMIN"))
                .count();
        assertEquals(1, count);
    }

    @Test
    void shouldInvokeNextFilterInChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        // MockFilterChain tracks whether the chain was invoked
        assertNotNull(chain.getRequest(), "Filter chain must be invoked");
    }
}

