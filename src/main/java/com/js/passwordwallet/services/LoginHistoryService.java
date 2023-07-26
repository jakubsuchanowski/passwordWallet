package com.js.passwordwallet.services;




import com.js.passwordwallet.entieties.LoginHistory;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.repository.LoginHistoryRepo;
import com.js.passwordwallet.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    @Autowired
    private LoginHistoryRepo loginHistoryRepo;
    @Autowired
    private UserRepo userRepo;

    private final FailedLoginService failedLoginService;

    public void storeLoginHistory(String login, Boolean loginResult) throws Exception {
        Optional<User> userFromDb = userRepo.findByLogin(login);
        String iPAddress = Inet4Address.getLocalHost().getHostAddress();
        LocalDateTime localDateTime = LocalDateTime.now();

        LoginHistory loginHistory = new LoginHistory(null, userFromDb.get(),localDateTime,loginResult, iPAddress);
        loginHistoryRepo.save(loginHistory);
        if(loginResult==false) {
            failedLoginService.storeFailedLogin(loginHistory);
        }
        else if(loginResult==true) {
            failedLoginService.correctLogin(loginHistory);
        }
    }

    public List<LoginHistory> showLoginHistory(String userLogin){
        Optional<User> userFromDb = userRepo.findByLogin(userLogin);
        return loginHistoryRepo.findAllByUserId(userFromDb.get().getId());
    }
}
