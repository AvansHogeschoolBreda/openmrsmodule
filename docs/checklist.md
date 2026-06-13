# Compliance Checklist: OpenMRS Module

> **Repository:** AvansHogeschoolBreda/openmrsmodule
> **Project:** LU2: Verbeter-onderzoek software-kwaliteit (NEN-7510)

Dit document is de centrale checklist voor alle opdrachten binnen dit project.
Per opdracht worden de eisen, status, bewijslast en verantwoordelijke bijgehouden.

---

## Inhoudsopgave

- [Opdrachtonderdeel 1: Verbeteronderzoek Onderhoudbaarheid](#opdrachtonderdeel-1-verbeteronderzoek-onderhoudbaarheid)
- [Opdrachtonderdeel 2: Verbeteronderzoek Security &amp; Compliance](#opdrachtonderdeel-2-verbeteronderzoek-security--compliance)
  - [Opdracht 1: Compliance Pipeline](#opdracht-1-compliance-pipeline)
  - [Opdracht 2: Compliance Verslag](#opdracht-2-compliance-verslag)
  - [Opdracht 3: Asset identificatie, Threat modeling &amp; Risico evaluatie](#opdracht-3-asset-identificatie-threat-modeling-risico-evaluatie)
  - [Opdracht 4: Compliance Scanning &amp; Risk Assessment Report](#opdracht-4-compliance-scanning-risk-assessment-report)
  - [Opdracht 5: Secure Coding, Logging &amp; Penetration Tests](#opdracht-5-secure-coding-logging--penetration-tests)
  - [Opdracht 6: Audit Reporting](#opdracht-6-audit-reporting)
- [Sprints](#sprints)

---

## Opdrachtonderdeel 1: Verbeteronderzoek Onderhoudbaarheid

**Bron:** [opdracht.md](assets/rubrics/opdracht.md) + [rubric-onderhoudbaarheid.md](assets/rubrics/rubric-onderhoudbaarheid.md)
**Doel:** Non-functional requirements opstellen, tooling inrichten (Qodana/SonarCloud), verbeteringen doorvoeren en met testen aantonen dat onderhoudbaarheid is verbeterd zonder regressie.
**Verantwoordelijke:**
**Periode:** 2026-06

---

### Deel 1: Analyse onderhoudbaarheid

| # | Eis                                                                                                | Status       | Bewijslast                                                         | Wie             | Notities                                                                                                     |
| - | -------------------------------------------------------------------------------------------------- | ------------ | ------------------------------------------------------------------ | --------------- | ------------------------------------------------------------------------------------------------------------ |
| 1 | Non-functional requirements voor onderhoudbaarheid bepaald en vastgelegd                           | ✅ Compliant | `Groep_6_Non-Functional-Requirements.md`                         | RafvanHooijdonk | NFR-1 t/m NFR-9 vastgelegd met drempelwaarden en onderbouwing                                                |
| 2 | Tooling ingericht (bijv. Qodana of SonarCloud) die CI laat falen bij niet-voldoen                  | ✅ Compliant | `.github/workflows/sonarcloud.yml`, `sonar-project.properties` | RafvanHooijdonk | SonarCloud quality gate gekoppeld aan CI; blokkeert merge bij falen                                          |
| 3 | Systematische analyse uitgevoerd met passende metrieken (complexiteit, duplicatie, coupling, enz.) | ✅ Compliant | Groep_6_Analyse-Onderhoudbaarheid.md                               | RafvanHooijdonk | Complexiteit, duplicatie, coupling, coverage en code smells geanalyseerd via SonarCloud + broncode-inspectie |
| 4 | Analyse-resultaten duidelijk gedocumenteerd met onderbouwing per metriek                           | ✅ Compliant | Groep_6_Analyse-Onderhoudbaarheid.md                               | RafvanHooijdonk | Per metriek: meetmethode, resultaat, onderbouwing en prioritering                                            |

---

### Deel 2: Testopzet en testresultaten (vóór verbetering)

| # | Eis                                                                            | Status  | Bewijslast | Wie | Notities                                      |
| - | ------------------------------------------------------------------------------ | ------- | ---------- | --- | --------------------------------------------- |
| 1 | Relevante tests opgesteld en uitgevoerd (eenheidstests, integratietests, enz.) | ❌ Open |            |     | Minimaal één testtype; meerdere voor "Goed" |
| 2 | Testresultaten duidelijk en bruikbaar vastgelegd                               | ❌ Open |            |     | Bijv. via Maven Surefire rapport of JaCoCo    |
| 3 | Teststrategie beschreven (welke typen, scope, tools)                           | ❌ Open |            |     |                                               |

---

### Deel 3: Verbeteringen - prioritering en onderbouwing

| # | Eis                                                                              | Status  | Bewijslast | Wie | Notities                                  |
| - | -------------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------- |
| 1 | Geprioriteerde lijst verbeteringen vastgelegd                                    | ❌ Open |            |     | Minimaal 3 concrete verbeterpunten        |
| 2 | Prioritering onderbouwd met criteria (bijv. impact, effort, risiconiveau)        | ❌ Open |            |     |                                           |
| 3 | Verwijzing naar analyse-resultaten en testresultaten verwerkt in de onderbouwing | ❌ Open |            |     | Traceerbaarheid: bevinding → verbetering |

---

### Deel 4: Aangepast ontwerp

| # | Eis                                                                 | Status  | Bewijslast | Wie | Notities                               |
| - | ------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------- |
| 1 | Aangepast ontwerp aanwezig voor geselecteerde verbeteringen         | ❌ Open |            |     | UML, diagram of beschrijving           |
| 2 | Ontwerp onderbouwd met ontwerpprincipes (bijv. SOLID, DRY, KISS)    | ❌ Open |            |     |                                        |
| 3 | Ontwerppatronen en/of refactoringpatronen benoemd en toegepast      | ❌ Open |            |     | Bijv. Extract Method, Strategy Pattern |
| 4 | Alternatieven overwogen en gemotiveerde keuze gemaakt (voor "Goed") | ❌ Open |            |     |                                        |

---

### Deel 5: Realisatie (PoC) & verantwoording

| # | Eis                                                                   | Status  | Bewijslast | Wie | Notities                                                 |
| - | --------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------------------------- |
| 1 | Verbeteringen gerealiseerd in een PoC dat overeenkomt met het ontwerp | ❌ Open |            |     | Code in repository als PR of branch                      |
| 2 | Gebruik van (AI-)tooling beschreven en verantwoord                    | ❌ Open |            |     | Welke tool, hoe gebruikt, wat was het resultaat          |
| 3 | Kritische reflectie op toolinggebruik aanwezig (voor "Goed")          | ❌ Open |            |     | Wat werkte wel/niet, welke keuzes zijn handmatig gemaakt |

---

### Deel 6: Validatie verbeteringen — testen & regressie

| # | Eis                                                                      | Status  | Bewijslast | Wie | Notities                                      |
| - | ------------------------------------------------------------------------ | ------- | ---------- | --- | --------------------------------------------- |
| 1 | Tests na verbetering uitgevoerd en resultaten vastgelegd                 | ❌ Open |            |     |                                               |
| 2 | Aantoonbaar dat onderhoudbaarheid is verbeterd (metriek voor vs. na)     | ❌ Open |            |     | Bijv. SonarCloud score, complexiteitsreductie |
| 3 | Aantoonbaar dat geen regressie is opgetreden (bestaande tests nog groen) | ❌ Open |            |     | CI-run na merge                               |

---

### Samenvatting Opdrachtonderdeel 1

| Categorie                | Aantal |
| ------------------------ | ------ |
| ✅ Compliant             | 4      |
| ⚠️ In uitvoering       | 0      |
| ❌ Open / Niet compliant | 15     |

### Openstaande actiepunten Opdrachtonderdeel 1

| Actie                                                             | Prioriteit | Wie |
| ----------------------------------------------------------------- | ---------- | --- |
| Teststrategie beschrijven en testresultaten vastleggen (Deel 2)   | Hoog       |     |
| Verbeteringen prioriteren en ontwerpen met patronen (Deel 3 + 4)  | Hoog       |     |
| PoC realiseren conform ontwerp + AI-tooling verantwoorden (Deel 5)| Hoog       |     |
| Validatie: testen voor én na, regressie aantonen (Deel 6)        | Hoog       |     |

### Wijzigingslog Opdrachtonderdeel 1

| Datum      | Versie | Wijziging                                                                                                    | Door            |
| ---------- | ------ | ------------------------------------------------------------------------------------------------------------ | --------------- |
| 2026-06-12 | 1.0    | Opdrachtonderdeel 1 toegevoegd aan globale checklist                                                         | RafvanHooijdonk |
| 2026-06-12 | 1.1    | Deel 1 volledig compliant: NFR-document, SonarCloud CI, systematische analyse gedocumenteerd (eisen 1 t/m 4) | RafvanHooijdonk |

---

## Opdrachtonderdeel 2: Verbeteronderzoek Security & Compliance

---

## Opdracht 1: Compliance Pipeline

**Bron:** [WS02: Hardening Dev Pipeline, slide 41](assets/presentaties/ICT-I2.4%20Security%20WS02%20-%20Hardening%20Dev%20Pipeline.pdf#page=41)
**Doel:** Aantoonbaar compliant CI/CD security pipeline opzetten conform NEN-7510.
**Verantwoordelijke:** RafvanHooijdonk
**Periode:** 2026-06
**Sprints:**

- **Sprint 1:** Repository inrichten, OTAP-omgeving, SBOM en CI/CD checks (Taken 5.2, 5.3, 5.4)

### Eisen

| #  | Eis                                                                        | Status                   | Bewijslast                                                | Wie             | Notities                                                                                                                      |
| -- | -------------------------------------------------------------------------- | ------------------------ | --------------------------------------------------------- | --------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| 1  | Branch protection actief op `main`: alleen via PR, reviews verplicht     | ⚠️ Tijdelijk compliant | Settings → Branches →`main` rule aanwezig             | RafvanHooijdonk | Regel geconfigureerd, maar wacht op goedkeuring docent i.v.m. limitatie Free plan (not enforced)                              |
| 2  | Alle CI-checks slagen voor merge (build, test, SAST)                       | ✅ Compliant             | `.github/workflows/ci.yml`                              | RafvanHooijdonk | Workflows draaien op echte idgen-module (`working-directory: openmrs-module-idgen`). Stub `pom.xml` niet langer gebruikt. |
| 3  | **CodeQL** of gelijkwaardige SAST actief                             | ✅ Compliant             | `.github/workflows/codeql.yml`                          | RafvanHooijdonk | CodeQL scant echte idgen-broncode op push, PR en wekelijks schedule.                                                          |
| 4  | **Secret Scanning** actief                                           | ❌ Niet compliant        | Settings → Advanced Security                             | RafvanHooijdonk | Niet beschikbaar op GitHub Free plan voor private repos. Vereist GitHub Advanced Security (betaald).                          |
| 5  | **Dependabot** alerts + security updates actief                      | ✅ Compliant             | Settings → Advanced Security +`.github/dependabot.yml` | RafvanHooijdonk | Monitort echte idgen-dependencies, wekelijkse updates.                                                                        |
| 6  | **Dependency Review Action** gekoppeld aan PR's                      | ✅ Compliant             | `.github/workflows/dependency-review.yml`               | RafvanHooijdonk | Actief op PR naar `main`, blokkeert HIGH/CRITICAL, weigert GPL-3.0 en AGPL-3.0.                                             |
| 7  | **SBOM** wordt gegenereerd (CycloneDX of SPDX) en geanalyseerd (SCA) | ✅ Compliant             | `.github/workflows/sbom.yml` + Actions artifacts        | RafvanHooijdonk | CycloneDX JSON via Anchore/Syft op `path: openmrs-module-idgen`. Bevat echte idgen-dependencies.                            |
| 8  | **GitHub Environments** gedefinieerd met protection rules            | ✅ Compliant             | Settings → Environments                                  | RafvanHooijdonk | `production` (1 protection rule, 1 secret) en `test` (1 secret) aanwezig                                                  |
| 9  | Secrets gescheiden per environment                                         | ✅ Compliant             | Settings → Environments                                  | RafvanHooijdonk | `production` en `test` hebben elk eigen geïsoleerde secrets                                                              |
| 10 | Pipeline-artifacts (rapporten, SBOM) worden bewaard                        | ⚠️ Gedeeltelijk        | Actions → SBOM run → Artifacts                          | RafvanHooijdonk | Retentie is 90 dagen (free plan maximum). Geconfigureerde 365 dagen wordt automatisch teruggebracht                           |
| 11 | **README.md** beschrijft beleid en procedure (mini-ISMS)             | ✅ Compliant             | `README.md`                                             | RafvanHooijdonk | Pipeline overzicht, verantwoordelijkheden, branch protection, environments, secrets, artifact bewaring                        |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 8      |
| ⚠️ Gedeeltelijk/Tijdelijk | 1      |
| ❌ Niet compliant           | 1      |

### Openstaande actiepunten

**Oplosbaar zonder plan-upgrade:**

Geen — alle oplosbare punten zijn verholpen na toevoeging van de echte idgen-module.

**Vereist GitHub plan-upgrade (Team of Enterprise):**

| Actie                                     | Blokker                                           |
| ----------------------------------------- | ------------------------------------------------- |
| Branch protection afdwingen op `main`   | GitHub Team/Enterprise vereist voor private repos |
| Secret Scanning activeren                 | GitHub Advanced Security vereist                  |
| Artifact retentie verhogen naar 365 dagen | Betaald plan vereist                              |

### Wijzigingslog Opdracht 1

| Datum      | Versie | Wijziging                                                                                                                         | Door            |
| ---------- | ------ | --------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Initiële checklist aangemaakt na volledige pipeline controle                                                                     | RafvanHooijdonk |
| 2026-06-03 | 1.1    | `sbom.yml` gefixed (Maven stap verwijderd, exit code 1 opgelost)                                                                | RafvanHooijdonk |
| 2026-06-03 | 1.2    | `README.md` bijgewerkt met mini-ISMS beleid en procedures                                                                       | RafvanHooijdonk |
| 2026-06-03 | 1.3    | `SECURITY.md` bijgewerkt met rapportageproces en prioritering                                                                   | RafvanHooijdonk |
| 2026-06-03 | 1.4    | `ci.yml` workflow aangemaakt voor build & test checks                                                                           | RafvanHooijdonk |
| 2026-06-03 | 1.5    | `checklist.md` aangemaakt voor doorlopende compliance tracking                                                                  | RafvanHooijdonk |
| 2026-06-03 | 1.6    | Stub `pom.xml` toegevoegd, eis #2 bijgewerkt naar tijdelijk compliant                                                           | RafvanHooijdonk |
| 2026-06-03 | 1.7    | Opdracht-foto toegevoegd, checklist hersteld na corruptie                                                                         | RafvanHooijdonk |
| 2026-06-03 | 1.8    | Eis #3, #5, #6, #7 bijgewerkt naar tijdelijk compliant (geen echte module)                                                        | RafvanHooijdonk |
| 2026-06-03 | 1.9    | Checklist omgezet naar globaal formaat met opdrachten als secties                                                                 | RafvanHooijdonk |
| 2026-06-03 | 1.10   | Module-keuze idgen en 3 geselecteerde NEN-7510 controls gedocumenteerd                                                            | Rowen Albers    |
| 2026-06-03 | 1.11   | Alle NEN-7510 jaartal-referenties geüpdatet van 2024 naar 2026                                                                   | Rowen Albers    |
| 2026-06-12 | 1.12   | Eisen #2, #3, #5, #6, #7 naar ✅ Compliant: alle workflows omgezet naar echte idgen-module, stub `pom.xml` niet langer gebruikt | RafvanHooijdonk |

---

## Opdracht 2: Compliance Verslag

**Bron:** [WS02: Hardening Dev Pipeline, slide 42](assets/presentaties/ICT-I2.4%20Security%20WS02%20-%20Hardening%20Dev%20Pipeline.pdf#page=42)

**Doel:** Aantonen dat de pipeline compliant is aan NEN-7510:2026 controls via een compliance verslag.
**Verantwoordelijke:** SinanSagir
**Periode:** 2026-06
**Sprints:**

- **Sprint 1:** Gap-analyse en Mini-complianceverslag (Taken 5.5, 5.6)
- **Sprint 3:** DPIA-check (Taak 3.4)
  **Oplevering:** Markdown of Word document, ingeleverd via de repo.

> Opdracht 1 hoeft nog niet 100% gereed te zijn voor deze opdracht.

### Eisen

| # | Eis                                                                                         | Status       | Bewijslast                | Wie        | Notities                                                         |
| - | ------------------------------------------------------------------------------------------- | ------------ | ------------------------- | ---------- | ---------------------------------------------------------------- |
| 1 | Per relevante NEN-7510:2026 control beschreven wat de control vereist                       | ✅ Compliant | Mini-Complianceverslag.md | SinanSagir | Controls 8.8, 8.15, 5.36 beschreven                              |
| 2 | Per control beschreven hoe de pipeline hieraan voldoet (verwijzing naar bestand/instelling) | ✅ Compliant | Mini-Complianceverslag.md | SinanSagir | Concrete verwijzingen naar workflows, Settings, README, SECURITY |
| 3 | Per control beschreven welk restrisico er nog is en waarom                                  | ✅ Compliant | Mini-Complianceverslag.md | SinanSagir | Stub pom.xml, branch protection niet afdwingbaar, 90d retentie   |
| 4 | Structuur: tabel of genummerde secties per control                                          | ✅ Compliant | Mini-Complianceverslag.md | SinanSagir | Overzichtstabel + genummerde secties per control                 |
| 5 | Opgeleverd als Markdown of Word document in de repo                                         | ✅ Compliant | Mini-Complianceverslag.md | SinanSagir | Markdown in `docs/LU2 - Kwaliteit en security.../`             |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 5      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 0      |

### Openstaande actiepunten

Geen openstaande actiepunten. Alle eisen zijn compliant.

### Wijzigingslog Opdracht 2

| Datum      | Versie | Wijziging                                                                           | Door            |
| ---------- | ------ | ----------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 2 toegevoegd aan globale checklist                                         | RafvanHooijdonk |
| 2026-06-03 | 1.1    | Mini-Complianceverslag ingevuld (5 controls), alle eisen compliant                  | SinanSagir      |
| 2026-06-03 | 1.2    | Mini-Complianceverslag aangepast (teruggebracht naar 3 controls: 8.8, 8.15 en 5.36) | Rowen Albers    |

---

## Opdracht 3: Asset identificatie, Threat modeling & Risico evaluatie

**Bron:** [WS03: Healthcare Risk Assessment, slides 48-51](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=48)
**Doel:** Risico's in kaart brengen voor de OpenMRS module via asset identificatie, bow-tie analyse en risico evaluatie van de CI-CD pipeline.
**Verantwoordelijke:** RafvanHooijdonk, SinanSagir
**Periode:** 2026-06
**Sprints:**

- **Sprint 2:** Asset-identificatie, Risicomatrix en Bow-tie analyse (Taken 2.1, 2.2, 2.3)
  **Oplevering:** Per deel een document of diagram, ingeleverd via de repo.

---

### Deel 1: Asset identificatie & Threat modeling

**Bron:** [WS03: Healthcare Risk Assessment, slide 49](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=49)

| # | Eis                                                                                               | Status       | Bewijslast             | Wie             | Notities                                                              |
| - | ------------------------------------------------------------------------------------------------- | ------------ | ---------------------- | --------------- | --------------------------------------------------------------------- |
| 1 | Documentatie van de OpenMRS module bestudeerd                                                     | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | OpenMRS Wiki, REST API docs, Data Model bestudeerd                    |
| 2 | Minimaal een aantal assets geidentificeerd (bijv. Patient Obs, User Credentials, System Settings) | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | 8 assets geidentificeerd (A1-A8)                                      |
| 3 | BIV-analyse (CIA) uitgevoerd per asset                                                            | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | Per asset: Vertrouwelijkheid, Integriteit, Beschikbaarheid vastgelegd |
| 4 | Gevoelige gegevens beschreven met referenties                                                     | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | Concrete veldnamen en OpenMRS-documentatiereferenties per asset       |
| 5 | Meest reele hazards per asset benoemd                                                             | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | 12 hazards (H1-H12) gescoord op kans x impact                         |
| 6 | Score schaal, risk appetite en grenswaarden vastgelegd                                            | ✅ Compliant | Asset-Identificatie.md | RafvanHooijdonk | Kansschaal 1-5, impactschaal 1-5, grenswaarden groen/oranje/rood      |

---

### Deel 2: Bow-tie van hazard

**Bron:** [WS03: Healthcare Risk Assessment, slide 50](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=50)

| # | Eis                                                        | Status       | Bewijslast | Wie        | Notities                                                                                 |
| - | ---------------------------------------------------------- | ------------ | ---------- | ---------- | ---------------------------------------------------------------------------------------- |
| 1 | Een hazard gekozen                                         | ✅ Compliant | Bow-Tie.md | SinanSagir | H10 (Hardcoded secret, score 15 rood) gekozen conform Asset-Identificatie sectie 6.3     |
| 2 | Top-event gedefinieerd                                     | ✅ Compliant | Bow-Tie.md | SinanSagir | Top-event: secret staat in git history en is toegankelijk voor ongeautoriseerde partijen |
| 3 | Bow-tie gemaakt met preventieve en correctieve maatregelen | ✅ Compliant | Bow-Tie.md | SinanSagir | 4 preventieve barrières (PB1-PB4) en 4 herstelbarrières (HB1-HB4)                      |
| 4 | Relevante NEN-7510:2026 controls gekoppeld aan de bow-tie  | ✅ Compliant | Bow-Tie.md | SinanSagir | Ctrl 5.17, 8.24, 8.8, 8.15, 6.8 gekoppeld per barrière                                  |
| 5 | Escalation factors opgenomen                               | ✅ Compliant | Bow-Tie.md | SinanSagir | 7 escalation factors (EF1-EF7), preventief en correctief gescheiden                      |
| 6 | Audit mindset verwerkt: niet gelogd = niet gebeurd         | ✅ Compliant | Bow-Tie.md | SinanSagir | Sectie 12: concrete impact op HB3 en HB4 als logs ontbreken                              |

---

### Deel 3: Risico evaluatie CI-CD

**Bron:** [WS03: Healthcare Risk Assessment, slide 51](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=51)

| # | Eis                                           | Status       | Bewijslast      | Wie        | Notities                                                                                                |
| - | --------------------------------------------- | ------------ | --------------- | ---------- | ------------------------------------------------------------------------------------------------------- |
| 1 | Risico matrix gemaakt voor de CI-CD pipeline  | ✅ Compliant | Risicomatrix.md | SinanSagir | 6 CI/CD-risico's (H8-H11, C1, C2) gescoord en visueel weergegeven; gebaseerd op pipeline uit Opdracht 1 |
| 2 | Bow-tie(s) gemaakt voor CI-CD risico's        | ✅ Compliant | Risicomatrix.md | SinanSagir | Bow-tie voor H8 (Workflow poisoning, score 10) in sectie 7; 3 preventieve en 4 herstelbarrières        |
| 3 | Escalation factors opgenomen in de bow-tie(s) | ✅ Compliant | Risicomatrix.md | SinanSagir | 6 escalation factors (EF1-EF6), preventief en correctief gescheiden                                     |

---

### Samenvatting Opdracht 3

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 15     |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 0      |

### Openstaande actiepunten Opdracht 3

Geen openstaande actiepunten. Alle eisen zijn compliant.

### Wijzigingslog Opdracht 3

| Datum      | Versie | Wijziging                                                                                            | Door            |
| ---------- | ------ | ---------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 3 toegevoegd aan globale checklist                                                          | RafvanHooijdonk |
| 2026-06-08 | 1.1    | Deel 1 voltooid: 8 assets, BIV-analyse, 12 hazards, scoreschaal en risk appetite in 03-assets.md     | RafvanHooijdonk |
| 2026-06-08 | 1.2    | Deel 2 voltooid: bow-tie voor H10 (hardcoded secret) aangemaakt in Groep_6_Bow-Tie.md                | SinanSagir      |
| 2026-06-08 | 1.3    | Deel 3 voltooid: CI/CD risicomatrix (6 risico's) en bow-tie H8 aangemaakt in Groep_6_Risicomatrix.md | SinanSagir      |

---

## Opdracht 4: Compliance Scanning & Risk Assessment Report

**Bron:** [WS04A: Compliance Scanning, slide 53](assets/presentaties/ICT-I2.4%20Security%20WS04A%20-%20Compliance%20Scanning.pdf#page=53)
**Doel:** SAST/SCA scan uitvoeren op de OpenMRS module, resultaten verwerken in een geprioriteerde security backlog en een Risk Assessment Report.
**Verantwoordelijke:** SimonEulenpesch, Rowen Albers
**Periode:** 2026-06
**Sprints:**

- **Sprint 2:** SAST/SCA scan, SBOM, Security backlog en Patchadvies (Taken 2.4, 2.5, 2.6)
- **Sprint 4:** SBOM-sectie finaliseren (Taak 4.4)
  **Oplevering:** PDF + bronbestanden (Markdown, spreadsheet) in de repo.

> WS04B (Testing & Pentesting) heeft geen aparte opdracht. Die sessie is een refresher; de testoutput (unit tests, DAST, pentest) is bewijsmateriaal voor Opdracht 4 en Opdracht 6.

### Deel 1: Scan codebase

| # | Eis                                                        | Status       | Bewijslast                   | Wie             | Notities                                                                                                |
| - | ---------------------------------------------------------- | ------------ | ---------------------------- | --------------- | ------------------------------------------------------------------------------------------------------- |
| 1 | SAST scan uitgevoerd op de module                          | ✅ Compliant | Security-Analyse.md          | SimonEulenpesch | Semgrep `p/java` op idgen: 0 findings op 57 targets, 60 regels. Aangevuld met handmatige code review. |
| 2 | SCA scan uitgevoerd op de module                           | ✅ Compliant | Security-Analyse.md          | SimonEulenpesch | OWASP Dependency-Check op idgen. Meerdere CRITICAL dependencies gesignaleerd.                           |
| 3 | SBOM gegenereerd (bijv. CycloneDX)                         | ✅ Compliant | bom.xml, bom.json            | SimonEulenpesch | CycloneDX 1.6 via makeAggregateBom, 116 componenten.                                                    |
| 4 | Technisch SAST-bestand opgeslagen als output (audit trail) | ✅ Compliant | sast-report.json             | SimonEulenpesch | Semgrep JSON-output bewaard in module-root.                                                             |
| 5 | Technisch SCA-bestand opgeslagen als output (audit trail)  | ✅ Compliant | dependency-check-report.html | SimonEulenpesch | Bewaard in target, api/target en omod/target.                                                           |
| 6 | Technisch SBOM-bestand opgeslagen als output (audit trail) | ✅ Compliant | bom.xml, bom.json            | SimonEulenpesch | Bewaard in target.                                                                                      |

### Deel 2: Geprioriteerde security backlog

| # | Eis                                                                                       | Status       | Bewijslast          | Wie             | Notities                                                                              |
| - | ----------------------------------------------------------------------------------------- | ------------ | ------------------- | --------------- | ------------------------------------------------------------------------------------- |
| 1 | Backlog bevat minimaal 8 bevindingen (4 SCA + 4 SAST)                                     | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | 10 bevindingen: 6 SCA (SCA-01 t/m SCA-06) + 4 SAST/code review (SAST-01 t/m SAST-04). |
| 2 | Elk item heeft: Finding ID, CVE/CWE, component, CVSS Base Score (geverifieerd via NVD)    | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | NVD Base Scores gebruikt, niet alleen scanner-score.                                  |
| 3 | Elk item heeft contextuele score (bereikbaarheid + healthcare-impact)                     | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | Contextuele score per bevinding in de overzichtstabel.                                |
| 4 | Elk item gekoppeld aan minimaal een NEN-7510 control                                      | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | Controls 8.8, 8.5, 8.15, 8.25, 8.28 gekoppeld.                                        |
| 5 | Elk item heeft: fix beschikbaar (ja/nee + versie), effort (S/M/L/XL), prioriteit, besluit | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | Kolommen aanwezig in de overzichtstabel.                                              |
| 6 | False positives en risicoacceptaties gedocumenteerd                                       | ✅ Compliant | Security-Analyse.md | SimonEulenpesch | Sectie 8.3: geen SAST-findings, geen onbehandelde acceptaties.                        |

### Deel 3: Risk Assessment Report (RAR)

| # | Eis                                                                             | Status       | Bewijslast                        | Wie          | Notities                                                  |
| - | ------------------------------------------------------------------------------- | ------------ | --------------------------------- | ------------ | --------------------------------------------------------- |
| 1 | Managementsamenvatting aanwezig (0.5-1 pagina, niet-technisch leesbaar)         | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 1.           |
| 2 | Scope & Methodologie beschreven (welke module, tools, periode, wat NIET getest) | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 2.           |
| 3 | Top-5 bevindingen beschreven (technisch + business + NEN-7510 koppeling)        | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 4.           |
| 4 | Security backlog opgenomen in rapport                                           | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 5.           |
| 5 | Kostenraming aanwezig (minimaal 3 kostenposten; fictief maar realistisch)       | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 6.           |
| 6 | Conclusie & aanbevelingen aanwezig (go/no-go voor productie-deployment)         | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Zie Groep_6_Risk-Assessment-Report.md sectie 7.           |
| 7 | Rapport opgeleverd als PDF + bronbestanden in de repo                           | ✅ Compliant | Groep_6_Risk-Assessment-Report.md | Rowen Albers | Bronbestand Groep_6_Risk-Assessment-Report.md opgeleverd. |

### Samenvatting Opdracht 4

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 19     |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 0      |

### Openstaande actiepunten Opdracht 4

Geen openstaande actiepunten. Alle eisen zijn compliant.

### Wijzigingslog Opdracht 4

| Datum      | Versie | Wijziging                                                                                                                       | Door            |
| ---------- | ------ | ------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 4 toegevoegd aan globale checklist                                                                                     | RafvanHooijdonk |
| 2026-06-10 | 1.1    | Deel 1 en Deel 2 voltooid: SCA, SAST en SBOM uitgevoerd op idgen, security backlog (10 findings) in Groep_6_Security-Analyse.md | SimonEulenpesch |
| 2026-06-10 | 1.2    | Deel 3 voltooid: Risk Assessment Report opgeleverd conform rubric en NEN-7510:2026                                              | SimonEulenpesch |
| 2026-06-10 | 1.3    | Risk Assessment Report gepolijst met gedetailleerde datavelden, threat actoren en clickable bronnen/CVE/CWE-links               | Rowen Albers    |

---

## Opdracht 5: Secure Coding, Logging & Penetration Tests

**Bron:** [WS05: Secure Coding, Privacy by Design, slide 52](assets/presentaties/ICT-I2.4%20Security%20WS05%20-%20Secure%20Coding%2C%20Privacy%20by%20Design.pdf#page=52)
**Doel:** Attack surface in kaart brengen, logging gap-analyse uitvoeren, compliant audit logging implementeren en testen, kwetsbaarheden aantonen via penetration tests, mitigeren en valideren, DPIA-check uitvoeren, code coverage activeren in CI.
**Verantwoordelijke:**
**Periode:** 2026-06
**Sprints:**

- **Sprint 3:** PoC Mitigatie, Attack Surface, Pentestrapportage voor mitigatie, DPIA-check (Taken 3.1, 3.2, 3.3, 3.4)
- **Sprint 4:** PoC afronden, Pentestrapportage na mitigatie, rapportages samenvoegen (Taken 4.2, 4.3)

### Deel 1: Attack Surface Mapping

| # | Eis                                                                                   | Status  | Bewijslast | Wie | Notities                                           |
| - | ------------------------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------------------- |
| 1 | Alle HTTP-endpoints geidentificeerd (pad, methode, vereiste privileges)               | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Geïdentificeerd in sectie 4 van het rapport |
| 2 | Externe inputs gedocumenteerd (gebruikersinvoer, uploads, omgevingsvariabelen)        | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Gedocumenteerd in sectie 3 (register) |
| 3 | Diagram gemaakt met vertrouwensmodel (hoge-risico entry points gemarkeerd)            | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Systeemgrenzen & vertrouwensmodel met Mermaid in sectie 2 |
| 4 | Per entry point: inputvalidatie en autorisatiecheck gedocumenteerd, gaps beschreven   | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | 12 security gaps in kaart gebracht gekoppeld aan NEN-7510 8.25 |
| 5 | Deliverable: tabel Endpoint / Methode / Privilege / Inputvalidatie / Autorisatiecheck | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Volledige endpoints/security mapping tabel in sectie 4 |

### Deel 2: Logging gap-analyse

| # | Eis                                                               | Status  | Bewijslast | Wie | Notities |
| - | ----------------------------------------------------------------- | ------- | ---------- | --- | -------- |
| 1 | Bestaande log-statements in de module in kaart gebracht           | ❌ Open |            |     |          |
| 2 | Tabel aanwezig: Event / Gelogd? / Compliant met NEN-7510 8.15?    | ❌ Open |            |     |          |
| 3 | Ontbrekende beveiligingsrelevante events geidentificeerd          | ❌ Open |            |     |          |
| 4 | Gecontroleerd of gevoelige data (BSN, wachtwoorden) in logs staat | ❌ Open |            |     |          |
| 5 | Gap gedocumenteerd                                                | ❌ Open |            |     |          |

### Deel 3: Logging implementeren

| # | Eis                                                                                               | Status  | Bewijslast | Wie | Notities                                  |
| - | ------------------------------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------- |
| 1 | Audit logging toegevoegd aan: inloggen/uitloggen, lezen patiëntdossier, aanmaken/wijzigen record | ❌ Open |            |     |                                           |
| 2 | Elk logbericht bevat: UserID, timestamp, event, uitkomst, resource-UUID (NEN-7510 8.15)           | ❌ Open |            |     | UserID via Context.getAuthenticatedUser() |
| 3 | Geen BSN of medische data in logs                                                                 | ❌ Open |            |     |                                           |

### Deel 4: Logging testen

| # | Eis                                              | Status  | Bewijslast | Wie | Notities                        |
| - | ------------------------------------------------ | ------- | ---------- | --- | ------------------------------- |
| 1 | JUnit-test: succesvolle toegang wordt gelogd     | ❌ Open |            |     | Gebruik ListAppender of Mockito |
| 2 | JUnit-test: mislukte toegang wordt gelogd        | ❌ Open |            |     |                                 |
| 3 | JUnit-test (negatief): logbericht bevat geen BSN | ❌ Open |            |     |                                 |
| 4 | Alle tests slagen (mvn test geeft groen)         | ❌ Open |            |     |                                 |

### Deel 5: DPIA-check

| # | Eis                                                                             | Status  | Bewijslast | Wie | Notities                                              |
| - | ------------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------------------- |
| 1 | Vastgesteld of verwerking van persoonsgegevens plaatsvindt in de module         | ❌ Open |            |     | Medische data, patiënt-ID's, logs met gebruikersdata |
| 2 | Bepaald of een volledige DPIA vereist is (AVG art. 35 drempelcriteria getoetst) | ❌ Open |            |     | Zorg = hoog risico → waarschijnlijk DPIA-plichtig    |
| 3 | Risico's t.a.v. privacy vastgelegd met mitigerende maatregelen                  | ❌ Open |            |     | Koppeling aan NEN-7510 controls en privacy by design  |
| 4 | DPIA-check gedocumenteerd in de repo                                            | ❌ Open |            |     |                                                       |

---

### Deel 6: Penetration Tests — aantonen kwetsbaarheden

| # | Eis                                                                                         | Status  | Bewijslast | Wie | Notities                                      |
| - | ------------------------------------------------------------------------------------------- | ------- | ---------- | --- | --------------------------------------------- |
| 1 | Minimaal één kritische kwetsbaarheid (uit backlog Opdracht 4) geselecteerd voor pentest   | ❌ Open |            |     | Kies bijv. hoogste CVSS uit SCA-01 t/m SCA-06 |
| 2 | Pentest vóór mitigatie uitgevoerd en navolgbaar gedocumenteerd (tool, commando's, output) | ❌ Open |            |     | Bijv. OWASP ZAP, Burp Suite, curl-aanval      |
| 3 | Misbruik van de kwetsbaarheid is aangetoond (screenshot/log als bewijs)                     | ❌ Open |            |     |                                               |
| 4 | Pentest-rapport vóór mitigatie opgeslagen in repo                                         | ❌ Open |            |     |                                               |

---

### Deel 7: Mitigatie & validatie

| # | Eis                                                                         | Status  | Bewijslast | Wie | Notities                                  |
| - | --------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------- |
| 1 | Kwetsbaarheid gemitigeerd (code-aanpassing of configuratie) via PR          | ❌ Open |            |     | PR-nummer als bewijslast                  |
| 2 | Gebruik van (AI-)tooling bij realisatie beschreven en verantwoord           | ❌ Open |            |     |                                           |
| 3 | Pentest ná mitigatie uitgevoerd; aangetoond dat securityrisico is verlaagd | ❌ Open |            |     | Vergelijkbaar scenario als vóór-pentest |
| 4 | Pentest-rapport ná mitigatie opgeslagen in repo                            | ❌ Open |            |     |                                           |
| 5 | Vergelijking voor/na gedocumenteerd (wat was het risico, wat is het nu)     | ❌ Open |            |     | Koppeling aan NEN-7510 control            |

---

### Deel 8: Code Coverage

| # | Eis                                                        | Status       | Bewijslast                                                               | Wie             | Notities                                                                                     |
| - | ---------------------------------------------------------- | ------------ | ------------------------------------------------------------------------ | --------------- | -------------------------------------------------------------------------------------------- |
| 1 | JaCoCo geconfigureerd en geactiveerd (mvn jacoco:report)   | ✅ Compliant | `openmrs-module-idgen/api/pom.xml` + `sonarcloud.yml`                 | RafvanHooijdonk | JaCoCo draait via `mvn verify` in sonarcloud.yml; rapporten gaan naar SonarCloud             |
| 2 | Coverage % bepaald en gedocumenteerd (met onderbouwing)    | ✅ Compliant | `Groep_6_Analyse-Onderhoudbaarheid.md` + `Groep_6_Non-Functional-Requirements.md` | RafvanHooijdonk | 50% overall (api 54.3%, omod 46.9%); per-bestand breakdown inclusief 0%-klassen gedocumenteerd |
| 3 | Coverage rapport opgeslagen als artifact in GitHub Actions | ✅ Compliant | `sonarcloud.yml` stap "Upload JaCoCo coverage reports"                  | RafvanHooijdonk | api + omod rapport geupload als artifact `jacoco-report-{run}`, 90 dagen retentie            |

### Samenvatting Opdracht 5

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 5      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 33     |

### Openstaande actiepunten Opdracht 5

| Actie                                              | Prioriteit | Wie |
| -------------------------------------------------- | ---------- | --- |
| Logging gap-analyse uitvoeren                      | Hoog       |     |
| Compliant audit logging implementeren en testen    | Hoog       |     |
| DPIA-check uitvoeren en documenteren               | Hoog       |     |
| Pentest vóór mitigatie uitvoeren en documenteren | Hoog       |     |
| Kwetsbaarheid mitigeren via PR                     | Hoog       |     |
| Pentest ná mitigatie uitvoeren en vergelijken     | Hoog       |     |
| JaCoCo activeren in CI                             | Hoog       |     |

### Wijzigingslog Opdracht 5

| Datum      | Versie | Wijziging                                                                                     | Door            |
| ---------- | ------ | --------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 5 toegevoegd aan globale checklist                                                   | RafvanHooijdonk |
| 2026-06-12 | 1.1    | Deel 5 (DPIA), Deel 6 (Pentest voor), Deel 7 (Mitigatie+validatie) toegevoegd; naam aangepast | RafvanHooijdonk |
| 2026-06-13 | 1.2    | Deel 1 (Attack Surface Mapping) ingevuld en op ✅ Compliant gezet               | Rowen Albers    |
| 2026-06-12 | 1.2    | Deel 8 volledig compliant: JaCoCo actief, coverage gedocumenteerd, artifact upload toegevoegd | RafvanHooijdonk |

---

## Opdracht 6: Audit Reporting

**Bron:** [WS06: Audit Reporting, slide 44](assets/presentaties/ICT-I2.4%20Security%20WS06%20-%20Audit%20Reporting.pdf#page=44)
**Doel:** Volledig auditrapport opleveren met executive summary, traceability matrix en technisch rapport. Plus een responsible disclosure scenario uitwerken.
**Verantwoordelijke:**
**Periode:** 2026-06
**Sprints:**

- **Sprint 3:** Auditrapport concept en Presentatiedeck concept (Taken 3.5, 3.6)
- **Sprint 4:** Auditrapport definitief maken en Presentatie oefenen (Taken 4.1, 4.5)
  **Oplevering:** Markdown of Word-document, opgeslagen als artifact in de repository.

### Deel 0: Responsible Disclosure

| # | Eis                                                                     | Status  | Bewijslast | Wie | Notities                                        |
| - | ----------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------------- |
| 1 | Scenario uitgewerkt: stappen, volgorde en rapportagekanalen beschreven  | ❌ Open |            |     | CVE-2022-42889 (Text4Shell, CVSS 9.8) als casus |
| 2 | NEN-7510 relevantie beschreven (6.8: informatiebeveiligingsgebeurtenis) | ❌ Open |            |     |                                                 |

### Deliverable 1: Executive Summary

| # | Eis                                                                   | Status  | Bewijslast | Wie | Notities                                    |
| - | --------------------------------------------------------------------- | ------- | ---------- | --- | ------------------------------------------- |
| 1 | Geschreven voor niet-technische lezer (Raad van Bestuur), geen jargon | ❌ Open |            |     | Geen CVE-nummers of CVSS-scores in de tekst |
| 2 | Overall RAG-status aanwezig met toelichting                           | ❌ Open |            |     |                                             |
| 3 | Status t.o.v. CRA en NEN-7510:2026 vermeld                            | ❌ Open |            |     |                                             |
| 4 | Top 3 risico's beschreven in zorg-impact (niet technische impact)     | ❌ Open |            |     |                                             |
| 5 | Geprioriteerde roadmap aanwezig (nu / deze sprint / later)            | ❌ Open |            |     |                                             |
| 6 | Maximaal 400 woorden                                                  | ❌ Open |            |     |                                             |

### Deliverable 2: Traceability Matrix

| # | Eis                                                                      | Status  | Bewijslast | Wie | Notities                                     |
| - | ------------------------------------------------------------------------ | ------- | ---------- | --- | -------------------------------------------- |
| 1 | Minimaal 5 NEN-7510:2026 controls opgenomen                              | ❌ Open |            |     | Aanbevolen: 8.28, 8.8, 8.15, 8.25, 5.35/5.36 |
| 2 | Per rij: norm, maatregel, voor (bevinding), aanpassing, na (bewijs)      | ❌ Open |            |     |                                              |
| 3 | Elk bewijs is een concreet artefact (PR-nummer, scan-datum, commit hash) | ❌ Open |            |     |                                              |

### Deliverable 3: Volledig auditrapport

| # | Eis                                                                                 | Status  | Bewijslast | Wie | Notities |
| - | ----------------------------------------------------------------------------------- | ------- | ---------- | --- | -------- |
| 1 | Managementsamenvatting aanwezig (= Deliverable 1 geintegreerd)                      | ❌ Open |            |     |          |
| 2 | Scope & Context aanwezig                                                            | ❌ Open |            |     |          |
| 3 | Audit Methodologie aanwezig                                                         | ❌ Open |            |     |          |
| 4 | Risico-analyse & Bevindingen aanwezig (minimaal 4 bevindingen)                      | ❌ Open |            |     |          |
| 5 | SBOM & Supply Chain Security sectie aanwezig (top 10 dependencies op risico + CVEs) | ❌ Open |            |     |          |
| 6 | Conclusie & Advies aanwezig                                                         | ❌ Open |            |     |          |
| 7 | Bijlagen aanwezig: SBOM, SAST-output, risicomatrix, bow-tie diagrammen, CRA-mapping | ❌ Open |            |     |          |
| 8 | Rapport opgeleverd als Markdown of Word in de repo (niet als e-mail bijlage)        | ❌ Open |            |     |          |

### Samenvatting Opdracht 6

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 19     |

### Openstaande actiepunten Opdracht 6

| Actie                                           | Prioriteit | Wie |
| ----------------------------------------------- | ---------- | --- |
| Responsible Disclosure scenario uitwerken       | Hoog       |     |
| Executive Summary schrijven (max 400 woorden)   | Hoog       |     |
| Traceability Matrix opstellen (min. 5 controls) | Hoog       |     |
| Volledig auditrapport schrijven (7 secties)     | Hoog       |     |

### Wijzigingslog Opdracht 6

| Datum      | Versie | Wijziging                                   | Door            |
| ---------- | ------ | ------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 6 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Sprints

De sprints zijn de uitvoeringsplanning. Per sprint staan de taken, verwachte output en eindcheck.
De gedetailleerde sprint-bestanden staan in `docs/sprints/`.

| Sprint   | Doel                                        | Bestand                                    | Status            |
| -------- | ------------------------------------------- | ------------------------------------------ | ----------------- |
| Sprint 1 | Omgeving inrichten & Gap-analyse            | [docs/sprints/sprint1.md](sprints/sprint1.md) | ⚠️ Gedeeltelijk |
| Sprint 2 | Risico-analyse & Security Backlog           | [docs/sprints/sprint2.md](sprints/sprint2.md) | ✅ Compliant      |
| Sprint 3 | Mitigatie, Pentest & Auditrapport (concept) | [docs/sprints/sprint3.md](sprints/sprint3.md) | ❌ Open           |
| Sprint 4 | Definitief & Oplevering                     | [docs/sprints/sprint4.md](sprints/sprint4.md) | ❌ Open           |

### Eindcheck Sprint 1

| # | Check                                                                | Status                   | Notities                                                                                                                                      | Wie             |
| - | -------------------------------------------------------------------- | ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 1 | GitHub repository heeft branch protection en Dependabot actief       | ⚠️ Tijdelijk compliant | Branch protection geconfigureerd (wacht op goedkeuring docent i.v.m. limitatie Free plan). Dependabot en CodeQL actief op echte idgen-module. | RafvanHooijdonk |
| 2 | SBOM-bestand wordt als CI-artifact aangemaakt in Actions             | ✅ Compliant             | `sbom.yml` actief op echte idgen-module (`path: openmrs-module-idgen`). Artifact aangemaakt met echte dependencies.                       | RafvanHooijdonk |
| 3 | Gap-analyse dekt minimaal 3 NEN-7510 controls met bewijs             | ✅ Compliant             | `Groep_6_Gap-Analyse.md` is ingevuld met 3 controls en concrete bewijzen.                                                                   | Rowen Albers    |
| 4 | Alle teamleden hebben een commit bijgedragen                         | ❌ Open                  | Niet verifieerbaar vanuit deze context.                                                                                                       | Iedereen        |
| 5 | Module-keuze gedocumenteerd en onderbouwd in Groep_6_Module-Keuze.md | ✅ Compliant             | Keuze vastgelegd en gemotiveerd in Groep_6_Module-Keuze.md. Bevat motivatie en 3 geselecteerde NEN-7510 controls (8.8, 8.15, 5.36).           | Rowen Albers    |

### Eindcheck Sprint 2

| # | Check                                                                       | Status       | Wie             |
| - | --------------------------------------------------------------------------- | ------------ | --------------- |
| 1 | Risicomatrix met minimaal 5 risico's aanwezig                               | ✅ Compliant | SinanSagir      |
| 2 | Bow-tie voor het hoogste risico uitgewerkt                                  | ✅ Compliant | SinanSagir      |
| 3 | SAST- en SBOM-scan loopt automatisch in CI en output is opgeslagen          | ✅ Compliant | SimonEulenpesch |
| 4 | Security backlog heeft minimaal 5 bevindingen met CVSS + NEN-7510 koppeling | ✅ Compliant | SimonEulenpesch |
| 5 | Patchadvies is onderbouwd met CVE-data uit de SBOM                          | ✅ Compliant | Rowen Albers    |

### Eindcheck Sprint 3

| # | Check                                                                                    | Status  | Wie |
| - | ---------------------------------------------------------------------------------------- | ------- | --- |
| 1 | PR met PoC-mitigatie gemerged (of review-ready)                                          | ❌ Open |     |
| 2 | Pentest voor en na mitigatie gedocumenteerd                                              | ❌ Open |     |
| 3 | DPIA-check aanwezig                                                                      | ❌ Open |     |
| 4 | Concept-auditrapport heeft alle secties (ook al zijn ze nog niet volledig uitgeschreven) | ❌ Open |     |
| 5 | Concept-presentatie heeft minimaal een structuur met titels per slide                    | ❌ Open |     |

### Eindcheck Sprint 4

| # | Check                                                               | Status  | Wie |
| - | ------------------------------------------------------------------- | ------- | --- |
| 1 | Repository is getagd als `v1.0-audit`                             | ❌ Open |     |
| 2 | Alle vijf rubric-producten zijn aanwezig en vindbaar in `/docs/`  | ❌ Open |     |
| 3 | CI-pipeline is groen op de main-branch                              | ❌ Open |     |
| 4 | Geen hardcoded secrets aanwezig (secret scanning geeft geen alerts) | ❌ Open |     |
| 5 | Presentatie is definitief en als PDF opgeslagen in de repository    | ❌ Open |     |

---

## Nieuwe opdracht toevoegen

Kopieer onderstaand template en plak het onder de laatste opdracht.
Voeg de opdracht ook toe aan de inhoudsopgave bovenaan.

```markdown
## Opdracht N: [Naam]

**Bron:** [Presentatienaam, slide N](assets/bestandsnaam.pdf#page=N)
**Doel:** ...
**Verantwoordelijke:** ...
**Periode:** ...

### Eisen

| #  | Eis | Status | Bewijslast | Wie | Notities |
| -- | --- | ------ | ---------- | --- | -------- |
| 1  | ... | ...    | ...        | ... | ...      |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Niet compliant           | 0      |

### Openstaande actiepunten

...

### Wijzigingslog Opdracht N

| Datum | Versie | Wijziging | Door |
| ----- | ------ | --------- | ---- |
| ...   | 1.0    | ...       | ...  |
```
