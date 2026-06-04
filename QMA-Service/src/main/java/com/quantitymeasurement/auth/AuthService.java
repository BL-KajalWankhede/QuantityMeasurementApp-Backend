package com.quantitymeasurement.auth;

import com.quantitymeasurement.auth.dto.AuthResponse;
import com.quantitymeasurement.auth.dto.LoginRequest;
import com.quantitymeasurement.auth.dto.SignupRequest;
import com.quantitymeasurement.auth.dto.UserProfileResponse;
import com.quantitymeasurement.model.AuthProvider;
import com.quantitymeasurement.model.RefreshTokenEntity;
import com.quantitymeasurement.model.UserEntity;
import com.quantitymeasurement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new AuthFlowException("User already exists with email: " + email);
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPicture(request.getPicture());
        user.setProvider(AuthProvider.LOCAL);
        UserEntity savedUser = userRepository.save(user);
        JwtService.TokenPayload tokenPayload = jwtService.generateToken(savedUser);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());
        return new AuthResponse(tokenPayload.token(), refreshToken.getToken(), tokenPayload.issuedAt(), tokenPayload.expiresAt(), refreshToken.getExpiryDate(),
                UserProfileResponse.fromUser(savedUser));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("Invalid email or password"));

        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new AuthFlowException("This account uses Google login. Continue with Google OAuth.");
        }
        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthFlowException("Invalid email or password");
        }

        JwtService.TokenPayload tokenPayload = jwtService.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(tokenPayload.token(), refreshToken.getToken(), tokenPayload.issuedAt(), tokenPayload.expiresAt(), refreshToken.getExpiryDate(),
                UserProfileResponse.fromUser(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByEmail(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("User not found"));
        user.getHistory().size();
        return UserProfileResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("User not found"));
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(refreshTokenStr);
        refreshTokenEntity = refreshTokenService.verifyExpiration(refreshTokenEntity);
        UserEntity user = refreshTokenEntity.getUser();
        
        JwtService.TokenPayload accessPayload = jwtService.generateToken(user);
        
        return new AuthResponse(accessPayload.token(), refreshTokenEntity.getToken(), accessPayload.issuedAt(), accessPayload.expiresAt(), refreshTokenEntity.getExpiryDate(), UserProfileResponse.fromUser(user));
    }

    @Transactional
    public void logout(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("User not found"));
        refreshTokenService.deleteTokensByUser(user);
    }
}