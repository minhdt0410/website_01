const myCart = document.getElementById("myCart");
const listItemCart = document.getElementById("listItemCart");

function loadCartPage(){
    const message = sessionStorage.getItem("message");
    if(message !== null){
        showNotification(message, "info");
        sessionStorage.removeItem("message");
    }
    $.ajax({
        url: "/cart/cartPage",
        method: "GET",
        success: function (data) {
            console.log(data)
            const listCart = document.getElementById("listCart");
            const cartMini = document.getElementById("cart-mini");
            cartMini.removeAttribute('data-bs-toggle');
            if(data !== ""){
                listItemCart.innerHTML = "";
                data.forEach(item => {
                    const row = renderCartPage(item);
                    listCart.innerHTML += row;
                })
            }
        }
    })

}

function loadCart(){
    $.ajax({
        url: `${apiV1}`,
        method: "GET",
        success: function (data) {
            const myCart = document.getElementById("myCart");
            if (data === "") {
                myCart.innerHTML = `<div class="text-center"><h3>Vui lòng đăng nhập</h3></div>`;
            }else {
                $.ajax({
                    url: "/cart/cartPage",
                    method: "GET",
                    success: function (data) {
                        if(data !== ""){
                            const listItemCart = document.getElementById("listItemCart");
                            listItemCart.innerHTML = "";
                            data.forEach(item => {
                                const row = renderCart(item);
                                listItemCart.innerHTML += row;
                            })
                        }
                    }
                })
            }
        },
        error: function (error) {
            console.log(error)
        }
    })
}


function renderCart(item) {
    item.tongTien = item.tongTien.toLocaleString();
    let newPrice = '';
    let oldPrice = '';
    if(item.sanPhamChiTiet.khuyenMaiChiTiet !== null){
        newPrice = item.sanPhamChiTiet.khuyenMaiChiTiet.giaMoi.toLocaleString();
        oldPrice = parseInt(item.sanPhamChiTiet.gia_ban,10).toLocaleString();
    }else {
        newPrice = parseInt(item.sanPhamChiTiet.gia_ban,10).toLocaleString();
    }
    let row = ``;
    row = `<div class="minicart-item d-flex" id="${item.id}">
                        <div class="mini-img-wrapper">
                            <img class="mini-img" src="${item.sanPhamChiTiet.anh_spct}" alt="img">
                        </div>
                        <div class="product-info">
                            <h2 class="product-title d-flex justify-content-between"><a href="#">${item.sanPhamChiTiet.sanPham.tensanpham} <sup><sup>đ</sup>${newPrice} <del>${oldPrice}</del></sup></a> <input type="checkbox" value="${item.id}" onclick="chooseCheckbox(${item.id})" name="integers"></h2>
                            <p class="product-vendor">${item.sanPhamChiTiet.size.ten_size} / ${item.sanPhamChiTiet.mauSac.tenmausac}</p>
                            <div class="misc d-flex align-items-end justify-content-between">
                                <div class="quantity d-flex align-items-center justify-content-between">
                                    <button class="qty-btn dec-qty" onclick="quantity(${item.id} , 'tru', event)"><img src="/static/src/web/assets/img/icon/minus.svg"
                                                                         alt="minus"></button>
                                    <input class="qty-input" type="number" name="qty" id="quantity-${item.id}" value="${item.soLuong}" onchange="checkQuantity(${item.id})" min="0">
                                    <button class="qty-btn inc-qty" onclick="quantity(${item.id} , 'cong', event)"><img src="/static/src/web/assets/img/icon/plus.svg"
                                                                         alt="plus"></button>
                                </div>
                                <div class="product-remove-area d-flex flex-column align-items-end">
                                    <div class="product-price" id="tongTien-${item.id}">${item.tongTien} VND</div>
                                    <a href="#" class="product-remove" onclick="removeCart(${item.id}, event)">Xóa</a>
                                </div>
                            </div>
                        </div>
                    </div>`;
    return row;
}

function renderCartPage(item){
    item.tongTien = item.tongTien.toLocaleString();
    item.sanPhamChiTiet.gia_ban = parseInt(item.sanPhamChiTiet.gia_ban, 10).toLocaleString();
    let newPrice = '';
    let oldPrice = '';
    if(item.sanPhamChiTiet.khuyenMaiChiTiet !== null){
        newPrice = item.sanPhamChiTiet.khuyenMaiChiTiet.giaMoi.toLocaleString();
        oldPrice = item.sanPhamChiTiet.gia_ban.toLocaleString();
    }else {
        newPrice = item.sanPhamChiTiet.gia_ban.toLocaleString();
    }
    let row = ``;
    row = `<tr class="cart-item" id="${item.id}">
                                          <td class="cart-item-media">
                                            <div class="mini-img-wrapper">
                                                <img class="mini-img" src="${item.sanPhamChiTiet.anh_spct}" alt="img">
                                            </div>                                    
                                          </td>
                                          <td class="cart-item-details">
                                            <h2 class="product-title"><a href="#">${item.sanPhamChiTiet.sanPham.tensanpham} <sup><sup>đ</sup>${newPrice} <del>${oldPrice}</del></sup></a></h2>
                                            <p class="product-vendor">${item.sanPhamChiTiet.size.ten_size} / ${item.sanPhamChiTiet.mauSac.tenmausac}</p>                                   
                                          </td>
                                          <td class="cart-item-quantity">
                                            <div class="quantity d-flex align-items-center justify-content-between">
                                                <button onclick="quantity(${item.id} , 'tru', event)" class="qty-btn dec-qty"><img src="/static/src/web/assets/img/icon/minus.svg"
                                                        alt="minus"></button>
                                                <input class="qty-input" id="quantity-${item.id}" onchange="checkQuantity(${item.id})" type="number" name="qty" value="${item.soLuong}" min="0">
                                                <button class="qty-btn inc-qty" onclick="quantity(${item.id} , 'cong', event)"><img src="/static/src/web/assets/img/icon/plus.svg"
                                                        alt="plus"></button>
                                            </div>
                                            <a href="#" class="product-remove mt-2" onclick="removeCart(${item.id}, event)">Xoá</a>
                                          </td>
                                          <td class="cart-item-price text-end">
                                            <input type="checkbox" value="${item.id}" onclick="chooseCheckbox(${item.id})" name="integers">
                                            <div class="product-price mt-3" id="tongTien-${item.id}">${item.tongTien} VND</div>
                                          </td>                        
                                        </tr>`;
    return row;
}

