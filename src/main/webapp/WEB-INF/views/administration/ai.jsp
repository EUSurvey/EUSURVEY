<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="esapi" uri="http://www.owasp.org/index.php/Category:OWASP_Enterprise_Security_API" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
	<title>EUSurvey - AI Caller</title>
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

            <br /><b>Queries (separated by line break)</b><br />
            <textarea id="queries" style="min-width: 800px; min-height: 150px;">
                I receive spam surveys from unknown senders.
                I received a phishing email pretending to come from EU Survey.
                My organisation is receiving spam surveys through EUSurvey.
                How can I report a survey that collects personal data illegally?
                I think my survey link is being misused by others.
                A user sent inappropriate content in a survey.
                I got an offensive message through a survey response.
                My survey has been cloned and is being distributed by someone else.
                Can I import contacts to use for invitations?
                I cannot import my contact list into the Address Book.
                The system says &quot;invalid email address&quot; during import.
                Some contacts are missing after importing my list.
                How can I delete or update existing contacts?
                The imported contacts don&#39;t appear in my survey invitations.
                My contact import fails without error.
                How do I group contacts for different surveys?
                Why are some emails duplicated in my Address Book?
                How can I change/delete my submitted answers?
                How can get the PDF copy of my answers?
                How can I get access to a survey?
                How to grant access for other persons to my survey?
                The given link does not work, how to access the survey?
                How to get access to my draft contribution?
                How can I add translations to my survey?
                How to add a text box into a choice question like &quot;Other, please specify...&quot;?
                My survey shows 'Page not found' when I open the invitation link.
                How can I reset a respondent who made a mistake in their submission?
                The captcha doesn&#39;t work when I try to access a survey.
                Can I see who already answered?
                I submitted my response, but the survey owner says they can&#39;t see it.
                My contribution disappeared after submission.
                I accidentally submitted twice - can I delete one contribution?
                My contribution shows as &quot;incomplete.&quot;
                The survey owner says some answers are missing from my submission.
                I can&#39;t open the contribution summary after submission.
                I edited my contribution, but the changes don&#39;t appear.
                I submitted the survey, but didn&#39;t receive a confirmation message.
                A respondent needs to correct their submission.
                I need to reopen a submission that was already completed.
                How can I allow a respondent to re-enter their survey?
                Can I bulk reset several respondents at once?
                A respondent wants to change one answer after submission.
                I reset a respondent, but they didn&#39;t receive a new link.
                I reset the wrong respondent by mistake.
                Can I reopen a respondent&#39;s submission without deleting their data?
                It seems my invitations were not delivered to all users. What can i do to check ?
                How can I send a reminder to respondents?
                Some recipients didn&#39;t receive the invitation email.
                The invitation link in the email doesn&#39;t work.
                How can I resend invitations to users who haven&#39;t answered?
                The invitation email shows the wrong language.
                I sent invitations but can&#39;t see their status.
                The system says &quot;invalid email address&quot; during invitation import.
                Can I personalize each invitation email?
                Recipients say they received the same invitation twice.
                How do I cancel an invitation I already sent?
                My invitation emails go to spam folders.
                I get an error saying access denied when opening my survey.
                The system is asking for EU login,  what should I do?
                I can&#39;t open a survey; it says &quot;Access denied.&quot;
                What second factor authentication method can I configure with my account?
                When I try to log in, it keeps redirecting me back to the homepage.
                I get an &quot;Invalid credentials&quot; message when trying to access my survey.
                My link worked yesterday, but now it asks me to log in again.
                The system logs me out while I&#39;m editing the survey.
                I can&#39;t access my private survey after duplication.
                I&#39;ve received reports from respondents that the survey link isn&#39;t opening.
                I get a &quot;403 forbidden&quot; error when opening the survey.
                The system doesn&#39;t recognize my EU Login account.
                Why is my survey loading so slowly?
                The survey takes too long to load.
                My respondents say the survey freezes after a few pages.
                Exporting results takes several minutes.
                The system becomes very slow when I edit questions.
                The survey lags when many users submit at the same time.
                I get timeout errors when generating reports.
                The platform seems slower than usual today.
                Uploading attachments in my survey is slow.
                The charts in Results take too long to appear.
                How can I improve the speed of my survey?
                I submitted the survey but don&#39;t see my answers in results.
                Why do I see 'technical problem' when exporting results?
                Where can I download all responses?
                Can I get statistics of my survey?
                Can I backup my survey/Can i take backup of my survey
                I can&#39;t see the latest submissions in my results.
                The report still shows old data even after new responses.
                My statistics don&#39;t match the number of contributions.
                Results show only part of the collected data.
                The charts don&#39;t update when new answers arrive.
                My exported file doesn&#39;t include new contributions.
                Contributions appear, but the report total is wrong.
                When will the results update automatically?
                I see more submissions in Contributions than in Results.
                The graphs show yesterday&#39;s results instead of today&#39;s.
                Can I edit my survey after publishing it?
                How do I delete a survey?
                Why does my question not appear when I select an option?
                What are different type of surveys in eusurvey
                I can&#39;t submit my survey; it says technical error.
                I get a &quot;technical problem&quot; error when submitting my survey.
                The system shows an error message when I open my survey.
                My survey crashes when respondents reach the last page.
                The survey freezes during submission.
                I can&#39;t access the preview mode - it gives an internal error.
                I received a &quot;500 internal server error&quot; when editing my survey.
                The system won&#39;t load my survey, it stays on a blank screen.
                When I try to duplicate a survey, I get an error message.
                A respondent reports the survey crashes after clicking Next.
                My export fails with &quot;unexpected error.&quot;
                I created my survey in English, how can I add translations?
                The French version of my survey is missing even though I added translations.
                Some labels remain in English after switching to German.
                My respondents see part of the text in English and part in their selected language.
                When exporting my survey, questions appear only in English.
                The PDF version of my survey isn&#39;t translated.
                I changed translations, but they don&#39;t show in the published version.
                Some system messages (like &quot;Next&quot; or &quot;Submit&quot;) are not translated.
                How can I translate the invitation email content?
                My survey automatically opens in English instead of the respondent&#39;s language.
                How do I make a question mandatory or optional?
                How do I give other people permission to edit my survey questionnaire?
                How do I set dependencies or visibility rules for questions?
                How can I randomize the order of answers or sections?
                How do I copy and paste questions or elements within my survey?
                How do I ensure my survey displays correctly in multiple languages?
                How can I publish a draft survey?
                How can I filter or search for specific contributions in my results?
                How do I export survey results for further analysis?
                How can I generate charts or statistics for my survey?
                Are there limits to the number of questions or dependencies I can add before performance is affected?
                Why is my survey loading slowly in the Editor?
                How can I use attributes for my contacts?
                What should I do if my open survey is being targeted by bots or trolls?
                How do I report abusive content in a survey?
            </textarea>

            <br />
            <b>Model</b><br />
            <select id="model">
                <option>gpt-4o</option>
                <option>llama-3.3-70b-instruct</option>
                <option>Mistral-Small-3.2-24B-Instruct-2506</option>
                <option>Mistral-Small-3.1-24B-Instruct-2503</option>
            </select>

            <br />
            <b>Temperature</b><br />
            <input id="temperature" value="0" />

            <br />
            <b>Threshold</b><br />
            <input id="threshold" value="0.2" />

            <br />
            <b>Waiting time [seconds] between requests</b><br />
            <input id="pause" value="10" />

            <br />
            <b>Number of parallel requests</b><br />
            <input id="parallel" value="10" />

            <br />
            <b>Maximum number of repeats for failed requests</b><br />
            <input id="repeats" value="3" />

            <br /><br />
            <button class="btn btn-primary" onclick="call2()">Execute</button>

            <br /><br /><b>Response</b><br />
            <table class="table">
                <thead>
                    <th>Query</th>
                    <th>Answer</th>
                    <th>References</th>
                </thead>
                <tbody id="response">

                </tbody>
            </table>

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

                let queriesArray = [];
                let counter = 0;

                function call2() {
                    const queries = document.getElementById("queries").value;
                    queriesArray = queries.split('\n');
                    counter = 0;
                    $("#response").empty();
                    executeBatch();
                }

                function executeBatch() {
                    const parallel = parseInt(document.getElementById("parallel").value);
                    const pause = parseInt(document.getElementById("pause").value);
                    let stop = false;
                    console.log("executeBatch");

                    for (let i = counter; i < counter + parallel; i++) {
                        if (i >= queriesArray.length) {
                            stop = true;
                            return;
                        }

                        executeQuery(queriesArray[i].trim(), 1, null);
                    }

                    if (stop) return;

                    counter += parallel;

                    setTimeout(() => {
                      executeBatch();
                    }, pause + "000");
                }

                function executeQuery(query, repeatCounter, tr) {
                    console.log("Executing '" + query + "'");

                    const repeats = parseInt(document.getElementById("repeats").value);
                    const token = document.getElementById("token").value;
                    const model = document.getElementById("model").value;
                    const temperature = document.getElementById("temperature").value;
                    const threshold = document.getElementById("threshold").value;
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
                            "query": query,
                            "system_prompt": prompt,
                            "model_id": model,
                            "temperature": parseInt(temperature),
                            "top_k": 5,
                            "datasources": ["a4_eusurvey"],
                            "streaming": false,
                            "relevance_threshold": parseFloat(threshold),
                            "include_metadata": false,
                            "snc": false
                          })
                    };

                    console.log(requestOptions);

                    var td;
                    var td2;

                    if (tr == null) {
                        tr = document.createElement("tr");
                        var td = document.createElement("td");
                        $(td).text(query);
                        $(tr).append(td);
                        td = document.createElement("td");
                        $(td).text("waiting for response...");
                        $(tr).append(td);
                        td2 = document.createElement("td");
                        $(tr).append(td2);
                    } else {
                        td = $(tr).find("td")[1];
                        td2 = $(tr).find("td")[2];
                    }

                    fetch(apiUrl, requestOptions)
                      .then(response => {
                        if (!response.ok) {
                          throw new Error('Network response was not ok');
                        }
                        return response.json();
                      })
                      .then(data => {
                        console.log(data);
                        $(td).text(data.answer);

                        var citations = data.citations;
                        for (let i = 0; i < citations.length; i++) {
                            var a = document.createElement("a");
                            $(a).attr("href", citations[i].link).append(citations[i].title);
                            $(td2).append(a).append("&nbsp;");
                        }
                      })
                      .catch(error => {
                        if (repeatCounter <= repeats) {
                            console.log("repeat " + repeatCounter);
                            $(td).text("repeat " + (repeatCounter));
                            repeatCounter += 1;
                            executeQuery(query, repeatCounter, tr);
                            return;
                        }
                        console.error('Error:', error);
                        $(td).text(error);
                      });

                    $("#response").append(tr);
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
