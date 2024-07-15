package org.example.velog.config;

import org.example.velog.entity.Role;
import org.example.velog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.findByRoleName("USER").isPresent()) {
            Role userRole = new Role();
            userRole.setRoleName("USER");
            roleRepository.save(userRole);
        }

        if (!roleRepository.findByRoleName("ADMIN").isPresent()) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            roleRepository.save(adminRole);
        }
    }
}
