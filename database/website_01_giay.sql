CREATE DATABASE QLY_WEBSITESD131;
GO
USE QLY_WEBSITESD131;
GO
--use master
--drop database QLY_WEBSITE



CREATE TABLE khuyen_mai (
    id INT PRIMARY KEY IDENTITY,
    ten_khuyen_mai NVARCHAR(255),
    gia_tri_giam INT,
    ngay_bat_dau DATETIME,
    ngay_ket_thuc DATETIME,
    ngay_tao DATETIME,
    ngay_sua DATETIME,
    tinh_trang NVARCHAR(255),
    loai_khuyen_mai NVARCHAR(255),
    trang_thai BIT
);

CREATE TABLE mau_sac (
    id INT PRIMARY KEY IDENTITY,
    ma_mau_sac NVARCHAR(50),
    ten_mau_sac NVARCHAR(255),
    trang_thai INT
);

CREATE TABLE size (
    id INT PRIMARY KEY IDENTITY,
    ma_size NVARCHAR(50),
    ten_size NVARCHAR(255),
    trang_thai INT
);

CREATE TABLE loai_de (
    id INT PRIMARY KEY IDENTITY,
    ma_loai_de NVARCHAR(50),
    ten_loai_de NVARCHAR(255),
    trang_thai INT
);

CREATE TABLE thuong_hieu (
    id INT PRIMARY KEY IDENTITY,
    ten_thuong_hieu NVARCHAR(255),
    mo_ta NVARCHAR(255)
);

CREATE TABLE chat_lieu (
    id INT PRIMARY KEY IDENTITY,
    ma_chat_lieu NVARCHAR(50),
    ten_chat_lieu NVARCHAR(255),
    trang_thai INT
);

-- Table san_pham with the updated foreign key relationship to loai_de
CREATE TABLE san_pham (
    id INT PRIMARY KEY IDENTITY,
    id_thuong_hieu INT FOREIGN KEY REFERENCES thuong_hieu(id),
    ten_san_pham NVARCHAR(255),
    ma_san_pham NVARCHAR(50),
    trang_thai INT,
	id_chat_lieu INT FOREIGN KEY REFERENCES chat_lieu(id),
    id_loai_de INT FOREIGN KEY REFERENCES loai_de(id)  -- Updated column added here
);

CREATE TABLE khach_hang (
    id INT PRIMARY KEY IDENTITY,
    ho_ten NVARCHAR(255),
    ngay_sinh DATE,
    so_dien_thoai NVARCHAR(15),
    email NVARCHAR(255),
    gioi_tinh NVARCHAR(10),
    xa NVARCHAR(255),
    huyen NVARCHAR(255),
    thanh_pho NVARCHAR(255),
    mat_khau NVARCHAR(255),
    trang_thai NVARCHAR(255),
    dia_chi NVARCHAR(255)
);

CREATE TABLE admin (
    id INT PRIMARY KEY IDENTITY,
    ho_ten NVARCHAR(255),
    email NVARCHAR(255),
    mat_khau NVARCHAR(255),
    trang_thai INT
);


-- Table chi_tiet_san_pham without id_loai_de
CREATE TABLE chi_tiet_san_pham (
    id INT PRIMARY KEY IDENTITY,
    id_san_pham INT FOREIGN KEY REFERENCES san_pham(id),
    id_mau_sac INT FOREIGN KEY REFERENCES mau_sac(id),
    id_size INT FOREIGN KEY REFERENCES size(id),
    ma_chi_tiet_san_pham NVARCHAR(50),
    id_san_pham_khuyen_mai INT,
    so_luong INT,
    gia_ban DECIMAL(18, 2),
    ngay_nhap DATETIME,
    trang_thai INT,
	anh_spct NVARCHAR(MAX)
);

CREATE TABLE hinh_anh (
    id INT PRIMARY KEY IDENTITY,  -- Cột ID sử dụng IDENTITY để tự động tăng
    id_chi_tiet_san_pham INT NOT NULL,
    ten_hinh NVARCHAR(255) NOT NULL,
    trang_thai INT,
    CONSTRAINT fk_hinh_anh_chi_tiet_san_pham FOREIGN KEY (id_chi_tiet_san_pham)
    REFERENCES chi_tiet_san_pham(id)  -- Ràng buộc khóa ngoại
);

