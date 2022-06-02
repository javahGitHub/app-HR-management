package com.pdp.apphrmanagement.service;

import com.pdp.apphrmanagement.entity.SalaryReport;
import com.pdp.apphrmanagement.entity.Task;
import com.pdp.apphrmanagement.entity.TourniquetHistory;
import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.payload.ApiResponse;
import com.pdp.apphrmanagement.payload.ChangeEmail;
import com.pdp.apphrmanagement.payload.InfoDto;
import com.pdp.apphrmanagement.payload.SalaryEditDto;
import com.pdp.apphrmanagement.repository.SalaryReportRepo;
import com.pdp.apphrmanagement.repository.TaskRepo;
import com.pdp.apphrmanagement.repository.TourniquetHistoryRepo;
import com.pdp.apphrmanagement.repository.UserRepo;
import com.pdp.apphrmanagement.utils.RestConstants;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmployeeService {


    @Autowired
    UserRepo userRepo;
    @Autowired
    SalaryReportRepo salaryRepo;
    @Autowired
    TourniquetHistoryRepo tourniquetHistoryRepo;
    @Autowired
    TaskRepo taskRepo;


    public ResponseEntity<?> info(String email,String from,String to) {
        Optional<User> optionalEmployee = userRepo.findByEmail(email);
        if (!optionalEmployee.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Employee not found", false));
        try {
            User employee = optionalEmployee.get();
            Timestamp fromDate =  Timestamp.valueOf(Date.valueOf(from) + " 00:00:00");
            Timestamp toDate = Timestamp.valueOf(Date.valueOf(to) + " 00:00:00");

            InfoDto infoDto = new InfoDto();
            infoDto.setEmail(employee.getEmail());
            infoDto.setFirstName(employee.getFirstName());
            infoDto.setLastName(employee.getLastName());

            List<TourniquetHistory> histories = tourniquetHistoryRepo.findAllByExitedAtBetween(fromDate, toDate);
            infoDto.setHistories(histories);

            histories.addAll(tourniquetHistoryRepo.findAllByEnteredAtBetween(fromDate, toDate));

            List<Task> tasks = taskRepo.findAllByCompletedAtBetweenAndEmployee_Email(fromDate, toDate, email);
            infoDto.setTasks(tasks);

            return ResponseEntity.status(200).body(new ApiResponse("OK", true, infoDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Date parse exception", false));
        }
    }


    public ResponseEntity<?> getEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return (ResponseEntity<?>) authentication.getDetails();
        }
        return null;
    }

    public ResponseEntity<?> getEmployeeByEmail(String email) {
        Optional<User> byEmail = userRepo.findByEmail(email);
        return byEmail.map(user -> ResponseEntity.status(200).body(new ApiResponse(user.toString(), true))).orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse("Employee not found :" + email, false)));
    }



    public ResponseEntity<?> editSalary(SalaryEditDto salaryEditDto){

        if(salaryEditDto.getSalary()<=0)
            return ResponseEntity.status(403).body(new ApiResponse("Salary can't be under 0 amount:"+salaryEditDto.getSalary(), false));

        Optional<User> optionalUser = userRepo.findByEmail(salaryEditDto.getEmail());
        if(!optionalUser.isPresent())
            return ResponseEntity.status(404).body(new ApiResponse("Email not found:"+salaryEditDto.getEmail(), false));
        Double lastSalary = optionalUser.get().getSalary();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityRole = authority.getAuthority();
            if(authorityRole.equals("ROLE_ADMIN") || authorityRole.equals("ROLE_MANAGER") && optionalUser.get().getRoles().contains("ROLE_DIRECTOR"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You can't edit Director salary",false));

            User editingUser=optionalUser.get();
            editingUser.setSalary(salaryEditDto.getSalary());

            //For save changes as SalaryReport class
            SalaryReport salaryReport=new SalaryReport();
            salaryReport.setLastSalary(lastSalary);
            salaryReport.setNewSalary(salaryEditDto.getSalary());
            salaryReport.setDistinction(lastSalary-salaryEditDto.getSalary());
            salaryReport.setChangedWhose(optionalUser.get().getId());
            User user= (User) authentication.getDetails();
            salaryReport.setChangedBy(user.getId());

        }

        return ResponseEntity.status(200).body(new ApiResponse("Salary changed "+lastSalary+" --> "+salaryEditDto.getSalary() ,true));

    }


    public ResponseEntity<?> fireEmployee(String email) {

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(!optionalUser.isPresent())
            return ResponseEntity.status(404).body(new ApiResponse("Email not found:"+email, false));


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if(authority.getAuthority().equals(RoleEnum.ROLE_MANAGER) && optionalUser.get().getRoles().equals(RoleEnum.ROLE_DIRECTOR));
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("You can't edit Director salary",false));
        }
        User user = optionalUser.get();
        user.setEnabled(false);
        return ResponseEntity.status(200).body(new ApiResponse(email+" fired.\nIf you want recover employee" ,true));

    }

    public ResponseEntity<?> recoverEmployee(String email) {

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(!optionalUser.isPresent())
            return ResponseEntity.status(404).body(new ApiResponse("Email not found:"+email, false));

        User user = optionalUser.get();
        user.setEnabled(true);
        return ResponseEntity.status(200).body(new ApiResponse(email+" recovered.\nIf you want recover employee" ,true));
    }


    public ResponseEntity<?> editEmail(ChangeEmail changeEmail){
        Optional<User> optionalUser = userRepo.findByEmail(changeEmail.getLastEmail());
        if(!optionalUser.isPresent())
            return ResponseEntity.status(404).body(new ApiResponse("Email not found:"+changeEmail.getLastEmail(), false));
        User user=optionalUser.get();
        user.setEmail(changeEmail.getNewEmail());
        userRepo.save(user);
        return ResponseEntity.status(200).body(new ApiResponse("Email changed to "+changeEmail.getNewEmail() ,true));
    }


}

