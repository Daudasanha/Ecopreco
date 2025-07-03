package com.ecopreco.service;

import com.ecopreco.dto.RegisterRequest;
import com.ecopreco.exception.BadRequestException;
import com.ecopreco.exception.ResourceNotFoundException;
import com.ecopreco.model.Role;
import com.ecopreco.model.User;
import com.ecopreco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username já está em uso");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email já está em uso");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEnabled(true);
        user.setRoles(Set.of(Role.ROLE_USER));

        return userRepository.save(user);
    }

    public User createAdminUser(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username já está em uso");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email já está em uso");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEnabled(true);
        user.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com email: " + email));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, String fullName, String email) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email já está em uso");
        }
        
        user.setFullName(fullName);
        user.setEmail(email);
        
        return userRepository.save(user);
    }

    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = getUserById(id);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Senha atual incorreta");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String generatePasswordResetToken(String email) {
        User user = getUserByEmail(email);
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);
        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException("Token de redefinição de senha inválido"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public void enableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void disableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

