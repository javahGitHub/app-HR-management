package com.pdp.apphrmanagement.controller;

import com.pdp.apphrmanagement.payload.ChangeEmail;
import com.pdp.apphrmanagement.payload.SalaryEditDto;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.service.EmployeeService;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.Valid;

@Slf4j
@RestController
@EnableGlobalMethodSecurity( prePostEnabled = true, proxyTargetClass = true)
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    UserRepo userRepo;
    @Autowired
    EmployeeService employeeService;



    @GetMapping("/home")
    public ResponseEntity<?> homePage(){
        return ResponseEntity.status(200).body("\n\nWelcome to my HR management app of big tech company\n\n");
    }

    @GetMapping("/info")
    public ResponseEntity<?> getEmployee(){
        return employeeService.getEmployee();
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_ADMIN','ROLE_MANAGER')")
    @GetMapping("/task/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email,@RequestParam String from,@RequestParam String to){
        return employeeService.info(email,from,to);
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_ADMIN','ROLE_MANAGER')")
    @GetMapping("/info/{email}")
    public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email){
        return employeeService.getEmployeeByEmail(email);
    }


    //todo add ROLE_MANAGER to PreAuthorize
    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_ADMIN','ROLE_MANAGER')")
    @GetMapping("/info/all")
    public ResponseEntity<?> allEmployee(){
        log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
        return ResponseEntity.status(200).body(userRepo.findAllByRolesNot(RoleEnum.ROLE_DIRECTOR));
    }


    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_ADMIN')")
    @GetMapping("/info/all/manager")
    public ResponseEntity<?> allManager(){
        return ResponseEntity.status(200).body(userRepo.findAllByRoles(RoleEnum.ROLE_MANAGER));
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR')")
    @GetMapping("/info/all/director")
    public ResponseEntity<?> allDirector(){
        return ResponseEntity.status(200).body(userRepo.findAllByRoles(RoleEnum.ROLE_DIRECTOR));
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR')")
    @GetMapping("/info/all/admin")
    public ResponseEntity<?> allAdmin(){
        return ResponseEntity.status(200).body(userRepo.findAllByRoles(RoleEnum.ROLE_ADMIN));
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_ADMIN','ROLE_MANAGER')")
    @GetMapping("/info/all/worker")
    public ResponseEntity<?> allWorker(){
        return ResponseEntity.status(200).body(userRepo.findAllByRoles(RoleEnum.ROLE_EMPLOYEE));
    }


    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @PostMapping("/edit/salary")
    public ResponseEntity<?> editSalary(@Valid @RequestBody SalaryEditDto salaryEditDto){
        return employeeService.editSalary(salaryEditDto);
    }

    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @PostMapping("/fire/{email}")
    public ResponseEntity<?> fireEmployee(@PathVariable String email){
           return employeeService.fireEmployee(email);
    }


    @PreAuthorize("hasAnyRole('ROLE_DIRECTOR','ROLE_MANAGER')")
    @PostMapping("/recover/{email}")
    public ResponseEntity<?> recoverEmployee(@PathVariable String email){
        return employeeService.recoverEmployee(email);
    }

    @PostMapping("/edit/email")
    public ResponseEntity<?> editEmail(@RequestBody ChangeEmail emails){
        return employeeService.editEmail(emails);
    }






}
