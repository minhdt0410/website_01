package com.example.website.Enity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Builder
@Table(name = "size")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ma_size")
    private String ma_size;

    @Column(name="ten_size")
    private String ten_size;

    @Column(name="trang_thai")
    private Integer trangthai;
}
