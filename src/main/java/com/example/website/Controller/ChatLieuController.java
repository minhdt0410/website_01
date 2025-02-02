package com.example.website.Controller;

import com.example.website.Enity.ChatLieu;
import com.example.website.Respository.ChatLieuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ChatLieuController {

    private final ChatLieuRepo chatLieuRepo;

    @GetMapping("/admin/chat-lieu")
    public String getAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("cl", chatLieuRepo.findAll());
        return "src/chat-lieu/hien-thi"; // Template hiển thị danh sách
    }

    @GetMapping("/chat-lieu/add")
    public String getAddPage() {
        return "src/chat-lieu/AddChatLieu"; // Template thêm chất liệu
    }

    @PostMapping("/chat-lieu/delete")
    public String delete(@RequestParam Integer id) {
        ChatLieu chatLieu = chatLieuRepo.getReferenceById(id);
        if (chatLieu != null) {
            chatLieu.setTrangthai(0); // Đặt trạng thái thành Inactive
            chatLieuRepo.save(chatLieu); // Lưu thay đổi
        }
        return "redirect:/admin/chat-lieu"; // Quay về danh sách sau khi xóa
    }

    @GetMapping("/chat-lieu/update")
    public String getUpdate(Model model, @RequestParam Integer id) {
        model.addAttribute("CL", chatLieuRepo.getReferenceById(id));
        return "src/chat-lieu/UpdateChatLieu"; // Template cập nhật chất liệu
    }

    @PostMapping("/chat-lieu/updateData")
    public String update(@ModelAttribute ChatLieu chatLieu) {
        chatLieu.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        chatLieuRepo.save(chatLieu); // Lưu cập nhật
        return "redirect:/admin/chat-lieu"; // Quay về danh sách sau khi cập nhật
    }

    @PostMapping("/chat-lieu/save")
    public String save(@ModelAttribute ChatLieu chatLieu) {
        chatLieu.setTrangthai(1); // Đặt trạng thái thành Active khi lưu
        chatLieuRepo.save(chatLieu); // Lưu chất liệu mới
        return "redirect:/admin/chat-lieu"; // Quay về danh sách sau khi lưu
    }
}
