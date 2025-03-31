package com.cloudnative.bff.config;

import java.io.IOException;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtRequestFilter extends OncePerRequestFilter {

   


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

       String authorizationHeader = request.getHeader("Authorization");

       if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
           String jwtToken = authorizationHeader.substring(7); 

           ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
           attributes.setAttribute("jwtToken", jwtToken, 0); 
       }

       
       filterChain.doFilter(request, response);
    }
}