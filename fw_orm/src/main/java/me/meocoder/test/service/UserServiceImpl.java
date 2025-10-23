package me.meocoder.test.service;

import me.meocoder.anotation.Transactional;
import me.meocoder.processor.EntityManager;
import me.meocoder.test.Users;

import java.util.List;

public class UserServiceImpl implements IUserService  {
    // will be set manually in Main for demo
    private final UserRepository userRepo;
    private final EntityManager em;
    public UserServiceImpl(UserRepository repo, EntityManager em) {
        this.userRepo = repo;
        this.em = em;
    }

    @Transactional
    @Override
    public void createUser(String name) {
        // use repository save (which uses em.persist internally)
        Users u = new Users(name);
        userRepo.save(u);
        System.out.println("[service] created user id=" + u.getId());
    }

    @Override
    public Users getUser(Long id) {
        return userRepo.findById(id);
    }

    @Override
    public List<Users> listAll() {
        return userRepo.findAll();
    }
}
