package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "ten_khuyen_mai")
    private String tenKhuyenMai;
    @Column(name = "gia_tri_giam")
    private Integer giaTriGiam;
    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;
    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;
    @Column(name = "tinh_trang")
    private String tinhTrang;
    @Column(name = "loai_khuyen_mai")
    private String loaiKhuyenMai;
    @Column(name = "trang_thai")
    private boolean trangThai;
}
