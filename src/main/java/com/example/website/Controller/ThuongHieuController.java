package com.example.website.Controller;

import com.example.website.Enity.ThuongHieu;
import com.example.website.Respository.ThuongHieuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ThuongHieuController {

    private final ThuongHieuRepo thuongHieuRepo;

    @GetMapping("/admin/thuong-hieu")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("thuongHieus", thuongHieuRepo.findAll());
        return "src/thuong-hieu/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/thuong-hieu/add")
    public String getAddPage() {
        return "src/thuong-hieu/AddThuongHieu"; // Template thêm thương hiệu
    }

    @PostMapping("/thuong-hieu/delete")
    public String delete(@RequestParam Integer id) {
        thuongHieuRepo.deleteById(id); // Xóa thương hiệu theo ID
        return "redirect:/admin/thuong-hieu"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/thuong-hieu/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("thuongHieu", thuongHieuRepo.getReferenceById(id));
        return "src/thuong-hieu/UpdateThuongHieu"; // Template cập nhật thương hiệu
    }

    @PostMapping("/thuong-hieu/updateData")
    public String update(@ModelAttribute ThuongHieu thuongHieu) {
        thuongHieuRepo.save(thuongHieu); // Lưu cập nhật
        return "redirect:/admin/thuong-hieu"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/thuong-hieu/save")
    public String save(@ModelAttribute ThuongHieu thuongHieu) {
        thuongHieuRepo.save(thuongHieu); // Lưu thương hiệu mới
        return "redirect:/admin/thuong-hieu"; // Quay về danh sách sau khi lưu
    }
}
