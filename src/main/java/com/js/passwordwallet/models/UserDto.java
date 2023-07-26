package com.js.passwordwallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private String login;
    private String password;
    private Boolean isPasswordKeptAsHash;
}
