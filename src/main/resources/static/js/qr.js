$(document).ready(function() {
        getQRCode();
    });
function getQRCode(){
    $.get("/code?username="+$("#inputUsername").val() + "&secret=" +$("#secret").val(),function(data) {
        $("#qr").append('<img src="'+data.url+'" />');
    });
}