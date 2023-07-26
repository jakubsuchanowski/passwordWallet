package com.js.passwordwallet.services;


import com.js.passwordwallet.encryptionAlghoritms.AESenc;
import com.js.passwordwallet.encryptionAlghoritms.HMAC;
import com.js.passwordwallet.encryptionAlghoritms.SHA512;
import com.js.passwordwallet.entieties.FailedLogin;
import com.js.passwordwallet.entieties.Password;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.exceptions.AccountBlockedException;
import com.js.passwordwallet.exceptions.ExceptionMessages;
import com.js.passwordwallet.exceptions.PermanetlyBlockException;
import com.js.passwordwallet.exceptions.UserLoginException;
import com.js.passwordwallet.holders.PepperHolder;
import com.js.passwordwallet.models.UserDto;
import com.js.passwordwallet.repository.FailedLoginRepo;
import com.js.passwordwallet.repository.PasswordRepo;
import com.js.passwordwallet.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.js.passwordwallet.encryptionAlghoritms.HMAC.calculateHMAC;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AESenc aeSenc;
    @Autowired
    private SHA512 sha512;
    @Autowired
    private HMAC hmac;
    @Autowired
    private PasswordRepo passwordRepo;
    @Autowired
    private FailedLoginRepo failedLoginRepo;

    @Autowired
    private LoginHistoryService loginHistoryService;

    @Autowired
    private PasswordService passwordService;



    String pepper = PepperHolder.getPepper();

    public List<User> showAllUsers(){
        return (List<User>) userRepo.findAll();
    }

    public void registerWithSha512Hash(User userDb) throws Exception {
        User user = sha512.encryptSha512(userDb, null);

        Key key = AESenc.generateKey(pepper);

        String password = user.getPasswordHash();
        user.setPasswordHash(aeSenc.encrypt(password,key));

        userRepo.save(user);
    }

    public void registerWithHmac(User userDb) throws Exception{
        User user = sha512.encryptSha512(userDb, null);

        user.setPasswordHash(calculateHMAC(user.getPasswordHash(),pepper));

        userRepo.save(user);
    }

    public void addUser(User userDb) throws Exception {
        Optional<User> userFromDb = userRepo.findByLogin(userDb.getLogin());
        if(userFromDb.isPresent()){
            throw new Exception(ExceptionMessages.USER_ALREADY_EXIST.getCode());
        }

        if(userDb.getIsPasswordKeptAsHash()){
            registerWithSha512Hash(userDb);
        }
        if(!userDb.getIsPasswordKeptAsHash()){
            registerWithHmac(userDb);
        }
    }
    public void login(User user) throws Exception {
        Optional<User> userFromDb = userRepo.findByLogin(user.getLogin());
        if (Objects.isNull(userFromDb)) {
            throw new Exception(ExceptionMessages.USER_DOES_NOT_EXIST.getCode());
        } else {
            User probablyUser = sha512.encryptSha512(user, userFromDb.get().getSalt());
            if (userFromDb.get().getIsPasswordKeptAsHash()) {
                Key key = aeSenc.generateKey(pepper);
                probablyUser.setPasswordHash(aeSenc.encrypt(probablyUser.getPasswordHash(), key));
                if(checkIfAccountBlockPermanetly(userFromDb.get())==true) {
                    throw new PermanetlyBlockException();
                }
                if(checkIfAccountLocked(userFromDb.get())==true){
                    showHowLongAccountLocked(userFromDb.get());
                }
                else {
                    if (!userFromDb.get().getPasswordHash().equals(probablyUser.getPasswordHash())) {
                        loginHistoryService.storeLoginHistory(user.getLogin(), false);
                        throw new UserLoginException(ExceptionMessages.WRONG_PASSWORD.getCode());
                    } else {
                        loginHistoryService.storeLoginHistory(user.getLogin(), true);
                    }
                }
            }
            if (!userFromDb.get().getIsPasswordKeptAsHash()) {
                String hmacCodedInComingUserPassword = calculateHMAC(probablyUser.getPasswordHash(), pepper);
                if(checkIfAccountBlockPermanetly(userFromDb.get())==true) {
                    throw new PermanetlyBlockException();
                }
                if (checkIfAccountLocked(userFromDb.get()) == true) {
                    showHowLongAccountLocked(userFromDb.get());
                } else {
                    if (!userFromDb.get().getPasswordHash().equals(hmacCodedInComingUserPassword)) {
                        loginHistoryService.storeLoginHistory(user.getLogin(), false);
                        throw new UserLoginException(ExceptionMessages.WRONG_PASSWORD.getCode());
                    } else {
                        loginHistoryService.storeLoginHistory(user.getLogin(), true);
                    }
                }
            }
        }
    }
    public void changePassword(String login, Password newPassword) throws Exception{
        Optional<User> userFromDb = userRepo.findByLogin(login);
        String oldUserPassword = userFromDb.get().getPasswordHash();
        if(userFromDb.get().getIsPasswordKeptAsHash()){
            String newSalt = sha512.generateSalt();
            String sha = SHA512.calculateSHA512(newSalt + newPassword.getPassword());
            Key key = aeSenc.generateKey(pepper);
            String newUserPasswordWithSha = aeSenc.encrypt(sha,key);
            userFromDb.get().setPasswordHash(newUserPasswordWithSha);
            userFromDb.get().setSalt(newSalt);
            userRepo.save(userFromDb.get());
        }

        if(!userFromDb.get().getIsPasswordKeptAsHash()) {
            String newSalt = sha512.generateSalt();
            String sha = SHA512.calculateSHA512(newSalt + newPassword.getPassword());
            String newUserPasswordWithHMAC = calculateHMAC(sha,pepper);
            userFromDb.get().setPasswordHash(newUserPasswordWithHMAC);
            userFromDb.get().setSalt(newSalt);
            userRepo.save(userFromDb.get());
        }
        passwordService.changeUserPasswords(login,oldUserPassword);

    }



    public boolean wrongPassword(Optional<User> userFromDb, User user){
        return !userFromDb.get().getPasswordHash().equals(user.getPasswordHash());
    }

    public boolean checkIfAccountBlockPermanetly(User user){
        Optional<FailedLogin> failedLoginFromDb = failedLoginRepo.findByUser(user);
        if(failedLoginFromDb.isPresent()) {
            if (failedLoginFromDb.get().isPernamentBlock()) {
                return true;
            }
        }
        return false;
    }
    public boolean checkIfAccountLocked(User user) {
        Optional<FailedLogin> failedLoginFromDb = failedLoginRepo.findByUser(user);
        if(!failedLoginFromDb.isPresent()){
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBlockTime = failedLoginFromDb.get().getBlockTime();
        if (now.isBefore(lastBlockTime)) {
            return true;
        }
        else return false;
    }

    public void showHowLongAccountLocked(User user){
        Optional<FailedLogin> failedLoginFromDb = failedLoginRepo.findByUser(user);
        throw new AccountBlockedException(failedLoginFromDb.get().getBlockTime());
    }

}
