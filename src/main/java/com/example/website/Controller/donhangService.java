package com.example.website.Controller;

import com.example.website.Enity.DonHang;
import com.example.website.Enity.HoaDon;
import com.example.website.Respository.DonHangRepo;
import com.example.website.Respository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Date;
@Controller
public class donhangService {
    @Autowired
    private HoaDonRepo donHangRepo ;

    // Phương thức lấy danh sách đơn hàng theo tháng
    public List<Map<String, Object>> getMonthlyStatistics(Integer year, Integer month) {
        List<Object[]> results = donHangRepo.findMonthlyStatistics(year, month);
        List<Map<String, Object>> statistics = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", result[0]);
            data.put("totalAmount", result[1]);
            data.put("totalQuantity", result[2]);
            statistics.add(data);
        }

        return statistics;
    }

    public Page<HoaDon> getHoaDonPage(Pageable pageable) {
        return donHangRepo.findAll(pageable); // Giả sử bạn sử dụng Spring Data JPA
    }

    public List<HoaDon> getAllHoaDonexecl(Pageable pageable) {
        // Nếu bạn muốn lấy tất cả hóa đơn của một trang cụ thể, sử dụng phương thức phân trang
        return donHangRepo.findAll(pageable).getContent();
    }

}

