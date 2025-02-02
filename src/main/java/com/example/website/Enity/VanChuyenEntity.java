package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "vanchuyen")
public class VanChuyenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_donhang")
    DonHang donHang;
    @Column(name = "phuongthucvanchuyen")
    private String phuongthucvanchuyen;

    @Column(name = "ngaygiaohang")
    private Date ngaygiaohang;

    @Column(name = "trangthai")
    private Integer trangthai;
}
