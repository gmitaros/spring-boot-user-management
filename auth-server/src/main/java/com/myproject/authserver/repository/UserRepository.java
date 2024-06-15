package com.myproject.authserver.repository;

import com.myproject.authserver.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActiveUsers(Pageable pageable);

}