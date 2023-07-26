package com.js.passwordwallet.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountBlockedException extends RuntimeException{
    public AccountBlockedException(LocalDateTime blockTime){
        super("Konto zablokowane do: " + blockTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
