package com.js.passwordwallet;

import com.js.passwordwallet.encryptionAlghoritms.HMAC;
import com.js.passwordwallet.encryptionAlghoritms.SHA512;
import com.js.passwordwallet.entieties.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class registerUserWithHMacTest {


        private SHA512 sha512;
        private HMAC hmac;

        private String salt;
        private String pepper;
        private User entryData;
        private User expResult;

        @Before
        public void init() {
            sha512 = new SHA512();
            hmac = new HMAC();
            salt = "QWERT12345!@#";
            pepper= "qwerty123456";
            entryData = new User("test","test");

            expResult = new User(0L,"test","n5yDOBXx51qq0xZW7GSGMGaow9GQaBpCA4eKbOoLUrut6ZzWKjN62x3jyM45ctoB2k4kcln4dUpUS0b8Sgf0gg==",salt,true,null,null);
        }

        @Test
        public void registerUserWithHMACTest_expectedUserHashData_whenDataSaltAndPaperProvider(){

            User newUser = sha512.encryptSha512(entryData, salt);
            newUser.setPasswordHash(hmac.calculateHMAC(newUser.getPasswordHash(),pepper));

            assertEquals(expResult.getLogin(),newUser.getLogin());
            assertEquals(expResult.getPasswordHash(),newUser.getPasswordHash());

        }
}
