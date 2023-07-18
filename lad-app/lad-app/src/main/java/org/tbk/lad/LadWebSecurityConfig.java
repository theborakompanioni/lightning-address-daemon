package org.tbk.lad;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class LadWebSecurityConfig implements WebSecurityCustomizer {

    @Override
    public void customize(WebSecurity web) {
        web.httpFirewall(new StrictHttpFirewall());
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                // allow GET requests to LNURL endpoints
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            antMatcher(HttpMethod.GET, "/.well-known/lnurlp/**"),
                            antMatcher(HttpMethod.GET, "/api/v1/lnurl/pay/**")
                    ).permitAll();
                })
                // allow static resources
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            antMatcher(HttpMethod.GET,"/"),
                            antMatcher(HttpMethod.GET,"/index.html"),
                            antMatcher(HttpMethod.GET,"/fonts/**"),
                            PathRequest.toStaticResources().atCommonLocations()
                    ).permitAll();
                })
                // allow swagger-ui
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        antMatcher(HttpMethod.GET, "/swagger-ui.html"),
                        antMatcher(HttpMethod.GET, "/swagger-ui/**"),
                        antMatcher(HttpMethod.GET, "/v3/api-docs/**")
                ).permitAll())
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                });

        return http.build();
    }
}
