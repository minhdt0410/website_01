package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Response.HoaDonResponse;
import com.example.website.Response.SanPhamChiTietResponse;
import com.example.website.Response.ThuocTiinhSPResponse;
import com.example.website.Response.TopSPResponse;
import com.example.website.Respository.*;
import com.example.website.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.website.Controller.WebControllerMap.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/web")
public class WebControllerApi {
    private final SanPhamRepo sanPhamRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final MauSacRepo mauSacRepo;
    private final SizeRepo sizeRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final LoaiDeRepo loaiDeRepo;
    private final ThuongHieuRepo thuongHieuRepo;
    private final KhachHangRepo khachHangRepo;
    private final AdminRepository adminRepository;
    private final HoaDonRepo hoaDonRepo;
    private final UserService userService;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("/top")
    public List<TopSPResponse> getTopSP(@RequestParam Integer quantity){
        List<TopSPResponse> topSPResponses = new ArrayList<>();
        List<SanPham> sanPhams = sanPhamRepo.findAll();
        for(SanPham sanPham : sanPhams){
            List<SanPhamChiTiet> sanPhamChiTiets = getSpctHoatDong(sanPhamChiTietRepo.findBySanPham(sanPham));
            if(!sanPhamChiTiets.isEmpty()){
                topSPResponses.add(getSpctBySP(sanPham, sanPhamChiTiets));
            }
        }
        return topSPResponses.stream().sorted(Comparator.comparing(TopSPResponse::getId).reversed())
                .limit(quantity)
                .toList();
    }

    @GetMapping("/topSPBanChay")
    public List<TopSPResponse> getSPBanChay(){
        List<TopSPResponse> topSPResponses = new ArrayList<>();
        List<Integer> integers = sanPhamRepo.listSPBanChay();
        for(Integer integer : integers){
            SanPham sanPham = sanPhamRepo.getReferenceById(integer);
            if(sanPham.getTrangthai()){
                List<SanPhamChiTiet> sanPhamChiTiets = getSpctHoatDong(sanPhamChiTietRepo.findBySanPham(sanPham));
                if(!sanPhamChiTiets.isEmpty()){
                    topSPResponses.add(getSpctBySP(sanPham, sanPhamChiTiets));
                }
            }
        }
        return topSPResponses;
    }

    @GetMapping("/quickView/{idSP}")
    public TopSPResponse quickView(@PathVariable Integer idSP){
        SanPham sanPham = sanPhamRepo.getReferenceById(idSP);
        TopSPResponse topSPResponse = getSpctBySP(sanPham, sanPhamChiTietRepo.findBySanPham(sanPham));
        List<SanPhamChiTiet> sanPhamChiTiets = topSPResponse.getSanPhamChiTiets();
        sanPhamChiTiets = getSpctHoatDong(sanPhamChiTiets).stream()
                .sorted(Comparator.comparing(sanPhamChiTiet -> sanPhamChiTiet.getSize().getTen_size(), Comparator.reverseOrder()))
                .toList();
        topSPResponse.setSanPhamChiTiets(sanPhamChiTiets);
        return topSPResponse;
    }

    @GetMapping("/listThocTinhSP")
    public ThuocTiinhSPResponse getAllThuocTinhSP(){
        List<MauSac> mauSacs = sanPhamChiTietRepo.findAll().stream()
                .map(SanPhamChiTiet::getMauSac)
                .distinct()
                .toList();
        List<Size> sizes = sanPhamChiTietRepo.findAll().stream()
                .map(SanPhamChiTiet::getSize)
                .distinct()
                .sorted(Comparator.comparing(Size::getTen_size).reversed())
                .toList();
        List<ChatLieu> chatLieus = sanPhamRepo.findAll().stream()
                .map(SanPham::getChatLieu)
                .distinct()
                .toList();
        List<LoaiDe> loaiDes = sanPhamRepo.findAll().stream()
                .map(SanPham::getLoaiDe)
                .distinct()
                .toList();
        List<ThuongHieu> thuongHieus = sanPhamRepo.findAll().stream()
                .map(SanPham::getThuongHieu)
                .distinct()
                .toList();

        ThuocTiinhSPResponse thuocTiinhSPResponse = new ThuocTiinhSPResponse();
        thuocTiinhSPResponse.setMauSacs(mauSacs);
        thuocTiinhSPResponse.setSizes(sizes);
        thuocTiinhSPResponse.setChatLieus(chatLieus);
        thuocTiinhSPResponse.setLoaiDes(loaiDes);
        thuocTiinhSPResponse.setThuongHieus(thuongHieus);
        return thuocTiinhSPResponse;
    }


