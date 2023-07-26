package com.js.passwordwallet.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionMessages {

    USER_DOES_NOT_EXIST("User does not exists!"),
    USER_ALREADY_EXIST("User already exist!"),
    WRONG_PASSWORD("Wrong password!"),
    PASSWORD_DOES_NOT_EXIST("Password does not exists!"),
    WRONG_LOGIN("Błędny login!"),
    WRONG_QUESTION("Błędne pytanie!"),
    WRONG_ANSWER("Błędna odpowiedź!");


    @Getter
    public final String code;

}
