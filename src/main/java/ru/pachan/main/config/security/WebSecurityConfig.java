package ru.pachan.main.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.pachan.main.util.enums.AuthorityEnum;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final RequestProvider requestProvider;

    @Bean
    @Order(1)
    public SecurityFilterChain managementFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                // EXPLAIN_V Без Spring Admin достаточно было бы 2 ЭП этих
                                // .requestMatchers("actuator/prometheus").permitAll()
                                // .requestMatchers("actuator/health").permitAll()
                                .requestMatchers("actuator/**").permitAll()
                                .anyRequest().denyAll()
                );
        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(it -> it
                        // TODO выглядит лишним
                        .requestMatchers("api/auth/**").hasAuthority(AuthorityEnum.VERIFIED_TOKEN.getAuthority())
                        .anyRequest().authenticated())
                .addFilterBefore(
                        new JwtFilter(requestProvider),
                        UsernamePasswordAuthenticationFilter.class
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
