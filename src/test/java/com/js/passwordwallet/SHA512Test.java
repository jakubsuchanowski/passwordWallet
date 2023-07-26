package com.js.passwordwallet;

import com.js.passwordwallet.encryptionAlghoritms.SHA512;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class SHA512Test {
    private SHA512 sha512;

    @Before
    public void init(){
        sha512 = new SHA512();
    }
    @Test
    @Parameters({"testCalculateSHA512,fccdd5df010b0fb541ee280301aba965a840b35e5bdb43638d7f35b8d575d35afe21284039a486634e80ef4440c71de676426fef8d21cf7a1f08fb65b2b5f8b6",
            "drugiTestCalculateSHA512qwerty,1632c50d5e5988dec977f5aafb0c155e196475b3ad3e901516fbd9dc37604c4797b283de9219abed8238744e4c5a8a5cec3c8c929d811fb91f5a6dae40194670"})
    public void calculateSha512Test_expectedHashString_whenValueProvider(String text, String expResult){
        String result = sha512.calculateSHA512(text);
        assertEquals(result,expResult);
    }


}
