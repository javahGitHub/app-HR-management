package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.entity.enums.RoleEnum;
import com.pdp.apphrmanagement.payload.LoginDto;
import com.pdp.apphrmanagement.payload.RegisterDto;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.AuthService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;
import java.util.UUID;

@Log
@RestController
@EnableGlobalMethodSecurity( prePostEnabled = true)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    UserRepo userRepo;
    @Autowired




    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping("/manager/add")
    public ResponseEntity<?> addManager(@RequestBody RegisterDto registerDto){
      return authService.addManagerByDirector(registerDto);
    }


    @PostMapping("/employee/add")
    public ResponseEntity<?> addEmployee(@RequestBody RegisterDto registerDto){

        return authService.addEmployeeByManager(registerDto);
    }







    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        return authService.login(loginDto);
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<?> enableUser(@RequestParam String email,@RequestParam(required = false) String emailCode){
        return  authService.verifyEmail(email, emailCode);
    }





    @GetMapping("/home")
    public ResponseEntity<?> homePage(){
        return ResponseEntity.status(200).body("\n\nWelcome to my HR management app of big tech company\n\n");
    }



    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER','ROLE_ADMIN')")
    @GetMapping("/infoAll")
    public ResponseEntity<?> all(){
        log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
        return ResponseEntity.status(200).body(userRepo.findAll());

    }






}
