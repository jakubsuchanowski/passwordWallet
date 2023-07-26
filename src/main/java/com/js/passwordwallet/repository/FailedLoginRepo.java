package com.js.passwordwallet.repository;

import com.js.passwordwallet.entieties.FailedLogin;
import com.js.passwordwallet.entieties.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FailedLoginRepo extends JpaRepository<FailedLogin, Long> {
    Optional<FailedLogin> findByUser(User user);
}
