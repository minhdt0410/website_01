package com.example.website.Respository;

import com.example.website.Enity.GioHang;
import com.example.website.Enity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GioHangRepo extends JpaRepository<GioHang, Integer> {
    List<GioHang> findByKhachHang(KhachHang khachHang);
}
