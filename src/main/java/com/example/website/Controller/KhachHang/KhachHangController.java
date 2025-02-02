package com.example.website.Controller.KhachHang;

import com.example.website.Service.MailService;
import com.example.website.Enity.KhachHang;
import com.example.website.Respository.KhachHangRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangRepo khachHangRepo;
    private final MailService mailService;

    @GetMapping("/admin/khachhang")
    public String getAdmin(Model model) {
        model.addAttribute("khachHangs", khachHangRepo.findAll());
        return "src/khachhang/KhachHang";
    }

    @GetMapping("/khachhang/add")
    public String getAddPage(
    ) {
        return "src/khachhang/AddKhachHang";
    }
    //    /admin/** => nhân viên hoặc admin mới vào đc
//    /khachhang/** => nhân viên hoặc admin mới vào đc
    @GetMapping("/khachhang/delete")
    public String deleteKH(@RequestParam Integer id) {
        KhachHang khachHang = khachHangRepo.getReferenceById(id);
        if (khachHang.getTrangThai().equals("Hoạt động")) {
            khachHang.setTrangThai("Ngừng hoạt động");
        } else {
            khachHang.setTrangThai("Hoạt động");
        }
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khachhang";
    }

    @GetMapping("/khachhang/update")
    public String getUpdate(
            Model model, @RequestParam Integer id) {
        model.addAttribute("KH", khachHangRepo.getReferenceById(id));
        return "src/khachhang/UpdateKhachHang";
    }

    @PostMapping("/khachhang/updateData")
    public String update(@ModelAttribute KhachHang khachHang) {
        KhachHang oldKhachHang = khachHangRepo.getReferenceById(khachHang.getId());

        khachHang.setTrangThai(oldKhachHang.getTrangThai());
        khachHang.setMatKhau(oldKhachHang.getMatKhau());
        khachHang.setId(oldKhachHang.getId());
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khachhang";
    }

    @PostMapping("/khachHang/save")
    public String save(
            @ModelAttribute KhachHang khachHang) throws MessagingException {
        khachHang.setTrangThai("Hoạt động");
        khachHang.setMatKhau(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
//        mailService.sendEmail(khachHang.getEmail(), khachHang.getHoTen(), khachHang.getMatKhau(), "/src/mail");
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khachhang";
    }

    private String[] getProvinceDetails(String provinceCode) {
        String url = "https://provinces.open-api.vn/api/p/" + provinceCode;
        return getDetailsFromApi(url);
    }

    private String[] getDistrictDetails(String districtCode) {
        String url = "https://provinces.open-api.vn/api/d/" + districtCode;
        return getDetailsFromApi(url);
    }

    private String[] getWardDetails(String wardCode) {
        String url = "https://provinces.open-api.vn/api/w/" + wardCode;
        return getDetailsFromApi(url);
    }

    private String[] getDetailsFromApi(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = response.getBody();


            Integer id = (Integer) data.get("code");
            String idString = id != null ? String.valueOf(id) : "Unknown";

            String name = (String) data.get("name");

            return new String[]{idString, name};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"Unknown", "Unknown"};
        }
    }
    @GetMapping("/khachHang/donhang")
    public String getDonHangMap(Model model){
        return "src/website/donhang";
    }
}
