# Widget Integration Guide — EUSurvey

How to embed the AI Support Assistant chatbot widget into EUSurvey.

---

## Overview

The chatbot can be embedded in two ways:

1. **`<ec-chatbot>` web component** — modern, Shadow DOM isolated, full-featured (recommended)
2. **`ChatWidget` standalone script** — simpler, legacy-compatible drop-in

Both connect to the same backend API and provide the same chat functionality.

---

## Option 1: Web Component (Recommended)

### Integration

Add to your EUSurvey page template (e.g. in the main layout JSP/Thymeleaf or a shared footer):

```html
<!-- Load the widget script -->
<script type="module" src="https://YOUR_HOST/eusurvey/chatbot-widget/ec-chatbot.js"></script>

<!-- Embed the widget -->
<ec-chatbot
  app-id="eusurvey"
  api-url="https://YOUR_HOST/eusurvey/chatbot-api/chatbot"
  mode="widget"
  position="bottom-right"
  locale="en"
  user-role="survey-manager"
  page-context="survey-editor"
></ec-chatbot>
```

### Attributes

| Attribute | Required | Default | Description |
|-----------|----------|---------|-------------|
| `app-id` | Yes | — | Application identifier. Use `"eusurvey"` |
| `api-url` | Yes | — | Backend bootstrap API URL |
| `mode` | No | `widget` | `widget` (floating FAB), `inline` (embedded), `fullpage` (fills container) |
| `position` | No | `bottom-right` | `bottom-right` or `bottom-left` (widget mode only) |
| `locale` | No | `en` | UI language code |
| `user-id` | No | — | Current user's ID (for session tracking) |
| `user-role` | No | — | User's role in EUSurvey (see roles below) |
| `page-context` | No | — | Current page/section name (see contexts below) |
| `entity-type` | No | — | Entity type being viewed (e.g. `"survey"`) |
| `entity-id` | No | — | Entity ID being viewed (e.g. survey alias or ID) |
| `theme` | No | — | Theme override (reserved for future use) |

### User Roles

Pass the current user's role to help the chatbot provide contextual answers:

| Role | When to use |
|------|-------------|
| `survey-manager` | User who creates/manages surveys |
| `contributor` | User filling out or submitting a survey |
| `admin` | EUSurvey platform administrator |
| `guest` | Unauthenticated user |

### Page Contexts

Pass the current page to get relevant starter suggestions and answer prioritization:

| Page Context | Where |
|--------------|-------|
| `survey-editor` | Creating or editing a survey |
| `survey-runner` | Filling out / responding to a survey |
| `survey-results` | Viewing survey results |
| `survey-settings` | Survey configuration (access rights, notifications) |
| `dashboard` | Main EUSurvey dashboard |
| `user-settings` | Account and profile settings |
| `admin-panel` | Platform administration |
| `help` | Help or documentation pages |

### Dynamic Context Updates

If the user navigates within a SPA or changes context, update the attributes:

```javascript
const chatbot = document.querySelector('ec-chatbot');

// User navigates to a different page
chatbot.setAttribute('page-context', 'survey-results');

// User opens a specific survey
chatbot.setAttribute('entity-type', 'survey');
chatbot.setAttribute('entity-id', 'my-survey-alias');
```

### Events

The widget emits custom events you can listen to:

```javascript
const chatbot = document.querySelector('ec-chatbot');

// Chat is ready
chatbot.addEventListener('chatbot-ready', (e) => {
  console.log('Chatbot ready:', e.detail.conversationId);
});

// User sent a message or received an answer
chatbot.addEventListener('chatbot-message', (e) => {
  console.log('Message:', e.detail.role, e.detail.content);
});

// User submitted feedback
chatbot.addEventListener('chatbot-feedback', (e) => {
  console.log('Feedback:', e.detail.rating);
});

// User escalated to support
chatbot.addEventListener('chatbot-escalation', (e) => {
  console.log('Escalation:', e.detail.issueKey);
});

// Widget opened/closed
chatbot.addEventListener('chatbot-toggle', (e) => {
  console.log('Widget open:', e.detail.isOpen);
});
```

### Programmatic Control

```javascript
const chatbot = document.querySelector('ec-chatbot');

chatbot.open();                    // Open the chat window
chatbot.close();                   // Close it
chatbot.toggle();                  // Toggle open/closed
chatbot.sendMessage('How do I publish a survey?');  // Send a message
chatbot.clearConversation();       // Clear chat history
```

