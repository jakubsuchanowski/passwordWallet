package com.js.passwordwallet;

import com.js.passwordwallet.encryptionAlghoritms.HMAC;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class HmacTest {

    private HMAC hmac;

    @Before
    public void init(){
        hmac = new HMAC();
    }

    @Test
    @Parameters({"testHmac123, klucz12345, D527kWEk5qPCsJHY3aOe4uDEkmgNl0nleXoCkRJ+f/LkCD1OwEf7xl3emcumry/sLx5i0ws1hRVo31JzG4S3bA==","drugiTestHmac768574,qwerty12345,XGJCENWaXA5Mjk7RpRUZnUPJiCMrvXFVyIp67usfXBoj4SA8oc0wP6lTU6DJ4ku+LEFfwp52fZh8eDrecdKVyQ=="})
    public void calculateHmacTest_expectedHmacHashString_whenSaltProvider(String text, String key, String expResult){
        String result = hmac.calculateHMAC(text, key);
        assertEquals(expResult, result);
    }

}
