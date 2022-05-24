package ru.kata.spring.boot_security.demo.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> index();

    User show(long id);

    void save(User user);


    void update(long id, User updateUser);

    void delete(long id);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
