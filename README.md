# OpenMRS idgen Module – Security & Kwaliteit (NEN-7510)

> **Project:** LU2 – Verbeteronderzoek software-kwaliteit  
> **Module:** ATIx IN-B2.4 Softwarearchitectuur & -kwaliteit 2025-26 P4  
> **Groep:** Groep 6: RafvanHooijdonk, SimonEulenpesch, SinanSagir, Rowen Albers  
> **Norm:** NEN-7510:2026 (informatiebeveiliging in de zorg)

---

## Inhoud van deze repository

| Map / Bestand | Beschrijving |
|---|---|
| `openmrs-module-idgen/` | Broncode van de OpenMRS ID Generation Module (v4.13.0) |
| `docs/checklist.md` | Centrale compliance checklist - status per eis, per opdracht |
| `docs/dast/` | DAST-rapport (OWASP ZAP) en instructies |
| `docs/sprints/` | Sprintplanningen en voortgang |
| `docs/assets/rubrics/` | Rubrics en opdrachten |
| `docs/assets/presentaties/` | Workshoppresentaties |
| `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/` | Alle security-deliverables (zie Documentatie hieronder) |
| `.github/workflows/` | CI/CD-pipelines (zie tabel hieronder) |
| `run-zap.sh` | Lokaal script om OWASP ZAP te draaien |
| `docker-compose.yml` | OpenMRS lokaal draaien via Docker |

---

## CI/CD Pipeline Overzicht

| Workflow | Trigger | Type | Doel | NEN-7510 |
|---|---|---|---|---|
| `ci-build-test.yml` | Push/PR op `main` | Build & Test | Maven build + JUnit tests | 8.25, 8.29 |
| `sast-codeql.yml` | Push/PR op `main`, wekelijks | SAST | CodeQL statische code-analyse op Java | 8.8, 8.29 |
| `sca-dependency-review.yml` | PR op `main` | SCA | Blokkeert HIGH/CRITICAL kwetsbaarheden en verboden licenties | 8.8 |
| `sbom-cyclonedx.yml` | Push op `main` | SBOM | CycloneDX SBOM generatie (116 componenten) | 8.8, 5.22 |
| `quality-gate-sonarcloud.yml` | Push/PR op `main` | Kwaliteitspoort | SonarCloud quality gate + JaCoCo coverage | 8.25, 8.28 |
| `dast-owasp-zap.yml` | Handmatig (`workflow_dispatch`) | DAST | OWASP ZAP full scan op draaiende OpenMRS | 8.29 |

> **Waarom is DAST handmatig?** OpenMRS heeft 3–8 minuten nodig om op te starten. Automatisch triggeren bij elke commit is niet praktisch; de workflow wordt handmatig gestart via Actions → Run workflow, waarna het rapport in `docs/dast/` wordt opgeslagen.

---

## Mini-ISMS: Beveiligingsbeleid en Procedures

### Doel en scope

Dit document beschrijft het beveiligingsbeleid en de CI/CD-procedures voor de OpenMRS idgen module repository, conform NEN-7510:2026 (informatiebeveiliging in de zorg). De scope omvat de broncode, CI/CD-pipeline, GitHub-omgeving en de bijbehorende deliverables van dit project.

### Verantwoordelijkheden

| Rol | Verantwoordelijkheid |
|---|---|
| Repository owner | Beheer van branch protection, environments, secrets en rulesets |
| Developer | Aanmaken van feature branches, openen van pull requests |
| Reviewer | Code review en goedkeuring van pull requests voor merge |
| Security Champion | Bewaken van security backlog, DAST en pentestrapportage |

### Branch Protection

Alle wijzigingen op `main` verlopen uitsluitend via een Pull Request. De configuratie wordt volledig afgedwongen via de ruleset **"Protect main – NEN-7510 Ctrl 8.4/8.32"** (status: Active, bypass list: leeg (niemand kan omzeilen)):

| Regel | Status |
|---|---|
| Pull Request verplicht voor elke merge naar `main` | ✅ Actief |
| Status checks (CI-workflows) moeten slagen vóór merge | ✅ Actief |
| CodeQL code scanning resultaten vereist (High of hoger) | ✅ Actief |
| Force pushes geblokkeerd | ✅ Actief |
| Deletions geblokkeerd | ✅ Actief |

### Security Controls

| Maatregel | Tool | Status | NEN-7510 |
|---|---|---|---|
| SAST | CodeQL (`sast-codeql.yml`) | ✅ Actief | 8.8, 8.29 |
| SCA - dependency check | Dependency Review Action (`sca-dependency-review.yml`) | ✅ Actief | 8.8 |
| SCA - alerts & updates | Dependabot | ✅ Actief | 8.8 |
| SBOM | CycloneDX via Syft (`sbom-cyclonedx.yml`) | ✅ Actief | 8.8, 5.22 |
| Kwaliteitspoort | SonarCloud (`quality-gate-sonarcloud.yml`) | ✅ Actief | 8.25, 8.28 |
| Codedekking | JaCoCo via SonarCloud | ✅ Actief | 8.29 |
| DAST | OWASP ZAP (`dast-owasp-zap.yml`) | ✅ Actief | 8.29 |
| Secret Scanning | GitHub Secret Protection + Push Protection | ✅ Actief | 8.28 |

### Secrets Beheer

Gevoelige waarden worden **nooit** in broncode opgeslagen. Alle secrets worden beheerd via GitHub Encrypted Secrets, gescheiden per environment:

| Environment | Secrets | Protection |
|---|---|---|
| `production` | Deployment-sleutels, productie-credentials | Required reviewers: 1; deployment vereist handmatige goedkeuring |
| `test` | Test-credentials | Geen extra goedkeuring vereist |

GitHub Push Protection blokkeert commits die bekende secret-patronen bevatten vóórdat ze de repository bereiken.

### Environments (OTAP)

| Environment | Protection Rule | Gebruik |
|---|---|---|
| `production` | ✅ Ja - Required reviewers (1) | Productie-deployments; vereist handmatige goedkeuring |
| `test` | ❌ Nee | Test-deployments |

### Artifact Bewaring

CI/CD-artifacts (SBOM, ZAP-rapport, CodeQL-resultaten) worden bewaard als GitHub Actions artifacts. Retentie is geconfigureerd op 365 dagen, maar het GitHub Free plan brengt dit automatisch terug naar **90 dagen**.

### Bekende beperkingen

| Beperking | Reden |
|---|---|
| Artifact retentie max 90 dagen | Free plan limiet |

---

## Lokaal draaien

### OpenMRS opstarten

```bash
docker-compose up -d
```

Bereikbaar op: http://localhost:8080/openmrs/ (wacht 3–8 minuten op volledige opstart)
