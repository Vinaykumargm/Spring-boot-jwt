package com.springjwt.sprignbootjwt.Controller;

import com.springjwt.sprignbootjwt.Service.UserService;
import com.springjwt.sprignbootjwt.model.Role;
import com.springjwt.sprignbootjwt.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
public class UserController {
    @Autowired
    private UserService service;


    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);

    }
@PostMapping("/login")
public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
    String token = service.verify(user);
    Map<String, String> response = new HashMap<>();
    response.put("token", token);
    return ResponseEntity.ok(response);
}


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin content";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userAccess() {
        return "User content";
    }

    @PostMapping("/register-admin")
    public Users registerAdmin(@RequestBody Users user) {
        user.setRoles(Set.of(Role.ADMIN));
        return service.register(user);
    }

}
