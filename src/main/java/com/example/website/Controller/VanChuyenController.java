package com.example.website.Controller;

import com.example.website.Enity.DonHang;
import com.example.website.Enity.VanChuyenEntity;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.VanChuyenRePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VanChuyenController {

    @Autowired
    VanChuyenRePo vanChuyenRePo;
    @Autowired
    DonHangRepo donHangRepo;

    @GetMapping("/admin/vanchuyen")
    public String index(@RequestParam(defaultValue = "0") int p, Model model) {
        int pageSize = 5;
        if (p < 0) {
            p = 0;
        }
        Pageable pageable = PageRequest.of(p, pageSize);
        Page<VanChuyenEntity> page = vanChuyenRePo.findAll(pageable);
        if (p >= page.getTotalPages() && page.getTotalPages() > 0) {
            p = page.getTotalPages() - 1;
            pageable = PageRequest.of(p, pageSize);
            page = vanChuyenRePo.findAll(pageable);
        }
        List<DonHang> list = donHangRepo.findAll();
        model.addAttribute("vc",list);
        model.addAttribute("page", page);
        model.addAttribute("vc", page.getContent());
        return "src/vanchuyen/VanChuyen";
    }
}
