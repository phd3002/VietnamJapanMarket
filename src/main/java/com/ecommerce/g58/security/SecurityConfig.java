package com.ecommerce.g58.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userService);
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }


//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userDetailsService);
//        auth.setPasswordEncoder(passwordEncoder());
//        return auth;
//    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/**").permitAll()  // Tài nguyên công cộng
                        .requestMatchers("/homepage/**", "/sign-up/confirm-code").permitAll()  // Cho phép truy cập ẩn danh vào trang chính
                        .requestMatchers("/admin/**").hasRole("Adnin")
                        .requestMatchers("/user/**").hasAnyRole("Customer", "Admin")
                        .requestMatchers("/sign-up", "/sign-in", "sign-in/**").permitAll()  // Cho phép công khai truy cập trang sign-up và sign-in
                        .anyRequest().authenticated()  // Các trang còn lại yêu cầu xác thực
                )
                .formLogin(login -> login
                        .loginPage("/sign-in")
//                        .usernameParameter("email")
                        .defaultSuccessUrl("/homepage", true)
                        .failureForwardUrl("/sign-in?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL để đăng xuất
                        .logoutSuccessUrl("/sign-in?logout=true")  // Chuyển đến trang đăng nhập khi đăng xuất thành công
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")  // Trang lỗi khi từ chối truy cập
                )
                .csrf(csrf -> csrf.disable())
                .build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/sign-up", "/sign-in", "/css/**", "/js/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/sign-in")
//                        .loginProcessingUrl("/sign-in")
//                        .defaultSuccessUrl("/homepage", true)
//                        .failureUrl("/sign-in?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/sign-in?logout=true")
//                        .permitAll()
//                )
//                .csrf(csrf -> csrf.disable())
//                .build();
//    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("system_admin")
                        .password(passwordEncoder().encode("khuong.hung2001"))
                        .build()
        );
    }
}

