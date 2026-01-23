<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div id="system-message-box" class="alert hideme">
	<div style="float: right; margin-left: 10px;"><a onclick="hideSystemMessage()"><span class="glyphicon glyphicon-remove"></span></a></div>
	<div style="float: left; margin: 5px; margin-top: 5px; margin-right: 10px"><img src="" alt="system message icon" /></div>
	<div style="margin-left: 10px; padding-top: 3px; padding-bottom: 5px;" id="system-message-box-content"></div>
	<div style="text-align: right">
		<button id="btnDeleteUserMessage" class="btn btn-default" onclick="deleteUserMessage()"><spring:message code="label.DeleteMessage" /></button>
	</div>
</div>


<div onclick="toggleChatbot()" style="position: fixed; right: 30px; bottom: 5px; background-color: #012d56; color: #fff; padding: 10px; border-radius: 30px; cursor: pointer;">
    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" class="bi bi-chat-text-fill" viewBox="0 0 16 16">
      <path d="M16 8c0 3.866-3.582 7-8 7a9 9 0 0 1-2.347-.306c-.584.296-1.925.864-4.181 1.234-.2.032-.352-.176-.273-.362.354-.836.674-1.95.77-2.966C.744 11.37 0 9.76 0 8c0-3.866 3.582-7 8-7s8 3.134 8 7M4.5 5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1zm0 2.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1zm0 2.5a.5.5 0 0 0 0 1h4a.5.5 0 0 0 0-1z"/>
    </svg>
</div>

<div id="chatbot" style="display: none; position: fixed; z-index: 10000; right: 5px; bottom: 70px; width: 400px; height: 500px; background-color: #fff; border: 1px solid #012d56; border-radius: 5px;">
    <div style="background-color: #012d56; color: #fff; padding: 10px; font-weight: bold;">
        <span style="float: right">
            <a role="button" onclick="toggleChatbot()" style="color: #fff">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
                  <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8z"/>
                </svg>
            </a>
        </span>
        EUSurvey Assistant
    </div>
    <div id="chatthread" style="background-color: #F9F9F9; height: 370px; overflow-y: auto;">
        <div class="chatbotresponse">Hi,<br/> I am the AI-powered EUSurvey assistant ready to help you.<br /> Please enter a question in the text box below.</div>
    </div>
    <div style="text-align: right; padding: 3px;">
        <textarea id="chatmessage" rows="2" class="form-control" style="margin-bottom: 5px;" onkeydown="checkChatKey(event)"></textarea>
        <button class="btn btn-primary btn-xs" onclick="sendChatMessage();">Send</button>
    </div>
</div>

<script>
    var firstChatMessage = true;

    function toggleChatbot() {
        $('#chatbot').toggle();
    }

    function checkChatKey(event)  {
        const key = window.event.keyCode;

        // If the user has pressed enter
        if (key === 13) {
            sendChatMessage();
            event.preventDefault();
        }
    }

    function addMessage(text, fromMe) {
        const div = document.createElement("div");
        $(div).append(text);
        if (fromMe) {
            $(div).addClass("chatbotquestion");
        } else {
            $(div).addClass("chatbotresponse");
        }
        $('#chatthread').append(div);
        if (fromMe) {
            const waitAnimation = document.createElement("img");
            $(waitAnimation).attr("alt", "wait animation").attr("src", "${contextpath}/resources/images/ajax-loader.gif");
            $('#chatthread').append(waitAnimation);
        } else {
            $('#chatthread').find("img").remove();
        }

        $("#chatthread").animate({ scrollTop: $('#chatthread').prop("scrollHeight")}, 1000);
    }

    function sendChatMessage() {
        var message = $('#chatmessage').val();
        addMessage(message, true);
        //$('#chatmessage').val("").focus()[0].setSelectionRange(0,0);
        document.getElementById("chatmessage").value = "";

        setTimeout(() => {
            $.ajax({
                type:'POST',
                url: "${contextpath}/chatbot/send",
                beforeSend: function(xhr){xhr.setRequestHeader(csrfheader, csrftoken);},
                data: {message: message, firstChatMessage: firstChatMessage},
                cache: false,
                success: function( data ) {
                    firstChatMessage = false;
                    if (data != "error") {
                        addMessage(data, false);
                    } else {
                        addMessage("<spring:message code="error.OperationFailed" />", false);
                        showError("<spring:message code="error.OperationFailed" />");
                    }
                },
                error: function(jqXHR) {
                      showAjaxError(jqXHR.status);
                }});
        }, 2000);

    }
</script>

<style>
    .chatbotquestion {
        background-color: #cfcfff;
        border-radius: 10px;
        margin: 20px;
        padding: 10px;
        margin-left: 50px;
    }

    .chatbotresponse {
        background-color: #cfcfcf;
        border-radius: 10px;
        margin: 20px;
        padding: 10px;
        margin-right: 50px;
    }

    #chatthread img {
        margin-left: 50px;
    }
</style>