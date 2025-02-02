package com.example.website.Controller;

import com.example.website.Enity.MauSac;
import com.example.website.Respository.MauSacRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class MauSacController {

    private final MauSacRepo mauSacRepo;

    @GetMapping("/admin/mau-sac")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("ms", mauSacRepo.findAll());
        return "src/mau-sac/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/mau-sac/add")
    public String getAddPage() {
        return "src/mau-sac/AddMauSac"; // Template thêm màu sắc
    }

    @PostMapping("/mau-sac/delete")
    public String delete(@RequestParam Integer id) {
        MauSac mauSac = mauSacRepo.getReferenceById(id);
        if (mauSac != null) {
            mauSac.setTrangthai(0); // Đặt trạng thái thành Inactive
            mauSacRepo.save(mauSac); // Lưu thay đổi
        }
        return "redirect:/admin/mau-sac"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/mau-sac/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("MS", mauSacRepo.getReferenceById(id));
        return "src/mau-sac/UpdateMauSac"; // Template cập nhật màu sắc
    }

    @PostMapping("/mau-sac/updateData")
    public String update(@ModelAttribute  MauSac mauSac, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "src/mau-sac/UpdateMauSac"; // Trả lại form với thông báo lỗi
        }
        mauSac.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        mauSacRepo.save(mauSac); // Lưu cập nhật
        return "redirect:/admin/mau-sac"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/mau-sac/save")
    public String save(@ModelAttribute @Valid MauSac mauSac, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "src/mau-sac/AddMauSac"; // Trả lại form với thông báo lỗi
        }
        mauSac.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        mauSacRepo.save(mauSac); // Lưu màu sắc mới
        return "redirect:/admin/mau-sac"; // Quay về danh sách sau khi lưu
    }
}