CREATE TABLE gio_hang (
    id INT PRIMARY KEY IDENTITY,
    id_khach_hang INT FOREIGN KEY REFERENCES khach_hang(id),
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES chi_tiet_san_pham(id),
    so_luong INT,
    tong_tien DECIMAL(18, 2)
);
CREATE TABLE phuong_thuc (
    id INT PRIMARY KEY IDENTITY,
    ten_phuong_thuc NVARCHAR(50) NOT NULL
);

CREATE TABLE don_hang (
    id INT PRIMARY KEY IDENTITY,
    id_khach_hang INT FOREIGN KEY REFERENCES khach_hang(id),
    ten_khach_hang NVARCHAR(255),
    email NVARCHAR(255),
    so_dien_thoai NVARCHAR(255),
    ma_don_hang NVARCHAR(50),
    ngay_dat_hang DATETIME,
    so_luong INT,
    dia_chi NVARCHAR(255),
    tong_tien DECIMAL(18, 2),
    trang_thai NVARCHAR(255),
	id_phuong_thuc INT FOREIGN KEY REFERENCES phuong_thuc(id),
    hinh_thuc NVARCHAR(255),
	tien_ship INT,
	ghi_chu NVARCHAR(255)
);

-- Table san_pham_khuyen_mai and hoa_don_chi_tiet as defined
CREATE TABLE san_pham_khuyen_mai (
    id INT PRIMARY KEY IDENTITY,
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES chi_tiet_san_pham(id),
    id_khuyen_mai INT FOREIGN KEY REFERENCES khuyen_mai(id),
    gia_moi INT
);

CREATE TABLE hoa_don_chi_tiet (
    id INT PRIMARY KEY IDENTITY,
    id_don_hang INT FOREIGN KEY REFERENCES don_hang(id),
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES chi_tiet_san_pham(id),
    so_luong INT,
    don_gia INT,
	gia_cu INT
);

CREATE TABLE thanh_toan (
    id INT PRIMARY KEY IDENTITY,
    id_don_hang INT FOREIGN KEY REFERENCES don_hang(id),
    phuong_thuc_thanh_toan NVARCHAR(255),
    ngay_thanh_toan DATETIME,
    trang_thai INT
);

CREATE TABLE van_chuyen (
    id INT PRIMARY KEY IDENTITY,
    id_don_hang INT FOREIGN KEY REFERENCES don_hang(id),
    phuong_thuc_van_chuyen NVARCHAR(255),
    ngay_giao_hang DATETIME,
    trang_thai INT
);

CREATE TABLE binh_luan (
    id INT PRIMARY KEY IDENTITY,
    id_khach_hang INT FOREIGN KEY REFERENCES khach_hang(id),
    id_chi_tiet_san_pham INT FOREIGN KEY REFERENCES chi_tiet_san_pham(id),
    binh_luan NVARCHAR(255),
    danh_gia INT
);

CREATE TABLE tin_nhan (
    id INT PRIMARY KEY IDENTITY,
    id_admin INT FOREIGN KEY REFERENCES admin(id),
    id_khach_hang INT FOREIGN KEY REFERENCES khach_hang(id),
    noi_dung NVARCHAR(255),
    ngay_gui DATETIME
);

-- Adding back the foreign key in chi_tiet_san_pham if required
ALTER TABLE chi_tiet_san_pham
ADD CONSTRAINT FK_san_pham_khuyen_mai FOREIGN KEY (id_san_pham_khuyen_mai)
REFERENCES san_pham_khuyen_mai(id);




INSERT INTO mau_sac (ma_mau_sac, ten_mau_sac, trang_thai) 
VALUES 
    ('MS01', N'Đen', 1),
    ('MS02', N'Trắng', 1),
    ('MS03', N'Nâu', 1);

INSERT INTO size (ma_size, ten_size, trang_thai) 
VALUES 
    ('S', N'38', 1),
    ('M', N'39', 1),
    ('L', N'40', 1),
    ('XL', N'41', 1),
    ('XXL', N'42', 1),
    ('XS', N'43', 1);


