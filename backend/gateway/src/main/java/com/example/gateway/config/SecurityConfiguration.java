package com.example.gateway.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import com.example.gateway.security.AuthoritiesConstants;
import com.example.gateway.web.filter.SpaWebFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.gateway.security.DatabaseReactiveUserDetailsService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.util.StringUtils;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(SecurityProperties properties) {
        SecurityProperties.User user = properties.getUser();
        UserDetails userDetails = User.withUsername(user.getName())
            .password("{noop}" + user.getPassword())
            .roles(StringUtils.toStringArray(user.getRoles()))
            .build();
        return new MapReactiveUserDetailsService(userDetails);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(DatabaseReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .securityMatcher(
                new NegatedServerWebExchangeMatcher(
                    new OrServerWebExchangeMatcher(pathMatchers("/app/**", "/i18n/**", "/content/**", "/swagger-ui/**"))
                )
            )
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new SpaWebFilter(), SecurityWebFiltersOrder.HTTPS_REDIRECT)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(frameOptions -> frameOptions.mode(Mode.DENY))
                    .referrerPolicy(referrer ->
                        referrer.policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    )
                    .permissionsPolicy(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeExchange(authz ->
                authz
                    // Public endpoints
                    .pathMatchers("/")
                    .permitAll()
                    .pathMatchers("/*.*")
                    .permitAll()
                    .pathMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .pathMatchers("/api/authenticate")
                    .permitAll()
                    // Allow user registration & access to user profiles (custom, depending on your use case)
                    .pathMatchers("/api/app-users", "/api/app-users/**")
                    .permitAll()
                    // Admin-only endpoints
                    .pathMatchers("/api/admin/**")
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .pathMatchers("/v3/api-docs/**")
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .pathMatchers("/management/**")
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    // Health and monitoring
                    .pathMatchers("/management/health")
                    .permitAll()
                    .pathMatchers("/management/health/**")
                    .permitAll()
                    .pathMatchers("/management/info")
                    .permitAll()
                    .pathMatchers("/management/prometheus")
                    .permitAll()
                    // Microservices service routes
                    .pathMatchers("/services/*/management/health/readiness")
                    .permitAll()
                    .pathMatchers("/services/*/v3/api-docs")
                    .hasAuthority(AuthoritiesConstants.ADMIN)
                    .pathMatchers("/services/**")
                    .authenticated()
                    // Default rule: all APIs need authentication
                    .pathMatchers("/api/**")
                    .authenticated()
                    // Any other request must be authenticated
                    .anyExchange()
                    .authenticated()
            )
            .httpBasic(basic -> basic.disable())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }
}
