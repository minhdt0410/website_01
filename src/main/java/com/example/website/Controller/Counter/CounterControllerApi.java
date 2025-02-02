package com.example.website.Controller.Counter;

import com.example.website.Enity.*;
import com.example.website.Response.FilterRequest;
import com.example.website.Response.HoaDonResponse;
import com.example.website.Response.ThuocTinhSPTaiQuayResponse;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.KhachHangRepo;
import com.example.website.Respository.SanPhamChiTietRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/counter")
@RequiredArgsConstructor
public class CounterControllerApi {

    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final KhachHangRepo khachHangRepo;

    @GetMapping
    public List<Integer> getBills() {
        return hoaDonRepo.findAll().stream()
                .filter(hoaDon -> hoaDon.getTrangThai().equals("Chờ mua hàng"))
                .map(HoaDon::getId)
                .toList();
    }

    @PostMapping("/createBill")
    public String createBill() {
        List<HoaDon> hoaDons = hoaDonRepo.findAll().stream()
                .filter(hoaDon -> hoaDon.getTrangThai().equals("Chờ mua hàng"))
                .toList();
        if (hoaDons.size() >= 5) {
            return "Chỉ có thể tạo tối đa 5 hoá đơn chờ";
        }
        HoaDon hoaDon = new HoaDon();
        hoaDon.setTrangThai("Chờ mua hàng");
        hoaDon.setHinhThuc("Tiền mặt");
        HoaDon saveHoaDon = hoaDonRepo.save(hoaDon);
        return String.valueOf(saveHoaDon.getId());
    }

    @GetMapping("/{idBill}")
    public HoaDonResponse findBillById(@PathVariable Integer idBill) throws Exception {
        Optional<HoaDon> existingHoaDon = hoaDonRepo.findById(idBill);
        if (existingHoaDon.isEmpty()) {
            return null;
        }
        HoaDon hoaDon = existingHoaDon.get();
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        return new HoaDonResponse(hoaDon, hoaDonChiTiets);
    }