---

## Option 2: Standalone Script (Legacy)

For simpler integration without module support:

```html
<script src="https://YOUR_HOST/eusurvey/chatbot-widget/chat-widget.js"></script>
<script>
  new ChatWidget({
    title: 'EUSurvey Help',
    apiUrl: 'https://YOUR_HOST/eusurvey/chatbot-api',
    debugMode: false,
    topOffset: 75  // Offset in px for EUSurvey header bar
  });
</script>
```

### Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `title` | `'Assistant'` | Window title |
| `apiUrl` | — | Backend API base URL (required) |
| `debugMode` | `true` | Show RAG debug info (set `false` for production) |
| `topOffset` | `0` | Pixels from top (use to avoid covering EUSurvey header) |
| `datasources` | `[]` | Array of datasource codes (leave empty for default) |
| `temperature` | `0` | LLM temperature (0 = deterministic) |
| `topK` | `5` | Documents to retrieve |
| `modelId` | `'mistral-medium-2508'` | LLM model identifier |
| `systemPrompt` | `''` | Custom system prompt override |

---

## Environment URLs

| Environment | Widget Script URL | API URL |
|-------------|-------------------|---------|
| Local Dev | `http://localhost:4200/chat-widget.js` | `http://localhost:8081` |
| Acceptance | `http://ovishime.cc.cec.eu.int:8090/eusurvey/chatbot-widget/chat-widget.js` | `http://ovishime.cc.cec.eu.int:8090/eusurvey/chatbot-api` |
| Production | `https://YOUR_PROD_HOST/eusurvey/chatbot-widget/chat-widget.js` | `https://YOUR_PROD_HOST/eusurvey/chatbot-api` |

For the `<ec-chatbot>` web component, the `api-url` should include the `/chatbot` suffix:
- Acceptance: `http://ovishime.cc.cec.eu.int:8090/eusurvey/chatbot-api/chatbot`

---

## CORS Configuration

The chatbot backend must allow the EUSurvey origin. Add it to `cors.allowed-origins` in the backend settings:

```properties
cors.allowed-origins=https://webgate.acceptance.ec.europa.eu,http://ovishime.cc.cec.eu.int:8080
```

Or update via the monitoring dashboard: Admin → Settings → Access & Security → Allowed Origins.

---

## EUSurvey-Specific Integration Example

### JSP Integration (Server-Side Rendered)

```jsp
<%-- In your main layout footer, before </body> --%>
<script type="module" src="${pageContext.request.contextPath}/chatbot-widget/ec-chatbot.js"></script>
<ec-chatbot
  app-id="eusurvey"
  api-url="${pageContext.request.contextPath}/chatbot-api/chatbot"
  mode="widget"
  user-role="${currentUser.role}"
  page-context="${currentPage}"
  entity-type="${entityType}"
  entity-id="${entityId}"
  locale="${pageContext.response.locale.language}"
></ec-chatbot>
```

### Angular/React SPA Integration

```typescript
// In your app component or layout
@Component({
  template: `
    <ec-chatbot
      app-id="eusurvey"
      [attr.api-url]="apiUrl"
      [attr.user-role]="userRole"
      [attr.page-context]="currentRoute"
      [attr.entity-type]="entityType"
      [attr.entity-id]="entityId"
    ></ec-chatbot>
  `
})
export class AppComponent {
  apiUrl = environment.chatbotApiUrl;
  // ... bind to your user/route state
}
```

---

## What the User Sees

1. A floating chat button (FAB) appears in the bottom-right corner
2. Clicking it opens a chat window with a welcome message and starter suggestions
3. The user types a question → the system retrieves relevant KB articles and generates an answer
4. Citations are shown below answers (links to source documentation)
5. The user can give thumbs up/down feedback on each answer
6. If unsatisfied, the user can escalate to support (creates a Jira ticket)
7. Font size controls (A+/A-) in the header for accessibility

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Widget doesn't appear | Check console for script load errors. Verify the script URL is correct. |
| "Failed to load configuration" | The `app-id` or `api-url` is wrong. Check network tab for the bootstrap call. |
| CORS error | Add the EUSurvey origin to `cors.allowed-origins` in backend settings. |
| Intermittent "Connection error" | Normal — the widget retries automatically up to 2 times. If persistent, check backend health. |
| Answers say "not enough info" | The system auto-retries with reformulated query. If still failing, KB may need more content on that topic. |
