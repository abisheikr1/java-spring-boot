package com.billing_software.billing_software.utils.jwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.models.authentication.TokenDetails;
import com.billing_software.billing_software.utils.exception.CustomException;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

@Service
public class JwtService {

    static byte[] keyBytes = Decoders.BASE64.decode("ydorNTjg2HRmTkdTwYkUlAARVdeNDNlhLZfD6UOWCtfwPk5V45uhZule2RXc6YQG");
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(keyBytes);

    // Generate JWT Token
    public String generateToken(String username, Map<String, Object> additionalClaims, Date expiryDate) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        claims.put("sub", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Retrieve Claims from JWT Token
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public TokenDetails getTokenDetails(String token) {
        if (token != null && !token.isEmpty() && !token.isBlank()) {
            Claims claims = getClaimsFromToken(token);
            if (!claims.getExpiration().before(new Date())) {
                TokenDetails tokenDetails = new TokenDetails(claims);
                return tokenDetails;
            } else {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "token expired");
            }
        } else {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "token can't be null/empty");
        }
    }

    // Validate JWT Token
    public boolean validateToken(String token, String username) {
        final String tokenUsername = getClaimsFromToken(token).getSubject();
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

}