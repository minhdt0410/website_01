package com.example.website.Controller.GioHang;

import com.example.website.Enity.*;
import com.example.website.Response.CheckOutResponse;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.website.Controller.GioHang.GioHangControllerApi.getGia_ban;
import static com.example.website.Controller.WebControllerMap.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckOutControllerApi {
    private final GioHangRepo gioHangRepo;
    private final KhachHangRepo khachHangRepo;
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;

    @PostMapping("/checkSoLuongSP")
    public String checkSoLuong(@RequestBody List<Integer> integers){
        List<GioHang> gioHangs = new ArrayList<>();
        for (Integer i : integers) {
            if(gioHangRepo.existsById(i)){
                gioHangs.add(gioHangRepo.getReferenceById(i));
            }else {
                return "Một số sản phẩm trong giỏ hàng đã được thay đổi. Vui lòng kiểm tra lại";
            }
        }
        for(GioHang gioHang : gioHangs){
            SanPhamChiTiet sanPhamChiTiet = gioHang.getSanPhamChiTiet();
            if(sanPhamChiTiet.getSo_luong() < gioHang.getSoLuong()){
                return "Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ sanPhamChiTiet.getSo_luong() +" . Vui lòng giảm số lượng để mua";
            }
        }
        return null;
    }

    @PostMapping("/getItem")
    public List<GioHang> getItem(@RequestBody List<GioHang> gioHangs){
        List<GioHang> gioHangList = new ArrayList<>();
        for(GioHang gioHang : gioHangs){
            GioHang existingGioHang = gioHangRepo.getReferenceById(gioHang.getId());
            GioHang gh = new GioHang();
            gh.setId(existingGioHang.getId());
            gh.setSoLuong(gioHang.getSoLuong());
            gh.setSanPhamChiTiet(existingGioHang.getSanPhamChiTiet());
            getGia_ban(gh.getSanPhamChiTiet(), gh);
            if(gh.getSanPhamChiTiet().getKhuyenMaiChiTiet() != null && !gh.getSanPhamChiTiet().getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                gh.getSanPhamChiTiet().setKhuyenMaiChiTiet(null);
            }
            gioHangList.add(gh);
        }
        return gioHangList;
    }

    @PostMapping("/successCheckout")
    public String success(
            @RequestBody CheckOutResponse checkOutResponse,
            @RequestParam String payment
    ){
        List<GioHang> gioHangs = checkOutResponse.getGioHangs();
        KhachHang khachHang = checkOutResponse.getKhachHang();
        int ship = checkOutResponse.getShip();
//        List<HoaDon> hoaDons = hoaDonRepo.findAll().stream()
//                .filter(hoaDon -> hoaDon.getTrangThai().equals("Chờ xác nhận") || hoaDon.getTrangThai().equals("Xác nhận"))
//                .toList();
//        for(GioHang gioHang : gioHangs){
//            SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.getReferenceById(gioHang.getSanPhamChiTiet().getId());
//            int soLuong = 0;
//            for (HoaDon hoaDon : hoaDons){
//                List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon).stream()
//                        .filter(hoaDonChiTiet -> hoaDonChiTiet.getSanPhamChiTiet().equals(sanPhamChiTiet))
//                        .toList();
//                if(!hoaDonChiTiets.isEmpty()){
//                    HoaDonChiTiet hoaDonChiTiet = hoaDonChiTiets.get(0);
//                    soLuong += hoaDonChiTiet.getSoLuong();
//                }
//            }
//            int soLuongTonKho = sanPhamChiTiet.getSo_luong() - soLuong;
//            if(soLuongTonKho < gioHang.getSoLuong()){
//                return "Số lượng của giày " + sanPhamChiTiet.getSanPham().getTensanpham() +" chỉ còn "+ soLuongTonKho +" sản phẩm khả dụng . Vui lòng giảm số lượng để mua";
//            }
//        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHangRepo.getReferenceById(khachHang.getId()));
        hoaDon.setDiaChi(khachHang.getDiaChi() + ", " + khachHang.getXa() + ", " + khachHang.getHuyen() + ", " + khachHang.getThanhPho());
        hoaDon.setTienShip(ship);
        updateBillToSuccess(khachHang, hoaDon);
        hoaDon.setTrangThai("Chờ xác nhận");
        if (payment.equals("1")) {
            hoaDon.setHinhThuc("Thanh toán khi nhận hàng");
        } else {
            hoaDon.setHinhThuc("Thanh toán bằng VN Pay");
        }
        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);

        int tongTien = 0;

        for (GioHang gioHang : gioHangs) {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(saveHoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(gioHang.getSanPhamChiTiet());
            hoaDonChiTiet.setSoLuong(gioHang.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHang.getTongTien());
//            hoaDonChiTiet.setGiaCu(0);
            tongTien += gioHang.getTongTien();
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }
        saveHoaDon.setTongTien(tongTien);
        hoaDonRepo.save(saveHoaDon);
        return null;
    }

    private void updateBillToSuccess(KhachHang khachHang, HoaDon hoaDon) {
        hoaDon.setMaDonHang("#" + UUID.randomUUID().toString().replace("-", "").substring(10).toUpperCase(Locale.ROOT));
        hoaDon.setTenKhachHang(khachHang.getHoTen());
        hoaDon.setEmail(khachHang.getEmail());
        hoaDon.setSoDienThoai(khachHang.getSoDienThoai());
        hoaDon.setNgayDatHang(new Date());
    }

    @PostMapping("/checkoutWithVNPAY")
    public void vnPay(
            @RequestBody CheckOutResponse checkOutResponse
    ){
        List<GioHang> gioHangs = checkOutResponse.getGioHangs();
        KhachHang khachHang = checkOutResponse.getKhachHang();
        int ship = checkOutResponse.getShip();

        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setDiaChi(khachHang.getDiaChi() + ", " + khachHang.getXa() + ", " + khachHang.getHuyen() + ", " + khachHang.getThanhPho());
        updateBillToSuccess(khachHang, hoaDon);
        hoaDon.setTrangThai("Xác nhận");
        hoaDon.setTienShip(ship);
        hoaDon.setHinhThuc("Thanh toán bằng VN Pay");
        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);

        int tongTien = 0;

        for (GioHang gioHang : gioHangs) {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setHoaDon(saveHoaDon);
            hoaDonChiTiet.setSanPhamChiTiet(gioHang.getSanPhamChiTiet());
            hoaDonChiTiet.setSoLuong(gioHang.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHang.getTongTien());
            tongTien += gioHang.getTongTien();
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }
        saveHoaDon.setTongTien(tongTien);
        hoaDonRepo.save(saveHoaDon);
    }
}
