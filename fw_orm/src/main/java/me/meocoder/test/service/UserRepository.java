package me.meocoder.test.service;

import me.meocoder.anotation.Repository;
import me.meocoder.test.Users;

import java.util.List;

@Repository
public interface UserRepository {
    Users findById(Long id);
    void save(Users u);
    List<Users> findAll();
}
