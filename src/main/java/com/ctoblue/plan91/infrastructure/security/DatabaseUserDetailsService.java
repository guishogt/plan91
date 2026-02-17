package com.ctoblue.plan91.infrastructure.security;

import com.ctoblue.plan91.adapter.out.persistence.entity.UserEntity;
import com.ctoblue.plan91.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security UserDetailsService implementation that loads users from the database.
 *
 * <p>This service is used by Spring Security to authenticate users during login.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    public DatabaseUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPasswordHash())
                .disabled(!userEntity.getEnabled())
                .accountLocked(userEntity.getAccountLocked())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
