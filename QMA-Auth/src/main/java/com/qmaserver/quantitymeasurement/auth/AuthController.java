package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.auth.dto.AuthResponse;
import com.qmaserver.quantitymeasurement.auth.dto.LoginRequest;
import com.qmaserver.quantitymeasurement.auth.dto.RefreshTokenRequest;
import com.qmaserver.quantitymeasurement.auth.dto.SignupRequest;
import com.qmaserver.quantitymeasurement.auth.dto.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.qmaserver.quantitymeasurement.util.SecurityUtils.resolveEmail;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieUtil authCookieUtil;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, AuthCookieUtil authCookieUtil, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.authCookieUtil = authCookieUtil;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        AuthResponse authResponse = authService.login(request);
        authCookieUtil.addAuthCookie(httpServletResponse, authResponse.getAccessToken(),
                jwtService.getJwtExpirationMs(), httpServletRequest.isSecure());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @GetMapping("/session")
    public ResponseEntity<UserProfileResponse> session(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equalsIgnoreCase(authentication.getName())) {
            return ResponseEntity.noContent().build();
        }

        String email = resolveEmail(authentication);
        if (email == null || email.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        try {
            return ResponseEntity.ok(authService.getProfileByEmail(email));
        } catch (AuthFlowException ignored) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Authentication authentication) {
        new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
        authCookieUtil.clearAuthCookie(httpServletResponse, httpServletRequest.isSecure());
        httpServletResponse.setHeader("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"");
        return ResponseEntity.noContent().build();
    }
}
