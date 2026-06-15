# CLAUDE.md

Rules for Claude when working in this repo. Read this before doing anything.

---

## Before starting any task

Read these files first. No exceptions.

1. `README.md`
2. `SECURITY.md`
3. `docs/checklist.md`
4. All files in `docs/sprints/`
5. Any other `.md` files in `docs/`
6. `docs/assets/rubrics/rubric-onderhoudbaarheid.md` and `docs/assets/rubrics/rubric-security.md` if working on a formal deliverable
7. Relevant workflow files in `.github/workflows/` if the task involves CI/CD

Then think. Then say what you are going to do and why. Then act.

---

## Announce before acting

Before making changes, write a short message explaining:
- What you are going to change
- Why
- Which files will be affected

Example: "Going to update eis #3 in docs/checklist.md to tijdelijk compliant because CodeQL currently scans a stub project with no real Java code."

Do not act silently. Do not surprise the user.

---

## Keep all documentation up to date

This is not optional. After every change, update the relevant docs.

When something in the pipeline changes:
- Update `docs/checklist.md` (status, notities, samenvatting, wijzigingslog)
- Update `README.md` if the Mini-ISMS table or pipeline overview is affected
- Update `SECURITY.md` if the security tooling or process changes

When work on a sprint task starts or is done:
- Update the "Wie" column in the relevant `docs/sprints/sprintN.md` to reflect who is working on it
- Update the Status column in the relevant `docs/sprints/sprintN.md`
- **Crucial:** Update the markdown checkbox (change `- [ ]` to `- [X]`) in the `Eindcheck Sprint X` list at the bottom of the relevant `docs/sprints/sprintN.md` file if a task is completed or Tijdelijk compliant.
- Update the corresponding eindcheck row in `docs/checklist.md` (Sprints section)
- Update the sprint overzichtstabel status if the whole sprint is done
- **Crucial:** Update the `Verantwoordelijke:` field at the top of the relevant Opdracht in `docs/checklist.md` to include the name of the person working on it (e.g. `RafvanHooijdonk, SinanSagir`).
- **Crucial:** Ensure the `Sprints:` mapping under the Opdracht header accurately details which specific tasks map to which sprint.

After completing ANY task, always perform a Sync Check:
- Verify that statuses in `docs/sprints/sprintN.md` perfectly match `docs/checklist.md`
- Ensure the samenvatting counts at the bottom of `docs/checklist.md` sections precisely reflect the row statuses
- Do not assume they are in sync; explicitly check them

---

## Hierarchy & Source of Truth

### Strict Consistency & Pre-Check Rule
Because this is a group project where team members work asynchronously, there is a high risk of conflicting documentation.
**Before creating, writing, or modifying any deliverable**, you MUST:
1. Search and read related files in the `docs/` directory to see what has already been established by other team members.
2. Read the `docs/checklist.md` to ensure you understand the exact boundaries and context of the assignment.
3. Guarantee that your new content perfectly aligns with existing findings, risk matrices, pipelines, and reports. 
4. DO NOT generate disconnected, conflicting, or duplicate information. You must be extremely strict in maintaining consistency across the entire repository, especially after large assignments have been completed.

To avoid duplication and confusion, respect the document hierarchy:
1. **`docs/checklist.md` is the Source of Truth.** It contains the EXTENSIVE, detailed requirements (eisen), exact NEN-7510 controls, and detailed proof.
2. **`docs/sprints/sprintN.md` are Execution Plans.** They contain high-level tasks used for daily tracking. They map to the detailed requirements in the checklist.

**Rule of thumb:** Do NOT duplicate exact, detailed requirements from the checklist into the sprint tasks. When a sprint task is updated, always check if the corresponding detailed requirement in `checklist.md` needs to be updated, and ensure their statuses are identically synced across all files.

When a new file is added to `docs/`:
- Add it to the inhoudsopgave in `docs/checklist.md` if it belongs there

When you add a new opdracht to the checklist:
- Add it to the inhoudsopgave at the top of `docs/checklist.md`
- Fill in doel, verantwoordelijke, periode, bron (with clickable PDF link and page number)
- Use the template at the bottom of `docs/checklist.md`

Checklist status values:
- `✅ Compliant` = works fully, no conditions
- `⚠️ Tijdelijk compliant` = works but relies on stub or placeholder, not the real module
- `⚠️ Gedeeltelijk` = partially working, structural limitation
- `❌ Niet compliant` = blocked or not implemented
- `❌ Open` = not started yet

When you change a status, also update the samenvatting counts for that opdracht.
When you make a notable change, add a row to the wijzigingslog and bump the version number.

