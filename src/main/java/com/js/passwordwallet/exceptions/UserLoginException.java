package com.js.passwordwallet.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserLoginException extends RuntimeException{

    public UserLoginException(String message) {
        super(message);
    }

}
