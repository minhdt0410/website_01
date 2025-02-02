function loadThankPage(){
    let data = sessionStorage.getItem('data');
    if(data){
        data = JSON.parse(data);
    }
    console.log(data)
    $.ajax({
        url : "/checkout/checkoutWithVNPAY",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(data),
        success : function (){
            sessionStorage.removeItem("data");
        },
        error: function (error){
            console.log(error)
        }
    })
}