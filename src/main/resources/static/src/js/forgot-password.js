function sendMail() {
    const username = document.getElementById("username").value;
    $.ajax({
        url: "/api/account/remember?email=" + username,
        method: "POST",
        success: function (message) {
            if (message === "success") {
                sessionStorage.setItem("message", "Gửi mail thành công");
                window.location.href= '/login';
            } else {
                showToast("Email không tồn tại", "error");
            }
        }
    })
}

function newPass(){
    const password = document.getElementById('password').value;
    const rePassword = document.getElementById('re-password').value;
    const id = document.getElementById("id").value;

    if (password.length < 6) {
        showToast("Mật khẩu phải có ít nhất 6 ký tự!", "warning")
    }
    else if (password !== rePassword) {
        showToast("Mật khẩu và nhập lại mật khẩu phải giống nhau!", "warning")
    }
    else {
        $.ajax({
            url: `/api/account/updatePass/${id}/${password}`,
            method: "POST",
            success: function () {
                sessionStorage.setItem("message", "Thành công");
                window.location.href = '/login';
            },
            error : function (error){
                console.log(error);
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