    @GetMapping("/getElement")
    public ThuocTinhSPTaiQuayResponse getThuocTinh() {
        List<String> names = sanPhamChiTietRepo.findAll().stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getTrang_thai().equals(true))
                .map(sanPhamChiTiet -> sanPhamChiTiet.getSanPham().getTensanpham())
                .distinct()
                .toList();
        List<String> sizes = sanPhamChiTietRepo.findAll().stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getTrang_thai().equals(true))
                .map(sanPhamChiTiet -> sanPhamChiTiet.getSize().getTen_size())
                .distinct()
                .toList();
        List<String> colors = sanPhamChiTietRepo.findAll().stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getTrang_thai().equals(true))
                .map(sanPhamChiTiet -> sanPhamChiTiet.getMauSac().getTenmausac())
                .distinct()
                .toList();
        return new ThuocTinhSPTaiQuayResponse(names, sizes, colors);
    }

    @PostMapping("/getAllProductDetail")
    public List<SanPhamChiTiet> getAllSPCT(@RequestBody FilterRequest filterRequest) {
        return sanPhamChiTietRepo.findAll().stream()
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getTrang_thai().equals(true))
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getSize().getTen_size().contains(filterRequest.getSize()))
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getMauSac().getTenmausac().contains(filterRequest.getColor()))
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getSanPham().getTensanpham().contains(filterRequest.getName()))
                .filter(sanPhamChiTiet -> sanPhamChiTiet.getId().toString().contains(filterRequest.getId()))
                .toList();
    }

    @GetMapping("/getBillDetails/{idBill}")
    public List<HoaDonChiTiet> getBillDetails(@PathVariable Integer idBill) {
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        for(HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets){
            SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
            if(sanPhamChiTiet.getKhuyenMaiChiTiet() != null
                    && sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().getTinhTrang().equals("Đang diễn ra")){
                int giaTriGiam = sanPhamChiTiet.getKhuyenMaiChiTiet().getKhuyenMai().getGiaTriGiam();
                int newPrice = 0;
                if(giaTriGiam > 100){
                    newPrice = sanPhamChiTiet.getGia_ban().intValue() - giaTriGiam;
                }else {
                    newPrice = sanPhamChiTiet.getGia_ban().intValue() - (sanPhamChiTiet.getGia_ban().intValue() * giaTriGiam / 100);
                }
                hoaDonChiTiet.setDonGia(newPrice * hoaDonChiTiet.getSoLuong());
            }else {
                hoaDonChiTiet.setDonGia(hoaDonChiTiet.getSanPhamChiTiet().getGia_ban().intValue() * hoaDonChiTiet.getSoLuong());
            }
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }
        return hoaDonChiTietRepo.findByHoaDon(hoaDon);
    }

    @PostMapping("/addToCart/{idBill}/{idProductDetail}")
    public Integer addToCart(@PathVariable Integer idBill, @PathVariable Integer idProductDetail) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.getReferenceById(idProductDetail);
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);

        for (HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets) {
            if (hoaDonChiTiet.getSanPhamChiTiet().equals(sanPhamChiTiet)) {
                hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + 1);
                return getInteger(sanPhamChiTiet, hoaDonChiTiet);
            }
        }
        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        hoaDonChiTiet.setSoLuong(1);
        hoaDonChiTiet.setHoaDon(hoaDon);
        hoaDonChiTiet.setSanPhamChiTiet(sanPhamChiTiet);
        return getInteger(sanPhamChiTiet, hoaDonChiTiet);
    }

    private Integer getInteger(SanPhamChiTiet sanPhamChiTiet, HoaDonChiTiet hoaDonChiTiet) {
        if(sanPhamChiTiet.getSo_luong() > 0){
            sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() - 1);
            sanPhamChiTietRepo.save(sanPhamChiTiet);
            hoaDonChiTietRepo.save(hoaDonChiTiet);
            return sanPhamChiTiet.getSo_luong();
        }else {
            return -1;
        }
    }

    @DeleteMapping("/removeBillDetail/{idBillDetail}")
    public void remove(@PathVariable Integer idBillDetail){
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepo.getReferenceById(idBillDetail);
        SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
        sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + hoaDonChiTiet.getSoLuong());
        sanPhamChiTietRepo.save(sanPhamChiTiet);
        hoaDonChiTietRepo.delete(hoaDonChiTiet);
    }

    @GetMapping("/getBill/{idBill}")
    public HoaDon getBill(@PathVariable Integer idBill){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        int tongTien = 0;
        for(HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets){
            tongTien += hoaDonChiTiet.getDonGia();
        }
        hoaDon.setTongTien(tongTien);
        return hoaDonRepo.save(hoaDon);
    }

    @PutMapping("/checkQty/{idBillDetail}")
    public List<String> updateQuantity(@PathVariable Integer idBillDetail, @RequestParam Integer quantity){
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepo.getReferenceById(idBillDetail);
        int price = hoaDonChiTiet.getDonGia() / hoaDonChiTiet.getSoLuong();
        SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
        List<String> strings = new ArrayList<>();

        if(quantity <= 0){
            sanPhamChiTiet.setSo_luong(hoaDonChiTiet.getSoLuong() + sanPhamChiTiet.getSo_luong() - 1);
            hoaDonChiTiet.setSoLuong(1);
            hoaDonChiTiet.setDonGia(price * hoaDonChiTiet.getSoLuong());
            hoaDonChiTietRepo.save(hoaDonChiTiet);
            strings.add(String.valueOf(hoaDonChiTiet.getSoLuong()));
            strings.add(String.valueOf(hoaDonChiTiet.getDonGia()));
            strings.add("Số lượng phải lớn hơn 0");
            return strings;
        }

        if(quantity < hoaDonChiTiet.getSoLuong()){
            sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + hoaDonChiTiet.getSoLuong() - quantity);
            hoaDonChiTiet.setSoLuong(quantity);
            hoaDonChiTiet.setDonGia(price * hoaDonChiTiet.getSoLuong());
            hoaDonChiTietRepo.save(hoaDonChiTiet);
            strings.add(String.valueOf(hoaDonChiTiet.getSoLuong()));
            strings.add(String.valueOf(hoaDonChiTiet.getDonGia()));
            strings.add("");
        }else {
            int quantityAdd = quantity - hoaDonChiTiet.getSoLuong();
            if(quantityAdd > sanPhamChiTiet.getSo_luong()){
                hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + sanPhamChiTiet.getSo_luong());
                sanPhamChiTiet.setSo_luong(0);
                hoaDonChiTiet.setDonGia(price * hoaDonChiTiet.getSoLuong());
                hoaDonChiTietRepo.save(hoaDonChiTiet);
                strings.add(String.valueOf(hoaDonChiTiet.getSoLuong()));
                strings.add(String.valueOf(hoaDonChiTiet.getDonGia()));
                strings.add("Số lượng đã vượt quá số lượng trong kho");
            }else {
                hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + quantityAdd);
                sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() -  quantityAdd);
                hoaDonChiTiet.setDonGia(price * hoaDonChiTiet.getSoLuong());
                hoaDonChiTietRepo.save(hoaDonChiTiet);
                strings.add(String.valueOf(hoaDonChiTiet.getSoLuong()));
                strings.add(String.valueOf(hoaDonChiTiet.getDonGia()));
                strings.add("");
            }
        }
        return strings;
    }

    @PutMapping("/updateFee/{idBill}")
    public void updateFee(@PathVariable Integer idBill, @RequestBody HoaDon hoaDon, @RequestParam Integer fee){
        HoaDon currentHoaDon = hoaDonRepo.getReferenceById(idBill);
        currentHoaDon.setTenKhachHang(hoaDon.getTenKhachHang());
        currentHoaDon.setSoDienThoai(hoaDon.getSoDienThoai());
        currentHoaDon.setDiaChi(hoaDon.getDiaChi());
        currentHoaDon.setTienShip(fee);
        hoaDonRepo.save(currentHoaDon);
    }
    @PutMapping("/uploadFee/{idBill}")
    public void uploadFee(@PathVariable Integer idBill, @RequestParam Integer fee){
        HoaDon currentHoaDon = hoaDonRepo.getReferenceById(idBill);
        currentHoaDon.setTienShip(fee);
        hoaDonRepo.save(currentHoaDon);
    }

    @GetMapping("/getAllCustomer")
    public List<KhachHang> getAllKhacHang(@RequestParam String key){
        return khachHangRepo.findAll().stream()
                .filter(khachHang -> khachHang.getSoDienThoai().contains(key))
                .toList();
    }

    @PutMapping("/updateCustomerOfBill/{idBill}/{idCustomer}")
    public void update(@PathVariable Integer idBill, @PathVariable Integer idCustomer){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        if(idCustomer < 0){
            hoaDon.setKhachHang(null);
        }else {
            KhachHang khachHang = khachHangRepo.getReferenceById(idCustomer);
            hoaDon.setKhachHang(khachHang);
        }
        hoaDon.setDiaChi(null);
        hoaDon.setTenKhachHang(null);
        hoaDon.setSoDienThoai(null);
        hoaDon.setTienShip(0);
        hoaDonRepo.save(hoaDon);
    }

    @PutMapping("/updateAddress/{idBill}")
    public HoaDon updateAddress(@PathVariable Integer idBill, @RequestParam Integer status){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        KhachHang khachHang = hoaDon.getKhachHang();
        if(status == 1){
            if(hoaDon.getKhachHang() != null ){
                if(hoaDon.getDiaChi() == null){
                    hoaDon.setTenKhachHang(khachHang.getHoTen());
                    hoaDon.setSoDienThoai(khachHang.getSoDienThoai());
                    hoaDon.setDiaChi(khachHang.getDiaChi()  + ", " + khachHang.getXa() + ", " + khachHang.getHuyen() + ", " + khachHang.getThanhPho());
                }
            }
        }else {
            hoaDon.setDiaChi(null);
            hoaDon.setTenKhachHang(null);
            hoaDon.setSoDienThoai(null);
            hoaDon.setTienShip(0);
        }
        return hoaDonRepo.save(hoaDon);
    }

    @PutMapping("/updatePttt/{idBill}")
    public HoaDon updatePttt(@PathVariable Integer idBill, @RequestParam Integer status){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        if(status == 1){
            hoaDon.setHinhThuc("Tiền mặt");
        }else {
            hoaDon.setHinhThuc("Chuyển khoản");
        }
        return hoaDonRepo.save(hoaDon);
    }

    @PutMapping("/updateStatusBill/{idBill}")
    public Integer updateStatusBill(@PathVariable Integer idBill){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        if(hoaDon.getTienShip() > 0){
            hoaDon.setTrangThai("Đang giao");
        }else {
            hoaDon.setTrangThai("Đã giao");
        }
        hoaDon.setNgayDatHang(new Date());
        hoaDon.setMaDonHang("#" + UUID.randomUUID().toString().replace("-", "").substring(10).toUpperCase(Locale.ROOT));
        hoaDonRepo.save(hoaDon);
        return hoaDon.getId();
    }

    @DeleteMapping("/deleteBill/{idBill}")
    public Integer deleteBill(@PathVariable Integer idBill){
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idBill);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        if(!hoaDonChiTiets.isEmpty()){
            for (HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets){
                SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.getReferenceById(hoaDonChiTiet.getSanPhamChiTiet().getId());
                sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() + hoaDonChiTiet.getSoLuong());
                sanPhamChiTietRepo.save(sanPhamChiTiet);
            }
            hoaDonChiTietRepo.deleteAll(hoaDonChiTiets);
        }
        hoaDonRepo.delete(hoaDon);
        return hoaDon.getId();
    }
}
