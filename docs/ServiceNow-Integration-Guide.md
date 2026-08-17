# ServiceNow Integration Guide — EUSurvey Support Ticket Client

This document describes how EUSurvey integrates with ServiceNow to create support incidents via the ServiceNow REST API. It is intended for teams who want to implement their own ServiceNow client following the same pattern.

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Configuration](#configuration)
4. [Authentication](#authentication)
5. [API Endpoints Used](#api-endpoints-used)
6. [Creating an Incident (Request/Response)](#creating-an-incident)
7. [Uploading Attachments](#uploading-attachments)
8. [Internal vs External Users](#internal-vs-external-users)
9. [Ticket Category Mapping](#ticket-category-mapping)
10. [Input Sanitization](#input-sanitization)
11. [Proxy Configuration](#proxy-configuration)
12. [Error Handling and Fallback](#error-handling-and-fallback)
13. [End-to-End Flow](#end-to-end-flow)
14. [Template Examples](#template-examples)
15. [Implementation Checklist](#implementation-checklist)

---

## Overview

EUSurvey uses the ServiceNow REST API (Table API) to create IT support incidents when users submit the contact/support form. The integration:

- Creates incidents via a JSON POST to the ServiceNow Incident table
- Uploads file attachments (up to 2, max 1 MB each) to the created incident
- Distinguishes between internal (EC) and external users, using different templates
- Falls back to email-based ticket creation if the ServiceNow call fails

The relevant source file is `src/main/java/com/ec/survey/controller/HomeController.java`, method `sendSupportSmt()`.

---

## Architecture

```
┌─────────────┐       POST /home/support       ┌───────────────────┐
│  User Form  │ ──────────────────────────────> │  HomeController   │
│ (support.jsp)│                                │  .supportPOST()   │
└─────────────┘                                 └────────┬──────────┘
                                                         │
                                                         ▼
                                              ┌──────────────────────┐
                                              │  sendSupportSmt()    │
                                              │  - Load template     │
                                              │  - LDAP lookup (int) │
                                              │  - Fill placeholders │
                                              │  - POST to ServiceNow│
                                              └────────┬─────────────┘
                                                       │
                              ┌─────────────────────────┼───────────────────────┐
                              │ Success (201)           │                        │ Failure
                              ▼                        ▼                        ▼
                  ┌────────────────────┐   ┌─────────────────────┐   ┌──────────────────┐
                  │ Upload Attachments │   │ Parse sys_id from    │   │ Fallback: send   │
                  │ (if any)           │◄──│ response JSON        │   │ email to helpdesk│
                  └────────────────────┘   └─────────────────────┘   └──────────────────┘
```

---

## Configuration

All configuration is in `spring.properties` (or equivalent per environment):

| Property | Description | Example |
|----------|-------------|---------|
| `support.smIncidentHost` | ServiceNow Incident Table API URL | `https://instance.service-now.com/api/now/table/incident` |
| `support.smAttachmentHost` | ServiceNow Attachment API URL (base, params appended at runtime) | `https://instance.service-now.com/api/now/attachment/file?table_name=incident&table_sys_id=` |
| `support.smBasicAuth` | Base64-encoded `username:password` for Basic Auth | `dXNlcm5hbWU6cGFzc3dvcmQ=` |
| `support.recipient` | Fallback email for external user tickets | `helpdesk@example.com` |
| `support.recipientinternal` | Fallback email for internal user tickets | `internal-helpdesk@example.com` |

### Example configuration block

```properties
## SMT Web Service / ServiceNow Integration
support.smIncidentHost=https://your-instance.service-now.com/api/now/table/incident
support.smAttachmentHost=https://your-instance.service-now.com/api/now/attachment/file?table_name=incident&table_sys_id=
support.smBasicAuth=c2VydmljZV91c2VyOnNlY3VyZV9wYXNzd29yZA==
support.recipient=helpdesk-external@example.com
support.recipientinternal=helpdesk-internal@example.com
```

---

## Authentication

The integration uses **HTTP Basic Authentication**.

The credentials are stored as a Base64-encoded `username:password` string in the `support.smBasicAuth` property and sent as:

```
Authorization: Basic <base64(username:password)>
```

This same credential is used for both incident creation and attachment upload requests.

### Setting up the ServiceNow service account

1. Create a service account in ServiceNow with permissions to:
   - Create records in the `incident` table
   - Create attachments via the Attachment API
2. Base64-encode `username:password` (no trailing newline)
3. Set the result as `support.smBasicAuth`

```bash
echo -n "myuser:mypassword" | base64
# Output: bXl1c2VyOm15cGFzc3dvcmQ=
```

---

## API Endpoints Used

### 1. Incident Table API (Create)

- **Method:** `POST`
- **URL:** Configured via `support.smIncidentHost`
- **Typical URL:** `https://<instance>.service-now.com/api/now/table/incident`
- **Content-Type:** `application/json; charset=utf-8`
- **Authentication:** Basic Auth

### 2. Attachment API (Upload)

- **Method:** `POST`
- **URL:** `<support.smAttachmentHost><sys_id>&file_name=<url_encoded_filename>`
- **Typical URL:** `https://<instance>.service-now.com/api/now/attachment/file?table_name=incident&table_sys_id=<sys_id>&file_name=<filename>`
- **Content-Type:** `application/octet-stream`
- **Authentication:** Basic Auth
- **Body:** Raw file bytes

---

## Creating an Incident

### Request

The system loads a JSON template file and fills in placeholders before sending.

**HTTP Request:**
```
POST https://<instance>.service-now.com/api/now/table/incident
Content-Type: application/json; charset=utf-8
Authorization: Basic <credentials>
```

**Template placeholders:**

| Placeholder | Source | Description |
|-------------|--------|-------------|
| `[CALLER]` | LDAP lookup by email (internal users only) | ServiceNow caller_id (user login/uid) |
| `[MESSAGE]` | Form field `message` (prefixed with reason label) | Incident description |
| `[SUBJECT]` | Form field `subject` | Short description |
| `[REASON]` | Mapped from `contactreason` (see [Ticket Category Mapping](#ticket-category-mapping)) | Category/type of ticket |
| `[ADDITIONALINFOUSERNAME]` | Form field `name` | Reporter's name |
| `[ADDITIONALINFOEMAIL]` | Form field `email` | Reporter's email |
| `[ADDITIONALINFO]` | Form field `additionalinfo` (browser/system info) | Technical details |
| `[ADDITIONALINFOSURVEYTITLE]` | Form field `additionalsurveyinfotitle` | Related survey title |
| `[ADDITIONALINFOSURVEYALIAS]` | Form field `additionalsurveyinfoalias` | Related survey alias |
| `[BUSINESSSERVICE]` | Hardcoded: `"EU Survey Solutions"` | ServiceNow business service |
| `[SERVICEOFFERING]` | Hardcoded: `"EU Survey - General issue"` | ServiceNow service offering |

### Example JSON template (reconstructed)

The actual templates are stored in `WEB-INF/Content/EC/createIncident.json` (internal) and `createIncidentExternal.json` (external). These are not included in the OSS repository, but based on the code, a template would look like:

```json
{
  "caller_id": "[CALLER]",
  "short_description": "[SUBJECT]",
  "description": "[MESSAGE]",
  "category": "[REASON]",
  "business_service": "[BUSINESSSERVICE]",
  "service_offering": "[SERVICEOFFERING]",
  "u_additional_info": "[ADDITIONALINFO]",
  "u_reporter_name": "[ADDITIONALINFOUSERNAME]",
  "u_reporter_email": "[ADDITIONALINFOEMAIL]",
  "u_survey_title": "[ADDITIONALINFOSURVEYTITLE]",
  "u_survey_alias": "[ADDITIONALINFOSURVEYALIAS]"
}
```

> **Note:** The actual field names depend on your ServiceNow instance's schema. The template uses standard ServiceNow fields (`caller_id`, `short_description`, `description`) plus any custom fields (`u_*`) defined for your instance.

For **external users**, the `[CALLER]` placeholder is not populated (no LDAP lookup is performed), so the external template likely omits or handles `caller_id` differently.

### Expected Response

**Success:** HTTP `200` or `201`

```json
{
  "result": {
    "sys_id": "a1b2c3d4e5f6...",
    "number": "INC0012345",
    "short_description": "...",
    ...
  }
}
```

The code extracts:
```java
JSONObject jsonResponse = new JSONObject(strResponse).getJSONObject("result");
String sys_id = jsonResponse.getString("sys_id");
```

The `sys_id` is required for subsequent attachment uploads.

**Failure:** Any HTTP status other than `200` or `201` is treated as a failure and triggers the email fallback.

---

## Uploading Attachments

After successful incident creation, attachments are uploaded one at a time (max 2 files).

### Pre-upload: Client-side file staging

Before the support form is submitted, files are uploaded to EUSurvey's server via:

```
POST /home/support/uploadfile
```

This returns a UID for each file which is stored as hidden form fields (`uploadedfile` and `uploadedfilename`).

### Upload to ServiceNow

For each attachment, the code performs:

**HTTP Request:**
```
POST https://<instance>.service-now.com/api/now/attachment/file?table_name=incident&table_sys_id=<sys_id>&file_name=<url_encoded_filename>
Content-Type: application/octet-stream
Authorization: Basic <credentials>

<raw file bytes>
```

**Parameters (in URL):**

| Parameter | Description |
|-----------|-------------|
| `table_name` | Always `incident` |
| `table_sys_id` | The `sys_id` from the incident creation response |
| `file_name` | URL-encoded original filename |

**Response:** HTTP `200` or `201` indicates success. Any other status is treated as a failure.

### Constraints

- Maximum 2 attachments per ticket
- Maximum file size: 1 MB per file (enforced client-side)
- Files are sent as raw binary with `application/octet-stream` content type

---

## Internal vs External Users

The integration distinguishes users by email domain:

| User Type | Detection | Template | LDAP Lookup |
|-----------|-----------|----------|-------------|
| Internal | Email ends with `ec.europa.eu` | `createIncident.json` | Yes — resolves `caller_id` from LDAP |
| External | All other emails | `createIncidentExternal.json` | No |

For internal users, the system queries an LDAP directory to resolve the user's organizational login (uid) from their email address, and populates the `[CALLER]` field so the incident is linked to the correct ServiceNow user record.

---

## Ticket Category Mapping

The support form's `contactreason` field is mapped to a ServiceNow category:

| Contact Reason | ServiceNow Category |
|---------------|-------------------|
| `technicalproblem` | `INCIDENT` |
| `idea` | `REQUEST FOR CHANGE` |
| `assistance` | `REQUEST FOR SERVICE` |
| `highaudience` | `REQUEST FOR SERVICE` |
| `generalquestion` | `REQUEST FOR INFORMATION` |
| `accessibility` | `REQUEST FOR INFORMATION` |
| `dataprotection` | `REQUEST FOR INFORMATION` |
| `organisation` | `REQUEST FOR INFORMATION` |
| `otherreason` | `REQUEST FOR INFORMATION` |

---

## Input Sanitization

All user input is sanitized before being inserted into the JSON template using `ConversionTools.removeHTML(input, false, true)`:

1. HTML is stripped using Jsoup (`Jsoup.parse(input).text()`)
2. The result is JSON-escaped using Gson (handles special characters like quotes, backslashes, control characters)

This prevents both XSS in ServiceNow's UI and JSON injection in the API payload.

**If you implement your own client**, ensure all user-provided values are properly JSON-escaped before insertion into the request body.

---

## Proxy Configuration

If your deployment requires an HTTP proxy to reach ServiceNow, configure these properties:

```properties
proxy.host=proxy.example.com
proxy.port=8080
proxy.user=proxyuser
proxy.password=proxypassword
```

The EUSurvey implementation sets JVM-wide system properties (`http.proxyHost`, `https.proxyHost`, etc.) before making HTTP calls. The HTTP client (`HttpClients.createSystem()`) respects these system properties.

If you're implementing your own client, configure your HTTP client's proxy settings accordingly.

---

## Error Handling and Fallback

The integration uses a **try/catch with email fallback** strategy:

1. If the ServiceNow API returns a non-200/201 status code → throw exception
2. If any network error, timeout, or parsing error occurs → catch exception
3. On any exception → fall back to sending the support request as an HTML email to the helpdesk team

```java
try {
    // ... create incident in ServiceNow ...
    // ... upload attachments ...
} catch (Exception e) {
    logger.error(e.getLocalizedMessage(), e);
    // fallback to email
    return sendSupportEmail(request, locale, model);
}
```

### Errors that trigger fallback

- HTTP status ≠ 200 and ≠ 201
- Network/connection errors
- Timeout
- JSON parsing failures
- Authentication failures (401/403)

### What to implement in your own client

- **Retry logic** (not implemented in EUSurvey — consider adding exponential backoff)
- **Timeout configuration** (EUSurvey uses Apache HttpClient defaults)
- **Logging** of failure responses for debugging
- **Graceful degradation** — always have a fallback mechanism

---

## End-to-End Flow

### Step 1: User uploads files (optional)

```
POST /home/support/uploadfile
Content-Type: multipart/form-data

→ Response: {"success": true, "uid": "temp-file-id", "name": "screenshot.png"}
```

### Step 2: User submits the support form

```
POST /home/support
Content-Type: application/x-www-form-urlencoded

Parameters:
  contactreason=technicalproblem
  name=John Doe
  email=john.doe@ec.europa.eu
  subject=Cannot publish survey
  message=I get an error when trying to publish...
  additionalinfo=Browser: Chrome 120, OS: Windows 11
  additionalsurveyinfotitle=Customer Satisfaction Q4
  additionalsurveyinfoalias=satisfaction-q4
  uploadedfile=abc123-temp-uid
  uploadedfilename=screenshot.png
  g-recaptcha-response=<captcha_token>
```

### Step 3: Server creates incident in ServiceNow

```
POST https://instance.service-now.com/api/now/table/incident
Content-Type: application/json; charset=utf-8
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=

{
  "caller_id": "jdoe",
  "short_description": "Cannot publish survey",
  "description": "INCIDENT: I get an error when trying to publish...",
  "category": "INCIDENT",
  "business_service": "EU Survey Solutions",
  "service_offering": "EU Survey - General issue",
  ...
}

→ Response 201:
{
  "result": {
    "sys_id": "abc123def456",
    "number": "INC0067890",
    ...
  }
}
```

### Step 4: Server uploads attachments

```
POST https://instance.service-now.com/api/now/attachment/file?table_name=incident&table_sys_id=abc123def456&file_name=screenshot.png
Content-Type: application/octet-stream
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=

<binary file content>

→ Response 201
```

### Step 5: Success page returned to user

---

## Template Examples

### Minimal incident creation template (JSON)

```json
{
  "short_description": "[SUBJECT]",
  "description": "[MESSAGE]",
  "category": "[REASON]",
  "contact_type": "email",
  "caller_id": "[CALLER]",
  "business_service": "[BUSINESSSERVICE]",
  "service_offering": "[SERVICEOFFERING]",
  "comments": "Reporter: [ADDITIONALINFOUSERNAME] ([ADDITIONALINFOEMAIL])\nSurvey: [ADDITIONALINFOSURVEYTITLE] ([ADDITIONALINFOSURVEYALIAS])\nSystem Info: [ADDITIONALINFO]"
}
```

### For external users (no caller_id)

```json
{
  "short_description": "[SUBJECT]",
  "description": "[MESSAGE]",
  "category": "[REASON]",
  "contact_type": "email",
  "business_service": "[BUSINESSSERVICE]",
  "service_offering": "[SERVICEOFFERING]",
  "comments": "External Reporter: [ADDITIONALINFOUSERNAME] ([ADDITIONALINFOEMAIL])\nSurvey: [ADDITIONALINFOSURVEYTITLE] ([ADDITIONALINFOSURVEYALIAS])\nSystem Info: [ADDITIONALINFO]"
}
```

---

## Implementation Checklist

Use this checklist when building your own ServiceNow client:

- [ ] **ServiceNow service account** with incident creation + attachment permissions
- [ ] **Configuration properties** for incident host URL, attachment host URL, and Base64 auth
- [ ] **JSON template(s)** mapped to your ServiceNow instance's schema
- [ ] **Input sanitization** — strip HTML tags and JSON-escape all user input before template insertion
- [ ] **HTTP client** with:
  - Basic Auth header
  - `Content-Type: application/json; charset=utf-8` for incident creation
  - `Content-Type: application/octet-stream` for attachments
  - Proxy support (if needed)
  - Timeout configuration
- [ ] **Response parsing** — extract `result.sys_id` from incident creation response
- [ ] **Attachment upload** — binary POST with `table_sys_id` and `file_name` as URL params
- [ ] **Error handling** — catch non-2xx responses and network errors, implement fallback
- [ ] **Logging** — log error responses for debugging
- [ ] **User type detection** (optional) — different templates for internal vs external users
- [ ] **LDAP integration** (optional) — resolve `caller_id` from user email for internal users

---

## Technology Stack (EUSurvey Reference Implementation)

| Component | Library/Version |
|-----------|----------------|
| HTTP Client | Apache HttpComponents (`HttpClients.createSystem()`) |
| JSON Parsing | `org.json.JSONObject` |
| HTML Stripping | Jsoup |
| JSON Escaping | Gson |
| Template Engine | Simple string replacement (`String.replace()`) |
| Framework | Spring MVC (Controller + `@PostMapping`) |

---

## Legacy SOAP Path

The code also supports a legacy SOAP-based path (the original "SMT" web service). This path is activated when `support.smIncidentHost` ends with `"wsdl"`. In that case:

- Content-Type is `text/xml;charset=UTF-8`
- A `SOAPAction: Create` header is added
- The template is `createIncident.xml`
- Success is determined by checking for `message="success"` in the response

New implementations should use the JSON/REST path.
