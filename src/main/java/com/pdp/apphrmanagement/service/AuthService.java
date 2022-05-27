package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.entity.enums.RoleEnum;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.LoginDto;
import com.pdp.apphrmanagement.payload.RegisterDto;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.CharacterData;

import javax.swing.text.Document;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Service
public class AuthService implements UserDetailsService {

    private static final int ATTEMPTS_LIMIT = 3;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    JavaMailSender javaMailSender;


    /**
     * Add new Manager to server
     *
     * @param registerDto
     * @return class ApiResponse { String message,boolean success}
     */
    public ResponseEntity<?> addManagerByDirector(RegisterDto registerDto) {


        sendEmail("jr2003mit@gmail.com", "Send method is working", "It works");

//
//        boolean existByEmail = userRepo.existsByEmail(registerDto.getEmail());
//        //Check email to be unique
//        if (existByEmail)
//            return ResponseEntity.status(401).body(new ApiResponse("Email already exist!", false));

//        boolean existsByPassword = userRepo.existsByPassword(registerDto.getPassword());
//        //Check password to be unique
//        if (existsByPassword)
//            return ResponseEntity.status(401).body(new ApiResponse("Password already exist!", false));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("Authentication principal: " + principal.toString());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info(authorities.toString());
        for (GrantedAuthority authority : authorities) {

            if (authority.getAuthority().equals("ROLE_DIRECTOR")) {

                String password = generatePassword();

                //todo generate default password for manager
                User user = new User();
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setEmail(passwordEncoder.encode(registerDto.getEmail()));
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_MANAGER)));
                user.setEmailCode(UUID.randomUUID().toString());


                //Send message to new Manager inbox for verify
                boolean b = sendEmail(registerDto.getEmail(), "Hi " + registerDto.getFirstName() + " it is your new Director " + authentication.getName() +
                        "\nPlease verify your email via link below\nYour username=" + registerDto.getEmail() + " and password=" + password + "\n\nNote:If you want to change password after login to server you can change only your password" +
                        "  http://localhost:8080/api/auth/verifyEmail?email=" + registerDto.getEmail() + "&emailCode=" + user.getEmailCode(), "Confirm your email");
                if (!b)
                    return ResponseEntity.status(401).body(new ApiResponse("Oops something went wrong with sending email", false));


                //Send message to Director inbox for be sure
                //todo : In JwtFilter class get username into credentials of usernamePasswordAuthenticationToken
                sendEmail((String) authentication.getCredentials(), "A verifyEmail message has been sent to your manager " + user.getEmail(), "Verify email in manager inbox");

