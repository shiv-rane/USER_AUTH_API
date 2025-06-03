package com.example.auth.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtils(){
        try {
            this.privateKey = RsaKeyUtil.getPrivateKey();
            this.publicKey = RsaKeyUtil.getPublicKey();

            if (privateKey == null || publicKey == null) {
                throw new IllegalStateException("Failed to load RSA keys");
            }

        }catch (Exception e){
            throw new RuntimeException("Failed to initialize JwtUtils",e);
        }

    }

    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * 60)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException | SecurityException e){
            return false;
        }
    }

    public String extractEmail(String token){
        return Jwts.parserBuilder().
                setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
