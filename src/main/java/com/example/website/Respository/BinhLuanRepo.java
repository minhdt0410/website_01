package com.example.website.Respository;

import com.example.website.Enity.BinhLuan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BinhLuanRepo extends JpaRepository<BinhLuan, Integer> {
    Page<BinhLuan> findByDanhGia(Integer danhGia, Pageable pageable);
    Page<BinhLuan> findByNgayBinhLuanBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<BinhLuan> findByDanhGiaAndNgayBinhLuanBetween(Integer danhGia, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
