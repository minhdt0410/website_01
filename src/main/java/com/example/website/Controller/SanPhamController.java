package com.example.website.Controller;

import com.example.website.Enity.MauSac;
import com.example.website.Enity.SanPham;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Enity.Size;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamRepo sanPhamRepo;
    private final ThuongHieuRepo thuongHieuRepo; // Thêm repo cho Thương Hiệu
    private final LoaiDeRepo loaiDeRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final SizeRepo sizeRepo;
    private final MauSacRepo mauSacRepo;
    private final SanPhamChiTietRepo sanPhamChiTietRepo;

    @GetMapping("/admin/san-pham")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("sp",sanPhamRepo.findAll());
        return "src/san-pham/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/san-pham/add")
    public String getAddPage(Model model) {
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        model.addAttribute("listLD", loaiDeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());

        model.addAttribute("listSize", sizeRepo.findAll());
        model.addAttribute("listMauSac", mauSacRepo.findAll());
        model.addAttribute("listSanPhamChiTiet", sanPhamChiTietRepo.findAll());
        String maSanPhamMoi = "SP" + String.format("%03d", sanPhamRepo.count() + 1);
        model.addAttribute("maSanPhamMoi", maSanPhamMoi);
        return "src/san-pham/AddSanPham";
    }

    @PostMapping("/san-pham/delete")
    public String delete(@RequestParam Integer id) {
        SanPham sanPham = sanPhamRepo.getReferenceById(id);
        if (sanPham != null) {
            sanPham.setTrangthai(false); // Đặt trạng thái thành Inactive
            sanPhamRepo.save(sanPham); // Lưu thay đổi
        }
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/san-pham/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        SanPham sanPham = sanPhamRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        // Truyền đối tượng sản phẩm vào model
        model.addAttribute("SP", sanPham);
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll()); // Lấy danh sách thương hiệu
        model.addAttribute("listLD", loaiDeRepo.findAll());
        model.addAttribute("listCL", chatLieuRepo.findAll());
        return "src/san-pham/UpdateSanPham"; // Template cập nhật sản phẩm
    }

    @PostMapping("/san-pham/updateData")
    public String update(@ModelAttribute SanPham sanPham) {
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamRepo.save(sanPham); // Lưu cập nhật
        return "redirect:/admin/san-pham"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/san-pham/save")
    public String save(@ModelAttribute SanPham sanPham, Model model,
                       @RequestParam(value = "sizes", required = false) List<Integer> sizeIds,
                       @RequestParam(value = "mauSacs", required = false) List<Integer> mauSacIds,
                       @RequestParam(value = "quantities", required = false) List<Integer> quantities,
                       @RequestParam(value = "prices", required = false) List<Double> prices,
                       @RequestParam(value = "images", required = false) List<String> images) {
        // Kiểm tra nếu tên sản phẩm trống hoặc chỉ chứa khoảng trắng
//        if (sanPham.getTensanpham().trim().isEmpty()) {
//            model.addAttribute("errorMessage", "Tên sản phẩm không thể để trống hoặc chỉ chứa khoảng trắng.");
//            return "src/san-pham/AddSanPham"; // Trả lại trang thêm sản phẩm với thông báo lỗi
//        }
        if (sanPham.getMa_sanpham() == null || sanPham.getMa_sanpham().isEmpty()) {
            sanPham.setMa_sanpham(generateProductCode());
        }
        sanPham.setTrangthai(true); // Đặt trạng thái thành Active
        sanPhamRepo.save(sanPham);
        // Kiểm tra size và màu sắc được chọn
        if (sizeIds != null && mauSacIds != null && quantities != null && prices != null) {
            int index = 0;
            for (Integer sizeId : sizeIds) {
                for (Integer mauSacId : mauSacIds) {
                    // Lấy thông tin size và màu sắc
                    Size size = sizeRepo.findById(sizeId).orElse(null);
                    MauSac mauSac = mauSacRepo.findById(mauSacId).orElse(null);
                    if (size != null && mauSac != null) {
                        SanPhamChiTiet spct = new SanPhamChiTiet();
                        spct.setSanPham(sanPham);
                        spct.setSize(size);
                        spct.setMauSac(mauSac);
                        spct.setSo_luong(quantities.get(index));
                        spct.setGia_ban(prices.get(index));// Lấy số lượng tương ứng
                        spct.setTrang_thai(true);
                        spct.setNgay_nhap(LocalDateTime.now()); // Ngày hiện tại
                        spct.setMa_SPCT("aaa");
                        spct.setAnh_spct(images.get(index));
                        sanPhamChiTietRepo.save(spct);
                    }
                    index++;
                }
            }
        }
        return "redirect:/admin/san-pham";
    }
    public String generateProductCode() {
        List<String> productCodes = sanPhamRepo.findAll()
                .stream()
                .map(SanPham::getMa_sanpham)
                .collect(Collectors.toList());
        int maxNumber = productCodes.stream()
                .filter(code -> code.startsWith("SP"))
                .map(code -> Integer.parseInt(code.substring(2)))
                .max(Comparator.naturalOrder())
                .orElse(0);
        int newNumber = maxNumber + 1;
        if (newNumber > 999) {
            throw new RuntimeException("Số lượng mã sản phẩm đã đạt giới hạn");
        }
        return String.format("SP%03d", newNumber);
    }
    public String a1() {
        List<String> existingCodes = sanPhamChiTietRepo.findAll()
                .stream()
                .map(SanPhamChiTiet::getMa_SPCT)
                .collect(Collectors.toList());
        int maxNumber = existingCodes.stream()
                .filter(code -> code.startsWith("CTSP"))
                .map(code -> Integer.parseInt(code.substring(4))) // Lấy số sau "SPCT"
                .max(Comparator.naturalOrder())
                .orElse(0);
        // Tìm mã mới không trùng
        int newNumber = maxNumber + 1;
        String newCode;
        do {
            newCode = String.format("CTSP%03d", newNumber++);
        } while (existingCodes.contains(newCode) && newNumber <= 999);
        if (newNumber > 999) {
            throw new RuntimeException("Số lượng mã sản phẩm chi tiết đã đạt giới hạn");
        }
        return newCode;
    }
}
