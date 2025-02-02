function register(e) {
    e.preventDefault();
    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;


    const hoTen = document.querySelector('input[name="hoTen"]').value;
    const email = document.querySelector('input[name="email"]').value;
    const matKhau = document.querySelector('input[name="matKhau"]').value;
    if (!hoTen || !email || !matKhau) {
        showToast("Vui lòng điền đầy đủ thông tin!", "warning");
    } else if(!emailPattern.test(email)){
        showToast("Email không hợp lệ!", "warning");
    }
    else if (matKhau.length < 6) {
        showToast("Mật khẩu phải có ít nhất 6 ký tự!", "warning");
    } else {
        const data = {
            hoTen : hoTen,
            email : email,
            matKhau : matKhau
        }
        $.ajax({
            url : "/api/v1/web/checkRegister",
            method : "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success: function (message){
                if(message === ""){
                    sessionStorage.setItem("message", "Đăng ký thành công");
                    window.location.href = '/login';
                }else {
                    showToast(message, "warning");
                }
            },
            error: function (error){
                console.log(error)
            }
        })
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