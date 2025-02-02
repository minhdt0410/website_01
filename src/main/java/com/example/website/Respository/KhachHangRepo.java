package com.example.website.Respository;

import com.example.website.Enity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepo extends JpaRepository<KhachHang, Integer> {
    KhachHang findByEmail(String email);
}
