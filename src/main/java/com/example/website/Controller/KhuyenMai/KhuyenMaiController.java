package com.example.website.Controller.KhuyenMai;


import com.example.website.Enity.KhuyenMai;
import com.example.website.Enity.KhuyenMaiChiTiet;
import com.example.website.Enity.SanPham;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Response.KhuyenMailResponse;
import com.example.website.Respository.KhuyenMaiChiTietRepo;
import com.example.website.Respository.KhuyenMaiRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import com.example.website.Respository.SanPhamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class KhuyenMaiController {
    private final KhuyenMaiRepo khuyenMaiRepo;
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhuyenMaiChiTietRepo khuyenMaiChiTietRepo;
    @GetMapping("/admin/khuyenmai")
    public String getAdmin(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<KhuyenMailResponse> khuyenMailResponses = new ArrayList<>();
        List<KhuyenMai> khuyenMais = khuyenMaiRepo.findAll();
        for(KhuyenMai khuyenMai : khuyenMais){
            KhuyenMailResponse khuyenMailResponse = new KhuyenMailResponse();
            BeanUtils.copyProperties(khuyenMai,khuyenMailResponse);
            if (khuyenMai.getNgayTao() != null) {
                khuyenMailResponse.setNgayTao(khuyenMai.getNgayTao().format(formatter));
            } else {
                khuyenMailResponse.setNgayTao("Ngày tạo không xác định");
            }
            khuyenMailResponse.setNgayBatDau(khuyenMai.getNgayBatDau().format(formatter));
            khuyenMailResponse.setNgayKetThuc(khuyenMai.getNgayKetThuc().format(formatter));
            if(khuyenMai.getNgaySua() != null){
                khuyenMailResponse.setNgaySua(khuyenMai.getNgaySua().format(formatter));
            }
            khuyenMailResponses.add(khuyenMailResponse);
        }
        model.addAttribute("khuyenMaiList", khuyenMailResponses);
        return "src/khuyenmai/KhuyenMai";
    }
    @GetMapping("/khuyenmai/addkhuyenmai")
    public String addKhuyenMai(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai());
        model.addAttribute("selectedProducts", new ArrayList<String>()); // Thêm dòng này
        List<SanPham> list = sanPhamRepo.findAll();
        List<SanPham> products = new ArrayList<>();
        for(SanPham product: list){
            List<SanPhamChiTiet> productDetails = sanPhamChiTietRepo.findBySanPham(product);
            for(SanPhamChiTiet productDetail : productDetails){
                if(productDetail.getKhuyenMaiChiTiet() == null){
                    products.add(product);
                    break;
                }
            }
        }
        model.addAttribute("products", products);
        System.out.println(products);
        return "src/khuyenmai/AddKhuyenMai";
    }
    @PostMapping("/khuyenmai/addkhuyenmai/save")
    public String saveKM(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai, @RequestParam(name = "selectedProducts", required = false) List<Integer> selectedProductIds){
        System.out.println(khuyenMai);
        khuyenMai.setNgayTao(LocalDateTime.now());
        khuyenMai.setTinhTrang("Sắp diễn ra");
        khuyenMai.setTrangThai(true);
        KhuyenMai saveKhuyenMai = khuyenMaiRepo.save(khuyenMai);

        for (Integer i : selectedProductIds){
            SanPhamChiTiet productDetail = sanPhamChiTietRepo.findById(i)
                    .orElseThrow(() -> new RuntimeException("productDetail not found"));
            KhuyenMaiChiTiet promotionDetail = getPromotionDetail(khuyenMai, saveKhuyenMai, productDetail);
            KhuyenMaiChiTiet savePromotionDetail = khuyenMaiChiTietRepo.save(promotionDetail);
            productDetail.setKhuyenMaiChiTiet(savePromotionDetail);
            sanPhamChiTietRepo.save(productDetail);
        }
        return "redirect:/admin/khuyenmai";
    }

    private static KhuyenMaiChiTiet getPromotionDetail(KhuyenMai promotion, KhuyenMai savePromotion, SanPhamChiTiet productDetail) {
        KhuyenMaiChiTiet promotionDetail = new KhuyenMaiChiTiet();
        promotionDetail.setKhuyenMai(savePromotion);
        promotionDetail.setSanPhamChiTiet(productDetail);
        if(promotion.getGiaTriGiam() <= 100){
            int price = (int) (productDetail.getGia_ban() - (productDetail.getGia_ban() * ((double) promotion.getGiaTriGiam() /100)));
            promotionDetail.setGiaMoi(price);
        }else {
            promotionDetail.setGiaMoi((int) (productDetail.getGia_ban() - promotion.getGiaTriGiam()));
        }
        return promotionDetail;
    }
    //    @GetMapping("/khuyenmai/delete/{id}")
