package com.example.integrationTest.controllers;

import com.example.integrationTest.models.LoginForm;
import com.example.integrationTest.services.JwtService;
import com.example.integrationTest.services.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyUserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserService myUserService;

    @GetMapping("/users")
    public String getLogin() {
        return "Witam nieautoryzowanego użytkownika";
    }

    @GetMapping("/user")
    public String getUser() {
        return "Witamy zautoryzowanego użytkownika";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Witamy zautoryzowanego admina";
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(myUserService.loadUserByUsername(loginForm.getUsername()));
        } else {
            throw new UsernameNotFoundException("Błąd przy autoryzacji");
        }
    }
}
