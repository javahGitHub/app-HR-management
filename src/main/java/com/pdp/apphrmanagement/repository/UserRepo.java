package com.pdp.apphrmanagement.repository;

import com.pdp.apphrmanagement.entity.User;
import com.pdp.apphrmanagement.utils.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String  email);

    boolean existsByPassword(String password);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(String username);

    Optional<User> findByEmailCode(String emailCode);

    List<Object> findAllByRoles(RoleEnum roleEnum);

    Optional<User> findAllByRolesNot(RoleEnum roleEnum);

    void deleteByEmail(String s);
}