INSERT INTO loai_de (ma_loai_de, ten_loai_de, trang_thai) 
VALUES 
    ('LD01', N'Đế mềm', 1),
    ('LD02', N'Đế cứng', 1),
    ('LD03', N'Đế dẻo', 1),
    ('LD04', N'Đế cao su', 1),
    ('LD05', N'Đế gỗ', 1),
    ('LD06', N'Đế kim loại', 1);

INSERT INTO thuong_hieu (ten_thuong_hieu, mo_ta) 
VALUES 
    (N'Nike', N'Thương hiệu nổi tiếng'),
    (N'Adidas', N'Thương hiệu thể thao'),
    (N'Puma', N'Thương hiệu năng động'),
    (N'Converse', N'Thương hiệu trẻ trung'),
    (N'Reebok', N'Thương hiệu thời trang'),
    (N'Vans', N'Thương hiệu đường phố');


INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu, trang_thai) 
VALUES 
    ('CL01', N'Da', 1),
    ('CL02', N'Vải', 1),
    ('CL03', N'Nhựa', 1),
    ('CL04', N'Cao su', 1),
    ('CL05', N'Lưới', 1),
    ('CL06', N'Len', 1);


-- Thêm các phương thức vào bảng phuong_thuc
INSERT INTO phuong_thuc (ten_phuong_thuc)
VALUES (N'Mua tại quầy'),
       (N'Online');

