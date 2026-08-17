# Why a duplicated private survey cannot be accessed

## Intent / Description

Explains why a survey that was copied (duplicated) from a private/restricted survey may not be accessible to respondents, and how to resolve this.

## Applies To

* Role(s): Survey Manager
* Feature: Survey duplication, Access configuration
* Context: A survey owner copies a restricted survey and finds the copy is also inaccessible

## Short Answer

When you duplicate a survey, EUSurvey copies the security and access configuration from the original. If the original survey was restricted (e.g. invitation-only, password-protected, or EU Login-restricted), the copy inherits those restrictions. The new survey will not be accessible until:

1. The survey is **published** (copies start as unpublished drafts).
2. The **access restrictions** are reviewed and updated for the new survey's intended audience.
3. New **participation groups** are created or assigned (invitation lists from the original are not automatically duplicated with live invitations).
4. The survey's **security settings** are appropriate for its new purpose.

## Steps / Procedure

1. Open the duplicated survey.
2. Go to the **Overview** page and check the survey status. If it says "Draft" or "Unpublished", it must be published first.
3. Go to **Properties** → **Security** and review the access settings:
   - Is it set to "Invited participants only"? If so, you need to create new invitations.
   - Is a password set? Ensure participants know the password.
   - Is EU Login required? Ensure your audience has EU Login accounts.
4. If you want the new survey to be open to all, change the security settings accordingly.
5. **Publish** the survey.
6. Share the new survey's URL with participants (the copied survey has a new shortname and URL).

## Important Conditions / Limitations

* **New URL**: The duplicated survey has a different shortname and URL. The original survey's URL does not point to the copy.
* **No copied invitations**: Participation groups structure may be copied but actual invitation records (sent emails, active tokens) are not duplicated. You must create new invitations.
* **Publication state**: Copies are always created as unpublished drafts regardless of the original's state.
* **Access settings inherited**: Security mode, password, and restriction type are copied. These must be manually updated if the copy should have different access rules.
* **Owner privileges**: The person who created the copy is the owner and has full access. Other users who had privileges on the original need to be explicitly granted access to the copy.

## Troubleshooting / Related Cases

* If respondents get "not yet published": publish the survey.
* If respondents get access denied: check security settings and create appropriate invitations or change the access mode.
* If the original survey owner lost access to the copy: only the copy's owner can manage it.

## Out of Scope / Separate Topics

* What to do when duplication fails (see: KB-EUSURVEY-TECH-005)
* How to restrict access to a survey (see: SM-44)
* How to publish a survey (see: SM-61)
* How to send invitations (see: SM-108)

## Documentation / Support Path

* Visit: https://ec.europa.eu/eusurvey/home/documentation

## Retrieval Metadata

* business_domain: access_authentication
* user_role: survey_manager
* feature: survey_duplication, access_configuration
* tags: duplicated survey inaccessible, copied survey cannot access, private survey copy, restricted after copy
* synonyms: copied survey not accessible, duplicate survey permission problem, survey copy is locked, cloned survey access denied
* product_terms: Copy, Security, published, invitation, EU Login, shortname
* exclude: duplication errors, original survey access, general 403 errors
