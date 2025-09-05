package com.example.concert.config;

import com.example.concert.cache.UserCacheService;
import com.example.concert.domain.user.User;
import com.example.concert.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserCacheService userCacheService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User cachedUser = userCacheService.getUser(email);

        if (cachedUser != null) {
            return new CustomUserDetails(cachedUser);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        userCacheService.saveUser(user);

        return customUserDetails;
    }

}
