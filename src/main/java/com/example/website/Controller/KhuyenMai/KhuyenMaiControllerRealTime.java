package com.example.website.Controller.KhuyenMai;

import com.example.website.Enity.KhuyenMai;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
public class KhuyenMaiControllerRealTime {
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @GetMapping("/api/stream/promotion")
    public SseEmitter streamVouchers() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }
    public void sendPromotionUpdate(KhuyenMai updateKM) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("promotionUpdate")
                        .data(updateKM));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
