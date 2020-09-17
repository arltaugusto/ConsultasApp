package com.project.consultas.security;

import com.project.consultas.entities.Student;
import com.project.consultas.entities.User;
import com.project.consultas.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String legajo) throws UsernameNotFoundException {
        List<User> allUsers = userRepository.findAll();
        Optional<User> user = userRepository.findByLegajo(legajo);
        user.orElseThrow(() -> new UsernameNotFoundException("not found " + legajo));
        return user.map(MyUserDetails::new).get();
    }

}
