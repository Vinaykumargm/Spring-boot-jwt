package com.springjwt.sprignbootjwt.Repo;

import com.springjwt.sprignbootjwt.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
}
