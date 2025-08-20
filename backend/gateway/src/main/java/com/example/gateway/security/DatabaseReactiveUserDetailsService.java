package com.example.gateway.security;

import com.example.gateway.repository.AppUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Service
@Primary
public class DatabaseReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final AppUserRepository appUserRepository;

    public DatabaseReactiveUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return appUserRepository
            .findByCin(username)
            .map(appUser -> {
                Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(appUser.getRole()));
                return User.withUsername(appUser.getCin()).password(appUser.getPasswordHash()).authorities(authorities).build();
            });
    }
}


