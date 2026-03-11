# Security Policy

## Reporting a Vulnerability

The EUSurvey team takes security vulnerabilities seriously.

If you discover a security vulnerability in EUSurvey, **please do not report it publicly via GitLab issues or discussions**.

Instead, please report it responsibly using one of the following channels:

- Email: DIGIT-EUSURVEY-SUPPORT@ec.europa.eu
  
Please include:

- A clear description of the vulnerability
- Steps to reproduce the issue
- The affected version (if known)
- Any proof-of-concept code or screenshots (if applicable)
- Your contact details (if you wish to receive follow-up information)

We will acknowledge receipt of your report as soon as reasonably possible.

---

## Responsible Disclosure

We kindly request that:

- You allow us reasonable time to investigate and fix the issue before public disclosure.
- You do not exploit the vulnerability beyond what is necessary to demonstrate its existence.
- You do not access, modify, or delete data that does not belong to you.

We aim to investigate and remediate confirmed vulnerabilities in a timely manner,
depending on their severity and impact.

---

## Scope

This security policy applies to:

- The open-source EUSurvey codebase hosted on code.europa.eu
- Official releases derived from this repository

This policy does **not** automatically cover:

- Private or modified deployments operated by third parties
- Infrastructure operated outside the official European Commission environment

For vulnerabilities affecting the production instance operated by the European Commission,
reports may also be submitted via the European Commission vulnerability disclosure channels,
where applicable.

---

## Supported Versions

Security updates are generally provided for:

- The latest stable release
- Actively maintained branches

Older versions may not receive security patches.

---

## Security Best Practices for Deployments

If you deploy EUSurvey yourself, we recommend:

- Keeping the application and dependencies up to date
- Using HTTPS/TLS for all communications
- Securing database access (no public exposure)
- Storing secrets outside of the source code
- Applying appropriate authentication and access control mechanisms
- Monitoring logs for abnormal activity

The maintainers are not responsible for insecure third-party deployments.

---

## Disclaimer

EUSurvey is distributed under the EUPL v1.2 and provided "as is".
While reasonable care is taken to ensure security,
no warranty is provided regarding fitness for a particular purpose.
