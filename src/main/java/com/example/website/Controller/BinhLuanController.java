    package com.example.website.Controller;

    import com.example.website.Enity.BinhLuan;

    import com.example.website.Enity.MauSac;
    import com.example.website.Respository.BinhLuanRepo;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Optional;

    @Controller
    @RequiredArgsConstructor
    public class BinhLuanController {
        @Autowired
        private final BinhLuanRepo binhLuanRepo;

        @GetMapping("/admin/binhluan")
        public String HienThi(
                @RequestParam(name = "danhGia", required = false) Integer danhGia,
                @RequestParam(name = "startDate", required = false) LocalDate startDate,
                @RequestParam(name = "endDate", required = false) LocalDate endDate,
                @RequestParam(defaultValue = "0") int page,
                Model model) {

            Pageable pageable = PageRequest.of(page, 5); // 5 items per page
            Page<BinhLuan> binhLuanPage;

            // Kiểm tra ngày bắt đầu và ngày kết thúc
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                model.addAttribute("errorMessage", "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
                return "src/binhluan/BinhLuan"; // Hoặc bạn có thể chuyển hướng đến trang khác
            }

            // Nếu cả hai bộ lọc đều có giá trị
            if (danhGia != null && startDate != null && endDate != null) {
                binhLuanPage = binhLuanRepo.findByDanhGiaAndNgayBinhLuanBetween(danhGia, startDate, endDate, pageable);
            }
            // Nếu chỉ lọc theo đánh giá
            else if (danhGia != null) {
                binhLuanPage = binhLuanRepo.findByDanhGia(danhGia, pageable);
            }
            // Nếu chỉ lọc theo ngày
            else if (startDate != null && endDate != null) {
                binhLuanPage = binhLuanRepo.findByNgayBinhLuanBetween(startDate, endDate, pageable);
            }
            // Nếu không có lọc, lấy tất cả
            else {
                binhLuanPage = binhLuanRepo.findAll(pageable);
            }

            model.addAttribute("bl", binhLuanPage.getContent());
            model.addAttribute("danhGias", List.of(1, 2, 3, 4, 5)); // Danh sách đánh giá
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", binhLuanPage.getTotalPages());
            model.addAttribute("danhGia", danhGia); // Để duy trì giá trị lọc
            model.addAttribute("startDate", startDate); // Để duy trì ngày bắt đầu
            model.addAttribute("endDate", endDate); // Để duy trì ngày kết thúc

            return "src/binhluan/BinhLuan"; // Đường dẫn đến file template
        }

        @PostMapping("/binhluan/delete")
        public String delete(@RequestParam Integer id, RedirectAttributes redirectAttributes) {
            Optional<BinhLuan> binhLuanOptional = binhLuanRepo.findById(id);
            if (binhLuanOptional.isPresent()) {
                binhLuanRepo.delete(binhLuanOptional.get()); // Xóa bình luận khỏi cơ sở dữ liệu
                redirectAttributes.addFlashAttribute("message", "Bình luận đã được xóa thành công.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Bình luận không tồn tại.");
            }
            return "redirect:/admin/binhluan";
        }

    }
