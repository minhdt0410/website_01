function loadLoginPage(){
    const message = sessionStorage.getItem('message');
    if(message !== null){
        showToast(message, "success");
        sessionStorage.removeItem('message');
    }
}

function checkLogin(e){
    e.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const data = {
        email : username,
        matKhau: password
    }
    $.ajax({
        url : "/api/v1/web/checkLogin",
        method : "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (message){
            const loginForm = document.getElementById("loginForm");
            if(message === ""){
                loginForm.submit();
            }else {
                showToast(message, 'warning');
            }
        },
        error: function (error){
            console.log(error)
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