---

## Writing style

No em dashes. No en dashes. Write like a human.

Bad: "This approach -- while elegant -- introduces risk."
Good: "This approach introduces risk."

Bad: "Module-keuze vastleggen -- documenteer de module."
Good: "Module-keuze vastleggen: documenteer de module."

No filler. No padding.

Bad: "It is worth noting that the pipeline currently lacks..."
Good: "The pipeline lacks..."

No AI language. Never write: certainly, absolutely, of course, great question, I'd be happy to, I'll help you with that.

Short sentences. Simple words. Direct.

Caveman mode. If caveman cannot understand, rewrite it.

When you are unsure whether something counts as a dash: if it is not a hyphen inside a word (like `docker-compose`), remove it and use a colon or period instead.

---

## Code rules

Comments explain why, not what.

Bad: `// loop through the list`
Good: `// order matters here, do not sort before this step`

When editing a workflow: check if other workflows already do the same thing. Do not duplicate.

When touching `pom.xml`: the real OpenMRS idgen module (v4.13.0) is in `openmrs-module-idgen/`. All Maven work targets that directory. There is no stub pom.xml.

---

## Repo context

LU2 project at Avans Hogeschool. Proves a compliant CI/CD security pipeline for an OpenMRS module under NEN-7510.

The real OpenMRS idgen module (v4.13.0) is in `openmrs-module-idgen/`. All workflows target this directory. All statuses in the checklist reflect this real module, not a stub.

Known limitations (GitHub Free plan):
- Branch protection: fully enforced via ruleset "Protect main – NEN-7510 Ctrl 8.4/8.32" (repo is public — bypass list is empty)
- Secret Scanning: Secret Protection + Push Protection are active (public repo, free)
- Artifact retention: capped at 90 days (only remaining limitation)

Known test exclusions (Java 11 incompatibilities in upstream idgen code, not our code):
- `LocationBasedPrefixProviderTest` — PowerMock 1.x incompatible with Java 11 module system
- `SequentialIdentifierGeneratorTest` — PowerMock 1.x incompatible with Java 11 module system
- `IdentifierResourceTest` — char[] cast from String fails on Java 11

These are documented as technical debt in `docs/LU2 - Kwaliteit en security - verbeteronderzoek onderhoudbaarheid/Groep_6_Non-Functional-Requirements.md`.

NFR baseline (2026-06-12, alle data uit SonarCloud):
- Coverage: 50% overall (api 54.3%, omod 46.9%) — quality gate gefaald (drempel 60% new code)
- Duplicatie: 5.8% overall (api 2.6%, omod 10.8%) — 405 regels, 19 blokken, 11 bestanden
- Cognitive Complexity totaal: 668 — top outlier: IdentifierSourceResource (CC 223, 498 LOC)
- Brain Methods: 3 methoden boven CC 15 (CC 101, 106, 27)
- Open issues: 204 (api 94, omod 110) — 3 dagen 7 uur herstelkost
- Security Rating: C (1 vulnerability: hardcoded password in IdgenModuleActivator)
- Maintainability Rating: A (overall), D (new code) — technical debt ratio 1.4% overall, 23.3% new code
- Reliability Rating: A (2 info-level issues in testcode)
- Security Hotspots: 0 unreviewed

NFR doelen (na PoC): coverage 70%, duplicatie 3%, security A, max 150 open issues, 0 Brain Methods.

SonarCloud project: AvansHogeschoolBreda_openmrsmodule (org: avanshogeschoolbreda).

Sprint files are in `docs/sprints/`. Each sprint has tasks with a Status and Wie column. Keep these up to date.

---

## What not to do

Do not create files unless asked.
Do not refactor things not mentioned in the task.
Do not add emoji unless they already exist in that file.
Do not change markdown table formatting unless fixing a bug.
Do not invent requirements. Ask if unsure.
Do not make changes without announcing them first.
Do not use em dashes, en dashes, or double hyphens as separators in text. Use a colon or period.

---

## Ironclad Repository Structure

To prevent ANY chaos, hallucination, or wrong file placements (even with random prompting), the following directory structure is STRICTLY enforced. You must NEVER create files outside of their designated directories.

### Root Directory (`/`)
- `README.md`: Mini-ISMS policy, procedures, pipeline overview.
- `SECURITY.md`: Security policy, vulnerability reporting process, tool status.
- `CLAUDE.md`: AI behavior rules (this file).
- `pom.xml`: Not present (removed). The real module lives in `openmrs-module-idgen/`.
- `docker-compose.yml`: For the OTAP setup (when created).
**Rule:** Do NOT put any formal deliverables, analysis documents, or markdown reports in the root directory.

