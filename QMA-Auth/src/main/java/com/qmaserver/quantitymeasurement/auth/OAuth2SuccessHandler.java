package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.model.AuthProvider;
import com.qmaserver.quantitymeasurement.model.RefreshTokenEntity;
import com.qmaserver.quantitymeasurement.model.UserEntity;
import com.qmaserver.quantitymeasurement.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LogManager.getLogger(OAuth2SuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthCookieUtil authCookieUtil;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService, RefreshTokenService refreshTokenService, AuthCookieUtil authCookieUtil) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authCookieUtil = authCookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new AuthFlowException("Google account does not have an email");
        }

        UserEntity user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setName(safeString(oauthUser.getAttribute("name"), email));
            newUser.setPicture(safeString(oauthUser.getAttribute("picture"), null));
            newUser.setProvider(AuthProvider.GOOGLE);
            return newUser;
        });

        user.setName(safeString(oauthUser.getAttribute("name"), user.getName()));
        user.setPicture(safeString(oauthUser.getAttribute("picture"), user.getPicture()));
        if (user.getProvider() == null) {
            user.setProvider(AuthProvider.GOOGLE);
        }
        UserEntity savedUser = userRepository.save(user);
        JwtService.TokenPayload tokenPayload = jwtService.generateToken(savedUser);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        log.trace("Starting OAuth success redirect");
        log.info("Google login successful");
        authCookieUtil.addAuthCookie(response, tokenPayload.token(), jwtService.getJwtExpirationMs(),
                request.isSecure());

        // Append token to redirect URI
        String finalRedirectUri = redirectUri + (redirectUri.contains("?") ? "&" : "?") + "token=" + tokenPayload.token();

        log.debug("Redirecting to client");
        response.sendRedirect(finalRedirectUri);
    }

    private String safeString(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