                userRepo.save(user);
                return ResponseEntity.status(200).body(new ApiResponse("Mr." + authentication.getName() + "  you added manager successfully. Email sent to both your and new manager inbox", true));

            }
        }
        return ResponseEntity.status(401).body(new ApiResponse("You can't add manager without permission", false));
    }




    /**
     * Add new Employee to server
     *
     * @param registerDto
     * @return class ApiResponse { String message,boolean success}
     */
    public ResponseEntity<?> addEmployeeByManager(RegisterDto registerDto) {


        boolean existByEmail = userRepo.existsByEmail(registerDto.getEmail());
        //Check email to be unique
        if (existByEmail)
            return ResponseEntity.status(401).body(new ApiResponse("Email already exist!", false));

//        boolean existsByPassword = userRepo.existsByPassword(registerDto.getPassword());
//        //Check password to be unique
//        if (existsByPassword)
//            return ResponseEntity.status(401).body(new ApiResponse("Password already exist!", false));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_MANAGER") || authority.getAuthority().equals("ROLE_DIRECTOR")) {

                String password=generatePassword();
                log.info("Manager has permission");
                User user = new User();
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setEmail(passwordEncoder.encode(registerDto.getEmail()));
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_MANAGER)));
                user.setEmailCode(UUID.randomUUID().toString());


                //Send message to new Employee inbox for verify
                boolean b = sendEmail(registerDto.getEmail(), "Hi " + user.getFirstName() + " it is your new Manager " + authentication.getName() +
                        "\nPlease verify your email via link below\nYour username=" + registerDto.getEmail() + " and password=" + password + "\n\nNote:If you want to change password after login to server you can change only your password" +
                        "\n\ngo to http://localhost:8080/api/auth/verifyEmail?email=" + registerDto.getEmail() + "&emailCode=" + user.getEmailCode(), "Confirm your email");
                if (!b)
                    return ResponseEntity.status(401).body(new ApiResponse("Oops something went wrong with sending message to email ", false));

                //Send message to Director inbox for be sure
                //todo : In JwtFilter class get username into credentials of usernamePasswordAuthenticationToken
                sendEmail((String) authentication.getCredentials(), "A verifyEmail message has been sent to new manager " + user.getEmail(), "Verify email in manager inbox");

                userRepo.save(user);
                log.info("New employee saved");
                return ResponseEntity.status(200).body(new ApiResponse("Mr." + authentication.getName() + "\nSuccessfully you add Employee. Email has been sent to both your and new employee inbox", true));

            }
        }
        return ResponseEntity.status(401).body(new ApiResponse("You can't add manager without permission", false));
    }


    public ResponseEntity<?> login(LoginDto loginDto) {

        //todo Use passwordEncode.matches()
        try {


        log.info("Before authenticate User");
//            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        log.info("After authenticate User");

        List<User> all = userRepo.findAll();
        for (User user : all) {
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                if (passwordEncoder.matches(loginDto.getUsername(), user.getUsername())) {

                    String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
                    return ResponseEntity.status(201).body(new ApiResponse("Token:", true, token));

                }
            }
        }


        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Username and Password not matched", false));
//            User user = (User) authenticate.getPrincipal();


        } catch (BadCredentialsException badCredentialsException) {
            log.info("In BadCredentialException catch");
            return ResponseEntity.status(401).body(new ApiResponse("Username and password not found!", false));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            return ResponseEntity.status(404).body(new ApiResponse("Your Username  not found", false));
        }
    }


    //->->->->->->->->->->->->->->->->->-> Methods ->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->

    /**
     * Send email to user verify code
     *
     * @param emailCode
     * @param email
     * @return class ApiResponse{String message ,boolean success}
     */
    public ResponseEntity<?> verifyEmail(String email, String emailCode) {
        log.info("email="+email+"  ,  emailCode="+emailCode);
        Optional<User> optionalUser = userRepo.findByEmailCode(emailCode);

        if (optionalUser.isPresent() & passwordEncoder.matches(email,optionalUser.get().getEmail())) {
            User user = optionalUser.get();

            user.setEnabled(true);
            user.setEmailCode(null);
            userRepo.save(user);
            return ResponseEntity.status(401).body(new ApiResponse("Successfully verified", true));
        }
        return ResponseEntity.status(401).body(new ApiResponse("Wrong email or verification code", false));
    }


    /**
     * Send email
     *
     * @param sendingEmail
     * @param text
     * @return boolean
     */
    Boolean sendEmail(String sendingEmail, String text, String subject) {
        try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Mr.Javakhir");
        message.setTo(sendingEmail);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
        return true;
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }
    }


    /**
     * Generate default password for users
     *
     * @return unique password
     */
    String generatePassword() {
        String all = "ASDFGHJKLQWERTYUIOPZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789!@#$%^&()?{}[]|";
        String pwd = RandomStringUtils.random(15, all);
        for (User user : userRepo.findAll())
            if (pwd.equals(user.getPassword())) return generatePassword();
        return pwd;
    }


    /**
     * Override method of UserDetailsService
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //        Optional<User> optionalUser = userRepo.findByUsername(username);
//        if(optionalUser.isPresent())
//            return optionalUser.get();
//        throw new UsernameNotFoundException(username+" not found");
        return userRepo.findByEmail(username).orElseThrow(() -> new RuntimeException(username + " not found"));
    }
}
