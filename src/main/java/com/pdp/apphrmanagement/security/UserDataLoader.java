package com.pdp.apphrmanagement.security;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.entity.enums.RoleEnum;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.HashSet;

@Log
@Component
public class UserDataLoader implements CommandLineRunner {

    @Autowired
    UserRepo userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    JavaMailSender javaMailSender;
    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
private void loadUserData() {

        String link = "http://localhost:8008/api/hire/verify?emailCode=" + "emailCode" + "&email=" + "sendingEmail";
        String body = "<form action=" + link + " method=\"post\">\n" +
            "<label>Create password for your cabinet</label>" +
            "<br/><input type=\"text\" name=\"password\" placeholder=\"password\">\n" +
            "<br/>  <button>Submit</button>\n" +
            "</form>";
    try {
        String from = "balance.060902@gmail.com";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setSubject("Confirm email");
        helper.setFrom(from);
        helper.setTo("jr2003mit@gmail.com");
        helper.setText(body, true);
        javaMailSender.send(message);
    } catch (Exception ignored) {
    }
    if (userRepository.count() == 0) {
//        userRepository.deleteByEmail("jr2003mit@gmail.com");
//        User user= new User();
//        user.setFirstName("Mr.Javohir");
//        user.setLastName("Rajabov");
//        user.setEmail("jr2003mit@gmail.com");
//        user.setPassword(passwordEncoder.encode("123dr"));
//        user.setEnabled(true);
//        user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_DIRECTOR)));
//        userRepository.save(user);

    }
    log.info("Count of Users :"+userRepository.count());
}
}
