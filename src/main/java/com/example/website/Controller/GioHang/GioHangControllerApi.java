package com.example.website.Controller.GioHang;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import com.example.website.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class GioHangControllerApi {
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final GioHangRepo gioHangRepo;
    private final KhachHangRepo khachHangRepo;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepo mauSacRepo;
    private final SizeRepo sizeRepo;
    private final UserService userService;

    @GetMapping("/cart")
    public String cart() {
        return "src/website/cart";
    }
    @PostMapping("/{idSanPham}/{idMauSac}/{idSize}/{soLuong}")
    public String addToCart(
            @PathVariable Integer idSanPham,
            @PathVariable Integer idMauSac,
            @PathVariable Integer idSize,
            @PathVariable Integer soLuong, Authentication authentication
    ){
        if(authentication == null){
            return "Vui lòng đăng nhập";
        }
        KhachHang khachHang = userService.currentKhachHang(authentication); // sau lấy từ authen rồi thế vào
        GioHang gioHang = new GioHang();
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findBySanPhamAndMauSacAndSize(
                sanPhamRepo.getReferenceById(idSanPham),
                mauSacRepo.getReferenceById(idMauSac),
                sizeRepo.getReferenceById(idSize));
        if(sanPhamChiTiet.getSo_luong() < soLuong){
            return "Sản phẩm không còn đủ hàng";
        }
        gioHang.setKhachHang(khachHang);
        boolean check = false;
        List<GioHang> gioHangs = gioHangRepo.findByKhachHang(khachHang);
        for(GioHang gh : gioHangs){
            if(gh.getSanPhamChiTiet().equals(sanPhamChiTiet)){
                if(gh.getSoLuong() + soLuong > sanPhamChiTiet.getSo_luong()){
                    return "Đã có " + gh.getSoLuong() + " sản phẩm trong giỏ hàng. Bạn chỉ có thể thêm " + (sanPhamChiTiet.getSo_luong() - gh.getSoLuong()) + " sản phẩm vào giỏ hàng";
                }
                gh.setSoLuong(gh.getSoLuong() + soLuong);
                gh.setTongTien((int) (sanPhamChiTiet.getGia_ban() * gh.getSoLuong()));
                gioHangRepo.save(gh);
                check = true;
                break;
            }
        }
        if(check){
            return null;
        }
        gioHang.setSanPhamChiTiet(sanPhamChiTiet);
        gioHang.setSoLuong(soLuong);
        gioHang.setTongTien((int) (sanPhamChiTiet.getGia_ban() * gioHang.getSoLuong()));
        gioHangRepo.save(gioHang);
        return null;
    }

    @PostMapping("/toCheckout/{idSanPham}/{idMauSac}/{idSize}/{soLuong}")
    public List<GioHang> shopNow(
            @PathVariable Integer idSanPham,
            @PathVariable Integer idMauSac,
            @PathVariable Integer idSize,
            @PathVariable Integer soLuong, Authentication authentication
    ){
        KhachHang khachHang = new KhachHang();
        List<GioHang> gioHangs = new ArrayList<>();
        GioHang gioHang = new GioHang();
        if(authentication == null){
            gioHang.setId(-1);
            gioHangs.add(gioHang);
            return gioHangs;
        }else {
            khachHang = userService.currentKhachHang(authentication);
        }
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findBySanPhamAndMauSacAndSize(
                sanPhamRepo.getReferenceById(idSanPham),
                mauSacRepo.getReferenceById(idMauSac),
                sizeRepo.getReferenceById(idSize));
        if(sanPhamChiTiet.getSo_luong() < soLuong){
            gioHang.setId(-2);
            gioHangs.add(gioHang);
            return gioHangs;
        }
        gioHang.setKhachHang(khachHang);
        gioHang.setSanPhamChiTiet(sanPhamChiTiet);
        gioHang.setSoLuong(soLuong);
        getGia_ban(sanPhamChiTiet, gioHang);
        gioHangs.add(gioHang);
        return gioHangs;
    }



    @GetMapping("/cartPage")
    public List<GioHang> cartPage(Authentication authentication){
        try {
            List<GioHang> errorGioHang = new ArrayList<>();
            KhachHang khachHang = userService.currentKhachHang(authentication);
            List<GioHang> gioHangs = gioHangRepo.findByKhachHang(khachHang);
            for(GioHang gioHang : gioHangs){
                SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
                if(gioHang.getSoLuong() > sanPhamChiTiet.getSo_luong()){
                    gioHang.setSoLuong(sanPhamChiTiet.getSo_luong());
                }
                getGia_ban(sanPhamChiTiet,gioHang);
                gioHangRepo.save(gioHang);
                if(gioHang.getSoLuong() <= 0){
                    errorGioHang.add(gioHang);
                }
            }
            gioHangRepo.deleteAll(errorGioHang);
            for(GioHang gioHang : gioHangs){
                if(gioHang.getSanPhamChiTiet().getKhuyenMaiChiTiet() != null && !gioHang.getSanPhamChiTiet().getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                    gioHang.getSanPhamChiTiet().setKhuyenMaiChiTiet(null);
                }
            }
            return gioHangRepo.findByKhachHang(khachHang);
        }catch (Exception e){
            return null;
        }
    }

    @PutMapping("/quantity/{idCart}")
    public GioHang setQuantity(@PathVariable Integer idCart, @RequestParam String status){
        GioHang gioHang = gioHangRepo.getReferenceById(idCart);
        SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
        if(status.equals("cong")){
            gioHang.setSoLuong(gioHang.getSoLuong() + 1);
            getGia_ban(sanPhamChiTiet, gioHang);
        }else {
            if(gioHang.getSoLuong() <= 1){
                return null;
            }
            gioHang.setSoLuong(gioHang.getSoLuong() - 1);
            gioHang.setTongTien((int) (gioHang.getSanPhamChiTiet().getGia_ban() * gioHang.getSoLuong()));
            getGia_ban(sanPhamChiTiet, gioHang);
        }
        sanPhamChiTietRepo.save(sanPhamChiTiet);
        return gioHangRepo.save(gioHang);
    }

    public static void getGia_ban(SanPhamChiTiet sanPhamChiTiet, GioHang gioHang) {
        if(sanPhamChiTiet.getKhuyenMaiChiTiet() == null){
            gioHang.setTongTien((int) (gioHang.getSanPhamChiTiet().getGia_ban() * gioHang.getSoLuong()));
        }else {
            KhuyenMaiChiTiet khuyenMaiChiTiet = sanPhamChiTiet.getKhuyenMaiChiTiet();
            KhuyenMai khuyenMai = khuyenMaiChiTiet.getKhuyenMai();
            if(khuyenMai.getTinhTrang().equals("Đang diễn ra")){
                int giaBan = sanPhamChiTiet.getKhuyenMaiChiTiet().getGiaMoi();
                gioHang.setTongTien((giaBan * gioHang.getSoLuong()));
            }else {
                gioHang.setTongTien((int) (gioHang.getSanPhamChiTiet().getGia_ban() * gioHang.getSoLuong()));
            }
        }
    }

    @GetMapping("/checkQuantity/{idCart}")
    public String checkQuantity(@PathVariable Integer idCart, @RequestParam String status){
        try {
            GioHang gioHang = gioHangRepo.getReferenceById(idCart);
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            gioHang.setSoLuong(gioHang.getSoLuong() + 1);
            if(gioHang.getSoLuong() > sanPhamChiTiet.getSo_luong() && status.equals("cong")){
                return "Số lượng phải nhỏ hơn " + sanPhamChiTiet.getSo_luong();
            }
            return null;
        }catch (Exception e){
            return "error";
        }
    }

    @GetMapping("/checkQuantityCauseChange/{idCart}")
    public GioHang checkQuantity1(@PathVariable Integer idCart, @RequestParam Integer quantity){
        try {
            GioHang gioHang = gioHangRepo.getReferenceById(idCart);
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(quantity < 1){
                gioHang.setId(-2);
                gioHang.setSoLuong(1);
                getGia_ban(sanPhamChiTiet, gioHang);
                return gioHang;
            }
            if(quantity > sanPhamChiTiet.getSo_luong()){
                gioHang.setId(-1);
                gioHang.setSoLuong(sanPhamChiTiet.getSo_luong());
                getGia_ban(sanPhamChiTiet, gioHang);
            } else {
                gioHang.setSoLuong(quantity);
                getGia_ban(sanPhamChiTiet, gioHang);
                gioHangRepo.save(gioHang);
            }
            return gioHang;
        }catch (Exception e){
            return null;
        }
    }
    @GetMapping("/checkQuantityIfMax/{idCart}")
    public GioHang checkQuantityIfMax(@PathVariable Integer idCart){
        try {
            GioHang gioHang = gioHangRepo.getReferenceById(idCart);
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(sanPhamChiTiet.getSo_luong() > 3){
                gioHang.setSoLuong(3);
                getGia_ban(sanPhamChiTiet, gioHang);
                return gioHangRepo.save(gioHang);
            }else {
                gioHang.setSoLuong(sanPhamChiTiet.getSo_luong());
                getGia_ban(sanPhamChiTiet, gioHang);
                GioHang saveGioHang = gioHangRepo.save(gioHang);
                saveGioHang.setId(-1);
                return saveGioHang;
            }
        }catch (Exception e){
            return null;
        }
    }


    @DeleteMapping("/deleteCart/{idCart}")
    public void deleteCart(@PathVariable Integer idCart){
        GioHang gioHang = gioHangRepo.getReferenceById(idCart);
        gioHangRepo.delete(gioHang);
    }

    @PostMapping("/tongTien")
    public Integer tongTien(@RequestBody Map<String, List<Integer>> requestData) {
        List<Integer> idGioHang = requestData.get("idGioHang");
        int tongTien = 0;
        for(Integer i : idGioHang){
            GioHang gioHang = gioHangRepo.getReferenceById(i);
            tongTien += gioHang.getTongTien();
        }
        return tongTien;
    }

}
