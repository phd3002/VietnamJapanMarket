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
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    @Autowired
    private UserService userService;

    @Bean
    public HttpFirewall allowUrlWithDoubleSlash() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }

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
                // Public APIs
                .antMatchers("/api/search", "/api/shipping-address/**").permitAll()

                // Public pages and resources
                .antMatchers(
                        "/products/**", "/category/**", "/resources/**", "/static/**", "/css/**", "/js/**", "/images/**",
                        "/", "/sign-up/confirm-code/**", "/address/**", "/cart-detail/**", "/checkout/**", "/coming-soon/**",
                        "/confirm-code/**", "/footer/**", "/head/**", "/header/**", "/homepage/**", "/homepageOrg/**",
                        "/homepageTest/**", "/my-account", "/my-shop/**", "/notification/**", "/order/**",
                        "/order-detail/**", "/privacy-policy/**", "/product-detail/**", "/product-list/**", "/sign-in/**",
                        "/sign-up/**", "/sign-up-seller/**", "/terms-of-service/**", "/view-store/**", "/wallet/**",
                        "/wishlist/**", "/forgot-password/**", "/reset-password/**", "/add_to_cart", "/cart-items",
                        "/sign-up-seller/**"
                ).permitAll()

                // Checkout page requires authentication
                .antMatchers("/checkout").authenticated()

                // Any other request must be authenticated
                .anyRequest().authenticated()
                .and()
                // Logout configuration
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/sign-in?logout")
                .permitAll()
                .and()
                // Exception handling for access denied
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                // Disable CSRF and CORS for simplicity (adjust as necessary)
                .csrf().disable()
                .cors().disable();
    }
}