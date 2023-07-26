package com.js.passwordwallet.controllers;


import com.js.passwordwallet.entieties.Password;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.models.CryptResponse;
import com.js.passwordwallet.services.LoginHistoryService;
import com.js.passwordwallet.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.lang.System.out;

@RestController
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private LoginHistoryService loginHistoryService;

    @PostMapping("/addPassword")
    public ResponseEntity addPassword(@RequestHeader("login") String login, @RequestBody Password password) {

        try {
            passwordService.addPassword(login, password);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/showPasswords")
    public ResponseEntity showPasswords(@RequestHeader("login") String userLogin) {
        try {
            passwordService.showPasswords(userLogin);
            return ResponseEntity.ok(passwordService.showPasswords(userLogin));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/encrypt")
    public ResponseEntity<CryptResponse> encryptPassword(@RequestParam Long passwordId) {
        try {
            String encryptedPassword = passwordService.encryptPassword(passwordId);
            CryptResponse response = new CryptResponse(encryptedPassword);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<CryptResponse> decryptPassword(@RequestParam Long passwordId, @RequestBody User userPassword) throws Exception {
        try {
            String decryptedPassword = passwordService.decryptPassword(passwordId, userPassword);
            CryptResponse response = new CryptResponse(decryptedPassword);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/showLoginHistory")
    public ResponseEntity showLoginHistory(@RequestHeader("login") String userLogin){
        try {
            loginHistoryService.showLoginHistory(userLogin);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


}
