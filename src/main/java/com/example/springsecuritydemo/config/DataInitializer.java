package com.example.springsecuritydemo.config;

import com.example.springsecuritydemo.repository.UserRepository;
import com.example.springsecuritydemo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserService userService, UserRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                userService.register("alice", "Alice", "passw0rd");
                userService.register("bob", "Bob", "secret456");
                System.out.println("Seeded users: alice/passw0rd, bob/secret456");
            }
        };
    }
}
