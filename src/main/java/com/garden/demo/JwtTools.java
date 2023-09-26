package com.garden.demo;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Configuration
public class JwtTools {

    private static String SECRET;

    public JwtTools(@Value(value = "{jwt.secret}") String secret) {
        JwtTools.SECRET = secret;
    }

    public static String createToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
        System.out.println(user.getAuthorities().iterator().next().getAuthority());

        String jwt = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", user.getAuthorities().iterator().next().getAuthority())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10800000))
                .sign(algorithm);

        System.out.println(jwt);

        return jwt;

    };

    public static UsernamePasswordAuthenticationToken validateJwt(String accessToken) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

        JWTVerifier verifier = JWT.require(algorithm).build();

        UsernamePasswordAuthenticationToken authToken = null;

        try {
            DecodedJWT decodedJWT = verifier.verify(accessToken);
            String username = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();
            SimpleGrantedAuthority auth = new SimpleGrantedAuthority(role);
            authToken = new UsernamePasswordAuthenticationToken(username, null, List.of(auth));

        } catch (Exception e) {

        }
        return authToken;
    }
}
