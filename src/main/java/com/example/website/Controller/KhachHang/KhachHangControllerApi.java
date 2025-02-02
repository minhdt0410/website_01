package com.example.website.Controller.KhachHang;

import com.example.website.Enity.KhachHang;
import com.example.website.Respository.KhachHangRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khachHang")
@RequiredArgsConstructor
public class KhachHangControllerApi {
    private final KhachHangRepo khachHangRepo;

    @GetMapping("/data")
    public List<KhachHang> khachHang(@RequestParam Integer id){
        if(id == 0){
            return khachHangRepo.findAll();
        }
        List<KhachHang> khachHangs = khachHangRepo.findAll();
        khachHangs.remove(khachHangRepo.getReferenceById(id));
        return khachHangs;
    }
}
