package com.ecommerce.g58.security;

import com.ecommerce.g58.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
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
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/403");
        return accessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Public APIs
                .antMatchers("/api/search", "/api/shipping-address/**").permitAll()
                // Public pages and resources
                .antMatchers(
                        "/", "/**", "/sign-up/confirm-code/**",
                        "/address/**", "/cart-detail/**",
                        "/coming-soon/**", "/confirm-code/**", "/footer/**",
                        "/head/**", "/header/**",
                        "/homepage/**", "/homepageOrg/**", "/homepageTest/**",
                        "/my-account", "/my-shop/**", "/notification/**",
                        "/order/**", "/order-detail/**", "/privacy-policy/**",
                        "/product-detail/**", "/product-list/**",
                        "/sign-in/**", "/sign-up/**", "/sign-up-seller/**",
                        "/terms-of-service/**", "/view-store/**", "/wallet/**",
                        "/wishlist/**", "/forgot-password/**", "/reset-password/**",
                        "/add_to_cart", "/cart-items", "/product/**",
                        "/notification",
                        "/store-info/**", "/store-save/**", "/addProductFull/**", "/addProductForm2/**",
                        "/vn/**", "/submitOrder/**", "/now/**", "/vnpay-payment/**"
                ).permitAll()
                // Quyền cho ADMIN
                .antMatchers("/admin/user-manager/**","admin/**")
                .hasRole("Admin")
                // Checkout page requires authentication
                .antMatchers("/checkout").authenticated()

                // Any other request must be authenticated
                .anyRequest().authenticated()
                .and()

                // Session Management Configuration
                .sessionManagement()
                .invalidSessionUrl("/sign-in?session=invalid") // Redirect on invalid session
                .sessionFixation().none() // Prevents creating a new session after login
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

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("Customer");
    }
}