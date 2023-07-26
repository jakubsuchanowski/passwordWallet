package com.js.passwordwallet.services;

import com.js.passwordwallet.entieties.FailedLogin;
import com.js.passwordwallet.entieties.LoginHistory;
import com.js.passwordwallet.repository.FailedLoginRepo;
import com.js.passwordwallet.repository.LoginHistoryRepo;
import com.js.passwordwallet.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class FailedLoginService {

    @Autowired
    private FailedLoginRepo failedLoginRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private LoginHistoryRepo loginHistoryRepo;

    public void storeFailedLogin(LoginHistory loginHistory){
        Optional<FailedLogin> failedLoginFromDb = failedLoginRepo.findByUser(loginHistory.getUser());
        if (failedLoginFromDb.isPresent()) {
            if (failedLoginFromDb.get().getFailedLoginNum() == 1)
            {
                failedLoginFromDb.get().setBlockTime(LocalDateTime.now().plusMinutes(1));
            }
            if(failedLoginFromDb.get().getFailedLoginNum()==2){
                failedLoginFromDb.get().setBlockTime(LocalDateTime.now().plusMinutes(1));
            }
            if(failedLoginFromDb.get().getFailedLoginNum()==3){
                failedLoginFromDb.get().setPernamentBlock(true);
            }
            failedLoginFromDb.get().setFailedLoginNum(failedLoginFromDb.get().getFailedLoginNum() + 1);
            failedLoginFromDb.get().setLastFailedLogin(loginHistory.getLastLoginDate());
            failedLoginRepo.save(failedLoginFromDb.get());
        } else {
            FailedLogin failedLogin = new FailedLogin(null, loginHistory.getUser(), 1, loginHistory.getLastLoginDate(), false, loginHistory.getIpAddress(),loginHistory.getLastLoginDate().minusYears(20));
            failedLoginRepo.save(failedLogin);
        }
    }

    public void correctLogin(LoginHistory loginHistory){
        Optional<FailedLogin> failedLoginFromDb = failedLoginRepo.findByUser(loginHistory.getUser());
        if(failedLoginFromDb.isPresent()){
            failedLoginFromDb.get().setBlockTime(LocalDateTime.now().minusYears(20));
            failedLoginFromDb.get().setFailedLoginNum(0);
            failedLoginRepo.save(failedLoginFromDb.get());
        }

    }




}

