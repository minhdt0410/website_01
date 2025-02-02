function loadingg() {
    const provinceName = document.getElementById("thanhPho").value;
    const districtName = document.getElementById("huyen").value;
    const wardName = document.getElementById("xa").value;

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
                    }
                    wardSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching wards:', error));
    } else {
        return Promise.resolve();
    }
}

function getAddress(){
    const province = document.getElementById("province").value;
    const district = document.getElementById("district").value;
    const ward = document.getElementById("ward").value;

    document.getElementById("thanhPho").value = document.querySelector(`select option[value="${province}"]`).innerText;
    document.getElementById("huyen").value = document.querySelector(`select option[value="${district}"]`).innerText;
    document.getElementById("xa").value = document.querySelector(`select option[value="${ward}"]`).innerText;
}

function checkValidate(e) {
    e.preventDefault();

    const form = document.getElementById('myForm');

    const formData = new FormData(form);
    let dataCheck = {};

    formData.forEach((value, key) => {
        dataCheck[key] = value.trim();
    });

    if (dataCheck.hoTen === "") {
        showToast("Không được bỏ trống tên", "error")
        return;
    }

    if (dataCheck.soDienThoai === "" || dataCheck.soDienThoai.length !== 10 || !/^\d{10}$/.test(dataCheck.soDienThoai)) {
        // console.log("Số điện thoại phải là 10 chữ số")
        showToast("Số điện thoại phải là 10 số", "error")
        return;
    }

    if (dataCheck.ngaySinh === "") {
        // console.log("Không được bỏ trống ngày sinh")
        showToast("Không được bỏ trống ngày sinh", "error")
        return;
    }

    const birthYear = new Date(dataCheck.ngaySinh).getFullYear();
    const currentYear = new Date().getFullYear();
    if (birthYear > currentYear - 16) {
        // console.log("Người dùng phải trên 16 tuổi")
        showToast("Người dùng phải trên 16 tuổi", "error")
        return;
    }

    if (dataCheck.email === "") {
        // error.textContent = "Không được bỏ trống email";
        showToast("Không được bỏ trống email", "error")
        return;
    }

    if (!isValidEmail(dataCheck.email)) {
        // error.textContent = "Email không hợp lệ";
        showToast("Email không hợp lệ", "error")
        return;
    }
    if (dataCheck.thanhPho === "") {
        // let error;
        // error.textContent = "Hãy chọn thành phố";
        showToast("Hãy chọn thành phố", "error")
        return;
    }

    if (dataCheck.huyen === "") {
        showToast("Hãy chọn huyện", "error")
        return;
    }

    if (dataCheck.xa === "") {
        // error.textContent = "Hãy chọn xã";
        showToast("Hãy chọn xã", "error")
        return;
    }

    if (dataCheck.diaChi === "") {
        console.log("Không được bỏ trống địa chỉ")
        return;
    }
    const id = document.getElementById("id").value;
    $.ajax({
        url: "/api/khachHang/data?id=" + id,
        method: "GET",
        success: function (data) {
            console.log(id, data)
            const isPhoneDuplicate = data.some(item => item.soDienThoai === dataCheck.soDienThoai);
            const isEmailDuplicate = data.some(item => item.email === dataCheck.email);

            if (isPhoneDuplicate) {
                // error.textContent = "Số điện thoại đã được sử dụng";
                showToast("Số điện thoại đã được sử dụng", "error")
                return;
            }

            if (isEmailDuplicate) {
                showToast("Email đã được sử dụng", "error")
                return;
            }
            Swal.fire({
                title: 'Xác nhận sửa khách hàng',
                html: 'Bạn có chắc chắn muốn sửa khách hàng không?',
                icon: 'warning',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    form.submit();
                }
            });
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function isValidEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
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
