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
@Table(name = "loai_de")
public class LoaiDe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ma_loai_de")
    private String ma_loaide;

    @Column(name="ten_loai_de")
    private String tenloaide;

    @Column(name="trang_thai")
    private Integer trangthai;
}
