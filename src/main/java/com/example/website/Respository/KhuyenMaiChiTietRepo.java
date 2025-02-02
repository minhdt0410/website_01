package com.example.website.Respository;

import com.example.website.Enity.KhuyenMai;
import com.example.website.Enity.KhuyenMaiChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KhuyenMaiChiTietRepo extends JpaRepository<KhuyenMaiChiTiet, Integer> {
    List<KhuyenMaiChiTiet> findByKhuyenMai(KhuyenMai khuyenMai);
}
