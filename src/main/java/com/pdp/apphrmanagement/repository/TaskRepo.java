package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task,Integer> {
    List<Task> findAllByCompletedAtBetweenAndEmployee_Email(Timestamp fromDate, Timestamp toDate, String email);
}
