package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LogManager.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.trace("Starting user detail load");
        return userRepository.findByEmailIgnoreCase(username)
                .map(UserPrincipal::fromUser)
                .orElseThrow(() -> {
                    log.fatal("Load failed: User missing from database");
                    return new UsernameNotFoundException("User not found");
                });
    }
}
