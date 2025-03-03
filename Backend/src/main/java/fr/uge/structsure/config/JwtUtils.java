package fr.uge.structsure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Class containing all the necessary method that will handle tokens
 */
@Component("jwtUtils")
public class JwtUtils {

    private final String secretKey;

    final long androidExpirationTime;

    final long expirationTime;

    /**
     * Default internal constructor FOR SPRINGBOOT to inject values
     * @param secretKey the secret key to encrypt tokens
     * @param androidExpirationTime the expiration delay for android
     * @param expirationTime the expiration delay for the web app
     */
    JwtUtils(
        @Value("${app.secret-key}") String secretKey,
        @Value("${app.android.expiration-time.days}") long androidExpirationTime,
        @Value("${app.expiration-time}") long expirationTime
    ) {
        this.secretKey = secretKey;
        this.androidExpirationTime = androidExpirationTime;
        this.expirationTime = expirationTime;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, expirationTime);
    }

    /**
     * Will generate token for android users
     * @param username the username
     * @return the generated token
     */
    public String generateAndroidToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, Duration.ofDays(androidExpirationTime).toMillis());
    }

    /**
     * Will generate token
     * @param claims the claims
     * @param username the username
     * @param duration the duration in milliseconds
     * @return String the generated token
     */
    private String createToken(Map<String, Object> claims, String username, long duration) {
        var currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + duration))
                .signWith(getSignkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignkey() {
        byte[] keyBytes = secretKey.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims,  T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignkey())
                .parseClaimsJws(token)
                .getBody();
    }
}
