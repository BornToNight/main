package ru.pachan.main.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${spring.boot.admin.client.username}")
    private String adminUsername;

    @Value("${spring.boot.admin.client.password}")
    private String adminPassword;

    private final RequestProvider requestProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(it -> it
                        .requestMatchers("api/auth/**").hasAuthority("VerifiedToken")
                        .requestMatchers("actuator/**").hasAuthority("ActuatorAdmin")
                        .requestMatchers("instances/**").hasAuthority("ActuatorAdmin")
                        .anyRequest().authenticated())
                .addFilterBefore(
                        new JwtFilter(requestProvider, adminUsername, adminPassword),
                        BasicAuthenticationFilter.class
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return httpSecurity.build();
    }

    // TODO исправить в будущем по нормальному
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("api/auth/generate")
                .requestMatchers("swagger")
                .requestMatchers("swagger-ui/**")
                .requestMatchers("apiDocs/**")
                .requestMatchers("graphiql/**")
                .requestMatchers("graphiql");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5004"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
