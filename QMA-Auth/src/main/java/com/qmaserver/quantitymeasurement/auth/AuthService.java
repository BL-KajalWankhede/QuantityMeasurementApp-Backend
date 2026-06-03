package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.auth.dto.AuthResponse;
import com.qmaserver.quantitymeasurement.auth.dto.LoginRequest;
import com.qmaserver.quantitymeasurement.auth.dto.SignupRequest;
import com.qmaserver.quantitymeasurement.auth.dto.UpdateUserProfileRequest;
import com.qmaserver.quantitymeasurement.auth.dto.UserProfileResponse;
import com.qmaserver.quantitymeasurement.model.AuthProvider;
import com.qmaserver.quantitymeasurement.model.RefreshTokenEntity;
import com.qmaserver.quantitymeasurement.model.UserEntity;
import com.qmaserver.quantitymeasurement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class AuthService {
    private static final Logger log = LogManager.getLogger(AuthService.class);
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
        log.trace("Starting signup process");
        log.info("Signup request");
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.fatal("Signup failed: Email already exists");
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
        log.trace("Starting login process");
        log.info("Login request");
        String email = request.getEmail().trim().toLowerCase();
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.fatal("Login failed: User not found");
                    return new AuthFlowException("Invalid email or password");
                });

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
        log.info("Fetching session");
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("User not found"));
        return UserProfileResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthFlowException("User not found"));
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request) {
        log.info("Updating user profile");
        UserEntity user = getUserByEmail(email);
        user.setName(request.getName().trim());
        user.setPicture(normalizePicture(request.getPicture()));
        UserEntity updatedUser = userRepository.save(user);
        return UserProfileResponse.fromUser(updatedUser);
    }

    private String normalizePicture(String picture) {
        if (picture == null || picture.isBlank()) {
            return null;
        }
        return picture.trim();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        log.info("Refresh token request");
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(refreshTokenStr);
        refreshTokenEntity = refreshTokenService.verifyExpiration(refreshTokenEntity);
        UserEntity user = refreshTokenEntity.getUser();
        
        JwtService.TokenPayload accessPayload = jwtService.generateToken(user);
        
        return new AuthResponse(accessPayload.token(), refreshTokenEntity.getToken(), accessPayload.issuedAt(), accessPayload.expiresAt(), refreshTokenEntity.getExpiryDate(), UserProfileResponse.fromUser(user));
    }
}