    @PostMapping("/allSP")
    public List<TopSPResponse> getAllSP(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int sizePage,
            @RequestBody ThuocTiinhSPResponse thuocTiinhSPResponse
    ){
        System.out.println(thuocTiinhSPResponse.getMaxPrice());
        List<TopSPResponse> topSPResponses = new ArrayList<>();
        List<SanPham> sanPhams = sanPhamRepo.findAll();
        for(SanPham sanPham : sanPhams){
            if(sanPham.getTrangthai()){
                List<SanPhamChiTiet> sanPhamChiTiets = getSpctHoatDong(sanPhamChiTietRepo.findBySanPham(sanPham));
                if(!sanPhamChiTiets.isEmpty()){
                    topSPResponses.add(getSpctBySP(sanPham, sanPhamChiTiets));
                }
            }
        }

        if (!thuocTiinhSPResponse.getMauSacs().isEmpty()) {
            List<MauSac> mauSacs = new ArrayList<>();
            for(MauSac mauSac : thuocTiinhSPResponse.getMauSacs()){
                mauSacs.add(mauSacRepo.getReferenceById(mauSac.getId()));
            }
            thuocTiinhSPResponse.setMauSacs(mauSacs);

            topSPResponses = topSPResponses.stream()
                    .filter(topSPResponse -> topSPResponse.getSanPhamChiTiets().stream()
                            .anyMatch(productDetail -> thuocTiinhSPResponse.getMauSacs().contains(productDetail.getMauSac())))
                    .collect(Collectors.toList());
        }
        if (!thuocTiinhSPResponse.getSizes().isEmpty()) {
            List<Size> sizes = new ArrayList<>();
            for(Size size : thuocTiinhSPResponse.getSizes()){
                sizes.add(sizeRepo.getReferenceById(size.getId()));
            }
            thuocTiinhSPResponse.setSizes(sizes);

            topSPResponses = topSPResponses.stream()
                    .filter(topSPResponse -> topSPResponse.getSanPhamChiTiets().stream()
                            .anyMatch(productDetail -> thuocTiinhSPResponse.getSizes().contains(productDetail.getSize())))
                    .collect(Collectors.toList());
        }
        if (!thuocTiinhSPResponse.getChatLieus().isEmpty()) {
            List<ChatLieu> chatLieus = new ArrayList<>();
            for(ChatLieu chatLieu : thuocTiinhSPResponse.getChatLieus()){
                chatLieus.add(chatLieuRepo.getReferenceById(chatLieu.getId()));
            }
            thuocTiinhSPResponse.setChatLieus(chatLieus);

            topSPResponses = topSPResponses.stream()
                    .filter(topSPResponse -> topSPResponse.getSanPhamChiTiets().stream()
                            .anyMatch(productDetail -> thuocTiinhSPResponse.getChatLieus().contains(productDetail.getSanPham().getChatLieu())))
                    .collect(Collectors.toList());
        }
        if (!thuocTiinhSPResponse.getLoaiDes().isEmpty()) {
            List<LoaiDe> loaiDes = new ArrayList<>();
            for(LoaiDe loaiDe : thuocTiinhSPResponse.getLoaiDes()){
                loaiDes.add(loaiDeRepo.getReferenceById(loaiDe.getId()));
            }
            thuocTiinhSPResponse.setLoaiDes(loaiDes);

            topSPResponses = topSPResponses.stream()
                    .filter(topSPResponse -> topSPResponse.getSanPhamChiTiets().stream()
                            .anyMatch(productDetail -> thuocTiinhSPResponse.getLoaiDes().contains(productDetail.getSanPham().getLoaiDe())))
                    .collect(Collectors.toList());
        }
        if (!thuocTiinhSPResponse.getThuongHieus().isEmpty()) {
            List<ThuongHieu> thuongHieus = new ArrayList<>();
            for(ThuongHieu thuongHieu : thuocTiinhSPResponse.getThuongHieus()){
                thuongHieus.add(thuongHieuRepo.getReferenceById(thuongHieu.getId()));
            }
            thuocTiinhSPResponse.setThuongHieus(thuongHieus);

            topSPResponses = topSPResponses.stream()
                    .filter(topSPResponse -> topSPResponse.getSanPhamChiTiets().stream()
                            .anyMatch(productDetail -> thuocTiinhSPResponse.getThuongHieus().contains(productDetail.getSanPham().getThuongHieu())))
                    .collect(Collectors.toList());
        }

        topSPResponses = topSPResponses.stream()
                .filter(topSPResponse -> topSPResponse.getGiaBan() > thuocTiinhSPResponse.getMinPrice())
                .toList();
        topSPResponses = topSPResponses.stream()
                .filter(topSPResponse -> topSPResponse.getGiaBan() < thuocTiinhSPResponse.getMaxPrice())
                .toList();
        topSPResponses = topSPResponses.stream()
                .filter(topSPResponse -> topSPResponse.getTenSP().toLowerCase().contains(thuocTiinhSPResponse.getTenSP().toLowerCase()))
                .toList();
        int start = page * sizePage;
        int end = Math.min(start + sizePage, topSPResponses.size());

        return topSPResponses.subList(start, end);
    }

