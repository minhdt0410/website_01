package com.example.website.Respository;

import com.example.website.Enity.LoaiDe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiDeRepo extends JpaRepository<LoaiDe, Integer> {
}
