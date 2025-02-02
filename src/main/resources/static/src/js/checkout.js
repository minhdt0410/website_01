const weightOfProductDetail = 400;

function loadCheckout(){
    loadCheckoutPage();

    const provinceName = document.getElementById("provinceName").value;
    const districtName = document.getElementById("districtName").value;
    const wardName = document.getElementById("wardName").value;

    loadProvinces(provinceName, districtName, wardName);
}

function loadProvinces(thanhPho, huyen, xa) {
    $.ajax({
        url: 'https://online-gateway.ghn.vn/shiip/public-api/master-data/province',
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
        },
        success: function (data) {
            console.log(data)
            let provinceSelect = document.getElementById('province');
            provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';

            data.data.forEach(function (province) {
                let option = document.createElement('option');
                option.value = province.ProvinceID;
                option.text = province.ProvinceName;
                if(thanhPho === province.ProvinceName){
                    option.selected = true;
                }
                provinceSelect.add(option);
            });
            if(huyen !== ""){
                loadDistricts(huyen, xa);
            }
        },
        error: function (xhr, status, error) {
            console.error('Error fetching provinces:', error);
        }
    });
}

function loadDistricts(huyen, xa) {
    let provinceId = document.getElementById('province').value;
    let districtSelect = document.getElementById('district');
    districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
    document.getElementById('ward').innerHTML = '<option value="">Chọn phường/xã</option>';
    if (provinceId) {
        return fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/district', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
            },
            body: JSON.stringify({"province_id": parseInt(provinceId)})
        })
            .then(response => response.json())
            .then(data => {
                data.data.forEach(district => {
                    let option = document.createElement('option');
                    option.value = district.DistrictID;
                    option.text = district.DistrictName;
                    if(huyen === district.DistrictName){
                        option.selected = true;
                    }
                    districtSelect.add(option);
                });
                if(xa !== ""){
                    loadWards(xa)
                }
            })
            .catch(error => console.error('Error fetching districts:', error));
    } else {
        return Promise.resolve();
    }
}

function loadWards(xa) {
    let districtId = document.getElementById('district').value;
    let wardSelect = document.getElementById('ward');
    wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';

    if (districtId) {
        return fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/ward', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
            },
            body: JSON.stringify({"district_id": parseInt(districtId)})
        })
            .then(response => response.json())
            .then(data => {
                data.data.forEach(ward => {
                    let option = document.createElement('option');
                    option.value = ward.WardCode;
                    option.text = ward.WardName;
                    if(xa === ward.WardName){
                        option.selected = true;
                        let qty = 0;
                        listGioHang.forEach(item => {
                            qty += item.soLuong;
                        })
                        console.log(qty)
                        const weight = Math.min(qty * weightOfProductDetail, 4999);
                        ship(districtId, weight);
                    }
                    wardSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching wards:', error));
    } else {
        return Promise.resolve();
    }
}

function loadXa(value){
    let xa = document.querySelector(`select option[value="${value}"]`).innerText;
    loadWards(xa);
}

function ship(toDistrictId, weight){
    const fromDistrictId = 3440;
    getServiceId(parseInt(fromDistrictId), parseInt(toDistrictId), weight)
        .then(serviceId => {
            if (!serviceId) {
                showToast('Không tìm thấy mã dịch vụ phù hợp.', "info");
                return;
            }
            return fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
                },
                body: JSON.stringify({
                    "from_district_id": parseInt(fromDistrictId),
                    "service_id": serviceId,
                    "to_district_id": parseInt(toDistrictId),
                    "weight": weight,
                    "length": 20,
                    "width": 15,
                    "height": 5
                })
            });
        })
        .then(response => response.json())
        .then(data => {
            if (data && data.code === 200) {
                console.log(data.data.total)
                const ship = document.getElementById("shipping");
                ship.innerText = data.data.total.toLocaleString() + " VND";
                const tongTien = document.getElementById("tongTien").innerText;
                const shipping = document.getElementById("shipping").innerText;
                const totalPrice = document.getElementById("totalPrice");
                const tongTienValue = parseInt(tongTien.replace(/\D/g, ''));
                const shippingValue = parseInt(shipping.replace(/\D/g, ''));
                const total = tongTienValue + shippingValue;
                totalPrice.innerText = total.toLocaleString() + ' VND';
            } else {
                console.error('Lỗi tính phí ship:', data.message);
            }
        })
}

function getServiceId(fromDistrictId, toDistrictId, weight) {
    return fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
        },
        body: JSON.stringify({
            "shop_id": 5445966,
            "from_district": fromDistrictId,
            "to_district": toDistrictId
        })

    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200 && data.data.length > 0) {
                console.log(data.data)
                const service = data.data.find(s => (weight < 5000 && s.short_name === "Hàng nhẹ") || (weight >= 5000 && s.short_name === "Hàng nặng"));
                return service ? service.service_id : null;
            } else {
                throw new Error('Không có dịch vụ nào khả dụng hoặc lỗi khi lấy service_id');
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy service_id:', error);
        });
}

const listGioHang = JSON.parse(sessionStorage.getItem('listGioHang'));

