package com.qmaserver.quantitymeasurement.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieUtil {
    private static final Logger log = LogManager.getLogger(AuthCookieUtil.class);
    public static final String AUTH_COOKIE_NAME = "QMA_AUTH_TOKEN";

    @Value("${app.auth.cookie.secure:false}")
    private boolean forceSecureCookie;

    public void addAuthCookie(HttpServletResponse response, String token, long maxAgeMs, boolean secure) {
        log.trace("Creating auth cookie");
        boolean resolvedSecure = resolveSecure(secure);
        ResponseCookie authCookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(resolvedSecure)
                .sameSite("None")
                .path("/")
                .maxAge(Math.max(0, maxAgeMs / 1000))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
        log.debug("Auth cookie created");
    }

    public void clearAuthCookie(HttpServletResponse response, boolean secure) {
        log.trace("Clearing auth cookie");
        boolean resolvedSecure = resolveSecure(secure);
        ResponseCookie clearCookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(resolvedSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
        log.debug("Auth cookie cleared");
    }

    private boolean resolveSecure(boolean requestSecure) {
        return forceSecureCookie || requestSecure;
    }
}
