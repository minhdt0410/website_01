package com.example.website.Respository;

import com.example.website.Enity.MauSac;
import com.example.website.Enity.SanPham;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Enity.Size;
import org.springframework.data.jpa.repository.Query;
import com.example.website.Enity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepo extends JpaRepository<SanPhamChiTiet,Integer> {
    List<SanPhamChiTiet> findBySanPham(SanPham sanPham);
    SanPhamChiTiet findBySanPhamAndMauSacAndSize(SanPham sanPham, MauSac mauSac, Size size);
    SanPhamChiTiet findByKhuyenMaiChiTiet(KhuyenMaiChiTiet khuyenMaiChiTiet);
    Optional<SanPhamChiTiet> findBySanPhamAndSize_Id(SanPham sanPham, Integer sizeId);
    @Query("SELECT sp FROM SanPhamChiTiet sp WHERE sp.sanPham.ma_sanpham = :maSanPham")
    Optional<SanPhamChiTiet> findBySanPham_MaSanPham(@Param("maSanPham") String maSanPham);

}
