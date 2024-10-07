package com.ecommerce.g58.security;

import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.implementation.UserDetailsServiceImpl;
import com.ecommerce.g58.service.implementation.UserServiceImp;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

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
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/**").permitAll()  // Tài nguyên công cộng
                        .requestMatchers("/homepage/**", "/sign-up/confirm-code").permitAll()  // Cho phép truy cập ẩn danh vào trang chính
                        .requestMatchers("/admin/**").hasRole("Adnin")
                        .requestMatchers("/user/**").hasAnyRole("Customer", "Admin")
                        .requestMatchers("/sign-up", "/sign-in").permitAll()  // Cho phép công khai truy cập trang sign-up và sign-in
                        .anyRequest().authenticated()  // Các trang còn lại yêu cầu xác thực
                )
                .formLogin
                        (login -> login
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

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("Customer");
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
