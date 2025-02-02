package com.example.website.Enity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Builder
@Table(name = "thuong_hieu")
public class ThuongHieu {
    @Column(name="id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="ten_thuong_hieu")
    String tenThuongHieu;
    @Column(name="mo_ta")
    String moTa;

}
