package com.example.website.Service;

import com.example.website.Enity.Ad;
import com.example.website.Enity.KhachHang;
import com.example.website.Respository.AdminRepository;
import com.example.website.Respository.KhachHangRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final KhachHangRepo khachHangRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(KhachHangRepo khachHangRepository, AdminRepository adminRepository) {
        this.khachHangRepository = khachHangRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm admin theo email
        Ad admin = adminRepository.findByEmail(username);
        if (admin != null) {
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getMatKhau()) // Mật khẩu plaintext từ cơ sở dữ liệu
                    .roles("ADMIN") // Gán quyền ADMIN
                    .build();
        }
        // Tìm khách hàng theo email
        KhachHang khachHang = khachHangRepository.findByEmail(username);
        System.out.println(khachHang);
        if (khachHang != null) {
            if(khachHang.getTrangThai().equals("Hoạt động")){
                return User.builder()
                        .username(khachHang.getEmail())
                        .password(khachHang.getMatKhau()) // Mật khẩu plaintext từ cơ sở dữ liệu
                        .roles("USER") // Gán quyền USER
                        .build();
            }else {
                throw new UsernameNotFoundException("Tài khoản đã bik khoá: " + username);
            }
        }

        throw new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + username);
    }

}