package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.payload.LoginDto;
import com.pdp.apphrmanagement.payload.RegisterDto;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.AuthService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Log
@RestController
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

    @PostMapping("/addManager")
    public ResponseEntity<?> addManager(@RequestBody RegisterDto registerDto){
      return authService.addManagerByDirector(registerDto);
    }


    @PostMapping("/addEmployee")
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

    @PostMapping("/test")
    public ResponseEntity<?> testPage(@RequestBody LoginDto loginDto){

       log.info("start finding via UsernamePasswordAuthenticationTon=ken");
       Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
               loginDto.getPassword()));
       log.info("end finding via UsernamePasswordAuthenticationTon=ken");
        boolean authenticated = authenticate.isAuthenticated();
        if(!authenticated)
        return ResponseEntity.status(200).body("User not found");
        User user = (User) authenticate.getPrincipal();
        return ResponseEntity.status(200).body(user);

    }
    @GetMapping("/all")
    public ResponseEntity<?> all(){

        return ResponseEntity.status(200).body(userRepo.findAll().get(0));

    }
}
