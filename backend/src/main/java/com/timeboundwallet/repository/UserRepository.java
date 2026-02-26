package com.timeboundwallet.repository;

import com.timeboundwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByNameIgnoreCaseAndEmailIgnoreCase(String name, String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
