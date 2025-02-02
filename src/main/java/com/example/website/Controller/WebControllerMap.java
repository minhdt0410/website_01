package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Response.HoaDonResponse;
import com.example.website.Response.TopSPResponse;
import com.example.website.Respository.*;
import com.example.website.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.website.Controller.WebControllerApi.getSpctBySP;
import static com.example.website.Controller.WebControllerApi.getSpctHoatDong;

@Controller
@RequiredArgsConstructor
public class WebControllerMap {
    private final UserService userService;
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final GioHangRepo gioHangRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final HoaDonRepo hoaDonRepo;
    private final KhachHangRepo khachHangRepo;


    @GetMapping("/home")
    public String homePage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));

        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepo.findAll().stream()
                .filter(SanPhamChiTiet::getTrang_thai)
                .sorted((sp1, sp2) -> Double.compare(sp2.getGia_ban(), sp1.getGia_ban()))
                .limit(10)
                .toList();

        model.addAttribute("listSPVip", sanPhamChiTiets);

        return "/src/Web/index-shoe";
    }
    @GetMapping("/home/shop")
    public String shopPage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        return "/src/Web/collection-left-sidebar";
    }
    @GetMapping("/home/shop/product/{idSP}")
    public String productPage(Authentication authentication, Model model, @PathVariable Integer idSP){
        model.addAttribute("user", getCurrentUser(authentication));
        SanPham sanPham = sanPhamRepo.getReferenceById(idSP);
        List<SanPhamChiTiet> sanPhamChiTiets = getSpctHoatDong(sanPhamChiTietRepo.findBySanPham(sanPham));
        TopSPResponse topSPResponse = getSpctBySP(sanPham, sanPhamChiTiets);
        List<String> urls = topSPResponse.getSanPhamChiTiets().stream()
                .map(SanPhamChiTiet::getAnh_spct)
                .toList();
        model.addAttribute("urls", urls);
        model.addAttribute("id", sanPham.getId());

        List<SanPham> sanPhamTuongTu = sanPhamRepo.findByThuongHieu(sanPham.getThuongHieu());
        sanPhamTuongTu.remove(sanPham);
        List<TopSPResponse> topSPResponses = new ArrayList<>();
        for(SanPham sp : sanPhamTuongTu){
            sanPhamChiTiets = sanPhamChiTietRepo.findBySanPham(sp);
            topSPResponses.add(getSpctBySP(sp,sanPhamChiTiets));
        }

        model.addAttribute("products",topSPResponses);
        return "/src/Web/product";
    }
    @GetMapping("/home/cart")
    public String cartPage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        if(authentication == null){
            return "src/Web/login";
        }
        return "/src/Web/cart";
    }

    @GetMapping("/home/about-us")
    public String aboutUsPage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        return "/src/Web/about-us";
    }

    @GetMapping("/home/cart/checkout")
    public String paymentPage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        if (authentication == null) {
            return "src/Web/login";
        }
        return "/src/Web/checkout";
    }

    @GetMapping("/home/cart/checkoutSuccess")
    public String checkoutSuccessfully(Authentication authentication, Model model
    ){
        model.addAttribute("user", getCurrentUser(authentication));
        return "/src/Web/thank";
    }

    @GetMapping("/home/cart/checkoutError")
    public String checkoutError(Authentication authentication, Model model
    ){
        model.addAttribute("user", getCurrentUser(authentication));
        return "/src/Web/error";
    }

    @GetMapping("/home/cart/checkCheckout")
    public String checkout(Authentication authentication, Model model, @RequestParam String vnp_ResponseCode
    ){
        model.addAttribute("user", getCurrentUser(authentication));
        if(vnp_ResponseCode.equals("00")){
            return "redirect:/home/cart/checkoutSuccess";
        }else {
            return "redirect:/home/cart/checkoutError";
        }
    }

    @GetMapping("/home/map")
    public String loadMap(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        return "/src/Web/thank";
    }

    @GetMapping("/login")
    public String Login(Authentication authentication, Model model) {
        model.addAttribute("user", getCurrentUser(authentication));
        return "src/Web/login";
    }

    @GetMapping("/register")
    public String register(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        return "src/Web/register";
    }

    @GetMapping("/forgotPassword")
    public String forgot(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        return "src/Web/forget-password";
    }

    @GetMapping("/newPassword/{email}")
    public String newPass(Authentication authentication, Model model, @PathVariable String email){
        model.addAttribute("user", getCurrentUser(authentication));
        KhachHang khachHang = khachHangRepo.findByEmail(email);
        System.out.println( khachHang);
        model.addAttribute("id", khachHang.getId());
        return "src/Web/new-password";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        if(authentication == null){
            return "src/Web/login";
        }
        return "src/Web/profile";
    }

    @GetMapping("/history")
    public String historyPage(Authentication authentication, Model model){
        model.addAttribute("user", getCurrentUser(authentication));
        if(authentication == null){
            return "src/Web/login";
        }
        return "src/Web/history";
    }

    private KhachHang getCurrentUser(Authentication authentication){
        KhachHang khachHang = new KhachHang();
        if(authentication != null){
            khachHang = userService.currentKhachHang(authentication);
        }
        return khachHang;
    }

    public static String[] getProvinceDetails(String provinceCode) {
        String url = "https://provinces.open-api.vn/api/p/" + provinceCode;
        return getDetailsFromApi(url);
    }

    public static String[] getDistrictDetails(String districtCode) {
        String url = "https://provinces.open-api.vn/api/d/" + districtCode;
        return getDetailsFromApi(url);
    }

    public static String[] getWardDetails(String wardCode) {
        String url = "https://provinces.open-api.vn/api/w/" + wardCode;
        return getDetailsFromApi(url);
    }

    public static String[] getDetailsFromApi(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> data = response.getBody();


            Integer id = (Integer) data.get("code");
            String idString = id != null ? String.valueOf(id) : "Unknown";

            String name = (String) data.get("name");

            return new String[]{idString, name};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{"Unknown", "Unknown"};
        }
    }
}
