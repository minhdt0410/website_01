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
@Table(name = "chat_lieu")
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ma_chat_lieu")
    private String ma_chatlieu;

    @Column(name="ten_chat_lieu")
    private String tenchatlieu;

    @Column(name="trang_thai")
    private Integer trangthai;
}
