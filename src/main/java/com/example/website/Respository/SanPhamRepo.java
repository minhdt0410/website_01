package com.example.website.Respository;

import com.example.website.Enity.SanPham;
import com.example.website.Enity.ThuongHieu;
import com.example.website.Response.SanPhamOfficeResponse;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SanPhamRepo extends JpaRepository<SanPham,Integer> , SanPhamRepoCustom{
    @Query("SELECT p FROM SanPham p ORDER BY p.id DESC")
    Page<SanPham> findTop10ByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "SELECT TOP 10 \n" +
            "    sp.id\n" +
            "FROM \n" +
            "    hoa_don_chi_tiet hdct\n" +
            "JOIN \n" +
            "    chi_tiet_san_pham cts ON hdct.id_chi_tiet_san_pham = cts.id\n" +
            "JOIN \n" +
            "    san_pham sp ON cts.id_san_pham = sp.id\n" +
            "GROUP BY \n" +
            "    sp.id\n" +
            "ORDER BY \n" +
            "    SUM(hdct.so_luong) DESC", nativeQuery = true)
    List<Integer> listSPBanChay();

    List<SanPham> findByThuongHieu(ThuongHieu thuongHieu);
}
