package com.ecommerce.g58.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()  // Public resources
                        .requestMatchers("/login", "/register", "/homepage/**", "/sign-up/confirm-code").permitAll()  // Public pages
                        .requestMatchers("/api/search").permitAll()  // Allow public access to /api/search
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/sign-up", "/sign-in").permitAll()  // Public access to sign-up and sign-in
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .formLogin(login -> login
                        .loginPage("/sign-in")
                        .defaultSuccessUrl("/homepage", true)
                        .failureUrl("/sign-in?error=true")  // Thêm dòng này để xử lý lỗi khi đăng nhập sai
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL để đăng xuất
                        .logoutSuccessUrl("/sign-in?logout=true")  // Chuyển đến trang đăng nhập khi đăng xuất thành công
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")  // Trang lỗi khi từ chối truy cập
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> response.sendRedirect("/403");
    }

}
