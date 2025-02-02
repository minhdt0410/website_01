package com.example.website.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamChiTietResponse {
    private int id;
    private int kho;
    private int giaCu;
    private int giaMoi;
}
