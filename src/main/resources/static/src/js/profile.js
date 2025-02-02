function loadProfile() {
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
                    }
                    wardSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching wards:', error));
    } else {
        return Promise.resolve();
    }
}

function editProfile() {
    document.getElementById('editForm').style.display = 'block';
    document.getElementById('editBtn').style.display = 'none';
}

function saveChanges() {
    const id = document.getElementById("id").value;
    const name = document.getElementById("editName").value.trim();
    const email = document.getElementById("editEmail").value.trim();
    const phone = document.getElementById("editPhone").value.trim();
    const date = document.getElementById("editDate").value;
    const genderRdo = document.getElementById("inlineRadio1");
    const province = document.getElementById("province").value;
    const district = document.getElementById("district").value;
    const ward = document.getElementById("ward").value;
    const address = document.getElementById("editAddress").value.trim();
    let gender = 'Nữ';
    if(genderRdo.checked){
        gender = 'Nam';
    }
    if(validateForm(name, email, phone, date, province, district, ward, address)){
        const data = {
            hoTen : name,
            soDienThoai : phone,
            ngaySinh : date,
            gioiTinh : gender,
            email : email,
            xa : document.querySelector(`select option[value="${ward}"]`).innerText,
            huyen : document.querySelector(`select option[value="${district}"]`).innerText,
            thanhPho : document.querySelector(`select option[value="${province}"]`).innerText,
            diaChi : address
        }

        $.ajax({
            url : "/api/v1/web/editProfile/" + id,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success : function (data){
                document.getElementById('username').innerText = name
                document.getElementById('userEmail').textContent = email;
                document.getElementById('userPhone').textContent = phone;
                document.getElementById("userDate").textContent = date;
                document.getElementById("userGender").textContent = gender;
                document.getElementById('userAddress').textContent = data;
                cancelEdit();
                showToast("Thành công", "success");
            },
            error : function (error){
                console.log(error);
                showToast("Lỗi hệ thống", "warning");
            }
        })
    }

}

function validateForm(name, email, phone, date, province, district, ward, address) {
    if(name === ""){
        showToast("Vui lòng nhập họ tên.", "warning");
        return false;
    }
    if(date === ""){
        showToast("Vui lòng nhập ngày sinh.", "warning");
        return false;
    }

    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (email === "") {
        showToast("Vui lòng nhập địa chỉ email.", "warning");
        return false;
    } else if (!emailPattern.test(email)) {
        showToast("Vui lòng nhập địa chỉ email hợp lệ.", "warning");
        return false;
    }

    const phonePattern = /^[0-9]{10}$/;
    if (phone === "") {
        showToast("Vui lòng nhập số điện thoại.", "warning");
        return false;
    } else if (!phonePattern.test(phone)) {
        showToast("Vui lòng nhập số điện thoại hợp lệ (10 chữ số).", "warning");
        return false;
    }

    if (province === "") {
        showToast("Vui lòng chọn Tỉnh/Thành phố.", "warning");
        return false;
    }

    if (district === "") {
        showToast("Vui lòng chọn Quận/Huyện.", "warning");
        return false;
    }

    if (ward === "") {
        showToast("Vui lòng chọn Xã.", "warning");
        return false;
    }

    if (address === "") {
        showToast("Vui lòng nhập Địa chỉ chi tiết.", "warning");
        return false;
    }

    return true;
}

function cancelEdit() {
    document.getElementById('editForm').style.display = 'none';
    document.getElementById('username').style.display = 'block';
    // document.getElementById('joinDate').style.display = 'block';
    document.getElementById('userEmail').style.display = 'block';
    document.getElementById('userPhone').style.display = 'block';
    document.getElementById('userAddress').style.display = 'block';
    document.getElementById('editBtn').style.display = 'inline';
}

function showChangePasswordForm() {
    document.getElementById('changePasswordForm').style.display = 'block';
    document.querySelector('.change-password-btn').style.display = 'none';
}

function changePassword() {
    const id = document.getElementById('id').value;
    const oldPassword = document.getElementById('oldPassword').value.trim();
    const newPassword = document.getElementById('newPassword').value.trim();
    const confirmPassword = document.getElementById('confirmPassword').value.trim();

    if (newPassword !== confirmPassword) {
        showToast("Mật khẩu mới và mật khẩu xác nhận không khớp!", "warning");
        return;
    }

    if (newPassword.length < 6) {
        showToast("Mật khẩu mới phải có ít nhất 6 ký tự!", "warning");
        return;
    }

    $.ajax({
        url: "/api/v1/web/resetPass/" + id + "?password=" + newPassword + "&oldPassword=" + oldPassword,
        method: "PUT",
        success: function (data) {
            if(data !== ""){
                showToast(data, 'warning');
                return;
            }
            showToast("Đổi mật khẩu thành công!", "success");
            [document.getElementById('oldPassword'), document.getElementById('newPassword'), document.getElementById('confirmPassword')]
                .forEach(item => item.value = '');
            cancelPasswordChange();
        },
        error: function (error) {
            console.log(error);
            showToast("Lỗi hệ thống", "warning");
        }
    })
}

function cancelPasswordChange() {
    document.getElementById('changePasswordForm').style.display = 'none';
    document.querySelector('.change-password-btn').style.display = 'block'; // Hiển thị lại nút Đổi mật khẩu
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