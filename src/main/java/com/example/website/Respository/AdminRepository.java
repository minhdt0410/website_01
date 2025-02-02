package com.example.website.Respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.website.Enity.Ad;

@Repository
public interface AdminRepository extends JpaRepository<Ad, Integer> {
    Ad findByEmail(String email);
}
