package com.example.gateway.web.rest;

import com.example.gateway.repository.AppUserRepository;
import com.example.gateway.security.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final AppUserRepository appUserRepository;

    public AccountResource(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    private static class AccountResourceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private AccountResourceException(String message) {
            super(message);
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @param principal the current user; resolves to {@code null} if not authenticated.
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public Mono<UserVM> getAccount(Principal principal) {
        if (principal instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken authToken = (AbstractAuthenticationToken) principal;
            String cin = authToken.getName();
            
            return appUserRepository.findByCin(cin)
                .map(appUser -> new UserVM(
                    appUser.getCin(),
                    Set.of(appUser.getRole()),
                    Map.of(
                        "firstName", appUser.getFirstName() != null ? appUser.getFirstName() : "",
                        "lastName", appUser.getLastName() != null ? appUser.getLastName() : "",
                        "email", appUser.getEmail() != null ? appUser.getEmail() : "",
                        "phone", appUser.getPhone() != null ? appUser.getPhone() : "",
                        "activated", true,
                        "langKey", "fr"
                    )
                ))
                .switchIfEmpty(Mono.error(new AccountResourceException("User not found in database")));
        } else {
            throw new AccountResourceException("User could not be found");
        }
    }

    private static class UserVM {

        private String login;
        private Set<String> authorities;
        private Map<String, Object> details;

        UserVM(String login, Set<String> authorities, Map<String, Object> details) {
            this.login = login;
            this.authorities = authorities;
            this.details = details;
        }

        public boolean isActivated() {
            return true;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public String getLogin() {
            return login;
        }

        @JsonAnyGetter
        public Map<String, Object> getDetails() {
            return details;
        }
    }
}
