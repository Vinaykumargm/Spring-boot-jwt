package com.springjwt.sprignbootjwt.Service;

import com.springjwt.sprignbootjwt.model.Users;
import org.springframework.stereotype.Service;


public interface UserService {
    Users register(Users user);
    String verify(Users user);
}
