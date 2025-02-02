package com.example.website.Response;

import com.example.website.Enity.GioHang;
import com.example.website.Enity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutResponse {
    private List<GioHang> gioHangs;
    private KhachHang khachHang;
    private int ship;
}
