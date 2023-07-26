package com.js.passwordwallet.controllers;

import com.js.passwordwallet.entieties.Password;
import com.js.passwordwallet.entieties.User;
import com.js.passwordwallet.services.ResetMasterPasswordService;
import com.js.passwordwallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ResetMasterPasswordService resetMasterPasswordService;

    @GetMapping("/users")
    public ResponseEntity getUser() {
        try {
            return ResponseEntity.ok(userService.showAllUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/registration")
    public ResponseEntity<?> addUser(@RequestBody User userDb) {
        try {
            userService.addUser(userDb);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        try {
            userService.login(user);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/changePassword")
    public ResponseEntity changeUserPassword(@RequestHeader("login") String login,
                                             @RequestBody Password newPassword){
        try {
            userService.changePassword(login,newPassword);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping("/resetPassword")
    public ResponseEntity resetMasterPassword(@RequestBody User user){
        try{
            resetMasterPasswordService.resetPassword(user);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();

    }

}
