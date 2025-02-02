package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "ho_ten")
    private String hoTen;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    @Column(name = "gioi_tinh")
    private String gioiTinh;
    @Column(name = "email")
    private String email;
    @Column(name = "xa")
    private String xa;
    @Column(name = "huyen")
    private String huyen;
    @Column(name = "thanh_pho")
    private String thanhPho;
    @Column(name = "mat_khau")
    private String matKhau;
    @Column(name = "trang_thai")
    private String trangThai;
    @Column(name = "dia_chi")
    private String diaChi;
}
