package com.js.passwordwallet.repository;

import com.js.passwordwallet.entieties.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordRepo extends JpaRepository<Password, Long> {
    List<Password> findAllByUserId(Long id);
    Optional<Password> findById(Long id);
}
