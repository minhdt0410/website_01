package com.example.website.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.example.website.Respository.SanPhamChiTietRepo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.website.Enity.HoaDon;
import com.example.website.Enity.HoaDonChiTiet;
import com.example.website.Enity.SanPhamChiTiet;
import com.example.website.Response.SanPhamOfficeResponse;
import com.example.website.Respository.HoaDonChiTietRepo;
import com.example.website.Respository.HoaDonRepo;
import com.example.website.Respository.SanPhamRepo;
import com.example.website.Sdi.createHoaDonOfficeSdi;
import com.example.website.Sdi.hdct;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HoaDonController {
    private final HoaDonRepo hoaDonRepo;
    private final HoaDonChiTietRepo hoaDonChiTietRepo;
    private final SanPhamRepo sanPhamRepo;

    private final donhangService donhangService;

    private final SanPhamChiTietRepo sanPhamChiTietRepo;
    // @GetMapping("/admin/hoadon")
    // public String getAdmin() {
    //     return "src/hoadon/HoaDon";
    // }

    @GetMapping("/admin/hoadon")
    public String getAdmin(Model model) {
        List<SanPhamOfficeResponse> sanPhamOffices = sanPhamRepo.findSanPhamOffice();
        List<HoaDon> danhSachHoaDon = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");

        // Truyền dữ liệu đếm vào model
        model.addAttribute("danhSachHoaDon", danhSachHoaDon);
        model.addAttribute("sanPhamOffices", sanPhamOffices);

        return "src/hoadon/HoaDon";
    }

    @GetMapping("/hoadon/fetch")
    public ResponseEntity<List<HoaDon>> fetchHoaDon() {
        List<HoaDon> danhSachHoaDon = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");
        return ResponseEntity.ok(danhSachHoaDon);
    }

    @GetMapping("/hoadon/detail")
    public String getDonHangDetail(@RequestParam int idDonHang, Model model) {
        HoaDon hoaDon = hoaDonRepo.findDetailSaleOffice(idDonHang);
        List<HoaDonChiTiet> chitiet = hoaDonChiTietRepo.findByHoaDon(hoaDon);
        // List<HoaDon> dh = hoaDonRepo.findAllSaleOffice("khách mua tại cửa hàng");

        model.addAttribute("dh", hoaDon);
        model.addAttribute("chitiet", chitiet);
        return "src/hoadon/HoaDonDetail";
    }

    @PostMapping("/hoadon/add")
    public ResponseEntity<?> addHoaDon(@RequestBody createHoaDonOfficeSdi input) {
        try{
            String hinhthucthanhtoan = input.getHinhthucthanhtoan();
            int tongtien = input.getTongtien();
            List<hdct> hdcts = input.getHdct();
            HoaDon hoaDon = new HoaDon();
            hoaDon.setTenKhachHang("khách mua tại cửa hàng");
            hoaDon.setNgayDatHang(new Date());
            hoaDon.setTrangThai("Đã giao");
            hoaDon.setHinhThuc(hinhthucthanhtoan);
            hoaDon.setTongTien(tongtien);
            hoaDon.setMaDonHang(UUID.randomUUID().toString().replace("-", "").substring(10));

            hoaDon = hoaDonRepo.save(hoaDon);
            if (hoaDon != null && hdcts.size() > 0) {
                for (hdct _hdct : hdcts) {
                    HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                    hoaDonChiTiet.setHoaDon(hoaDon);
                    hoaDonChiTiet.setSoLuong(_hdct.getSoluong());
                    hoaDonChiTiet.setDonGia(_hdct.getDongia());
                    SanPhamChiTiet spct = new SanPhamChiTiet();
                    spct.setId(_hdct.getIdchitietsanpham());
                    SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietRepo.getReferenceById(_hdct.getIdchitietsanpham());
                    sanPhamChiTiet.setSo_luong(sanPhamChiTiet.getSo_luong() - hoaDonChiTiet.getSoLuong());
                    sanPhamChiTietRepo.save(sanPhamChiTiet);
                    hoaDonChiTiet.setSanPhamChiTiet(spct);
                    hoaDonChiTietRepo.save(hoaDonChiTiet);
                }
            }
            return ResponseEntity.ok().build();
        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).build();
        }
        
    }
    @GetMapping("/hoadon/export")
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) throws IOException {

        // Tạo đối tượng Pageable để phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách hóa đơn từ service hoặc repository (dựa trên phân trang)
        List<HoaDon> danhSachHoaDon = donhangService.getAllHoaDonexecl(pageable);

        // Tạo workbook và sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh Sách Hóa Đơn");

        // Tạo tiêu đề cho các cột
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Đơn Hàng");
        headerRow.createCell(1).setCellValue("Tên Khách Hàng");
        headerRow.createCell(2).setCellValue("Ngày Đặt Hàng");
        headerRow.createCell(3).setCellValue("Tổng Tiền");
        headerRow.createCell(4).setCellValue("Hình Thức");
        headerRow.createCell(5).setCellValue("Trạng Thái");

        // Thêm dữ liệu vào các dòng
        int rowNum = 1;
        for (HoaDon dh : danhSachHoaDon) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dh.getMaDonHang());
            row.createCell(1).setCellValue(dh.getTenKhachHang());
            row.createCell(2).setCellValue(dh.getNgayDatHang().toString()); // Định dạng lại nếu cần
            row.createCell(3).setCellValue(dh.getTongTien());
            row.createCell(4).setCellValue(dh.getHinhThuc());
            row.createCell(5).setCellValue(dh.getTrangThai());
        }

        // Set response headers để browser nhận dạng và tải file
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=danh_sach_hoa_don.xlsx");

        // Ghi dữ liệu ra file và gửi đến client
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
