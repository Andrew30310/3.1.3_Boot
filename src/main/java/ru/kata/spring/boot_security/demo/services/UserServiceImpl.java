package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void updateUser(long oldUsersId, User newUser) {
        User user = userRepository.getById(oldUsersId);
        if (user != null) {
            user.setUsername(newUser.getUsername());
            user.setFirstName(newUser.getFirstName());
            user.setAge(newUser.getAge());
            user.setPassword(newUser.getPassword());
            user.setLastName(newUser.getLastName());
            if (newUser.getRoles() != null){
                user.setRoles(newUser.getRoles());
            }
            userRepository.saveAndFlush(user);
            return;
        }

        userRepository.saveAndFlush(newUser);
    }

    @Override
    public User getUser(long id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getUsersList() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void addUser(User user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }


}
