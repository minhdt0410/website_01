package com.example.website.Controller.account;

import com.example.website.Enity.KhachHang;
import com.example.website.Respository.KhachHangRepo;
import com.example.website.Service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountControllerApi {
    private final KhachHangRepo khachHangRepo;
    private final MailService mailService;

    @PostMapping("/remember")
    public String sendMail(@RequestParam String email) throws MessagingException {
        KhachHang khachHang = khachHangRepo.findByEmail(email);
        if(khachHang != null){
            mailService.sendEmailRemember(khachHang.getEmail(), khachHang.getHoTen(), "http://localhost:8080/newPassword/"+khachHang.getEmail(),"/src/mailResetPassword");
            return "success";
        }
        return null;
    }

    @PostMapping("/updatePass/{id}/{newPassword}")
    public void updatePass(
            @PathVariable Integer id,
            @PathVariable String newPassword
    ){
        System.out.println(newPassword);
        System.out.println(id);
        KhachHang khachHang = khachHangRepo.getReferenceById(id);
        khachHang.setMatKhau(newPassword);
        khachHangRepo.save(khachHang);
    }
}
