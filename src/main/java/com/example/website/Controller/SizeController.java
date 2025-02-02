package com.example.website.Controller;

import com.example.website.Enity.Size;
import com.example.website.Respository.SizeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SizeController {

    private final SizeRepo sizeRepo;

    @GetMapping("/admin/size")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("sizes", sizeRepo.findAll());
        return "src/size/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/size/add")
    public String getAddPage() {
        return "src/size/AddSize"; // Template thêm kích thước
    }

    @PostMapping("/size/delete")
    public String delete(@RequestParam Integer id) {
        Size size = sizeRepo.getReferenceById(id);
        if (size != null) {
            size.setTrangthai(0); // Đặt trạng thái thành Inactive
            sizeRepo.save(size); // Lưu thay đổi
        }
        return "redirect:/admin/size"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/size/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("size", sizeRepo.getReferenceById(id));
        return "src/size/UpdateSize"; // Template cập nhật kích thước
    }

    @PostMapping("/size/updateData")
    public String update(@ModelAttribute Size size) {
        size.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        sizeRepo.save(size); // Lưu cập nhật
        return "redirect:/admin/size"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/size/save")
    public String save(@ModelAttribute Size size) {
        size.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        sizeRepo.save(size); // Lưu kích thước mới
        return "redirect:/admin/size"; // Quay về danh sách sau khi lưu
    }
}
