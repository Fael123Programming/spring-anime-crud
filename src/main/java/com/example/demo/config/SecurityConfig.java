package com.example.demo.config;

import com.example.demo.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {
    private final SysUserService sysUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests((http) -> http
                        .requestMatchers(new AntPathRequestMatcher("/animes/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/animes/**")).hasRole("USER")
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        // In production, csrf token should never be disabled!!!
        return httpSecurity.build();
    }

        // In memory user.
//        @Bean
//        public InMemoryUserDetailsManager userDetailsService() {
//            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//            UserDetails user = User.withUsername("leafar")
//                    .password(encoder.encode("leafar"))
//                    .roles("USER")
//                    .build();
//            UserDetails admin = User.withUsername("admin")
//                    .password(encoder.encode("admin"))
//                    .roles("USER", "ADMIN")
//                    .build();
//            return new InMemoryUserDetailsManager(user, admin);
//        }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        log.info("'leafar' encoded {}", encoder.encode("leafar"));
//        log.info("'admin' encoded {}", encoder.encode("admin"));
        return authenticationConfiguration.getAuthenticationManager();
    }
}
