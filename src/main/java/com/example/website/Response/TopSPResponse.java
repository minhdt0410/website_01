package com.example.website.Response;

import com.example.website.Enity.SanPhamChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopSPResponse {
    private int id;
    private String tenSP;
    private int giaBan;
    private int giamGia;
    private List<SanPhamChiTiet> sanPhamChiTiets;
}
