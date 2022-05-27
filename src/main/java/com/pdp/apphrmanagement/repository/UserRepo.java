package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String  email);

    boolean existsByPassword(String password);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(String username);


    @Transactional
    @Modifying
    @Query("delete from users u where u.email = ?1")
    int deleteByEmail(String email);

    @Query("select u from users u where u.emailCode = ?1")
    Optional<User> findByEmailCode(String emailCode);
}
