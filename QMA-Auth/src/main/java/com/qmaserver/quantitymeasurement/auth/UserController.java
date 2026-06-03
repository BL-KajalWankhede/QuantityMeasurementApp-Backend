package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.auth.dto.UpdateUserProfileRequest;
import com.qmaserver.quantitymeasurement.auth.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.qmaserver.quantitymeasurement.util.SecurityUtils.resolveEmail;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> myProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getProfileByEmail(resolveEmail(authentication)));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(resolveEmail(authentication), request));
    }
}
