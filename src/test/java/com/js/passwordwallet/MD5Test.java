package com.js.passwordwallet;


import com.js.passwordwallet.encryptionAlghoritms.AESenc;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MD5Test {

    private AESenc aeSenc;
    private String entryData;
    private byte[] expResult;

    @Before
    public void init(){
        aeSenc = new AESenc();

        entryData = "testMD5qwerty";
        expResult = new byte [] {-13, 22, 52, -19, -42, -125, -78, 121, -72, 108, -67, 22, 70, 75, 122, 114};
    }
    @Test
    public void calculateMD5Test_expectedMD5HashByte_whenValueProvider(){
       byte[] result = aeSenc.calculateMD5(entryData);
        assertArrayEquals(result, expResult);
    }
}
