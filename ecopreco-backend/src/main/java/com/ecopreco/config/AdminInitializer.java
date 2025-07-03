package com.ecopreco.config;

import com.ecopreco.model.Role;
import com.ecopreco.model.User;
import com.ecopreco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Criando usuário administrador padrão...");
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@ecopreco.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFullName("Administrador EcoPreco");
            adminUser.setEnabled(true);
            adminUser.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            userRepository.save(adminUser);
            log.info("Usuário administrador padrão criado com sucesso!");
        }
    }
}


