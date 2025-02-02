package com.example.website.Service;

import com.example.website.Controller.KhuyenMai.KhuyenMaiControllerRealTime;
import com.example.website.Enity.KhuyenMai;
import com.example.website.Enity.KhuyenMaiChiTiet;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.KhuyenMaiChiTietRepo;
import com.example.website.Respository.KhuyenMaiRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {
    private final KhuyenMaiRepo khuyenMaiRepo;
    private final KhuyenMaiChiTietRepo khuyenMaiChiTietRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhuyenMaiControllerRealTime khuyenMaiControllerRealTime;

    @Transactional
    public void updateStatusPromotion(){
        List<KhuyenMai> list = khuyenMaiRepo.findAll();
        for(KhuyenMai khuyenMai: list){
            if (LocalDateTime.now().isAfter(khuyenMai.getNgayBatDau()) && LocalDateTime.now().isBefore(khuyenMai.getNgayKetThuc())) {
                khuyenMai.setTinhTrang("Đang diễn ra");
            } else if (LocalDateTime.now().isBefore(khuyenMai.getNgayBatDau())) {
                khuyenMai.setTinhTrang("Chưa diễn ra");
            } else {
                khuyenMai.setTinhTrang("Đã kết thúc");
                List<KhuyenMaiChiTiet> khuyenMaiChiTiets = khuyenMaiChiTietRepo.findByKhuyenMai(khuyenMai);
                for(KhuyenMaiChiTiet khuyenMaiChiTiet : khuyenMaiChiTiets){
                    SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findByKhuyenMaiChiTiet(khuyenMaiChiTiet);
                    if(sanPhamChiTiet != null){
                        sanPhamChiTiet.setKhuyenMaiChiTiet(null);
                        sanPhamChiTietRepo.save(sanPhamChiTiet);
                    }
                }
            }
            khuyenMaiRepo.save(khuyenMai);
            khuyenMaiControllerRealTime.sendPromotionUpdate(khuyenMai);
        }
    }
}
