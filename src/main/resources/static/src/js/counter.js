let idBill = 0;
let weightOfProductDetail = 400;

function loading() {
    renderBill();
}

function renderBill() {
    $.ajax({
        url: "/api/v1/counter",
        method: "GET",
        success: function (data) {
            const list = document.getElementById("list-bill");
            list.innerHTML = ``;
            if (data.length > 0) {
                data.forEach(id => {
                    list.innerHTML += `<button class="btn" onclick="highlightButton(this, ${id})" id="btn-bill-${id}">#${id}</button>`;
                })
            }
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function createBill() {
    $.ajax({
        url: "/api/v1/counter/createBill",
        method: "POST",
        success: function (data) {
            const list = document.getElementById("list-bill");
            if (!isNaN(data)) {
                list.innerHTML += `<button class="btn" onclick="highlightButton(this, ${data})">#${data}</button>`;
                showToast("Tạo thành công", "success");
            } else {
                showToast(data, 'info');
            }
        }
    })
}

function highlightButton(button, id) {
    let buttons = document.querySelectorAll('#list-bill .btn');
    buttons.forEach(btn => btn.classList.remove('active'));

    button.classList.add('active');
    idBill = id;
    renderBillDetail(idBill);
}

function renderThuocTinh() {
    console.log("haha")
    $.ajax({
        url: "/api/v1/counter/getElement",
        method: "GET",
        success: function (data) {
            console.log(data)
            const listName = document.getElementById("list-name");
            const listSize = document.getElementById("list-size");
            const listColor = document.getElementById("list-color");

            listName.innerHTML = ``;
            listSize.innerHTML = ``;
            listColor.innerHTML = ``;

            data.names.forEach(item => {
                listName.innerHTML += `<option value="${item}">`;
            })
            data.sizes.forEach(item => {
                listSize.innerHTML += `<option value="${item}">`;
            })
            data.colors.forEach(item => {
                listColor.innerHTML += `<option value="${item}">`;
            })
            renderProductDetail();
        }
    })
}

function renderProductDetail() {
    const name = document.getElementById("name").value.trim();
    const size = document.getElementById("size").value.trim();
    const color = document.getElementById("color").value.trim();
    const id = document.getElementById("id-product-detail").value.trim();

    const data = {
        name: name,
        size: size,
        color: color,
        id: id
    };

    setTimeout(function (){
        $.ajax({
            url: "/api/v1/counter/getAllProductDetail",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (data) {
                console.log(data);
                let listProductDetail = document.getElementById("list-product-detail");
                let fragment = document.createDocumentFragment(); // Tạo fragment

                let i = 1;

                data.forEach(item => {
                    let price = 0;
                    let oldPrice = 0;
                    if (item.khuyenMaiChiTiet === null) {
                        price = item.gia_ban;
                    } else {
                        if (item.khuyenMaiChiTiet.khuyenMai.tinhTrang === "Đang diễn ra") {
                            oldPrice = item.gia_ban;
                            if (item.khuyenMaiChiTiet.khuyenMai.giaTriGiam >= 100) {
                                price = (item.gia_ban - item.khuyenMaiChiTiet.khuyenMai.giaTriGiam);
                            } else {
                                price = (item.gia_ban - (item.gia_ban * item.khuyenMaiChiTiet.khuyenMai.giaTriGiam / 100));
                            }
                        } else {
                            price = item.gia_ban;
                        }
                    }

                    let row = document.createElement("tr");
                    row.innerHTML = `<th scope="row">${i++}</th>
                                 <td>${item.sanPham.tensanpham} [ ${item.size.ten_size} - ${item.mauSac.tenmausac} ]</td>
                                 <td>${parseInt(price, 10).toLocaleString()} <sup>đ</sup>
                                    <del>${oldPrice === 0 ? `` : `${parseInt(oldPrice, 10).toLocaleString()} <sup>đ</sup>`}</del>
                                 </td>
                                 <td id="quantity-${item.id}">${item.so_luong}</td>
                                 <td>
                                    <button class="btn" onclick="addProductDetailToCart(${item.id}, this)">Chọn</button>
                                 </td>`;

                    fragment.appendChild(row);
                });

                listProductDetail.innerHTML = "";
                listProductDetail.appendChild(fragment);
            }
        });
    }, 500);
}

function renderBillDetail(idBill) {
    document.getElementById('cart').style.display = 'block';
    $.ajax({
        url: `/api/v1/counter/getBillDetails/${idBill}`,
        method: "GET",
        success: function (data) {
            if (data.length === 0) {
                document.getElementById('noProduct').innerHTML = `<div class="">
                                                Không có sản phẩm nào trong giỏ hàng
                                            </div>`;
            } else {
                document.getElementById('noProduct').innerHTML = `<table class="table">
                                                <thead>
                                                <tr class="text-center">
                                                    <th scope="col">STT</th>
                                                    <th scope="col">Sản phẩm</th>
                                                    <th scope="col">Giá bán</th>
                                                    <th scope="col">Số lượng</th>
                                                    <th scope="col">Tổng tiền</th>
                                                    <th scope="col">Hoạt động</th>
                                                </tr>
                                                </thead>
                                                <tbody class="text-center" id="list-cart">

                                                </tbody>
                                            </table>`;

                let listCart = document.getElementById("list-cart");
                listCart.innerHTML = ``;
                let i = 1;
                data.forEach(item => {
                    let price = item.donGia / item.soLuong;
                    let oldPrice = parseInt(item.sanPhamChiTiet.gia_ban, 10);
                    console.log(price, oldPrice)
                    listCart.innerHTML += `<tr>
                                                    <th scope="row">${i++}</th>
                                                    <td>${item.sanPhamChiTiet.sanPham.tensanpham} [ ${item.sanPhamChiTiet.size.ten_size} - ${item.sanPhamChiTiet.mauSac.tenmausac} ]</td>
                                                    <td>${price.toLocaleString()} <sup>đ</sup>
                                                        <del>${oldPrice === price ? `` : `${oldPrice.toLocaleString()} <sup>đ</sup>`}</del>
                                                    </td>
                                                    <td><input type="number" class="w-25" style="height: 30px" value="${item.soLuong}" onchange="checkQty(${item.id}, this)"></td>
                                                    <td id="total-price-billDetail-${item.id}">${parseInt(item.donGia, 10).toLocaleString()} <sup>đ</sup></td>
                                                    <td>
                                                        <button class="btn" onclick="removeBillDetail(${item.id}, this.parentNode.parentNode)"><i class="fa-solid fa-trash"></i></button>
                                                    </td>
                                                </tr>`
                })
            }
            showBillInfo();
            console.log(data)
        },
        error: function (error) {
            console.log(error)
        }
    })
}

function addProductDetailToCart(idProductDetail, btn) {
    console.log(idProductDetail)
    btn.disabled = true;
    btn.innerText = "...";
    setTimeout(function() {
        $.ajax({
            url: `/api/v1/counter/addToCart/${idBill}/${idProductDetail}`,
            method: "POST",
            success: function (data) {
                console.log(data)
                if (data < 0) {
                    showToast("Sản phẩm đã hết hàng", "info");
                    return;
                }
                document.getElementById('quantity-' + idProductDetail).innerText = data;
                showToast("Đã thêm sản phẩm vào giỏ hàng", "success");
            },
            complete: function (){
                btn.disabled = false;
                btn.innerText = "Chọn";
                renderBillDetail(idBill);
                const ship = document.getElementById("ship");
                if(ship.checked){
                    getIdDistrict();
                }
            }
        })
    }, 500);
}

function checkQty(idBillDetail, input) {
    $.ajax({
        url: `/api/v1/counter/checkQty/${idBillDetail}?quantity=${input.value}`,
        method: "PUT",
        success: function (data) {
            console.log(data)
            input.value = data[0];
            document.getElementById(`total-price-billDetail-${idBillDetail}`).innerHTML = `${parseInt(data[1], 10).toLocaleString()} <sup>đ</sup>`;
            if (data[2] !== "") {
                showToast(data[2], "info");
            }
            getIdDistrict();
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function removeBillDetail(idBillDetail, tr) {
    $.ajax({
        url: `/api/v1/counter/removeBillDetail/${idBillDetail}`,
        method: "DELETE",
        success: function () {
            tr.remove();
            showToast("Xoá sản phẩm khỏi giỏ hàng", "success");
            showBillInfo();
            getIdDistrict();
        },
        error: function (error) {
            console.log(error);
            tr.remove();
        }
    })
}

function showBillInfo() {
    $.ajax({
        url: `/api/v1/counter/getBillDetails/${idBill}`,
        method: "GET",
        success: function (data) {
            if (data.length > 0) {
                document.getElementById('bill').style.display = 'block';
                renderBankInfo();
            } else {
                document.getElementById('bill').style.display = 'none';
                document.getElementById('noProduct').innerHTML = `<div class="">
                                                Không có sản phẩm nào trong giỏ hàng
                                            </div>`;
            }
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function renderBankInfo() {
    $.ajax({
        url: `/api/v1/counter/getBill/${idBill}`,
        method: "GET",
        success: function (data) {
            console.log(data)
            const bankInfo = document.getElementById('bank-info');
            bankInfo.innerHTML = `<div class="col-12">
                                                <h4>Thông tin thanh toán</h4>
                                                <div class="d-flex mt-4">
                                                    <div class="col-5">
                                                        <strong>Khách hàng:</strong>
                                                    </div>
                                                    <div class="col-7 text-end">
                                                        <strong style="cursor: pointer" data-bs-toggle="modal" data-bs-target="#staticBackdrop" onclick="modalCustomer('')">${data.khachHang === null ? `Chọn khách hàng` : `${data.khachHang.hoTen}`}</strong>
                                                    </div>
                                                </div>
                                                <div class="d-flex align-items-center">
                                                    <div class="me-5">
                                                        <strong>Giao hàng</strong>
                                                    </div>
                                                    <div class="form-check form-switch">
                                                        <input class="form-check-input" onchange="showAddress()" type="checkbox" id="ship" ${data.diaChi === null ? '' : 'checked'}>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-5">
                                                    <strong>Tiền sản phẩm:</strong>
                                                </div>
                                                <div class="col-7 text-end">
                                                    <strong>${data.tongTien.toLocaleString()} <sup>đ</sup></strong>
                                                </div>
                                                <div class="col-5 mt-3">
                                                    <strong>Tiền ship:</strong>
                                                </div>
                                                <div class="col-7 text-end mt-3">
                                                    <strong id="ship-money">${data.tienShip.toLocaleString()} <sup>đ</sup></strong>
                                                </div>
                                                <div class="col-12 text-center mt-2">
                                                    <hr class="w-100">
                                                </div>
                                                <div class="col-5">
                                                    <strong>Tổng tiền:</strong>
                                                </div>
                                                <div class="col-7 text-end">
                                                    <strong id="total-bill">${(data.tongTien + data.tienShip).toLocaleString()} <sup>đ</sup></strong>
                                                </div>
                                            </div>`;
            if(data.hinhThuc === 'Tiền mặt'){
                document.getElementById("option1").checked = true;
            }else {
                document.getElementById("option2").checked = true;
            }
            showAddress();
        },
        error: function (error) {
            console.log(error);
        },
    })
}

function showAddress() {
    const ship = document.getElementById('ship');
    const address = document.getElementById('address-info');

    $.ajax({
        url: `/api/v1/counter/getBill/${idBill}`,
        method: "GET",
        success: function (data) {
            if (ship.checked) {
                address.style.display = 'block';
                const recipientName = document.getElementById("recipient-name");
                const phoneNumber = document.getElementById("phone-number");
                const detailedAddress = document.getElementById("detailed-address");
                $.ajax({
                    url: `/api/v1/counter/updateAddress/${idBill}?status=1`,
                    method: "PUT",
                    success: function (data) {
                        console.log(data)
                        if(data.diaChi !== null){
                            let parts = data.diaChi.split(',').map(part => part.trim()).filter(Boolean);
                            let xa = parts[parts.length - 3];
                            let huyen = parts[parts.length - 2];
                            let thanhPho = parts[parts.length - 1];
                            let diaChiChiTiet = parts.slice(0, parts.length - 3).join(", ");

                            recipientName.value = data.tenKhachHang;
                            phoneNumber.value = data.soDienThoai;
                            detailedAddress.value = diaChiChiTiet;

                            loadProvinces(thanhPho, huyen, xa);
                        } else {
                            loadProvinces("", "", "")
                        }
                    },
                    error: function (error) {
                        console.log(error);
                    }
                })
            } else {
                address.style.display = 'none';
                $.ajax({
                    url: `/api/v1/counter/updateAddress/${idBill}?status=2`,
                    method: "PUT",
                    success: function (data) {
                        document.getElementById('ship-money').innerHTML = `${data.tienShip.toLocaleString()} <sup>đ</sup>`
                        document.getElementById('total-bill').innerHTML = `${(data.tongTien + data.tienShip).toLocaleString()} <sup>đ</sup>`
                    },
                    error: function (error) {
                        console.log(error);
                    }
                })
            }
        },
        error: function (error) {
            console.log(error);
        }
    })
}


function submitAddress() {
    const recipientName = document.getElementById("recipient-name").value.trim();
    const phoneNumber = document.getElementById("phone-number").value.trim();
    const city = document.getElementById("province").value;
    const district = document.getElementById("district").value;
    const commune = document.getElementById("ward").value;
    const detailedAddress = document.getElementById("detailed-address").value.trim();

    if (recipientName.length <= 6) {
        showToast("Tên người nhận phải có ít nhất 6 ký tự.", "info");
        return;
    }

    const phoneRegex = /^0\d{9}$/;
    if (!phoneRegex.test(phoneNumber)) {
        showToast("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.", "info");
        return;
    }

    if (!city || !district || !commune) {
        showToast("Thành phố, Quận/Huyện và Xã không được bỏ trống.", "info");
        return;
    }

    if (!detailedAddress) {
        showToast("Địa chỉ chi tiết không được bỏ trống.", "info");
        return;
    }

    Swal.fire({
        title: 'Xác nhận địa chỉ giao hàng',
        html: 'Bạn có chắc chắn xác nhận địa chỉ giao hàng này. Hệ thống sẽ tính tiền ship dựa trên địa chỉ giao hàng',
        icon: 'info',
        showCancelButton: true,       // Hiển thị nút "Hủy"
        confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
        cancelButtonText: 'Hủy'        // Nút hủy
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: `/api/v1/counter/getBillDetails/${idBill}`,
                method: "GET",
                success: function (data) {
                    let qty = 0;
                    data.forEach(item => {
                        qty += item.soLuong;
                    })
                    let weight = Math.min(400 * qty, 2999);
                    let huyen = document.querySelector(`select option[value="${district}"]`).innerText;
                    let xa = document.querySelector(`select option[value="${commune}"]`).innerText;
                    let thanhPho = document.querySelector(`select option[value="${city}"]`).innerText;
                    const bill = {
                        tenKhachHang: recipientName,
                        soDienThoai: phoneNumber,
                        diaChi: `${detailedAddress}, ${xa}, ${huyen}, ${thanhPho}`
                    }
                    calculateShippingFee(weight, bill);
                },
                error: function (error) {
                    console.log(error);
                }
            })
        }
    });
}

function modalCustomer(key) {
    document.getElementById("search").value = '';
    $.ajax({
        url: '/api/v1/counter/getAllCustomer?key=' + key.trim(),
        method: 'GET',
        success: function (data) {
            console.log(data)
            const listCustomer = document.getElementById("list-customer");
            listCustomer.innerHTML = `<tr>
                                <th scope="row">0</th>
                                <td>Khách lẻ</td>
                                <td>Không có</td>
                                <td>Không có</td>
                                <td>
                                    <button class="btn" onclick="chooseCustomer(-1)" data-bs-dismiss="modal">Chọn</button>
                                </td>
                            </tr>`;
            let i = 1;
            data.forEach(customer => {
                listCustomer.innerHTML += `<tr>
                                <th scope="row">${i++}</th>
                                <td>${customer.hoTen}</td>
                                <td>${customer.soDienThoai}</td>
                                <td>${customer.email}</td>
                                <td>
                                    <button class="btn" onclick="chooseCustomer(${customer.id})" data-bs-dismiss="modal">Chọn</button>
                                </td>
                            </tr>`;
            })
        },
        error: function (error) {
            console.error(error);
        }
    });
}

function chooseCustomer(idCustomer){
    $.ajax({
        url: `/api/v1/counter/updateCustomerOfBill/${idBill}/${idCustomer}`,
        method: 'PUT',
        success: function () {
            const ship = document.getElementById("ship");
            if(ship.checked){
                getIdDistrict();
            }else {
                renderBankInfo();
            }
        },
        error: function (error) {
            console.error(error);
        }
    });
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

function calculateShippingFee(weight, bill) {
    console.log(weight)
    const fromDistrictId = 3440;
    const toDistrictId = document.getElementById('district').value;
    getServiceId(parseInt(fromDistrictId), parseInt(toDistrictId), weight)
        .then(serviceId => {
            if (!serviceId) {
                console.warn('Không tìm thấy mã dịch vụ phù hợp.');
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
                $.ajax({
                    url: `/api/v1/counter/updateFee/${idBill}?fee=${data.data.total}`,
                    method: "PUT",
                    contentType: "application/json",
                    data: JSON.stringify(bill),
                    success: function () {
                        renderBankInfo();
                    },
                    error: function (error) {
                        console.log(error);
                    }
                })
            } else {
                console.error('Lỗi tính phí ship:', data.message);
            }
        })
        .catch(error => console.error('Lỗi tính phí ship:', error));

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

function updateFee(){
    $.ajax({
        url: `/api/v1/counter/getBillDetails/${idBill}`,
        method: "GET",
        success: function (data) {
            let qty = 0;
            data.forEach(item => {
                qty += item.soLuong;
            })
            let weight = weightOfProductDetail * qty;
            calculateShippingFee(weight);
        },
        error: function (error) {
            console.log(error);
        }
    })
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
                $.ajax({
                    url: `/api/v1/counter/uploadFee/${idBill}?fee=${data.data.total}`,
                    method: "PUT",
                    success: function () {
                        renderBankInfo();
                    },
                    error: function (error) {
                        console.log(error);
                    }
                })
            } else {
                console.error('Lỗi tính phí ship:', data.message);
            }
        })
}

function getIdDistrict(){
    $.ajax({
        url : "/api/v1/counter/getBill/" + idBill,
        method: "GET",
        success: function (data){
            if(data.diaChi !== null){
                let parts = data.diaChi.split(',').map(part => part.trim()).filter(Boolean);
                let huyen = parts[parts.length - 2];
                let thanhPho = parts[parts.length - 1];
                let provinceId = "";
                $.ajax({
                    url: "/api/v1/counter/getBillDetails/" + idBill,
                    method: "GET",
                    success: function (data){
                        let qty = 0;
                        data.forEach(item => {
                            qty += item.soLuong;
                        })
                        if(qty > 0){
                            let weight = Math.min(weightOfProductDetail * qty , 2999);
                            $.ajax({
                                url: 'https://online-gateway.ghn.vn/shiip/public-api/master-data/province',
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
                                },
                                success: function (data) {
                                    data.data.forEach(function (province) {
                                        if(thanhPho === province.ProvinceName){
                                            provinceId = province.ProvinceID
                                        }
                                    });
                                },
                                error: function (xhr, status, error) {
                                    console.error('Error fetching provinces:', error);
                                },
                                complete : function (){
                                    $.ajax({
                                        url: 'https://online-gateway.ghn.vn/shiip/public-api/master-data/district',
                                        method: 'POST',
                                        contentType: 'application/json',
                                        headers: {
                                            'Token': 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'
                                        },
                                        data: JSON.stringify({ "province_id": parseInt(provinceId) }),
                                        success: function(data) {
                                            data.data.forEach(function(district) {
                                                if (huyen === district.DistrictName) {
                                                    ship(district.DistrictID, weight);
                                                }
                                            });
                                        }
                                    })
                                }
                            });
                        }
                    }
                })
            } else {
                renderBankInfo();
            }
        }
    })

}

function formBill(status){
    const province = document.getElementById("province").value.trim();
    const district = document.getElementById("district").value.trim();
    const ward = document.getElementById("ward").value.trim();
    const addressDetail = document.getElementById("detailed-address").value.trim();
    const name = document.getElementById("recipient-name").value.trim();
    const phone = document.getElementById("phone-number").value.trim();

    if(status === 2){
        const ship = document.getElementById("ship");
        $.ajax({
            url: "/api/v1/counter/getBill/" + idBill,
            method: "GET",
            success: function (data){
                if(ship.checked){
                    const address = data.diaChi;
                    let huyen = document.querySelector(`select option[value="${district}"]`).innerText;
                    let xa = document.querySelector(`select option[value="${ward}"]`).innerText;
                    let thanhPho = document.querySelector(`select option[value="${province}"]`).innerText;
                    const nowAddress = `${addressDetail}, ${xa}, ${huyen}, ${thanhPho}`;
                    if(address !== nowAddress || data.tenKhachHang !== name || data.soDienThoai !== phone){
                        showToast("Vui lòng xác nhận địa chỉ", "info");
                        return;
                    }
                    if(data.tienShip <= 0){
                        showToast("Vui lòng xác nhận địa chỉ giao hàng", "info");
                        return;
                    }
                }
                $.ajax({
                    url: `/api/v1/counter/updatePttt/${idBill}?status=${status}` ,
                    method: "PUT",
                    success: function (data){
                        console.log(data)
                        Swal.fire({
                            html: `<img
                                    src="https://img.vietqr.io/image/mbbank-400290412004-s.png?amount=${data.tongTien + data.tienShip}&addInfo=Thanh toán cho hoá đơn ${data.id}&accountName=Cao Thanh Tùng"
                                    alt=""
                                    style="display: block; margin: 0 auto; max-width: 100%; height: auto;" />
                                    <p style="text-align: center;">Chuyển tiền thanh toán QR CODE cho hoá đơn #${data.id}</p>`,
                            icon: null,
                            width: '400px',
                        });
                    },
                    error: function (error){
                        console.log(error)
                    },
                    complete: function (){
                        $.ajax({
                            url: "/api/v1/counter/getBill/" + idBill,
                            method: "GET",
                            success: function (data){
                                console.log(data)
                                if(data.hinhThuc === 'Tiền mặt'){
                                    console.log("abc1")
                                    document.getElementById("option1").checked = true;
                                    document.getElementById("option2").checked = false;
                                }else {
                                    console.log("abc")
                                    document.getElementById("option2").checked = true;
                                    document.getElementById("option1").checked = false;
                                }
                            },
                            error: function (error){
                                console.log(error)
                            }
                        })
                    }
                });
            }
        });
    }else {
        $.ajax({
            url: `/api/v1/counter/updatePttt/${idBill}?status=${status}` ,
            method: "PUT",
            success: function (data){
                console.log(data)
            },
            error: function (error){
                console.log(error)
            }
        });
    }
}

function updateStatusBill(){
    const ship = document.getElementById("ship");
    const province = document.getElementById("province").value.trim();
    const district = document.getElementById("district").value.trim();
    const ward = document.getElementById("ward").value.trim();
    const addressDetail = document.getElementById("detailed-address").value.trim();
    const name = document.getElementById("recipient-name").value.trim();
    const phone = document.getElementById("phone-number").value.trim();

    $.ajax({
        url: "/api/v1/counter/getBill/" + idBill,
        method: "GET",
        success: function (data){
            if(ship.checked){
                const address = data.diaChi;
                let huyen = document.querySelector(`select option[value="${district}"]`).innerText;
                let xa = document.querySelector(`select option[value="${ward}"]`).innerText;
                let thanhPho = document.querySelector(`select option[value="${province}"]`).innerText;
                const nowAddress = `${addressDetail}, ${xa}, ${huyen}, ${thanhPho}`;
                if(address !== nowAddress || data.tenKhachHang !== name || data.soDienThoai !== phone){
                    showToast("Vui lòng xác nhận địa chỉ", "info");
                    return;
                }
                if(data.tienShip <= 0){
                    showToast("Vui lòng xác nhận địa chỉ giao hàng", "info");
                    return;
                }
            }
            Swal.fire({
                title: 'Xác nhận tạo hoá đơn',
                html: 'Bạn có chắc chắn xác nhận tạo hoá đơn này',
                icon: 'info',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: `/api/v1/counter/updateStatusBill/${idBill}` ,
                        method: "PUT",
                        success: function (data){
                            document.getElementById(`btn-bill-${data}`).remove();
                            document.getElementById('cart').style.display = 'none';
                            document.getElementById('bill').style.display = 'none';
                            showToast("Thành công", "success");
                        },
                        error: function (error){
                            console.log(error)
                        }
                    });
                }
            });
        }
    });
}

function removeBill(){
    Swal.fire({
        title: 'Xoá đơn hàng',
        html: 'Bạn có chắc chắn xác nhận xoá hoá đơn này',
        icon: 'info',
        showCancelButton: true,       // Hiển thị nút "Hủy"
        confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
        cancelButtonText: 'Hủy'        // Nút hủy
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: `/api/v1/counter/deleteBill/${idBill}` ,
                method: "DELETE",
                success: function (data){
                    document.getElementById(`btn-bill-${data}`).remove();
                    document.getElementById('cart').style.display = 'none';
                    document.getElementById('bill').style.display = 'none';
                    showToast("Thành công", "success");
                },
                error: function (error){
                    console.log(error)
                }
            });
        }
    });
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