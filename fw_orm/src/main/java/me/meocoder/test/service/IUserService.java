package me.meocoder.test.service;

import me.meocoder.test.Users;

import java.util.List;

public interface IUserService {
    void createUser(String name);
    Users getUser(Long id);
    List<Users> listAll();
}
