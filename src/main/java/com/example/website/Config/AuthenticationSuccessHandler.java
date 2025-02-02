package com.example.website.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        System.out.println("Tài khoản: " + userDetails.getUsername() + " đã đăng nhập vào hệ thống");

        if (isAdmin) {
            System.out.println("toi la admin");
            setDefaultTargetUrl("/admin");
        } else if (isCustomer) {
            setDefaultTargetUrl("/home");
            setAlwaysUseDefaultTargetUrl(true);
        }

        request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