function loadCheckoutPage(){
    console.log(listGioHang)
    const tongTien = document.getElementById("tongTien");
    const listSP = document.getElementById("listSP");
    let total = 0;
    listGioHang.forEach(item => {
        listSP.innerHTML += `<div class="minicart-item d-flex">
                                        <div class="mini-img-wrapper">
                                            <img class="mini-img" src="${item.sanPhamChiTiet.anh_spct}" alt="img">
                                        </div>
                                        <div class="product-info">
                                            <h2 class="product-title">${item.sanPhamChiTiet.sanPham.tensanpham}</h2>
                                            <p class="product-vendor">${item.sanPhamChiTiet.size.ten_size} / ${item.sanPhamChiTiet.mauSac.tenmausac}</p>
                                            <p class="product-vendor">${item.sanPhamChiTiet.khuyenMaiChiTiet == null ? parseInt(item.sanPhamChiTiet.gia_ban, 10).toLocaleString() : parseInt(item.sanPhamChiTiet.khuyenMaiChiTiet.giaMoi, 10).toLocaleString()} <del>${item.sanPhamChiTiet.khuyenMaiChiTiet == null ? '' : parseInt(item.sanPhamChiTiet.gia_ban).toLocaleString()}</del> x ${item.soLuong}</p>
                                        </div>
                                    </div>`;
        total += item.tongTien
    })
    tongTien.innerText = total.toLocaleString() + ' VND';
}

let userData = {}

function validateForm() {
    // Lấy các giá trị từ form
    const id = document.getElementById("idKh").value;
    const hoTen = document.getElementsByName("hoTen")[0].value.trim();
    const email = document.getElementsByName("email")[0].value.trim();
    const soDienThoai = document.getElementsByName("soDienThoai")[0].value.trim();
    const diaChi = document.getElementsByName("diaChi")[0].value.trim();
    const thanhPho = document.getElementsByName("thanhPho")[0].value;
    const huyen = document.getElementsByName("huyen")[0].value;
    const xa = document.getElementsByName("xa")[0].value;

    // Kiểm tra từng trường và hiển thị lỗi đầu tiên gặp phải
    if (hoTen === "") {
        showToast("Họ tên không được để trống.", "info");
        return false;
    }

    // Kiểm tra định dạng email
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (email === "") {
        showToast("Email không được để trống.", "info");
        return false;
    } else if (!emailPattern.test(email)) {
        showToast("Email không hợp lệ.", "info");
        return false;
    }

    // Kiểm tra số điện thoại (chỉ gồm 10 chữ số)
    const phonePattern = /^\d{10}$/;
    if (soDienThoai === "") {
        showToast("Số điện thoại không được để trống.", "info");
        return false;
    } else if (!phonePattern.test(soDienThoai)) {
        showToast("Số điện thoại phải gồm 10 chữ số.", "info");
        return false;
    }

    // Kiểm tra địa chỉ chi tiết
    if (diaChi === "") {
        showToast("Địa chỉ chi tiết không được để trống.", "info");
        return false;
    }

    // Kiểm tra các trường Tỉnh/Thành phố, Quận/Huyện, Phường/Xã
    if (thanhPho === "") {
        showToast("Vui lòng chọn Tỉnh/Thành phố.", "info");
        return false;
    }
    if (huyen === "") {
        showToast("Vui lòng chọn Quận/Huyện.", "info");
        return false;
    }
    if (xa === "") {
        showToast("Vui lòng chọn Phường/Xã.", "info");
        return false;
    }
    let province = document.querySelector(`select option[value="${thanhPho}"]`).innerText;
    let district = document.querySelector(`select option[value="${huyen}"]`).innerText;
    let ward = document.querySelector(`select option[value="${xa}"]`).innerText;

    // Tạo mảng hoặc đối tượng khachHang với các thông tin từ form
    userData = {
        id: id,
        hoTen: hoTen,
        email: email,
        soDienThoai: soDienThoai,
        diaChi: diaChi,
        thanhPho: province,
        huyen: district,
        xa: ward
    };
    return true;
}


function checkout(e){
    e.preventDefault();
    if (validateForm()){
        const formSubmit = document.getElementById("formCheckOut");
        const inputs = document.querySelectorAll('.integers');
        const integers = Array.from(inputs).map(input => parseInt(input.value, 10));
        const ship = document.getElementById("shipping").innerText;
        const shipValue = parseInt(ship.replace(/\D/g, ''));
        let data = {
            gioHangs : listGioHang,
            khachHang : userData,
            ship: shipValue
        }
        const radio = document.getElementById("flexRadioDefault1");
        if(radio.checked){
            $.ajax({
                url : `/checkout/successCheckout?payment=1&ship=${shipValue}`,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(data),
                success : function (message){
                    if(message !== ""){
                        sessionStorage.setItem("message", message);
                        window.location.href = "/home/cart";
                    }else {
                        formSubmit.submit();
                    }
                }
            })
        }else {
            const tongTien = document.getElementById("totalPrice").innerText;
            const tongTienValue = parseInt(tongTien.replace(/\D/g, ''));
            $.ajax({
                url : "/api/v1/payment/createPayment?money=" + tongTienValue,
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(data),
                success : function (dto){
                    console.log(dto)
                    if(dto.message !== null){
                        sessionStorage.setItem("message", dto.message);
                        window.location.href = "/home/cart";
                    }else {
                        sessionStorage.setItem("data", JSON.stringify(data));
                        window.location.href = dto.url;
                    }
                },
                error: function (error){
                    console.log(error)
                }
            })
        }

    }

}

function showToast(message, status) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });
    Toast.fire({
        icon: status,
        title: message
    });
}
