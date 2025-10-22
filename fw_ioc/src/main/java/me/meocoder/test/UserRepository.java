package me.meocoder.test;

import me.meocoder.anotation.Repository;

@Repository
public class UserRepository {
    public String findUserName(Long id) {
        return "User#" + id;
    }
}
