<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - <spring:message code="label.Languages" /></title>	
	<%@ include file="../includes.jsp" %>	
</head>
<body>

	<div class="page-wrap">
	<%@ include file="../header.jsp" %>
	<%@ include file="../menu.jsp" %>

	<div>
		<div style="margin-left: auto; margin-right: auto; padding-top: 100px; width: 850px">
			<h1>AI Caller</h1>

            Hint: this page only works if your EULogin account is registered to use the EUSurvey datasource.

            <h2>Step 1</h2>
            Go to <a target="_blank" href="https://intragate.acceptance.ec.europa.eu/reuse-oidc-tool/">https://intragate.acceptance.ec.europa.eu/reuse-oidc-tool/</a>, click the <b>API Gateway</b> button and copy &amp; paste the header from the last box at the bottom of the page into the following box.

            <br /><br /><b>Header</b><br />
            <textarea id="header" style="min-width: 800px; min-height: 150px;"></textarea>

            <h2>Step 2</h2>
            Click the button below to create a client ID using the federation token service.<br /><br />
            <button class="btn btn-primary" onclick="call1()">Generate Token</button><br /><br />
            <textarea id="token" style="min-width: 800px; min-height: 150px;"></textarea>

            <h2>Step 3</h2>
            Click the button below to execute the prompt.
            <br /><br /><b>Prompt</b><br />
            <textarea id="prompt" style="min-width: 800px; min-height: 150px;"></textarea>
            <button class="btn btn-primary" onclick="call2()">Execute</button>

            <br /><br /><b>Response</b><br />
            <textarea id="response" style="min-width: 800px; min-height: 150px;"></textarea>

            <script>
                function call1() {
                    const header = document.getElementById("header").value.replace("Authorization: pop ","");
                    const apiUrl = 'https://api.np.myworkplace.services.ec.europa.eu/capi/federation-token-service-keycloak/dev/token';

                    const requestOptions = {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                        },
                        body: new URLSearchParams({
                            'consumer_key': 'DIGIT_A4_C_EUSURVEY',
                            'grant_type': 'oauth2:eui',
                            'eul_access_token': header,
                            'tenant': 'ec-rag-search'
                        })
                    };

                    fetch(apiUrl, requestOptions)
                      .then(response => {
                        if (!response.ok) {
                          throw new Error('Network response was not ok');
                        }
                        return response.json();
                      })
                      .then(data => {
                        console.log(data);
                        document.getElementById("token").value = data.access_token;
                      })
                      .catch(error => {
                        console.error('Error:', error);
                      });
                }

                function call2() {
                    const token = document.getElementById("token").value;
                    const prompt = document.getElementById("prompt").value;
                    const apiUrl = 'https://np.capi.aws.cloud.tech.ec.europa.eu/api/rag/acc/retrieval/generation';

                    const requestOptions = {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ` + token,
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            "client_id": "DIGIT_A4_C_EUSURVEY",
                            "query": prompt,
                            "model_id": "llama-3.3-70b-instruct",
                            "temperature": 0,
                            "top_k": 5,
                            "datasources": ["a4_eusurvey"],
                            "streaming": false,
                            "relevance_threshold": 0.2,
                            "include_metadata": false,
                            "snc": false
                          })
                    };

                    fetch(apiUrl, requestOptions)
                      .then(response => {
                        if (!response.ok) {
                          throw new Error('Network response was not ok');
                        }
                        return response.json();
                      })
                      .then(data => {
                        console.log(data);
                        document.getElementById("response").value = data.access_token;
                      })
                      .catch(error => {
                        console.error('Error:', error);
                      });
                }
            </script>

		</div>		
	</div>
	</div>

	<%@ include file="../footer.jsp" %>	
	
	<c:if test="${messages != null}">
		<script type="text/javascript">
			var messages = new Array();
			<c:forEach items="${messages}" var="message">
				messages.push('${message}');
			</c:forEach>
		
			showGenericMessages(messages);
		</script>
	</c:if>

</body>
</html>
