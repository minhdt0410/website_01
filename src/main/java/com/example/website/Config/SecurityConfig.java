package com.example.website.Config;
import com.example.website.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);  // Đăng ký dịch vụ lấy thông tin người dùng
        authenticationProvider.setPasswordEncoder(new NoOpPasswordEncoder());  // Không mã hóa mật khẩu (plaintext)
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/login", "/register","/website").permitAll();
                    registry.requestMatchers(
                            "/quenMk/**",
                            "/api/**",
                            "/detail/**",
                            "/static/src/website/css/**",
                            "/static/src/website/js/**",
                            "/static/src/website/css/**",
                            "/static/src/website/**",
                            "/static/src/website/lib/**").permitAll();
                    registry.requestMatchers("/login", "/register").permitAll();
//                    registry.requestMatchers("/website",
//                            "/cart/**",
//                            "/home/**",
//                            "/static/src/web/**",
//                            "/static/src/js/**",
//                            "/static/src/assets/**",
//                            "/quenMk/**",
//                            "/api/**",
//                            "/detail/**",
//                            "/static/src/website/css/**",
//                            "/static/src/website/js/**",
//                            "/static/src/website/css/**",
//                            "/static/src/website/**",
//                            "/static/src/website/lib/**").permitAll();
                    registry.requestMatchers("/cart").hasRole("USER");
                    registry.anyRequest().permitAll();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(authenticationSuccessHandler)
                            .permitAll();
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/logout") // URL để đăng xuất
                            .logoutSuccessUrl("/login") // URL sau khi đăng xuất thành công
                            .invalidateHttpSession(true) // Hủy phiên làm việc
                            .deleteCookies("JSESSIONID") // Xóa cookie phiên
                            .permitAll(); // Cho phép tất cả người dùng truy cập vào /logout
                })
                .build();
//        http.csrf().disable() // Tắt CSRF protection (không khuyến khích cho môi trường sản xuất)
//                .authorizeRequests()
//                // Chỉ cho phép admin truy cập các endpoint bắt đầu bằng /admin/**
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                // Yêu cầu đăng nhập với các endpoint liên quan đến giỏ hàng
//                .requestMatchers("/cart").authenticated()
//                // Cho phép tất cả người dùng truy cập các endpoint liên quan đến website và chi tiết sản phẩm
//                .requestMatchers("/website", "/detail/**","http://www.thymeleaf.org","img/favicon.ico","/static/src/website/lib/animate/animate.min.css","/static/src/website/lib/owlcarousel/assets/owl.carousel.min.css","/static/src/website/css/style.css","https://fonts.gstatic.com","/static/src/website/js/main.js","/static/src/website/mail/contact.js","/static/src/website/mail/jqBootstrapValidation.min.js","/static/src/website/lib/owlcarousel/owl.carousel.min.js","/static/src/website/lib/easing/easing.min.js","https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js","https://code.jquery.com/jquery-3.4.1.min.js").permitAll()
//                // Cho phép không cần đăng nhập với các trang login và register
//                .requestMatchers("/login", "/register").permitAll()
//                // Yêu cầu đăng nhập cho tất cả các yêu cầu khác
//                .anyRequest().authenticated()
//                .and()
//                // Cấu hình trang đăng nhập
//                .formLogin(form -> form
//                        .loginPage("/login") // Đường dẫn đến trang login tùy chỉnh
//                        .usernameParameter("email") // Tên tham số cho trường email trong form đăng nhập
//                        .passwordParameter("matKhau") // Tên tham số cho trường mật khẩu trong form đăng nhập
//                        .defaultSuccessUrl("/website", true) // Trang đích sau khi đăng nhập thành công
//                        .failureUrl("/login?error") // Đường dẫn khi đăng nhập thất bại
//                        .permitAll() // Cho phép tất cả người dùng truy cập trang đăng nhập
//                        .successHandler((request, response, authentication) -> {
//                            // Kiểm tra quyền của người dùng sau khi đăng nhập thành công
//                            if (authentication.getAuthorities().stream()
//                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
//                                // Nếu người dùng là admin, chuyển hướng đến trang dành cho admin
//                                response.sendRedirect("/admin");
//                            } else {
//                                // Nếu không phải admin, chuyển đến trang mặc định
//                                response.sendRedirect("/website");
//                            }
//                        })
//                );

        // Trả về cấu hình SecurityFilterChain

//        return http.build();
//        http.csrf().disable() // Tắt CSRF protection (không khuyến khích cho môi trường sản xuất)
//                .authorizeRequests()
//                // Chỉ cho phép admin truy cập các endpoint bắt đầu bằng /admin/**
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                // Yêu cầu đăng nhập với các endpoint liên quan đến giỏ hàng
//                .requestMatchers("/cart").authenticated()
//                // Cho phép tất cả người dùng truy cập các endpoint liên quan đến website và chi tiết sản phẩm
//                .requestMatchers("/website", "/detail/**","http://www.thymeleaf.org","img/favicon.ico","/static/src/website/lib/animate/animate.min.css","/static/src/website/lib/owlcarousel/assets/owl.carousel.min.css","/static/src/website/css/style.css","https://fonts.gstatic.com","/static/src/website/js/main.js","/static/src/website/mail/contact.js","/static/src/website/mail/jqBootstrapValidation.min.js","/static/src/website/lib/owlcarousel/owl.carousel.min.js","/static/src/website/lib/easing/easing.min.js","https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js","https://code.jquery.com/jquery-3.4.1.min.js").permitAll()
//                // Cho phép không cần đăng nhập với các trang login và register
//                .requestMatchers("/login", "/register").permitAll()
//                // Yêu cầu đăng nhập cho tất cả các yêu cầu khác
//                .anyRequest().authenticated()
//                .and()
//                // Cấu hình trang đăng nhập
//                .formLogin(form -> form
//                        .loginPage("/login") // Đường dẫn đến trang login tùy chỉnh
//                        .usernameParameter("email") // Tên tham số cho trường email trong form đăng nhập
//                        .passwordParameter("matKhau") // Tên tham số cho trường mật khẩu trong form đăng nhập
//                        .defaultSuccessUrl("/website", true) // Trang đích sau khi đăng nhập thành công
//                        .failureUrl("/login?error") // Đường dẫn khi đăng nhập thất bại
//                        .permitAll() // Cho phép tất cả người dùng truy cập trang đăng nhập
//                        .successHandler((request, response, authentication) -> {
//                            // Kiểm tra quyền của người dùng sau khi đăng nhập thành công
//                            if (authentication.getAuthorities().stream()
//                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
//                                // Nếu người dùng là admin, chuyển hướng đến trang dành cho admin
//                                response.sendRedirect("/admin");
//                            } else {
//                                // Nếu không phải admin, chuyển đến trang mặc định
//                                response.sendRedirect("/website");
//                            }
//                        })
//                );
//        // Trả về cấu hình SecurityFilterChain
//        return http.build();
    }

}