package com.ecommerce.g58.security;

import com.ecommerce.g58.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    @Autowired
    private UserService userService;

    @Bean
    public WebMvcConfigurer configureWebMvc() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendRedirect("/sign-in");  // Redirect to login page
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/search").permitAll()
                .antMatchers("/api/shipping-address/**").permitAll()
                .antMatchers("/products/**", "/category/**").permitAll()
                .antMatchers("/checkout").authenticated()  // Only authenticated users can access checkout
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers(
                        "/", "/sign-up/confirm-code/**",
                        "/address/**", "/cart-detail/**", "/checkout/**",
                        "/coming-soon/**", "/confirm-code/**", "/footer/**",
                        "/head/**", "/header/**",
                        "/homepage/**", "/homepageOrg/**", "/homepageTest/**",
                        "/my-account", "/my-shop/**", "/notification/**",
                        "/order/**", "/order-detail/**", "/privacy-policy/**",
                        "/product-detail/**", "/product-list/**",
                        "/sign-in/**", "/sign-up/**", "/sign-up-seller/**",
                        "/terms-of-service/**", "/view-store/**", "/wallet/**",
                        "/wishlist/**", "/forgot-password/**", "/reset-password/**",
                        "/add_to_cart", "/cart-items"
                ).permitAll()
                .anyRequest().authenticated()  // All other requests need authentication
                .and()
                .formLogin()
                .loginPage("/sign-in")  // Redirect to sign-in page if not authenticated
                .defaultSuccessUrl("/homepage", true)
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/sign-in?logout")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/sign-in");  // Redirect to sign-in page when unauthorized
                })
                .and()
                .csrf().disable()
                .cors().disable();
    }
}
