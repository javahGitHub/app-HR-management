package com.pdp.apphrmanagement.security;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.entity.enums.RoleEnum;
import com.pdp.apphrmanagement.repository.RoleRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
private void loadUserData() {
    if (userRepository.count() == 5) {
//        User user= new User();
//        user.setFirstName("Mr.Javohir");
//        user.setLastName("Rajabov");
//        user.setEmail("jr2003mit@gmail.com");
//        user.setPassword("123dr");
//        user.setEnabled(true);
//        user.setRoles(Collections.singleton(roleRepo.findByRoleEnum(RoleEnum.ROLE_DIRECTOR)));
//        userRepository.save(user);
        userRepository.deleteByEmail("$2a$10$zQ2fQjMwjed0hkleuWbQ1Obq20WuIWWgj1dessNy2vg2SVG9LliWm");
    }
    log.info("Count of Users :"+userRepository.count());
}
}
