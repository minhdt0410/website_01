package com.example.website.Respository;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.KhachHang;
import com.example.website.Enity.ThongKe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepo extends JpaRepository<HoaDon, Integer> {
    // Thêm phương thức sắp xếp theo ngày đặt hàng giảm dần
    @Query("SELECT h FROM HoaDon h ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findAllOrderByNgayDatHangDesc();

    @Query("SELECT h FROM HoaDon h WHERE h.trangThai = :trangThai ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findByTrangThaiOrderByNgayDatHangDesc(@Param("trangThai") String trangThai);
    List<HoaDon> findByTrangThai(String trangThai);
    long countByTrangThai(String trangThai);
    List<HoaDon> findByKhachHang(KhachHang khachHang);

    @Query("SELECT h FROM HoaDon h WHERE h.tenKhachHang = :tenKhachHang ORDER BY h.ngayDatHang DESC")
    List<HoaDon> findAllSaleOffice(@Param("tenKhachHang") String tenKhachHang);

    @Query("SELECT h FROM HoaDon h WHERE h.id = :idDonHang")
    HoaDon findDetailSaleOffice(@Param("idDonHang") int idDonHang);

    @Query(value = "SELECT MONTH(ngay_dat_hang) AS month, SUM(tong_tien) AS totalSales "
            + "FROM don_hang "
            + "WHERE YEAR(ngay_dat_hang) = :year "
            + "GROUP BY MONTH(ngay_dat_hang) "
            + "ORDER BY MONTH(ngay_dat_hang) ASC",
            nativeQuery = true)
    List<Object[]> findSalesByYear(@Param("year") int year);
    @Query("SELECT SUM(dh.tongTien) FROM HoaDon dh WHERE YEAR(dh.ngayDatHang) = :year AND MONTH(dh.ngayDatHang) = :month")
    double tinhTongTien(Integer year, Integer month);

    @Query("SELECT MONTH(d.ngayDatHang) as month, " +
            "SUM(d.tongTien) as totalAmount, " +
            "SUM(d.soLuong) as totalQuantity " +
            "FROM HoaDon d " +
            "WHERE (:year IS NULL OR YEAR(d.ngayDatHang) = :year) " +
            "AND (:month IS NULL OR MONTH(d.ngayDatHang) = :month) " +
            "GROUP BY MONTH(d.ngayDatHang) " +
            "ORDER BY MONTH(d.ngayDatHang)")
    List<Object[]> findMonthlyStatistics(@Param("year") Integer year, @Param("month") Integer month);


}
