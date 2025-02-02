package com.example.website.Response;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonResponse {
    private HoaDon hoaDon;
    private List<HoaDonChiTiet> hoaDonChiTiets;
}
