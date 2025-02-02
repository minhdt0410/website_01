package com.example.website.Enity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "san_pham_khuyen_mai")
public class KhuyenMaiChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai")
    private KhuyenMai khuyenMai;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_chi_tiet_san_pham")
    private SanPhamChiTiet sanPhamChiTiet;
    private int giaMoi;

}
