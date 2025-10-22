package me.meocoder;

import me.meocoder.processor.SimpleApplicationContext;
import me.meocoder.test.UserService;

public class MainTest {
    public static void main(String[] args) {
        SimpleApplicationContext context = new SimpleApplicationContext("me.meocoder");

        UserService userService = context.getBean(UserService.class);
        userService.printUser(123L);
        System.out.printf("Done");
    }
}
