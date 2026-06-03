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
6. Relevant workflow files in `.github/workflows/` if the task involves CI/CD

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

When touching `pom.xml`: it is currently a stub. Note this. The real OpenMRS module pom.xml replaces it later.

---

## Repo context

LU2 project at Avans Hogeschool. Proves a compliant CI/CD security pipeline for an OpenMRS module under NEN-7510.

The actual OpenMRS module code is not in this repo yet. Most workflows run against a stub `pom.xml` with only JUnit 5. CodeQL, Dependabot, Dependency Review, and SBOM are all tijdelijk compliant because of this.

When the real module is added, replace the stub `pom.xml`. Update checklist statuses accordingly.

Known permanent limitations (GitHub Free plan, private repo):
- Branch protection is configured but not enforced
- Secret Scanning is unavailable
- Artifact retention is capped at 90 days

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
- `pom.xml`: Maven configuration (currently a stub).
- `docker-compose.yml`: For the OTAP setup (when created).
**Rule:** Do NOT put any formal deliverables, analysis documents, or markdown reports in the root directory.

### `.github/workflows/`
Contains all CI/CD pipelines (`ci.yml`, `codeql.yml`, `dependabot.yml`, `dependency-review.yml`, `sbom.yml`).

### `docs/`
The ONLY place for documentation. But do NOT dump deliverables directly in `docs/`.
- `docs/checklist.md`: The absolute **Source of Truth** for detailed requirements, compliance metrics, and changelogs.

### `docs/sprints/`
Contains `sprint1.md`, `sprint2.md`, etc.
- These are high-level **Execution Plans**.
- **Rule:** Do NOT put detailed deliverables here. Only sprint planning and tracking.

### `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/`
**ONLY formal security deliverables go here:**
- Gap-analyse, Mini-complianceverslag, Risk Assessment Report, Pentest, SBOM analyse, Audit rapport, Bow-tie, DPIA, Patchadvies.

### `docs/LU2 - Kwaliteit en security - verbeteronderzoek onderhoudbaarheid/`
**ONLY formal maintainability deliverables go here:**
- Module-keuze, Code kwaliteitsrapport, Klasse diagrammen, Refactoring onderbouwing, Testplan, Coverage rapport.

### `docs/assets/`
For PDFs, images, presentations, and raw source materials.

---

## Strict Naming & Header Rules for Deliverables

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
