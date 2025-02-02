package com.example.website.Controller;

import com.example.website.Enity.LoaiDe;
import com.example.website.Respository.LoaiDeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LoaiDeController {

    private final LoaiDeRepo loaiDeRepo;

    @GetMapping("/admin/loai-de")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("ld", loaiDeRepo.findAll());
        return "src/loai-de/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/loai-de/add")
    public String getAddPage() {
        return "src/loai-de/AddLoaiDe"; // Template thêm loại đế
    }

    @PostMapping("/loai-de/delete")
    public String delete(@RequestParam Integer id) {
        LoaiDe loaiDe = loaiDeRepo.getReferenceById(id);
        if (loaiDe != null) {
            loaiDe.setTrangthai(0); // Đặt trạng thái thành Inactive
            loaiDeRepo.save(loaiDe); // Lưu thay đổi
        }
        return "redirect:/admin/loai-de"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/loai-de/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("LD", loaiDeRepo.getReferenceById(id));
        return "src/loai-de/UpdateLoaiDe"; // Template cập nhật loại đế
    }

    @PostMapping("/loai-de/updateData")
    public String update(@ModelAttribute LoaiDe loaiDe) {
        loaiDe.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        loaiDeRepo.save(loaiDe); // Lưu cập nhật
        return "redirect:/admin/loai-de"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/loai-de/save")
    public String save(@ModelAttribute LoaiDe loaiDe) {
        loaiDe.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        loaiDeRepo.save(loaiDe); // Lưu loại đế mới
        return "redirect:/admin/loai-de"; // Quay về danh sách sau khi lưu
    }
}
