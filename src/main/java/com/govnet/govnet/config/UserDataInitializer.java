package com.govnet.govnet.config;

import com.govnet.govnet.entity.MyUser;
import com.govnet.govnet.enums.Role;
import com.govnet.govnet.repo.MyUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDataInitializer {

    private final MyUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(MyUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        createDefaultUser("user", "123", Role.ROLE_USER);
        createDefaultUser("admin", "123", Role.ROLE_ADMIN);
    }

    private void createDefaultUser(String username, String rawPassword, Role role) {
        Optional<MyUser> existing = userRepository.findByUsername(username);
        if (existing.isEmpty()) {
            MyUser user = new MyUser();
            user.setEmail(username + "@example.com");
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setIsActive(true);
//            user.setIsEmailVerified(true);
            userRepository.save(user);
            System.out.println("✅ Default " + role.name() + " user created: " + username);
        }
    }
}
