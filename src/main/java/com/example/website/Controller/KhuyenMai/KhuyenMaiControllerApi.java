package com.example.website.Controller.KhuyenMai;

import com.example.website.Enity.KhuyenMai;
import com.example.website.Enity.KhuyenMaiChiTiet;
import com.example.website.Enity.SanPham;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.KhuyenMaiChiTietRepo;
import com.example.website.Respository.KhuyenMaiRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import com.example.website.Respository.SanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class KhuyenMaiControllerApi {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhuyenMaiRepo khuyenMaiRepo;
    private final KhuyenMaiChiTietRepo khuyenMaiChiTietRepo;

    @GetMapping("/khuyenmai")
    public List<KhuyenMai> getAllKM(){
        return khuyenMaiRepo.findAll();
    }

    @GetMapping("/khuyenmai/sanphamchitiet/{idSanPham}")
    public List<SanPhamChiTiet> getProductDetailsToAdd(@PathVariable int idSanPham) {
        SanPham product = sanPhamRepo.findById(idSanPham)
                .orElseThrow(()->new RuntimeException("Product not found"));
        return sanPhamChiTietRepo.findBySanPham(product).stream()
                .filter(productDetail -> productDetail.getKhuyenMaiChiTiet() == null)
                .toList();
    }

    @GetMapping("/khuyenmai/checkPrice/{id}")
    public String checkPrice(@PathVariable Integer id, @RequestParam Integer price){
        SanPhamChiTiet productDetail = sanPhamChiTietRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));
        if(price >= productDetail.getGia_ban()){
            return productDetail.getSanPham().getTensanpham();
        }
        return "";
    }

    @GetMapping("/khuyenmai/promotionDetails/productDetail/{idPromotion}")
    public List<SanPhamChiTiet> getProductDetailsByPromotionDetails(
            @PathVariable Integer idPromotion
    ){
        KhuyenMai promotion = khuyenMaiRepo.findById(idPromotion)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        List<KhuyenMaiChiTiet> list = khuyenMaiChiTietRepo.findByKhuyenMai(promotion);
        List<SanPhamChiTiet> productDetails = new ArrayList<>();
        for(KhuyenMaiChiTiet p : list){
            productDetails.add(p.getSanPhamChiTiet());
        }
        return productDetails;
    }

    @GetMapping("/khuyenmai/getPromotion/{id}")
    public String getPromotion(@PathVariable Integer id){

        KhuyenMai promotion = khuyenMaiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found"));
        if(promotion.getNgayKetThuc().isBefore(LocalDateTime.now())){
            return "fail1";
        }else if(LocalDateTime.now().isAfter(promotion.getNgayBatDau()) && LocalDateTime.now().isBefore(promotion.getNgayKetThuc())){
            return "fail2";
        }
        return "notFail";
    }

    @GetMapping("/khuyenmai/{id}/{idPromotion}")
    public List<SanPhamChiTiet> getProductDetails(@PathVariable int id, @PathVariable Integer idPromotion) {
        SanPham product = sanPhamRepo.findById(id).orElseThrow(()->new RuntimeException(""));
        List<SanPhamChiTiet> productDetails = new ArrayList<>();
        KhuyenMai promotion = khuyenMaiRepo.findById(idPromotion)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        List<KhuyenMaiChiTiet> productDetailList = khuyenMaiChiTietRepo.findByKhuyenMai(promotion).stream()
                .filter(promotionDetail -> promotionDetail.getSanPhamChiTiet().getSanPham().getId().equals(product.getId()))
                .toList();

        List<SanPhamChiTiet> productDetailsOfPromotion = new ArrayList<>();
        for(KhuyenMaiChiTiet promotionDetail : productDetailList){
            productDetailsOfPromotion.add(promotionDetail.getSanPhamChiTiet());
        }

        for(SanPhamChiTiet productDetail: productDetailsOfPromotion){
            if(productDetail.getSanPham().getId().equals(product.getId())){
                productDetails.add(productDetail);
            }
        }

        for(SanPhamChiTiet productDetail: sanPhamChiTietRepo.findBySanPham(product)){
            if(productDetail.getKhuyenMaiChiTiet() == null){
                productDetails.add(productDetail);
            }
        }

        return productDetails;
    }

    @PutMapping("/khuyenmai/delete/{id}")
    public KhuyenMai khuyenMai(@PathVariable Integer id){
        KhuyenMai khuyenMai = khuyenMaiRepo.getReferenceById(id);
        if(khuyenMai.isTrangThai()){
            khuyenMai.setTrangThai(false);
            List<KhuyenMaiChiTiet> khuyenMaiChiTiets = khuyenMaiChiTietRepo.findByKhuyenMai(khuyenMai);
            for(KhuyenMaiChiTiet khuyenMaiChiTiet : khuyenMaiChiTiets){
                SanPhamChiTiet sanPhamChiTiet = khuyenMaiChiTiet.getSanPhamChiTiet();
                sanPhamChiTiet.setKhuyenMaiChiTiet(null);
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
        }
        return khuyenMaiRepo.save(khuyenMai);
    }
}
