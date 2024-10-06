package com.ecommerce.g58.security;

import com.ecommerce.g58.service.UserService;
import com.ecommerce.g58.service.implementation.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()  // Tài nguyên công cộng
                        .requestMatchers("/login", "/register", "/homepage/**").permitAll()  // Cho phép truy cập ẩn danh vào trang chính
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/sign-up", "/sign-in").permitAll()  // Cho phép công khai truy cập trang sign-up và sign-in
                        .anyRequest().authenticated()  // Các trang còn lại yêu cầu xác thực
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
//                .csrf().disable();  // Tắt CSRF cho môi trường phát triển (nên bật CSRF trong môi trường sản xuất)

        return http.build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                // Cho phép tất cả mọi người truy cập vào các URL này
//                .antMatchers("/check-header/**").permitAll()
//                .antMatchers("/resources/**", "/templates/**", "/static/**",
//                        "/css/**", "/js/**", "/img/**", "/scss/**", "/vendors/**",
//                        "/dashboard/**", "/register","/forgot-password","/reset-password/**","/terms-of-service-and-privacy-policy","/now").permitAll()
//                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
//                .antMatchers("/register/**", "/register", "/register/verify", "/change-password/**", "/change-password").permitAll()
//                .antMatchers("/", "/login/**","/login", "/homepage/**", "/canteens/**", "/canteen_details", "/canteen_info", "/food_details","/update_cart_quantity").permitAll()
//                .antMatchers("/add_to_cart","/foodByCategory/{categoryId}","/assign-confirm","/assign-confirm/**","/cart/**","/cart/payment","/cart/remove-from-cart-provisional").permitAll()
//                // Các quyền truy cập yêu cầu xác thực
//                .antMatchers("/view-profile/**", "/update-profile", "/staff-change-password/**", "/staff-change-password",
//                        "/submit-feedback/**","/feedback-system-form/**",
//                        "/vn/**","/submitOrder/**","/now/**","/vnpay-payment/**")
//                .hasAnyRole("ADMIN", "MANAGER", "STAFF", "CUSTOMER")
//                // Quyền cho ADMIN
//                .antMatchers("/search-staff", "/dashboard/", "/manage-user/**",
//                        "/edit-profile/**", "/edit-user/**", "/add-user/**",
//                        "/manage-canteen/**", "/add-canteen", "/search-canteen",
//                        "/edit-canteen/**", "/delete-canteen", "/dashboard-admin",
//                        "/assign-manager-form/**","/check-email-manager/**","/assign-manager-confirm/**")
//                .hasRole("ADMIN")
//                // Quyền cho MANAGER
//                .antMatchers("/manage-staff/**", "/search-staff", "/add-staff/**",
//                        "/edit-staff/**", "/canteen-details/**", "/canteen/update-profile-canteen/**",
//                        "/canteen/edit-profile-canteen/**","/manage-food/**","/manage-food","/canteen/**",
//                        "/add-food-form", "/add-food-form/**", "/add-food", "/add-food/**","/check-email/**",
//                        "/assign-staff-form/**","/assign-confirm/**", "/manage-category/**",
//                        "/add-category-form", "/add-category", "/edit-category/**",
//                        "dashboard-manager/**",
//                        "/manage-feedback","/approve-feedback/**","/reject-feedback/**","/bulk-approve-feedback/**","/bulk-reject-feedback/**",
//                        "/search-food/**","/add-food-form/**","/add-food","/edit-food/**",
//                        "/add-quantity/**","/order-list/**","/update-order-status/**",
//                        "/bulk-assign-orders/**","/reject-order/**","/order-list-reject/**","/order-list-refund/**",
//                        "/refund-order/**", "/create-order-at-couter"
//                )
//                .hasRole("MANAGER")
//                // Quyền cho STAFF
//                .antMatchers("/order-list-ship/**","/complete-order/**", "/reject-order-ship/**")
//                .hasRole("STAFF")
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/homepage", true)
//                .failureUrl("/login?error")
//                .permitAll()
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .permitAll()
//                .and()
//                .csrf(AbstractHttpConfigurer::disable)
//                .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
//    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> response.sendRedirect("/403");
    }
}
