package com.example.website.Sdi;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class createHoaDonOfficeSdi {
    private int tongtien;
    private String hinhthucthanhtoan;
    private List<hdct> hdct;
}