function checkQuantity(idCart){
    const quantity = document.getElementById("quantity-"+idCart)
    const tongTien = document.getElementById("tongTien-" + idCart);
    $.ajax({
        url: "/cart/checkQuantityCauseChange/" + idCart +"?quantity=" + quantity.value.trim(),
        method: "GET",
        success: function (data) {
            if (data === ""){
                document.getElementById(idCart).remove();
                return
            }
            if(data.id === -1){
                showToast("Số lượng phải nhỏ hơn " + data.soLuong, "error");
            }else if(data.id === -2){
                showToast("Số lượng phải lớn hơn 1", "error");
            }
            quantity.value = data.soLuong;
            tongTien.innerText = data.tongTien.toLocaleString() + " VND";
        }
    })
}

function quantity(idCart, status, e) {
    e.preventDefault();
    let listCart = document.getElementById("listCart");
    const quantity = document.getElementById("quantity-" + idCart);
    const tongTien = document.getElementById("tongTien-" + idCart);
    $.ajax({
        url: "/cart/checkQuantity/" + idCart + "?status=" + status,
        method: "GET",
        success: function (message) {
            console.log(message)
            if(message === "error"){
                document.getElementById(idCart).remove();
                return;
            }
            if(message !== ""){
                showToast(message, "error");
            }else {
                $.ajax({
                    url: "/cart/quantity/" + idCart + "?status=" + status,
                    method: "PUT",
                    success: function (data) {
                        if (data !== "") {
                            quantity.value = data.soLuong;
                            tongTien.innerText = data.tongTien.toLocaleString() + " VND";
                            chooseCheckbox();
                        } else {
                            showToast("Số lượng phải lớn hơn 1", "error");
                        }
                    }
                })
            }
        }
    })
}

function checkbox() {
    let listCart = document.getElementById("listCart");
    if(listCart === null){
        listCart = document.getElementById("myCart");
    }
    const checkboxes = listCart.querySelectorAll('input[type="checkbox"]:checked');
    return Array.from(checkboxes).map(checkbox => checkbox.value);
}

function chooseCheckbox(){
    const data = checkbox();
    console.log(data)
    chooseCart(data);
}

function chooseCart(selectedIds) {
    $.ajax({
        url: "/cart/tongTien",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({idGioHang: selectedIds}),
        success: function (data) {
            // console.log(data)
            const money = document.getElementById("money");
            money.innerText = data.toLocaleString() + " VND";
        },
        error: function (error) {
            console.error("Lỗi:", error);
        }
    });
}

function removeCart(idCart, e) {
    e.preventDefault();
    $.ajax({
        url: "/cart/deleteCart/" + idCart,
        method: "DELETE",
        success: function () {
            const row = document.getElementById(idCart);
            row.remove();
            showToast("Xoá thành công", "success");
        }
    })
}

function checkToCheckout(e, status){
    e.preventDefault()
    if(checkbox().length === 0){
        showToast("Vui lòng chọn sản phẩm", "warning");
    }else {
        $.ajax({
            url : "/checkout/checkSoLuongSP",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(checkbox()),
            success : function (message){
                if(message !== ""){
                    sessionStorage.setItem("message", message);
                    window.location.href = '/home/cart';
                }else {
                    $.ajax({
                        url : "/checkout/getItem",
                        method: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(getSelectedItems()),
                        success: function (data){
                            sessionStorage.setItem('listGioHang', JSON.stringify(data));
                            document.getElementById('formCart' + status).submit();
                        }
                    })
                }
            }
        })
    }
}

function getSelectedItems() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    const selectedItems = [];
    checkboxes.forEach((checkbox) => {
        const itemId = checkbox.value;
        const quantityInput = document.querySelector(`#quantity-${itemId}`);
        const quantity = quantityInput ? parseInt(quantityInput.value) : 0;
        selectedItems.push({ id: itemId, soLuong: quantity });
    });

    return selectedItems;
}

function showNotification(message, status) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 7500,
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