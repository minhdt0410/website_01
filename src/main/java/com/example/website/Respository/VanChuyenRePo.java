package com.example.website.Respository;


import com.example.website.Enity.VanChuyenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VanChuyenRePo extends JpaRepository<VanChuyenEntity,Integer> {
}
