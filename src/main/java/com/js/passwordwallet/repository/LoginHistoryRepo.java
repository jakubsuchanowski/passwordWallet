package com.js.passwordwallet.repository;

import com.js.passwordwallet.entieties.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepo extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findAllByUserId(Long id);
}
