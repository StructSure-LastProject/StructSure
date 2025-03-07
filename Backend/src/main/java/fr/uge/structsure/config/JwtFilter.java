package fr.uge.structsure.config;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

/**
 * This is a filter that will be called before any request. Its role is to check
 * if the client is authorized to access the API.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtFilter(CustomUserDetailsService customUserDetailsService, JwtUtils jwtUtils) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * This is the main function that will check if the user is authorized
     * by verifying if there is a token in the request and if it is valid.
     * @param request the request of the client
     * @param response the response for the client
     * @param filterChain the filter
     * @throws IOException if an I/ O error occurs during the processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.endsWith("null")) {
            String encodedRedirectURL = response.encodeRedirectURL(
                request.getContextPath() + "/login");
            response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
            response.setHeader("Location", encodedRedirectURL);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            String username = jwtUtils.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            tryRenewToken(request, response, jwt, username);
        } catch (ExpiredJwtException | UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Puts an updated token with a longer expiration date in the
     * response header if the given one is about to expire.
     * @param request to make sure the call is an API endpoint
     * @param response to put the new token in
     * @param jwt the current token to check
     * @param username the login of the user
     */
    private void tryRenewToken(HttpServletRequest request, HttpServletResponse response, String jwt, String username) {
        if (!request.getRequestURI().startsWith("/api")) return;
        var tokenAlreadyRenewed = response.getHeader("Authorization") != null;
        if (tokenAlreadyRenewed) return;
        var remainingTime = jwtUtils.extractExpirationDate(jwt).getTime() - new Date().getTime();
        if (remainingTime > 0 && remainingTime < jwtUtils.expirationTime / 2) {
            var newToken = jwtUtils.generateToken(username);
            response.setHeader("Authorization", newToken);
        }
    }

    /**
     * Defines the endpoints that should not be filtered because they are allowed without authentication
     * @param request the request of the user
     * @return boolean true if request should not be filtered by the filter, and false if not
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/android/login")
            || request.getServletPath().startsWith("/api/login")
            || request.getServletPath().startsWith("/api/register")
            || request.getServletPath().startsWith("/swagger-ui/index.html")
            || !request.getRequestURI().startsWith("/api");
    }
}
