package com.tripgenie.service;

import com.tripgenie.dto.AuthSucessDto;
import com.tripgenie.model.Role;
import com.tripgenie.model.User;
import com.tripgenie.repository.RoleRepository;
import com.tripgenie.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Map<String, Object> register(User user) {

        // Set user role
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Set.of(userRole));

        // Encrypt user password and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        // Generate token and return authSuccess dto
        savedUser.setPassword(null);
        return Map.of("user", savedUser, "message", "user registered successfully!");
    }

    public AuthSucessDto login(String usernameOrEmail, String password) {
        // Authenticate
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        // Set current auth
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate token
        String token = jwtService.generateToken(authentication);

        // Find user
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setPassword(null);
        }
        return new AuthSucessDto(user, token, "Login successful!");
    }
}
