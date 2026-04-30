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
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

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
                            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/.well-known/lnurlp/**"),
                            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/v1/lnurl/pay/**")
                    ).permitAll();
                })
                // allow static resources
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations(),
                            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/"),
                            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/index.html"),
                            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/fonts/**")
                    ).permitAll();
                })
                // allow swagger-ui
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/swagger-ui.html"),
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/swagger-ui/**"),
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/v3/api-docs/**")
                ).permitAll())
                .authorizeHttpRequests(auth -> {
                    auth.anyRequest().authenticated();
                });

        return http.build();
    }
}
