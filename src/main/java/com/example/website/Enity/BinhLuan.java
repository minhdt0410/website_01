package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "binh_luan")
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_chi_tiet_san_pham")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "binh_luan")
    private String binhLuan;

    @Column(name = "danh_gia")
    private Integer danhGia;

    @Column(name = "ngay_binh_luan")
    private LocalDate ngayBinhLuan;
}
