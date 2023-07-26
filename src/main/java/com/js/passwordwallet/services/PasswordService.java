package com.js.passwordwallet.services;

import com.js.passwordwallet.encryptionAlghoritms.AESenc;
import com.js.passwordwallet.encryptionAlghoritms.SHA512;
import com.js.passwordwallet.entieties.Password;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.exceptions.ExceptionMessages;
import com.js.passwordwallet.exceptions.PasswordException;
import com.js.passwordwallet.repository.PasswordRepo;
import com.js.passwordwallet.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;
import java.util.Optional;

import static com.js.passwordwallet.encryptionAlghoritms.HMAC.calculateHMAC;

@Service
public class PasswordService {

    @Autowired
    private PasswordRepo passwordRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AESenc aeSenc;
    String pepper = "qwerty123456";


    public void addPassword(String login, Password webPassword) throws Exception {
        Optional<User> userFromDb = userRepo.findByLogin(login);

        Key key = aeSenc.generateKey(userFromDb.get().getPasswordHash());

        Password password = new Password(null, aeSenc.encrypt(webPassword.getPassword(), key), userFromDb.get(), webPassword.getWebAddress(), webPassword.getDescription(), webPassword.getLogin());
        passwordRepo.save(password);

    }


    public List<Password> showPasswords(String userLogin) {
        Optional<User> userFromDb = userRepo.findByLogin(userLogin);

        return passwordRepo.findAllByUserId(userFromDb.get().getId());
    }

    public String encryptPassword(Long passwordId) throws Exception {
        Optional<Password> passwordFromDb = passwordRepo.findById(passwordId);
        if(passwordFromDb.isEmpty()){
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
        else{
            Password password = passwordFromDb.get();
            return password.getPassword();

        }
    }



    public String decryptPassword(Long passwordId, User userPassword) throws Exception{
        Optional<Password> passwordFromDb = passwordRepo.findById(passwordId);

        String decryptedPassword;
        if(!passwordFromDb.get().getUser().getIsPasswordKeptAsHash()){
            String sha = SHA512.calculateSHA512(passwordFromDb.get().getUser().getSalt() + userPassword.getPasswordHash());
            String userPasswordWithHmac = calculateHMAC(sha, pepper);
            Password password = passwordFromDb.get();
            Key key = aeSenc.generateKey(userPasswordWithHmac);
            decryptedPassword =  aeSenc.decrypt(password.getPassword(), key);
            return decryptedPassword;
        }

        if(passwordFromDb.get().getUser().getIsPasswordKeptAsHash()){
            String sha = SHA512.calculateSHA512(passwordFromDb.get().getUser().getSalt() + userPassword.getPasswordHash());
            Key key2 =aeSenc.generateKey(pepper);
            String userPasswordWithSha = aeSenc.encrypt(sha,key2);
            Key key = aeSenc.generateKey(userPasswordWithSha);
            Password password = passwordFromDb.get();

            decryptedPassword = aeSenc.decrypt(password.getPassword(), key);
            return decryptedPassword;
        }
        else {
            throw new PasswordException(ExceptionMessages.PASSWORD_DOES_NOT_EXIST.getCode());
        }
    }
    public void changeUserPasswords(String login, String oldUserPassword) throws Exception {
        Optional<User> userFromDb = userRepo.findByLogin(login);
        List<Password> passwordsList = passwordRepo.findAllByUserId(userFromDb.get().getId());
        for(int i=0; i<passwordsList.size();i++) {
            Password password = passwordsList.get(i);
            System.out.println(password.getPassword());
            Key key = aeSenc.generateKey(oldUserPassword);
            String decryptedPassword = aeSenc.decrypt(password.getPassword(),key);
            Key key2 = aeSenc.generateKey(userFromDb.get().getPasswordHash());

            String encryptedPassword = aeSenc.encrypt(decryptedPassword,key2);

            password.setPassword(encryptedPassword);

            passwordRepo.save(password);
        }
    }
}



