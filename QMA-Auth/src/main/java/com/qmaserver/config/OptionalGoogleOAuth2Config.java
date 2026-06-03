package com.qmaserver.config;

import com.qmaserver.quantitymeasurement.auth.OAuth2SuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

//Google OAuth2 configuration
@Component
public class OptionalGoogleOAuth2Config {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public OptionalGoogleOAuth2Config(OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

   // Google OAuth2 login configuration to HttpSecurity object.
    public void configure(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorization"))
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*"))
                .successHandler(oAuth2SuccessHandler)
        );
    }
}
