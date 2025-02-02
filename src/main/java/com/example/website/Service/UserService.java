package com.example.website.Service;

import com.example.website.Enity.Ad;
import com.example.website.Enity.KhachHang;
import com.example.website.Respository.AdminRepository;
import com.example.website.Respository.KhachHangRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final KhachHangRepo khachHangRepo;
    private final AdminRepository adminRepository;

    public KhachHang currentKhachHang(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return khachHangRepo.findByEmail(userDetails.getUsername());
    }
}