INSERT INTO khuyen_mai (
    ten_khuyen_mai, gia_tri_giam, ngay_bat_dau, ngay_ket_thuc, 
    ngay_tao, ngay_sua, tinh_trang, loai_khuyen_mai, trang_thai
) 
VALUES 
    (N'Giảm giá Tết', 20, '2024-01-01', '2024-01-31', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Phần trăm', 1),
    (N'Khuyến mãi hè', 15, '2024-06-01', '2024-06-30', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Phần trăm', 1),
    (N'Mùa tựu trường', 10, '2024-08-15', '2024-09-15', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Phần trăm', 1),
    (N'Black Friday', 50, '2024-11-29', '2024-11-29', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Phần trăm', 1),
    (N'Mua 1 tặng 1', 0, '2024-12-01', '2024-12-10', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Sản phẩm', 1),
    (N'Giảm giá cuối năm', 30, '2024-12-20', '2024-12-31', GETDATE(), GETDATE(), N'Còn hiệu lực', N'Phần trăm', 1);


INSERT INTO san_pham (id_thuong_hieu, ten_san_pham, ma_san_pham, trang_thai, id_chat_lieu, id_loai_de)
VALUES
    (1, N'Giày Boost Nam Classic', 'SP001', 1, 1, 1),
    (2, N'Giày Boost Nam Sporty', 'SP002', 1, 2, 2),
    (3, N'Giày Boost Nam Runner', 'SP003', 1, 3, 3),
    (4, N'Giày Boost Nam Street', 'SP004', 1, 4, 4),
    (5, N'Giày Boost Nam Pro', 'SP005', 1, 5, 5),
    (6, N'Giày Boost Nam Trendy', 'SP006', 1, 6, 6),
    (1, N'Giày Boost Nam Active', 'SP007', 1, 1, 2),
    (2, N'Giày Boost Nam Max', 'SP008', 1, 2, 3),
    (3, N'Giày Boost Nam Prime', 'SP009', 1, 3, 4),
    (4, N'Giày Boost Nam Ultra', 'SP010', 1, 4, 5);

	-- Thêm dữ liệu vào bảng sản phẩm chi tiết
DECLARE @IdSanPham INT = 1;
DECLARE @IdMauSac INT = 1;
DECLARE @IdSize INT = 1;
DECLARE @MaChiTietSanPham NVARCHAR(50) = 'SPCT001';

-- Vòng lặp thêm 100 sản phẩm chi tiết
WHILE @IdSanPham <= 10
BEGIN
    DECLARE @Count INT = 1;
    WHILE @Count <= 3
    BEGIN
        INSERT INTO chi_tiet_san_pham (
            id_san_pham, id_mau_sac, id_size, ma_chi_tiet_san_pham, 
            id_san_pham_khuyen_mai, so_luong, gia_ban, 
            ngay_nhap, trang_thai, anh_spct
        )
        VALUES (
            @IdSanPham, 
            @IdMauSac, 
            @IdSize, 
            @MaChiTietSanPham, 
            NULL, 
            50, 
            100, 
            GETDATE(), 
            1, 
            'https://example.com/images/SPCT' + CAST(@IdSanPham AS NVARCHAR) + '_' + CAST(@Count AS NVARCHAR) + '.jpg'
        );

        -- Tăng mã sản phẩm chi tiết
        SET @MaChiTietSanPham = 'SPCT' + RIGHT('000' + CAST(CAST(SUBSTRING(@MaChiTietSanPham, 5, LEN(@MaChiTietSanPham)) AS INT) + 1 AS NVARCHAR), 3);

        -- Thay đổi màu sắc và size luân phiên
        SET @IdMauSac = CASE WHEN @IdMauSac = 3 THEN 1 ELSE @IdMauSac + 1 END;
        SET @IdSize = CASE WHEN @IdSize = 6 THEN 1 ELSE @IdSize + 1 END;

        SET @Count = @Count + 1;
    END;

    SET @IdSanPham = @IdSanPham + 1;
END;


INSERT INTO khach_hang (ho_ten, ngay_sinh, so_dien_thoai, email, gioi_tinh, xa, huyen, thanh_pho, mat_khau, trang_thai, dia_chi)
VALUES 
(N'Nguyễn Văn A', '1990-05-15', '0912345678', 'nguyenvana@gmail.com', N'Nam', N'Phường 1', N'Quận 1', N'Hồ Chí Minh', '123456', N'Hoạt động', N'123 đường ABC'),
(N'Phạm Thị B', '1995-08-20', '0987654321', 'phamthib@gmail.com', N'Nữ', N'Phường 2', N'Quận 2', N'Hà Nội', 'abcdef', N'Hoạt động', N'456 đường DEF');

INSERT INTO admin (ho_ten, email, mat_khau, trang_thai)
VALUES 
('Admin A', 'adminA@gmail.com', 'passA', 1),
('Admin B', 'adminB@gmail.com', 'passB', 1),
('Admin C', 'adminC@gmail.com', 'passC', 1),
('Admin D', 'adminD@gmail.com', 'passD', 1),
('Admin E', 'adminE@gmail.com', 'passE', 1),
('Admin F', 'adminF@gmail.com', 'passF', 1),
('Admin G', 'adminG@gmail.com', 'passG', 1),
('Admin H', 'adminH@gmail.com', 'passH', 1),
('Admin I', 'adminI@gmail.com', 'passI', 1),
('Admin J', 'adminJ@gmail.com', 'passJ', 1);


INSERT INTO gio_hang (id_khach_hang, id_chi_tiet_san_pham, so_luong, tong_tien)
VALUES 
(1, 1, 2, 1000),
(2, 2, 1, 500);



INSERT INTO don_hang (id_khach_hang, ten_khach_hang, email, so_dien_thoai, ma_don_hang, ngay_dat_hang, so_luong, dia_chi, tong_tien, trang_thai, hinh_thuc, id_phuong_thuc, ghi_chu, tien_ship)
VALUES 
(1, N'Nguyễn Văn A', 'nguyenvana@gmail.com', '0912345678', 'DH001', GETDATE(), 2, N'123 đường ABC', 2000, N'Đã giao', N'COD', 1, N'Giao hàng nhanh',0),
(2, N'Phạm Thị B', 'phamthib@gmail.com', '0987654321', 'DH002', GETDATE(), 1, N'456 đường DEF', 1000, N'Chờ xác nhận', N'Thẻ tín dụng', 2, N'Liên hệ trước khi giao',0);

INSERT INTO san_pham_khuyen_mai (id_chi_tiet_san_pham, id_khuyen_mai, gia_moi)
VALUES 
(1, 1, 900),
(2, 1, 800);

INSERT INTO hoa_don_chi_tiet (id_don_hang, id_chi_tiet_san_pham, so_luong, don_gia)
VALUES 
(1, 1, 2, 900),
(2, 2, 1, 800);



update chi_tiet_san_pham set anh_spct = 'https://png.pngtree.com/png-vector/20201128/ourlarge/pngtree-casual-shoes-png-image_2394294.jpg'

select*from chi_tiet_san_pham where id=1