//    public String deleteKM(@PathVariable Integer id){
//        KhuyenMai khuyenMai = khuyenMaiRepo.getReferenceById(id);
//        khuyenMai.setTrangThai(!khuyenMai.isTrangThai());
//        khuyenMaiRepo.save(khuyenMai);
//        return "redirect:/admin/khuyenmai";
//    }
    @GetMapping("/khuyenmai/updatePage")
    public String updatePage(Model model, @RequestParam Integer id){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        KhuyenMai khuyenMai = khuyenMaiRepo.getReferenceById(id);
        KhuyenMailResponse khuyenMailResponse = new KhuyenMailResponse();
        BeanUtils.copyProperties(khuyenMai, khuyenMailResponse);
        khuyenMailResponse.setNgayBatDau(khuyenMai.getNgayBatDau().format(formatter));
        khuyenMailResponse.setNgayKetThuc(khuyenMai.getNgayKetThuc().format(formatter));
        model.addAttribute("KM", khuyenMailResponse);

        List<KhuyenMaiChiTiet> promotionDetails = khuyenMaiChiTietRepo.findByKhuyenMai(khuyenMai);
        List<SanPhamChiTiet> promotionDetailOfPromotion = new ArrayList<>();
        for(KhuyenMaiChiTiet promotionDetail: promotionDetails){
            promotionDetailOfPromotion.add(promotionDetail.getSanPhamChiTiet());
        }
        List<SanPham> list = sanPhamRepo.findAll();
        List<SanPham> products = new ArrayList<>();
        for(SanPham product: list){
            List<SanPhamChiTiet> productDetails = sanPhamChiTietRepo.findBySanPham(product);
            for(SanPhamChiTiet productDetail : productDetails){
                if(productDetail.getKhuyenMaiChiTiet() == null || productDetail.getKhuyenMaiChiTiet().getKhuyenMai().equals(khuyenMai)){
                    products.add(product);
                    break;
                }
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("productDetails", promotionDetailOfPromotion);
        return "src/khuyenmai/UpdateKhuyenMai";
    }
    @PostMapping("/khuyenmai/update")
    public String updateKM(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai, @RequestParam(name = "selectedProducts", required = false) List<Integer> selectedProductIds){
        KhuyenMai newKM = khuyenMaiRepo.getReferenceById(khuyenMai.getId());
        newKM.setNgaySua(LocalDateTime.now());
        newKM.setTenKhuyenMai(khuyenMai.getTenKhuyenMai());
        newKM.setGiaTriGiam(khuyenMai.getGiaTriGiam());
        newKM.setNgayKetThuc(khuyenMai.getNgayKetThuc());
        newKM.setNgayBatDau(khuyenMai.getNgayBatDau());
        newKM.setLoaiKhuyenMai(khuyenMai.getLoaiKhuyenMai());

        List<SanPhamChiTiet> oldProductDetails = new ArrayList<>();
        for(KhuyenMaiChiTiet promotionDetail : khuyenMaiChiTietRepo.findByKhuyenMai(newKM)){
            oldProductDetails.add(promotionDetail.getSanPhamChiTiet());
        }

        List<SanPhamChiTiet> newProductDetails = new ArrayList<>();
        for(Integer i : selectedProductIds){
            SanPhamChiTiet productDetail = sanPhamChiTietRepo.findById(i)
                    .orElseThrow(() -> new RuntimeException("Data not found"));
            newProductDetails.add(productDetail);
        }
        List<SanPhamChiTiet> newsProductDetails = new ArrayList<>(newProductDetails);
        newProductDetails.removeAll(oldProductDetails);
        oldProductDetails.removeAll(newsProductDetails);

        for(SanPhamChiTiet productDetail : newProductDetails){
            KhuyenMaiChiTiet promotionDetail = new KhuyenMaiChiTiet();
            promotionDetail.setSanPhamChiTiet(productDetail);
            promotionDetail.setKhuyenMai(newKM);
            KhuyenMaiChiTiet savePromotionDetail = khuyenMaiChiTietRepo.save(promotionDetail);
            productDetail.setKhuyenMaiChiTiet(savePromotionDetail);
            if(newKM.getGiaTriGiam() <= 100){
                int price = (int) (productDetail.getGia_ban() - (productDetail.getGia_ban() * ((double) khuyenMai.getGiaTriGiam() /100)));
                promotionDetail.setGiaMoi(price);
            }else {
                promotionDetail.setGiaMoi((int) (productDetail.getGia_ban() - khuyenMai.getGiaTriGiam()));
            }
            khuyenMaiChiTietRepo.save(promotionDetail);
        }
        for(KhuyenMaiChiTiet promotionDetail : khuyenMaiChiTietRepo.findByKhuyenMai(newKM)){
            for(SanPhamChiTiet productDetail : oldProductDetails){
                if(promotionDetail.getSanPhamChiTiet().getId().equals(productDetail.getId())){
                    productDetail.setKhuyenMaiChiTiet(null);
                    sanPhamChiTietRepo.save(productDetail);
                    khuyenMaiChiTietRepo.delete(promotionDetail);
                }
            }
        }

        khuyenMaiRepo.save(newKM);
        return "redirect:/admin/khuyenmai";
    }
}
