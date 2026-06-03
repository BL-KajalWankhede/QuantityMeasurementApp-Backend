package com.qmaserver.quantitymeasurement.util;

import com.qmaserver.quantitymeasurement.auth.AuthFlowException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class SecurityUtils {

    public static String resolveEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof OAuth2User oauth2User) {
            Object email = oauth2User.getAttribute("email");
            if (email instanceof String emailString && !emailString.isBlank()) {
                return emailString;
            }
        }

        String authName = authentication.getName();
        if (authName != null && authName.contains("@")) {
            return authName;
        }

        throw new AuthFlowException("Unable to resolve user email from authentication");
    }
}
