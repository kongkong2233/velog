package org.example.velog.config;

import org.example.velog.entity.Role;
import org.example.velog.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    //기본 역할 초기화
    @Bean
    public CommandLineRunner loadData(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByRoleName("USER") == null) {
                Role userRole = new Role();
                userRole.setRoleName("USER");
                roleRepository.save(userRole);
            }

            if (roleRepository.findByRoleName("ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setRoleName("ADMIN");
                roleRepository.save(adminRole);
            }
        };
    }
}
