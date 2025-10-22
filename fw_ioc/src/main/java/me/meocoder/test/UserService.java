package me.meocoder.test;

import me.meocoder.anotation.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

//    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void printUser(Long id) {
        System.out.println("UserService: " + userRepository.findUserName(id));
    }
}
