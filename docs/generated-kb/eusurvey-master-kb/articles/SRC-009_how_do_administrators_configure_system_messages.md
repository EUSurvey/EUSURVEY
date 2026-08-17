# SRC-009 — How do administrators configure system messages?

## Intent / Description

This article explains how administrators configure system-wide messages displayed to users.

## Applies To

* Role(s): Administrator
* EUSurvey area: Administration
* Environment: All
* Article type: How-To
* UI location: Administration > System
* Backend location: SystemController

## Short Answer

Administrators can configure system-wide messages from Administration > System. These messages appear as banners to all users or specific user groups. Messages can be used for maintenance notices, announcements, or warnings.

## Prerequisites / Required Permissions

* SystemManagement global privilege

## Procedure

1. Navigate to Administration > System.
2. Click 'Configure System Message'.
3. Set the message text.
4. Select the message type and criticality.
5. Choose the target audience (all users, runners, admins).
6. Set auto-deactivation if desired.
7. Activate the message.

## Important Conditions / Limitations

* Messages can target different user groups: all users, survey participants (runners), administrators.
* Messages have criticality levels that affect visual appearance.
* Messages can be set to auto-deactivate after a specified time.
* Multiple messages can be active simultaneously.
* Messages can be edited, deactivated, or deleted.
* Static system messages persist until manually removed.

## Troubleshooting

* Message not appearing: Check that the message is activated and targets the correct audience.

## Related Articles

* ES-041 — How do administrators manage users?
* ES-042 — How do administrators search surveys?

## Evidence / Source Traceability

* Frontend files: src/main/webapp/WEB-INF/views/administration/system.jsp
* Backend files: src/main/java/com/ec/survey/controller/SystemController.java, src/main/java/com/ec/survey/service/SystemService.java, src/main/java/com/ec/survey/model/Message.java
* Classes: SystemController, SystemService, Message
* Methods: configureMessage, disableMessage, getSystemMessages, deleteMessage
* Routes: GET /administration/system/messages, GET /administration/system/message
* Message keys: label.Messages, label.ConfigureSystemMessage, label.MessageText, label.MessageType, label.Criticality, label.AutoDeactivateOn
* Configuration keys: N/A

## Confidence and Review Status

High

## Metadata

* Domain: Administration
* EUSurvey area: System Messages
* Feature: System Messages
* User intent: How do administrators configure system messages?
* Article type: How-To
* User type: Administrator
* Required permission: Administrator
* Survey status: N/A
* Environment: All
* Keywords: message, system, announcement, notice, banner
* Synonyms: system announcement, maintenance notice, user notification
* Acronyms: N/A
* Related entities: Message
* Security / privacy relevance: None
* Search boost terms: system messages, admin announcements
* Source files: src/main/java/com/ec/survey/controller/SystemController.java, src/main/java/com/ec/survey/service/SystemService.java, src/main/java/com/ec/survey/model/Message.java
* Duplicate status: New
