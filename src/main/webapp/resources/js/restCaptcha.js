let language = "Change Language ...";
let useAudio = false;
let EuCaptchaToken;

let serverprefix = "http://localhost:8080/EuCaptcha/api/"

function onPlayAudio(){
    useAudio = true;
}
function getLastSelectedValue(){
    const language = localStorage.getItem("language");
    if(language)
    {
        document.getElementById('dropdown-language').value = language;
    }
}
$(function(){
    function getcaptcha(){
        const getCaptchaUrl = $.ajax({
            type: "GET",
            url: serverprefix + 'captchaImg',
            success: function (data, textStatus, request) {
                EuCaptchaToken = getCaptchaUrl.getResponseHeader("token"); //clam
                const jsonData = data; //clam JSON.parse(data);
                $("#captchaImg").attr("src", "data:image/png;base64," + jsonData.captchaImg);
                $("#captchaImg").attr("captchaId", jsonData.captchaId);
                $("#audioCaptcha").attr("src", "data:audio/wav;base64," + jsonData.audioCaptcha);
            }
        });
    }
    function reloadCaptcha(){
        const reloadCaptchaUrl = $.ajax({
            type: "GET",
            url: serverprefix + 'reloadCaptchaImg/' + $("#captchaImg").attr("captchaId"),
            crossDomain: true,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.setRequestHeader("jwtString", EuCaptchaToken);
            },
            success: function (data) {
                EuCaptchaToken = reloadCaptchaUrl.getResponseHeader("token"); //clam
                const jsonData = data; //clam JSON.parse(data);
                $("#captchaImg").attr("src", "data:image/png;base64," + jsonData.captchaImg);
                $("#captchaImg").attr("captchaId", jsonData.captchaId);
                $("#audioCaptcha").attr("src", "data:audio/wav;base64," + jsonData.audioCaptcha);
                $("#captchaAnswer").val("");
                useAudio = false;
                //document.getElementById('dropdown-language').value = language;
            }
        });
    }
    function validateCaptcha(){
    	
    	var values = new FormData();
    	values.append("captchaAnswer", $("#captchaAnswer").val());
    	values.append("useAudio", useAudio);
    	
        const validateCaptcha = $.ajax({
            type: "POST",
            //contentType: 'application/json; charset=utf-8',
            url:  serverprefix + "validateCaptcha/" + $("#captchaImg").attr("captchaId"), // + "?captchaAnswer=" + $("#captchaAnswer").val(),
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                //xhr.setRequestHeader("Content-Type", "application/json");
                xhr.setRequestHeader("jwtString", EuCaptchaToken);
            },
            data: jQuery.param({
                captchaAnswer: $("#captchaAnswer").val(),
                useAudio: useAudio
            }),
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            processData: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                $("input").css({"border": ""});
                obj = data; //clam JSON.parse(data);
                if ('success' === obj.responseCaptcha) {
                    $("#success").css("visibility", "visible");
                    $("#fail").css("visibility", "hidden");
                } else {
                    $("#fail").css("visibility", "visible");
                    $("#success").css("visibility", "hidden");
                    reloadCaptcha();
                }
            },
            error: function (e) {
                $("input").css({"border": "2px solid red"});
                $("#error").css("visibility", "visible");
                $("#fail").css("visibility", "hidden");
                $("#success").css("visibility", "hidden");
                reloadCaptcha();
            }
        });
    }

    $("#captchaReload").click(function(){
        $("#fail").css("visibility", "hidden");
        $("#success").css("visibility", "hidden");
        reloadCaptcha();
    });

    $("#captchaSubmit").click(function(){
        validateCaptcha();
    });

    $('#captchaAnswer').keypress(function(e) {
        if (e.keyCode === 13) {
            validateCaptcha();
            return false; // prevent the button click from happening
        }
    });

    $(document).ready(function () {
        $("#dropdown-language").change(function () {
            const selectedOption = $('#dropdown-language').val();
            if (selectedOption !== '') {
                localStorage.setItem("language", selectedOption);
                window.location.replace('?lang=' + selectedOption);
            }
        });
    });
    getcaptcha();
});
