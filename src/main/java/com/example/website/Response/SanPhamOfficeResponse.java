package com.example.website.Response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamOfficeResponse {
    private int idchitietsanpham;
    private String tensanpham;
    private BigDecimal gia;
    private String tenmausac;
    private String tensize;
}
