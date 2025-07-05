package com.springjwt.sprignbootjwt.Service;

import com.springjwt.sprignbootjwt.Repo.UserRepo;
import com.springjwt.sprignbootjwt.model.UserPrincipal;
import com.springjwt.sprignbootjwt.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService  implements UserDetailsService {
private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user1 = userRepo.findByUsername(username);
        if (user1 == null) throw new UsernameNotFoundException("User not found");
        return new UserPrincipal(user1);
    }

}
