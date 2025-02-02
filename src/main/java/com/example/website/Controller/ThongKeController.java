package com.example.website.Controller;

import com.example.website.Enity.DonHang;
import com.example.website.Enity.HoaDon;
import com.example.website.Enity.ThongKe;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.HoaDonRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ThongKeController {
    private final donhangService DonHangRepo;

    @GetMapping("/admin/thongke")
    public String getAdmin() {
        return "src/thongke/ThongKe";
    }

//    @GetMapping("/theo-thang")
//    public Map<String, Object> getRevenueStatistics(@RequestParam("year") int year, @RequestParam("month") int month) {
//        return DonHangRepo.getTotalRevenueAndQuantityByYearAndMonth(year, month);
//    }

@GetMapping("/thong-ke")
public ResponseEntity<List<Map<String, Object>>> getMonthlyStatistics(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month) {
    List<Map<String, Object>> statistics = DonHangRepo.getMonthlyStatistics(year, month);
    return ResponseEntity.ok(statistics);
}
}

