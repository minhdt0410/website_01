package com.example.website.Response;

import com.example.website.Enity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuocTiinhSPResponse {
    private List<MauSac> mauSacs;
    private List<Size> sizes;
    private List<ChatLieu> chatLieus;
    private List<LoaiDe> loaiDes;
    private List<ThuongHieu> thuongHieus;
    private String tenSP;
    private int minPrice;
    private int maxPrice;
}
