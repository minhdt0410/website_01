package com.example.website.Controller;

import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Enity.HoaDon;
import com.example.website.Respository.SanPhamChiTietRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DonHangController {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;

    @GetMapping("/admin/donhang")
    public String getAdmin(@RequestParam(defaultValue = "") String trangThai, Model model) {
        List<HoaDon> danhSachHoaDon;
        if (trangThai.isEmpty()) {
            danhSachHoaDon = hoaDonRepo.findAllOrderByNgayDatHangDesc().stream()
                    .filter(hoaDon -> hoaDon.getMaDonHang() != null)
                    .toList();
        } else {
            danhSachHoaDon = hoaDonRepo.findByTrangThaiOrderByNgayDatHangDesc(trangThai).stream()
                    .filter(hoaDon -> hoaDon.getMaDonHang() != null)
                    .toList();
        }

        // Đếm số lượng đơn hàng theo từng trạng thái
        long soDonChuaXacNhan = hoaDonRepo.countByTrangThai("Chờ xác nhận");
        long soDonXacNhan = hoaDonRepo.countByTrangThai("Xác nhận");
        long soDonDangGiao = hoaDonRepo.countByTrangThai("Đang giao");
        long soDonDaGiao = hoaDonRepo.countByTrangThai("Đã giao");
        long soDonDoiTra = hoaDonRepo.countByTrangThai("Đổi trả");
        long soDonBiHuy = hoaDonRepo.countByTrangThai("Đơn bị hủy");

        // Truyền dữ liệu đếm vào model
        model.addAttribute("danhSachHoaDon", danhSachHoaDon);
        model.addAttribute("trangThaiHienTai", trangThai);
        model.addAttribute("soDonChuaXacNhan", soDonChuaXacNhan);
        model.addAttribute("soDonXacNhan", soDonXacNhan);
        model.addAttribute("soDonDangGiao", soDonDangGiao);
        model.addAttribute("soDonDaGiao", soDonDaGiao);
        model.addAttribute("soDonDoiTra", soDonDoiTra);
        model.addAttribute("soDonBiHuy", soDonBiHuy);

        return "src/donhang/DonHang";
    }


    @GetMapping("/donhang/detail")
    public String getDonHangDetail(@RequestParam Integer id, Model model) {
        // Lấy hóa đơn dựa trên ID
        HoaDon hoaDon = hoaDonRepo.findById(id).orElse(new HoaDon());

        // Lấy danh sách chi tiết hóa đơn
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepo.findByHoaDon(hoaDon);

        // Thêm dữ liệu vào model
        model.addAttribute("dh", hoaDon);
        model.addAttribute("chiTietList", chiTietList); // Danh sách chi tiết hóa đơn
        return "src/donhang/DonHangDetail";
    }

    @PostMapping("/donhang/updateData")
    public String updateTrangThaiDonHang(@RequestParam Integer id, @RequestParam String trangThai) {
        HoaDon hoaDon = hoaDonRepo.findById(id).orElse(null);
        if (hoaDon != null) {
            // Kiểm tra logic cập nhật trạng thái
            switch (hoaDon.getTrangThai()) {
                case "Chờ xác nhận":
                    // Chỉ có thể chuyển từ "Chờ xác nhận" sang các trạng thái khác (bao gồm hủy)
                    if ("Xác nhận".equals(trangThai) || "Đơn bị hủy".equals(trangThai)) {
                        hoaDon.setTrangThai(trangThai);
                    }
                    break;
                case "Xác nhận":
                    // Chỉ có thể chuyển từ "Xác nhận" sang "Đang giao" hoặc "Đơn bị hủy"
                    if ("Đang giao".equals(trangThai)) {
                        boolean check = false;
                        for(HoaDonChiTiet hoaDonChiTiet : hoaDonChiTietRepo.findByHoaDon(hoaDon)){
                            SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
                            if(sanPhamChiTiet.getSo_luong() < hoaDonChiTiet.getSoLuong()){
                                check = true;
                                break;
                            }
                        }
                        if (!check){
                            for(HoaDonChiTiet hoaDonChiTiet : hoaDonChiTietRepo.findByHoaDon(hoaDon)){
                                SanPhamChiTiet sanPhamChiTiet = hoaDonChiTiet.getSanPhamChiTiet();
                                sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() - hoaDonChiTiet.getSoLuong());
                                sanPhamChiTietRepo.save(sanPhamChiTiet);
                            }
                            hoaDon.setTrangThai(trangThai);
                        }
                    }
                    break;
                case "Đang giao":
                    // Chỉ có thể chuyển từ "Đang giao" sang "Đã giao"
                    if ("Đã giao".equals(trangThai)) {
                        hoaDon.setTrangThai(trangThai);
                    }
                    break;
                case "Đã giao":
                    // Chỉ có thể chuyển từ "Đã giao" sang "Đổi trả" (trả hàng)
                    if ("Đổi trả".equals(trangThai)) {
                        hoaDon.setTrangThai(trangThai);
                    }
                    break;
                case "Đơn bị hủy":
                    // Đơn bị hủy không thể thay đổi lại trạng thái
                    break;
                default:
                    hoaDon.setTrangThai(trangThai);
            }
            hoaDonRepo.save(hoaDon);
        }
        return "redirect:/admin/donhang";
    }

    @PostMapping("/donhang/cancelOrder")
    public String cancelOrder(@RequestParam Integer id) {
        HoaDon hoaDon = hoaDonRepo.findById(id).orElse(null);
        if (hoaDon != null && "Chờ xác nhận".equals(hoaDon.getTrangThai())|| "Xác nhận".equals(hoaDon.getTrangThai())) {
            hoaDon.setTrangThai("Đơn bị hủy");
            hoaDonRepo.save(hoaDon);
        }
        return "redirect:/admin/donhang";
    }

}