package com.example.website.Enity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Builder
@Table(name = "san_pham")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SanPham {
    @Column(name="id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="ma_san_pham")
    String ma_sanpham;
    @Column(name="ten_san_pham")
    String tensanpham;
    @Column(name="trang_thai")
    Boolean trangthai;
    @ManyToOne
    @JoinColumn(name = "id_thuong_hieu")
    ThuongHieu thuongHieu;
    @ManyToOne
    @JoinColumn(name = "id_loai_de")
    LoaiDe loaiDe;
    @ManyToOne
    @JoinColumn(name = "id_chat_lieu")
    ChatLieu chatLieu;
//    @Column(name="gia_ban")
//    Double giaban;
//    String url_anh;
}
