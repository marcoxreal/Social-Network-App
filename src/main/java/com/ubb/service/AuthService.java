package com.ubb.service;

import com.ubb.domain.User;
import com.ubb.repository.AuthRepositoryDB;
import com.ubb.security.HashingService;

import java.util.Optional;

public class AuthService {

    private final AuthRepositoryDB authRepo;
    private final HashingService hashingService = new HashingService();

    public AuthService(AuthRepositoryDB authRepo) {
        this.authRepo = authRepo;
    }

    public Optional<User> login(String emailOrUsername, String plainPassword) {
        Optional<User> userOpt = authRepo.findByEmailOrUsername(emailOrUsername);

        if (userOpt.isEmpty())
            return Optional.empty();

        User user = userOpt.get();
        if (!hashingService.checkPassword(plainPassword, user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
