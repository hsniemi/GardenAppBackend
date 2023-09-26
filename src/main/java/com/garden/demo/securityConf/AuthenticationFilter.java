package com.garden.demo.securityConf;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garden.demo.JwtTools;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {
        String[] cred = getBasicCredentials(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(cred[0], cred[1]);

        return this.getAuthenticationManager().authenticate(authToken);

    }

    private String[] getBasicCredentials(HttpServletRequest request) throws AuthenticationException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Basic")) {
            throw new BadCredentialsException("Login failed");
        }

        String cred = header.substring("Basic".length() + 1);

        // username:password
        cred = new String(Base64.getDecoder().decode(cred));

        String[] basicCred = cred.split(":");
        return basicCred;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        String jwt = JwtTools.createToken((User) authResult.getPrincipal());
        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("access_token", jwt);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        System.out.println("CREATED A TOKEN");
        new ObjectMapper().writeValue(response.getOutputStream(), jsonBody);
    }
}
