package com.example.website.Controller;

import com.example.website.Enity.*;
import com.example.website.Respository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SanPhamChiTietController {
    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    private final SanPhamRepo sanPhamRepo;
    private final MauSacRepo mauSacRepo;
    private final SizeRepo sizeRepo;

    @GetMapping("/admin/san-pham-chi-tiet")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("spct",sanPhamChiTietRepo.findAll());
        return "src/san-pham-chi-tiet/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/san-pham-chi-tiet/add")
    public String getAddPage(Model model) {
        model.addAttribute("listSP", sanPhamRepo.findAll());
        model.addAttribute("listMS", mauSacRepo.findAll());
        model.addAttribute("listSize", sizeRepo.findAll());
        // Tạo mã sản phẩm mới (mã SP tự động tăng)
        String maSanPhamMoi = "SPCT" + String.format("%03d", sanPhamChiTietRepo.count() + 1);  // Giả sử mã sản phẩm tăng dần từ 001
        model.addAttribute("maSanPhamMoi", maSanPhamMoi);
        return "src/san-pham-chi-tiet/AddSPCT"; // Template thêm sản phẩm
    }

    @GetMapping("/san-pham-chi-tiet/delete")
    public String delete(@RequestParam("id")  Integer id) {
        SanPhamChiTiet spct = sanPhamChiTietRepo.getReferenceById(id);
        System.out.println(id);
        if (spct != null) {
            spct.setSo_luong(0); // Đặt số lượng sản phẩm chi tiết thành 0
            spct.setTrang_thai(false); // Đặt trạng thái thành Inactive
            sanPhamChiTietRepo.save(spct); // Lưu thay đổi
        }
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/san-pham-chi-tiet/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        // Truyền đối tượng sản phẩm vào model
        model.addAttribute("spct", sanPhamChiTiet);
//        model.addAttribute("spct", sanPhamChiTietRepo.getReferenceById(id));
        model.addAttribute("listSP", sanPhamRepo.findAll());
        model.addAttribute("listMS", mauSacRepo.findAll());
        model.addAttribute("listSize", sizeRepo.findAll());
        return "src/san-pham-chi-tiet/UpdateSPCT"; // Template cập nhật sản phẩm
    }

    @PostMapping("/san-pham-chi-tiet/updateData")
    public String update(@ModelAttribute SanPhamChiTiet spct, Model model) {
        // Kiểm tra số lượng và giá bán
        if (spct.getSo_luong() <= 0 || spct.getGia_ban() <= 0) {
            model.addAttribute("errorMessage", "Số lượng và giá bán phải lớn hơn 0");
            model.addAttribute("spct", spct); // Truyền lại sản phẩm chi tiết để người dùng có thể sửa
            model.addAttribute("listSP", sanPhamRepo.findAll());
            model.addAttribute("listMS", mauSacRepo.findAll());
            model.addAttribute("listSize", sizeRepo.findAll());
            return "src/san-pham-chi-tiet/UpdateSPCT"; // Quay lại trang cập nhật nếu có lỗi
        }
        spct.setTrang_thai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamChiTietRepo.save(spct); // Lưu cập nhật
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/san-pham-chi-tiet/save")
    public String save(@ModelAttribute SanPhamChiTiet spct, Model model) {
        // Kiểm tra số lượng và giá bán
        if (spct.getSo_luong() < 0 || spct.getGia_ban() < 0) {
            model.addAttribute("errorMessage", "Số lượng và giá bán phải lớn hơn hoặc bằng 0");
            return "src/san-pham-chi-tiet/AddSPCT"; // Quay lại trang thêm nếu có lỗi
        }
        if (spct.getMa_SPCT() == null || spct.getMa_SPCT().isEmpty()) {
            spct.setMa_SPCT(generateProductCode());
        }
        spct.setTrang_thai(true); // Đặt trạng thái thành Active khi lưu
        sanPhamChiTietRepo.save(spct); // Lưu sản phẩm mới
        return "redirect:/admin/san-pham-chi-tiet"; // Quay về danh sách sau khi lưu
    }
    public String generateProductCode() {
        // Lấy danh sách mã sản phẩm
        List<String> productCodes = sanPhamChiTietRepo.findAll()
                .stream()
                .map(SanPhamChiTiet::getMa_SPCT)
                .collect(Collectors.toList());

        // Tìm mã sản phẩm cao nhất
        int maxNumber = productCodes.stream()
                .filter(code -> code.startsWith("SPCT")) // Lọc mã bắt đầu bằng "SP"
                .map(code -> Integer.parseInt(code.substring(4))) // Lấy phần số sau "SP"
                .max(Comparator.naturalOrder())
                .orElse(0); // Nếu không có mã nào, mặc định là 0

        // Sinh mã mới
        int newNumber = maxNumber + 1;
        if (newNumber > 999) {
            throw new RuntimeException("Số lượng mã sản phẩm đã đạt giới hạn");
        }

        // Trả về mã sản phẩm mới (dạng SP001, SP002,...)
        return String.format("SP%03d", newNumber);
    }
}