### `.github/workflows/`
Contains all CI/CD pipelines (`ci-build-test.yml`, `sast-codeql.yml`, `dependabot.yml`, `sca-dependency-review.yml`, `sbom-cyclonedx.yml`).

### `docs/`
The ONLY place for documentation. But do NOT dump deliverables directly in `docs/`.
- `docs/checklist.md`: The absolute **Source of Truth** for detailed requirements, compliance metrics, and changelogs.

### `docs/sprints/`
Contains `sprint1.md`, `sprint2.md`, etc.
- These are high-level **Execution Plans**.
- **Rule:** Do NOT put detailed deliverables here. Only sprint planning and tracking.

### `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/`
**ONLY formal security deliverables go here:**
- Gap-analyse, Mini-complianceverslag, Risk Assessment Report, Pentest, SBOM analyse, Audit rapport, Bow-tie, DPIA, Patchadvies, Asset-Identificatie, Risicomatrix, Security-Backlog, Patchadvies.

**Rule:** Do NOT create subdirectories like `docs/auditrapport/` or similar. All security deliverables go directly in this folder with the `Groep_6_` prefix. If a sprint file references a different output path, the sprint file is wrong. Follow CLAUDE.md, not the sprint file.

### `docs/LU2 - Kwaliteit en security - verbeteronderzoek onderhoudbaarheid/`
**ONLY formal maintainability deliverables go here:**
- Module-keuze, Code kwaliteitsrapport, Klasse diagrammen, Refactoring onderbouwing, Testplan, Coverage rapport.

### `docs/assets/`
For PDFs, images, presentations, and raw source materials.

### `docs/assets/rubrics/`
Contains the official assessment rubrics for LU2, both as PDF (original) and markdown:
- `rubric-onderhoudbaarheid.md`: Rubric for Verbeteronderzoek Onderhoudbaarheid (max 100 pt, pass at 55)
- `rubric-security.md`: Rubric for Verbeteronderzoek Security (max 100 pt, pass at 55)

**Read the relevant rubric before writing or reviewing any formal deliverable.** The rubrics define what Onvoldoende, Voldoende, and Goed look like per criterion. Use them to verify that deliverables meet at minimum the Voldoende level on every criterion.

**The target is always "Goed" (maximum score), not "Voldoende".** Do not stop at the minimum. Every deliverable must be written to the highest rubric level.

---

## Always aim for Goed (maximum score)

This section is mandatory reading before writing any formal deliverable. It translates the rubric into concrete requirements.

### What "Goed" requires across all security deliverables

**Grondig (thorough):**
- Cover the full scope. Do not skip assets, controls, or risks because they seem minor.
- Include threat actors with motivations, capabilities, and relevance to the specific system.
- Apply a named threat modeling methodology (STRIDE is the default for this project).
- Document existing controls per asset, not just the threats.
- Document residual risk: what remains after existing controls are applied.

**Herleidbaar (traceable/auditable):**
- Every claim must reference a source. No unsupported assertions.
- Reference specific NEN-7510:2026 control numbers (e.g. Ctrl 8.5, not just "NEN-7510").
- Reference specific AVG articles (e.g. Art. 9, Art. 32, Art. 33).
- Reference CWE numbers for vulnerabilities (e.g. CWE-307, CWE-89).
- Reference OWASP Top 10 (2021) categories where applicable (e.g. A01, A07).
- Reference external threat intelligence sources: NCSC CSBN, Verizon DBIR, HaveIBeenPwned.
- Every score (kans, impact, risico) must include a one-line justification with a source.

**Onderbouwd met bronnen en normen (backed by sources and standards):**
- Include a sources section at the top listing all references with URLs.
- Use sector-specific data for kans scores (healthcare breach statistics, NCSC reports).
- Cross-reference multiple standards when they align (NEN-7510 + AVG + OWASP).

**Prioritering en aanpak expliciet gemotiveerd (explicitly justified prioritization):**
- When choosing which item gets the bow-tie, the most detailed analysis, or the first fix: explain why this item and not another.
- If a lower-scoring item is chosen over a higher one, explain the reasoning (context, controls, demonstrability).
- State what happens if a risk is NOT mitigated (concrete consequences, not vague language).

### Rubric criteria and what feeds them

