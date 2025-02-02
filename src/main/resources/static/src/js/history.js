function loadHistoryPage(){
    const defaultButton = document.querySelector(".status-buttons button");
    defaultButton.classList.add("active");
    showOrders("Chờ xác nhận", defaultButton);

}

function showOrders(status, button) {
    const statuses = document.querySelectorAll('.order-summary');
    statuses.forEach(function(statusElement) {
        statusElement.classList.remove('active');
    });

    // const activeStatus = document.getElementById(status);
    // if (activeStatus) {
    //     activeStatus.classList.add('active');
    // }

    const buttons = document.querySelectorAll('.status-buttons button');
    buttons.forEach(function(btn) {
        btn.classList.remove('active');
    });

    $.ajax({
        url : "/api/v1/web/history/" + status,
        method : "GET",
        success: function (data){
           console.log(data)
            const row = document.getElementById('listHoaDon');
            row.innerHTML = data.length > 0 ? `<h2>Đơn Hàng ${convertTex(status)}</h2>` : ``;
            data.forEach(item => {
                row.innerHTML += renderHoaDon(item);
                if (status !== "Chờ xác nhận"){
                    if(item.hoaDon.trangThai !== "Xác Nhận"){
                        document.getElementById("btn-cancle-"+item.hoaDon.id).style.display = 'none';
                    }
                }
                if(status !== "Đơn bị hủy"){
                    document.getElementById("rs-"+item.hoaDon.id).style.display = 'none';
                }

                if(status === "Đổi trả"){
                    document.getElementById("rs-"+item.hoaDon.id).style.display = 'inline';
                }

                if(item.hoaDon.trangThai !== "Đã Giao"){
                    document.getElementById("btn-back-"+item.hoaDon.id).style.display = 'none';
                }
            })
        },
        error: function (error){
            console.log(error)
        }
    })

    button.classList.add('active');
}

function renderHoaDon(item){
    let billDetail = ``;
    item.hoaDonChiTiets.forEach(hd => {
        hd.donGia = hd.donGia.toLocaleString() + ' VND';
        billDetail += `<div class="product">
                            <img src="${hd.sanPhamChiTiet.anh_spct}" alt="Sản phẩm 3">
                            <p style="font-size: 18px">${hd.sanPhamChiTiet.sanPham.tensanpham} <sup>x${hd.soLuong}</sup></p>
                            <p>${hd.sanPhamChiTiet.size.ten_size} / ${hd.sanPhamChiTiet.mauSac.tenmausac}</p>  
                            <p>Giá: <span class="price">${hd.donGia}</span></p>
                        </div>`;
    })
    item.hoaDon.trangThai = convertTex(item.hoaDon.trangThai);
    return `
                <div class="order-item" id="${item.hoaDon.id}">
                    <div class="d-flex justify-content-between" style="margin-bottom: 20px;">
                        <h5>Hóa đơn: ${item.hoaDon.maDonHang}</h5>
                        <button class="text-end" id="btn-cancle-${item.hoaDon.id}" data-bs-toggle="modal" data-bs-target="#exampleModal" onclick="idBill(${item.hoaDon.id})">HỦY</button>
                        <button class="text-end" id="btn-back-${item.hoaDon.id}" data-bs-toggle="modal" data-bs-target="#doitraModal"  onclick="idBillDoiTra(${item.hoaDon.id})">TRẢ HÀNG</button>
                        <p id="rs-${item.hoaDon.id}">${item.hoaDon.ghiChu}</p>
                    </div>
                    <div class="item-info">
                        ${billDetail}
                    </div>
                </div>`;
}

function convertTex(str) {
    return str.split(' ').map(function(word) {
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    }).join(' ');
}

function chooseReason(status){
    const description = document.getElementById("description");
    const btnHuyDon = document.getElementById("btn-huydon");
    description.value = status;
    btnHuyDon.disabled = false;
}

function chooseReasonDT(status){
    const description = document.getElementById("descriptionDT");
    const btnDT = document.getElementById("btn-doitra");
    description.value = status;
    btnDT.disabled = false;
}

let id = 0;
function idBill(idBill){
    console.log(idBill)
    id = idBill;
    const description = document.getElementById("description");
    const btnHuyDon = document.getElementById("btn-huydon");
    if(description.value === ""){
        btnHuyDon.disabled = true;
    }else {
        btnHuyDon.disabled = false
    }
}
let idDoiTra = 0;
function idBillDoiTra(idBill){
    idDoiTra = idBill;
    const description = document.getElementById("descriptionDt");
    const btnDoiTra = document.getElementById("btn-doitra");
    if(description.value === ""){
        btnDoiTra.disabled = true;
    }else {
        btnDoiTra.disabled = false
    }
}

function huydon(){
    const description = document.getElementById("description").value;
    $.ajax({
        url : "/khachHang/huydon/" + id + "?message=" + description,
        method : "PUT",
        success : function (){
            const row = document.getElementById(id);
            row.remove();
            showToast("Huỷ đơn thành công", "success");
        },
        error: function (error) {
            console.log(error)
            showToast("Hủy thất bại", "error");
        }

    })
}

function doitra(){
    const description = document.getElementById("descriptionDT").value;
    $.ajax({
        url : "/khachHang/doitra/" + idDoiTra + "?message=" + description,
        method : "PUT",
        success : function (){
            const row = document.getElementById(idDoiTra);
            row.remove();
            showToast("Đổi trả thành công", "success");
        },
        error: function (xhr) {
            const errorMessage = xhr.responseText || "Có lỗi xảy ra, vui lòng thử lại!";
            showToast(errorMessage, "error");
        }
    })
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