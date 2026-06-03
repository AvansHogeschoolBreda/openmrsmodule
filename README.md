# OpenMRS Module: Security Pipeline (NEN-7510)

## LU2: Verbeter-onderzoek software-kwaliteit

Je onderzoekt een legacysysteem op kwaliteitsverbeteringen rond onderhoudbaarheid en security.
Je ontwikkelt proof-of-concepts ter ondersteuning van je onderzoeksresultaten en verdedigt deze
in een individueel interview.

Met een gestructureerd software-assessment analyseer je de onderhoudbaarheid van het systeem.
Op basis van bevindingen ontwerp en implementeer je verbeteringen als PoC, inclusief onderbouwing
van gemaakte keuzes. Met een herassessment toon je de kwaliteitsverbeteringen aan.

Op het gebied van security voer je een systematische analyse uit conform NEN-7510 en stelt een
geprioriteerde aanpak op. Je analyseert 3rd party afhankelijkheden en adviseert over updates.
Via (AI-)tooling voer je security code reviews uit en toon je de effectiviteit van mitigaties aan.

---

## Huidige staat van de repo

De CI/CD security pipeline is ingericht. De echte OpenMRS module code is nog niet toegevoegd.
De meeste workflows draaien momenteel tegen een stub `pom.xml` met alleen JUnit 5.
Zie `docs/checklist.md` voor de volledige compliance status per eis.

---

## Mini-ISMS: Beveiligingsbeleid en Procedures

### Doel

Dit document beschrijft het beveiligingsbeleid en de CI/CD-procedures voor de OpenMRS module
repository, conform NEN-7510 (informatiebeveiliging in de zorg).

### Verantwoordelijkheden

| Rol              | Verantwoordelijkheid                                                              |
| ---------------- | --------------------------------------------------------------------------------- |
| Repository owner | Beheer van branch protection, environments en secrets                             |
| Developer        | Aanmaken van feature branches, openen van pull requests                           |
| Reviewer         | Code review en goedkeuring van pull requests voor merge                           |

### Branch Protection

Alle wijzigingen op `main` verlopen uitsluitend via een Pull Request.
Minimaal 1 goedkeuring is verplicht voordat een PR gemerged mag worden.
Verouderde goedkeuringen worden automatisch ingetrokken bij nieuwe commits.
Status checks (CI-workflows) moeten slagen voor merge.

**Let op:** branch protection is geconfigureerd maar niet afgedwongen op GitHub Free (private repo).

### CI/CD Pipeline Overzicht

| Workflow                  | Trigger                          | Doel                                                              | Status                  |
| ------------------------- | -------------------------------- | ----------------------------------------------------------------- | ----------------------- |
| `ci.yml`                  | Push/PR op `main`               | Build & test met Maven (Java 17)                                  | ⚠️ Tijdelijk compliant |
| `codeql.yml`              | Push/PR op `main`, wekelijks    | SAST: statische code-analyse (NEN-7510 Ctrl 8.8)                | ⚠️ Tijdelijk compliant |
| `dependency-review.yml`   | PR op `main`                    | SCA: controle op kwetsbare/verboden licenties in dependencies    | ⚠️ Tijdelijk compliant |
| `sbom.yml`                | Push op `main`                  | Generatie van CycloneDX SBOM (Software Bill of Materials)         | ⚠️ Tijdelijk compliant |
| `dependabot.yml`          | Wekelijks (maandag 06:00)       | Automatische dependency updates voor Maven en GitHub Actions      | ⚠️ Tijdelijk compliant |

Tijdelijk compliant = workflows draaien en slagen, maar op een stub project zonder echte module code.
Zodra de echte OpenMRS module is toegevoegd en `pom.xml` vervangen is, worden deze volledig compliant.

### Security Controls

**SAST (CodeQL):** Elke push en PR wordt geanalyseerd op kwetsbaarheden in Java code.
**Dependency Review:** Pull requests worden geblokkeerd bij HIGH/CRITICAL kwetsbaarheden of verboden licenties (GPL-3.0, AGPL-3.0).
**Dependabot:** Automatische security updates en versie-updates voor dependencies.
**SBOM:** Bij elke push naar `main` wordt een CycloneDX SBOM gegenereerd en opgeslagen als artifact (90 dagen retentie).
**Secrets beheer:** Secrets zijn gescheiden per environment. Productie-secrets zijn alleen toegankelijk in `production`, voorzien van een protection rule.

### Environments

| Environment  | Protection Rule | Gebruik                                              |
| ------------ | --------------- | ---------------------------------------------------- |
| `production` | Ja (1 rule)     | Productie-deployments, beveiligd met goedkeuringsregel |
| `test`       | Nee             | Test-deployments                                     |

### Bekende beperkingen (GitHub Free plan)

| Beperking                         | Reden                                          |
| --------------------------------- | ---------------------------------------------- |
| Branch protection niet afgedwongen | Vereist GitHub Team/Enterprise voor private repos |
| Secret Scanning niet beschikbaar  | Vereist GitHub Advanced Security               |
| Artifact retentie max 90 dagen    | Free plan limiet                               |

### Incident & Vulnerability Rapportage

Beveiligingslekken worden gerapporteerd via het proces beschreven in `SECURITY.md`.
Dependabot-alerts en CodeQL-bevindingen worden beoordeeld door de repository owner en
verholpen op basis van prioriteit (CRITICAL > HIGH > MEDIUM > LOW).