| Rubric criterium | Deliverables die dit voeden | Wat "Goed" concreet betekent |
|---|---|---|
| Security audit: wetgeving & normen (20pt) | Gap-analyse, Complianceverslag, Asset-identificatie | Alle NEN-7510 controls met specifieke nummers, AVG-grondslag per datatype, prioritering met expliciete motivatie, herleidbare bronnen |
| Secure pipelines (15pt) | Pipeline setup (Opdracht 1), README/SECURITY | OTAP-scheiding met niet-herleidbare data gedocumenteerd, keuzes verantwoord |
| Advies updates: SBOM, CVE, CVSS (15pt) | Patchadvies, Security backlog | CVSS-scores geverifieerd via NVD (niet alleen Snyk), prioritering op basis van bereikbaarheid en healthcare-impact, concrete versie-aanbevelingen |
| Security code review & kwetsbaarheden (15pt) | Security backlog, RAR | CWE per bevinding, CVSS contextuele score, relatie kwetsbaarheid tot daadwerkelijk systeemgebruik uitgelegd |
| Penetration tests (15pt) | Pentest rapport | Systematisch opgezet, reproduceerbaar, daadwerkelijke exploitatie aangetoond |
| Mitigatie & validatie (20pt) | PoC mitigatie, her-pentest | Kwantitatieve vergelijking voor/na, kritische reflectie op tooling en aanpak |

### Minimum required elements per security deliverable

Elk formeel security deliverable moet bevatten:

1. Scope (wat valt wel en niet binnen dit document)
2. Methodiek (welke aanpak, waarom)
3. Relevante wet- en regelgeving (NEN-7510 controls met nummers, AVG artikelen)
4. Bronnenlijst (URLs, rapportnamen, versies)
5. Per bevinding/asset/risico: classificatie, bestaande controls, residueel risico
6. Expliciete prioritering met motivatie
7. Koppeling naar andere deliverables (wat volgt hieruit, wat levert dit aan voor de bow-tie/RAR/etc.)

---

## Checklist tabel opmaak

Houd de Bewijslast-kolom in `docs/checklist.md` kort. Gebruik alleen de bestandsnaam zonder het `Groep_6_` prefix en zonder pad.

Goed: Asset-Identificatie.md, Settings → Environments, ci-build-test.yml
Fout: `Asset-Identificatie.md` (geen backticks, want monospace font zorgt voor extra breedte en wrapping)
Fout: `Groep_6_Asset-Identificatie.md` (geen Groep_6_ prefix)
Fout: `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Asset-Identificatie.md` (geen volledig pad)

Dezelfde regel geldt voor alle andere kolommen in checklist-tabellen: houd celinhoud beknopt zodat tabellen leesbaar blijven. Als een verwijzing meer context nodig heeft, zet die in de Notities-kolom, niet in Bewijslast.

---

## Links altijd clickable maken

Schrijf nooit een kale URL. Gebruik altijd de markdown link-syntax: `[tekst](url)`.

Fout: `https://owasp.org/Top10/`
Fout: `OWASP Top 10: https://owasp.org/Top10/`
Goed: `[OWASP Top 10 (2021)](https://owasp.org/Top10/)`

Dit geldt voor:
- Bronnenlijsten in deliverables
- Referentieregels per asset/bevinding/control
- Links in tabellen
- Links in checklist.md en sprint-bestanden

Voor bronnen zonder publieke URL (betaalde normen zoals NEN-7510): link naar de productpagina van de uitgever (bv. https://www.nen.nl/nen-7510).
Voor interne PDFs in de repo (bv. presentaties in `docs/assets/presentaties/`): gebruik een relatief pad als markdown link, bv. `[WS03](../assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf)`.
Uitzondering: interne repo-verwijzingen naar `.md`-bestanden en mappen worden niet als hyperlink geschreven maar als code-inline (backticks), omdat relatieve paden in die context geen meerwaarde hebben.

---

## Strict Naming & Header Rules for Deliverables

`Groep_6_*.md` files are official deliverables, not tracking documents. They do NOT get a wijzigingslog or versie/datum fields in the header. Changes are tracked in `docs/checklist.md`.

Date format in deliverables: dd/mm/yyyy, e.g. `12/06/2026`. Use ISO (YYYY-MM-DD) only in wijzigingslog tables in checklist and sprint files.

When creating a formal document in one of the `LU2...` folders:

1. **Naming:** `Groep_6_[Naam-van-Document].md` (e.g., `Groep_6_Gap-Analyse.md`).
2. **Header:** Every official document MUST start exactly with this block:

```markdown
# [Titel van het document]

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---
```

No exceptions. Every file in these two formal folders gets this header.
Copy this block exactly. Do not reformat the table or change the spacing.
