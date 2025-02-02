package com.example.website.Controller;

import com.example.website.Enity.KhachHang;
import com.example.website.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserControllerApi {
    private final UserService userService;

    @GetMapping
    public KhachHang getCurrentKhachHang(Authentication authentication){
        if(authentication != null){
            return userService.currentKhachHang(authentication);
        }
        return null;
    }
}
