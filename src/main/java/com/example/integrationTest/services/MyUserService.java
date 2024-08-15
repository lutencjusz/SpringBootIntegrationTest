package com.example.integrationTest.services;


import com.example.integrationTest.models.MyUser;
import com.example.integrationTest.repositories.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> actualUser = myUserRepository.findByUsername(username);
        if (actualUser.isPresent()) {
            var userObject = actualUser.get();
            return User.builder()
                    .username(userObject.getUsername())
                    .password(userObject.getPassword())
                    .roles(getRoles(userObject))
                    .build();
        } else {
            throw new UsernameNotFoundException("Użytkownik nie istnieje");
        }
    }

    private String[] getRoles(MyUser myUser) {
        if (myUser.getRole() == null) {
            return new String[]{"USER"};
        }
        return new String[]{myUser.getRole()};
    }
}
