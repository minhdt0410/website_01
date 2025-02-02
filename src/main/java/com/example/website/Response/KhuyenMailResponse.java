package com.example.website.Response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhuyenMailResponse {
    private int id;
    private String tenKhuyenMai;
    private Integer giaTriGiam;
    private String ngayBatDau;
    private String ngayKetThuc;
    private String ngayTao;
    private String ngaySua;
    private String tinhTrang;
    private String loaiKhuyenMai;
    private boolean trangThai;
}
