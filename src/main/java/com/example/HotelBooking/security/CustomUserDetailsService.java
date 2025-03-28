package com.example.HotelBooking.security;

import com.example.HotelBooking.entities.User;
import com.example.HotelBooking.exceptions.NotFoundException;
import com.example.HotelBooking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username).
                orElseThrow(() -> new NotFoundException("User Email Not Found"));

        return AuthUser.builder()
                .user(user)
                .build();
    }
}



//Step-by-Step Flow
//1 User tries to log in with an email and password.
//2 Spring Security calls CustomUserDetailsService.loadUserByUsername(email).
//3 This method queries the database using UserRepository.findByEmail(email).
//4 If the user exists, it wraps the User object inside AuthUser and returns it.
//5 Spring Security then uses AuthUser to verify authentication.