package com.example.website.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KhuyenMaiServiceRealtime {
    private final KhuyenMaiService khuyenMaiService;

    @Scheduled(cron = "*/1 * * * * ?")
    public void updatePromotionStatus(){
        khuyenMaiService.updateStatusPromotion();
    }
}