    @GetMapping("/loadPriceForProduct/{idSP}")
    public SanPhamChiTietResponse loadPrice(
            @RequestParam int idColor,
            @RequestParam int idSize,
            @PathVariable Integer idSP
    ){
        SanPham sanPham = sanPhamRepo.getReferenceById(idSP);
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepo.findBySanPham(sanPham).stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getSize().getId() == idSize && sanPhamChiTiet.getMauSac().getId() == idColor)
                .toList();

        SanPhamChiTietResponse sanPhamChiTietResponse = new SanPhamChiTietResponse();
        sanPhamChiTietResponse.setId(sanPhamChiTiets.get(0).getId());
        sanPhamChiTietResponse.setKho(sanPhamChiTiets.get(0).getSo_luong());
        sanPhamChiTietResponse.setGiaCu(sanPhamChiTiets.get(0).getGia_ban().intValue());
        if(sanPhamChiTiets.get(0).getKhuyenMaiChiTiet() != null){
            KhuyenMaiChiTiet khuyenMaiChiTiet = sanPhamChiTiets.get(0).getKhuyenMaiChiTiet();
            if(khuyenMaiChiTiet.getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                sanPhamChiTietResponse.setGiaMoi(khuyenMaiChiTiet.getGiaMoi());
            }
        }
        return sanPhamChiTietResponse;
    }

    @GetMapping("/checkTT/{idSP}")
    public List<Integer> reloadTT(
            @RequestParam(defaultValue = "0") Integer idColor,
            @RequestParam(defaultValue = "0") Integer idSize,
            @PathVariable Integer idSP
    ){
        SanPham sanPham = sanPhamRepo.getReferenceById(idSP);
        List<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepo.findBySanPham(sanPham);
        if(idColor == 0){
            return sanPhamChiTiets.stream()
                    .filter(sanPhamChiTiet -> sanPhamChiTiet.getSize().getId().equals(idSize))
                    .map(sanPhamChiTiet -> sanPhamChiTiet.getMauSac().getId())
                    .toList();
        }else {
            return sanPhamChiTiets.stream()
                    .filter(sanPhamChiTiet -> sanPhamChiTiet.getMauSac().getId().equals(idColor))
                    .map(sanPhamChiTiet -> sanPhamChiTiet.getSize().getId())
                    .toList();
        }
    }

    public static TopSPResponse getSpctBySP(SanPham sanPham, List<SanPhamChiTiet> sanPhamChiTiets){
        TopSPResponse topSPResponse = new TopSPResponse();
        topSPResponse.setId(sanPham.getId());
        topSPResponse.setGiaBan(
                (int) sanPhamChiTiets.stream()
                        .peek(sanPhamChiTiet -> {
                            if (sanPhamChiTiet.getKhuyenMaiChiTiet() != null && sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                                sanPhamChiTiet.setGia_ban((double) sanPhamChiTiet.getKhuyenMaiChiTiet().getGiaMoi());
                            }
                        })
                        .mapToDouble(SanPhamChiTiet::getGia_ban)
                        .min()
                        .orElse(0.0));
        topSPResponse.setTenSP(sanPham.getTensanpham());
        topSPResponse.setSanPhamChiTiets(sanPhamChiTiets);
        for(SanPhamChiTiet sanPhamChiTiet : sanPhamChiTiets){
            if(sanPhamChiTiet.getKhuyenMaiChiTiet() != null && sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().isTrangThai() && sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                topSPResponse.setGiamGia(sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().getGiaTriGiam());
            }
        }
        return topSPResponse;
    }

    public static List<SanPhamChiTiet> getSpctHoatDong(List<SanPhamChiTiet> sanPhamChiTiets){
        return sanPhamChiTiets.stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getTrang_thai() && sanPhamChiTiet.getSo_luong() > 0)
                .toList();
    }

    @PostMapping("/editProfile/{idUser}")
    public String editProfile(@RequestBody KhachHang khachHang, @PathVariable Integer idUser){
        KhachHang oldKhachHang = khachHangRepo.getReferenceById(idUser);
        khachHang.setId(idUser);
        khachHang.setTrangThai(oldKhachHang.getTrangThai());
        khachHang.setMatKhau(oldKhachHang.getMatKhau());
        khachHangRepo.save(khachHang);
        return khachHang.getDiaChi() + ", " + khachHang.getXa() + ", " + khachHang.getHuyen() + ", " + khachHang.getThanhPho();
    }

    @PostMapping("/checkRegister")
    public String register(@RequestBody KhachHang khachHang){
        KhachHang existingKhachHang = khachHangRepo.findByEmail(khachHang.getEmail());
        if(existingKhachHang != null){
            return "Tài khoản này đã có người đăng ký";
        }
        khachHang.setTrangThai("Hoạt động");
        khachHang.setDiaChi("");
        khachHang.setXa("");
        khachHang.setHuyen("");
        khachHang.setThanhPho("");

        khachHangRepo.save(khachHang);
        return null;
    }

    @PostMapping("/checkLogin")
    public String login(@RequestBody KhachHang khachHang){
        KhachHang existingKhachHang = khachHangRepo.findByEmail(khachHang.getEmail());
        Ad ad = adminRepository.findByEmail(khachHang.getEmail());
        if(existingKhachHang != null && existingKhachHang.getMatKhau().equals(khachHang.getMatKhau())){
            if(existingKhachHang.getTrangThai().equals("Hoạt động")){
                return null;
            }
            return "Tài khoản của bạn đã bị khóa";
        }
        if(ad != null && ad.getMatKhau().equals(khachHang.getMatKhau())){
            if(ad.getTrangThai() == 1){
                return null;
            }
            return "Tài khoản của bạn đã bị khóa";
        }
        return "Thông tin tài khoản mật khẩu không chính xác";
    }

    @PutMapping("/resetPass/{idUser}")
    public String resetPass(
            @PathVariable Integer idUser,
            @RequestParam String password,
            @RequestParam String oldPassword
    ){
        KhachHang khachHang = khachHangRepo.getReferenceById(idUser);
        if(oldPassword.equals(khachHang.getMatKhau())){
            khachHang.setMatKhau(password);
            khachHangRepo.save(khachHang);
            return null;
        }
        return "Mật khẩu cũ không chính xác";
    }

    @GetMapping("/history/{status}")
    public List<HoaDonResponse> getHistoryBill(@PathVariable String status, Authentication authentication){
        List<HoaDon> hoaDons = hoaDonRepo.findByKhachHang(getCurrentUser(authentication));
        List<HoaDonResponse> hoaDonResponses = new ArrayList<>();
        for(HoaDon hoaDon : hoaDons){
            List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
            hoaDonResponses.add(new HoaDonResponse(hoaDon, hoaDonChiTiets));
        }
        return hoaDonResponses.stream()
                .filter(hoaDonResponse -> hoaDonResponse.getHoaDon().getTrangThai().equals(status))
                .toList();
    }

    private KhachHang getCurrentUser(Authentication authentication){
        KhachHang khachHang = new KhachHang();
        if(authentication != null){
            khachHang = userService.currentKhachHang(authentication);
        }
        return khachHang;
    }
}
