package com.example.ictassetstracking.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ictassetstracking.entity.UserAccount;
import com.example.ictassetstracking.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || !username.matches("\\d+")) {
            throw new UsernameNotFoundException("Login must use numeric check number");
        }

        Long checkNumber = Long.valueOf(username);
        UserAccount user = userRepository.findByCheckNumber(checkNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with check number: " + username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new UsernameNotFoundException("User has no password set: " + checkNumber);
        }

        return new User(String.valueOf(checkNumber), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }
}
