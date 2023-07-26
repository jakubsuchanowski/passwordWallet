package com.js.passwordwallet.services;


import com.js.passwordwallet.encryptionAlghoritms.AESenc;
import com.js.passwordwallet.encryptionAlghoritms.SHA512;
import com.js.passwordwallet.entieties.FailedLogin;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.exceptions.ExceptionMessages;
import com.js.passwordwallet.exceptions.ResetPasswordException;
import com.js.passwordwallet.holders.PepperHolder;
import com.js.passwordwallet.repository.FailedLoginRepo;
import com.js.passwordwallet.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Optional;

import static com.js.passwordwallet.encryptionAlghoritms.HMAC.calculateHMAC;

@Service
@RequiredArgsConstructor
public class ResetMasterPasswordService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FailedLoginRepo failedLoginRepo;

    @Autowired
    private SHA512 sha512;
    @Autowired
    private AESenc aeSenc;
    String pepper = PepperHolder.getPepper();

    public void resetPassword(User user) throws Exception{
        Optional<User> userFromDb = userRepo.findByLogin(user.getLogin());
        if(userFromDb.get().getLogin().equals(user.getLogin())){
            if(userFromDb.get().getAuxiliaryQuestion().equals(user.getAuxiliaryQuestion())){
                if(userFromDb.get().getAnswer().equals(user.getAnswer())){
                    if(userFromDb.get().getIsPasswordKeptAsHash()){
                        String newSalt = sha512.generateSalt();
                        String sha = SHA512.calculateSHA512(newSalt + user.getPasswordHash());
                        Key key = aeSenc.generateKey(pepper);
                        String newUserPasswordWithSha = aeSenc.encrypt(sha,key);
                        userFromDb.get().setPasswordHash(newUserPasswordWithSha);
                        userFromDb.get().setSalt(newSalt);
                        unlockAccount(userFromDb.get());
                        userRepo.save(userFromDb.get());
                    }

                    if(!userFromDb.get().getIsPasswordKeptAsHash()) {
                        String newSalt = sha512.generateSalt();
                        String sha = SHA512.calculateSHA512(newSalt + user.getPasswordHash());
                        String newUserPasswordWithHMAC = calculateHMAC(sha,pepper);
                        userFromDb.get().setPasswordHash(newUserPasswordWithHMAC);
                        userFromDb.get().setSalt(newSalt);
                        unlockAccount(userFromDb.get());
                        userRepo.save(userFromDb.get());
                    }
                }else throw new ResetPasswordException(ExceptionMessages.WRONG_ANSWER.getCode());
            }else throw new ResetPasswordException(ExceptionMessages.WRONG_QUESTION.getCode());
        } else throw new ResetPasswordException(ExceptionMessages.WRONG_LOGIN.getCode());
    }

    public void unlockAccount(User user){
        Optional<FailedLogin> failedLoginFromDb =  failedLoginRepo.findByUser(user);
        if(failedLoginFromDb.isPresent()){
            failedLoginFromDb.get().setPernamentBlock(false);
            failedLoginFromDb.get().setFailedLoginNum(0);
        }
    }
}
