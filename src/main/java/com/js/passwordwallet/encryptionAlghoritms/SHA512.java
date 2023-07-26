package com.js.passwordwallet.encryptionAlghoritms;



import com.js.passwordwallet.entieties.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SHA512 {
    public User encryptSha512(User userDb, String salt){
        if(salt==null){
            salt = generateSalt();
        }

        String password = calculateSHA512(salt + userDb.getPasswordHash());

        return new User(null, userDb.getLogin(),password,salt,userDb.getIsPasswordKeptAsHash(), userDb.getAuxiliaryQuestion(), userDb.getAnswer());

    }
    public static String calculateSHA512(String text)
    {
        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //calculate message digest of the input string - returns byte array
            byte[] messageDigest = md.digest(text.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // If wrong message digest algorithm was specified
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String generateSalt(){
        return UUID.randomUUID().toString();
    }


}
