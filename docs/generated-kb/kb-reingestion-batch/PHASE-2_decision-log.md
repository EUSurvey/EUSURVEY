# Phase 2 — KB Decision Log

**Generated**: 2026-07-13

This document classifies every identified problematic article or missing topic, and specifies the action required.

---

## Decision Log Table

| ID | Theme | User Intent | Current Status | Action | Related Failed Cases | Reason | Priority |
|----|-------|-------------|----------------|--------|---------------------|--------|----------|
| D-001 | Abuse | How do I report a spam or abusive survey? | article exists but too broad (PM-06_01) | rewrite | abuse/spam retrieval failures | PM-06_01 is generic, no abuse types listed, retrieval too broad | high |
| D-002 | Abuse | How do I report phishing emails from EUSurvey? | missing article | create | phishing/fake email queries hit wrong topic | No coverage at all — users fall into generic abuse article or fallback | high |
| D-003 | Abuse | What should my organisation do about suspicious EUSurvey emails? | missing article | create | phishing queries returning generic answers | No organisational guidance; queries get vague or wrong-topic responses | high |
| D-004 | Abuse | What abuse types can I report? | missing article | create | abuse queries getting incomplete answers | Code shows 6 types but KB never lists them — causes incomplete answers | high |
| D-005 | Abuse | Can a malicious or cloned survey be removed? | missing article | create | survey impersonation queries hitting wrong topic | Users asking about fake surveys get unrelated results | high |
| D-006 | Contributions | What does "incomplete contribution" mean? | missing article | create | incomplete/draft confusion causing wrong answers | Draft vs submitted distinction undocumented — causes misguided answers | high |
| D-007 | Contributions | How do I reset a respondent? | missing article | create | reset/reopen queries falling back or hitting SM-50 | Reset mechanism not documented — SM-50 covers config only, not action | high |
| D-008 | Contributions | Can a respondent change their submitted answers? | article exists but unclear (SM-48, SM-50) | enrich + split | edit-after-submission queries giving config-focused answers | SM-48 and SM-50 overlap and mix manager config with respondent experience | high |
| D-009 | Contributions | What happens if a respondent submits twice? | article overlap / near-duplicate (SM-46/SM-47) | create (distinct angle) | duplicate submission queries retrieving anti-bot articles | SM-46/47 focus on prevention config, not on what actually happens | medium |
| D-010 | Deletion | What happens when I delete a survey? | article exists but unclear (SM-23) | rewrite | deletion-consequence queries getting minimal answer | SM-23 says "cannot be undone" but no details on what is lost | high |
| D-011 | Deletion | Can I undo survey deletion? | missing article | create | undo/recovery queries hitting SM-23 which says "cannot be undone" | Users need recovery guidance, not just a warning | high |
| D-012 | Deletion | How do I delete a contribution? | article exists but too broad (WS-042 API only) | create (end-user version) | contribution deletion queries returning API-focused article | WS-042 is Web Service documentation, not end-user help | high |
| D-013 | Deletion | Why is the delete option missing? | missing article | create | delete-unavailable queries causing fallback | No article explains why delete might not be visible | medium |
| D-014 | Deletion | How does inactive survey auto-deletion work? | article exists OK (SM-09_11) | enrich | inactive deletion queries missing timing/condition details | Good article but needs more detail on exact triggers from code | medium |
| D-015 | Invitations | Why are some imported contacts skipped? | missing article | create | import-failure queries causing fallback | No article explains validation rules or skip reasons | high |
| D-016 | Invitations | Why do recipients get duplicate invitations? | missing article | create | duplicate invitation queries causing fallback | No coverage — common support question | medium |
| D-017 | Invitations | How do I check invitation delivery status? | missing article | create | delivery-status queries causing fallback | No article explains invitation tracking | medium |
| D-018 | Results | Why do Contributions and Results show different numbers? | missing article | create | count-mismatch queries causing wrong-topic or fallback | Most confusing user question with no direct article | high |
| D-019 | Results | Why does my export not include latest responses? | article exists but unclear (SM-87) | enrich (new article better) | stale-export queries getting vague "12 hours" answer | SM-87 too brief; new article adds actionable steps | high |
| D-020 | Results | Why are charts not updated? | likely retrieval confusion (SM-87 same topic) | de-prioritize SM-87 for charts, new article covers both | chart-related queries pulling generic "results delay" content | Need specific chart-sync explanation | medium |
| D-021 | Technical | Why am I asked to log in again? | missing article | create | session-timeout queries causing fallback | Common user frustration with zero coverage | high |
| D-022 | Technical | Why does the survey freeze during submission? | missing article | create | freeze/error queries causing generic fallback | Top technical complaint with no article | high |
| D-023 | Technical | What should I do if I see a technical problem message? | article exists but too broad (SM-08) | title not matching user wording | generic-error queries returning "contact support" only | SM-08 only says "contact support" — not actionable | high |
| D-024 | Configuration | Why does a conditional question not appear? | missing article | create | visibility/dependency queries causing fallback | Complex feature with zero troubleshooting guidance | high |
| D-025 | Configuration | Can I edit a survey after publishing? | article exists but unclear (SM-67) | enrich (new article better) | post-publish editing queries getting partial answer | SM-67 explains possibility but not "Apply Changes" workflow clearly | medium |
| D-026 | Configuration | How do translations work in published surveys? | likely prompt-sensitive but KB needs tightening | enrich existing (SM-57) | translation-in-published queries getting mixed content | SM-57 covers publish/unpublish translations but not the user-visible effect | medium |

---

## Status Classification Legend

| Status | Meaning |
|--------|---------|
| missing article | No article exists for this user intent |
| article exists but unclear | An article exists but is too vague or brief to be useful |
| article exists but too broad | An article covers multiple intents, harming precision |
| article overlap / near-duplicate | Multiple articles compete for same query |
| title not matching user wording | Article exists but title/keywords don't match how users ask |
| likely retrieval confusion | Retrieval system may pick adjacent content instead of correct answer |
| likely prompt-sensitive | Answer depends heavily on prompt wording; KB tightening helps reduce variance |
