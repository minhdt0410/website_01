package com.example.website.Enity;

import jakarta.persistence.Entity;
import lombok.*;
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class ThongKe {
    private int month;
    private double totalSales;
    public ThongKe(int month, double totalSales) {
        this.month = month;
        this.totalSales = totalSales;
    }
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }
}
