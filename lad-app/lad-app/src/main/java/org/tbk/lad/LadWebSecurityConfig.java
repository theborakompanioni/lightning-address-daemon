package org.tbk.lad;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class LadWebSecurityConfig implements WebSecurityCustomizer {

    @Override
    public void customize(WebSecurity web) {
        web
                .ignoring()
        ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        antMatcher("/.well-known/lnurlp/**"),
                        antMatcher("/api/v1/lnurl/pay/**")
                ).permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        antMatcher("/"),
                        antMatcher("/index.html"),
                        antMatcher("/fonts/**"),
                        PathRequest.toStaticResources().atCommonLocations()
                ).permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        antMatcher("/swagger-ui.html"),
                        antMatcher("/swagger-ui/**"),
                        antMatcher("/v3/api-docs/swagger-config"),
                        antMatcher("/v3/api-docs")
                ).permitAll();

        return http.build();
    }
}