package com.springjwt.sprignbootjwt.Service;

import com.springjwt.sprignbootjwt.Repo.UserRepo;
import com.springjwt.sprignbootjwt.model.Role;
import com.springjwt.sprignbootjwt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserServiceImpl(JWTService jwtService, AuthenticationManager authenticationManager, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
    }

    @Override
    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setLastActiveAt(LocalDateTime.now());
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.USER));
        }
        return userRepo.save(user);
    }

    @Override
    public String verify(Users user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            Users dbUser = userRepo.findByUsername(user.getUsername());
            dbUser.setLastActiveAt(LocalDateTime.now());
            userRepo.save(dbUser);
            return jwtService.generateToken(user.getUsername());
        }
        throw new RuntimeException("Invalid credentials");
    }

}
