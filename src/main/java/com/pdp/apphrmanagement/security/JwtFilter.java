package com.pdp.apphrmanagement.security;



import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.AuthService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@Log
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AuthService authService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain         ) throws ServletException, IOException {
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("jetbrainforlicense10@gmail.com","s(&ingeD!&Ncd!E"));
        if(authentication.isAuthenticated())log.info("AuthenticationManager works well can find manager : "+authentication.getPrincipal());
        String token = request.getHeader("Authorization");

        if(token!=null && token.startsWith("Bearer ")){
            String username = jwtProvider.getUsername(token.substring(7));
            if(username!=null){

                for (User user : userRepo.findAll()) {
                    if(passwordEncoder.matches(username,user.getEmail())){

                        UserDetails userDetails = authService.loadUserByUsername(user.getEmail());
                        log.info("Token is correct");
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, username, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }



            }
        }
        filterChain.doFilter(request,response);
    }

}
