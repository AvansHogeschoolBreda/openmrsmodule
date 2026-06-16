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

| # | Eis                                                                                                | Status       | Bewijslast                                                                      | Wie             | Notities                                                                                                     |
| - | -------------------------------------------------------------------------------------------------- | ------------ | ------------------------------------------------------------------------------- | --------------- | ------------------------------------------------------------------------------------------------------------ |
| 1 | Non-functional requirements voor onderhoudbaarheid bepaald en vastgelegd                           | ✅ Compliant | `Groep_6_Non-Functional-Requirements.md`                                      | RafvanHooijdonk | NFR-1 t/m NFR-9 vastgelegd met drempelwaarden en onderbouwing                                                |
| 2 | Tooling ingericht (bijv. Qodana of SonarCloud) die CI laat falen bij niet-voldoen                  | ✅ Compliant | `.github/workflows/quality-gate-sonarcloud.yml`, `sonar-project.properties` | RafvanHooijdonk | SonarCloud quality gate gekoppeld aan CI; blokkeert merge bij falen                                          |
| 3 | Systematische analyse uitgevoerd met passende metrieken (complexiteit, duplicatie, coupling, enz.) | ✅ Compliant | Groep_6_Analyse-Onderhoudbaarheid.md                                            | RafvanHooijdonk | Complexiteit, duplicatie, coupling, coverage en code smells geanalyseerd via SonarCloud + broncode-inspectie |
| 4 | Analyse-resultaten duidelijk gedocumenteerd met onderbouwing per metriek                           | ✅ Compliant | Groep_6_Analyse-Onderhoudbaarheid.md                                            | RafvanHooijdonk | Per metriek: meetmethode, resultaat, onderbouwing en prioritering                                            |

---

### Deel 2: Testopzet en testresultaten (vóór verbetering)

| # | Eis                                                                            | Status       | Bewijslast  | Wie             | Notities                                                                                                        |
| - | ------------------------------------------------------------------------------ | ------------ | ----------- | --------------- | --------------------------------------------------------------------------------------------------------------- |
| 1 | Relevante tests opgesteld en uitgevoerd (eenheidstests, integratietests, enz.) | ✅ Compliant | Testplan.md | SimonEulenpesch | 5 testtypen (unit, component/integratie, REST-resource, REST web-controller, IT); 134 tests gedraaid, 132 groen |
| 2 | Testresultaten duidelijk en bruikbaar vastgelegd                               | ✅ Compliant | Testplan.md | SimonEulenpesch | Surefire-resultaten per module en per klasse vastgelegd; 0 failures, 0 errors, 2 @Ignore-skipped                |
| 3 | Teststrategie beschreven (welke typen, scope, tools)                           | ✅ Compliant | Testplan.md | SimonEulenpesch | Typen, scope, tools (JUnit 4, Surefire, Failsafe, JaCoCo) en reproduceerbare commando's beschreven              |

---

### Deel 3: Verbeteringen - prioritering en onderbouwing

| # | Eis                                                                              | Status  | Bewijslast | Wie | Notities                                  |
| - | -------------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------- |
| 1 | Geprioriteerde lijst verbeteringen vastgelegd                                    | ✅ Compliant | Analyse-Onderhoudbaarheid.md | RafvanHooijdonk | Sectie 8.2: 10 geprioriteerde verbeteracties (actie 1 t/m 10) |
| 2 | Prioritering onderbouwd met criteria (bijv. impact, effort, risiconiveau)        | ✅ Compliant | Analyse-Onderhoudbaarheid.md | RafvanHooijdonk | Onderbouwd met Impact, Inspanning (effort) en Prioriteit (Kritiek/Hoog/Middel/Laag) per actie |
| 3 | Verwijzing naar analyse-resultaten en testresultaten verwerkt in de onderbouwing | ✅ Compliant | Analyse-Onderhoudbaarheid.md | RafvanHooijdonk | Sectie 8.2 koppelt acties 1 t/m 4 aan de analyse-bevindingen en acties 5+6 aan de testresultaten (Testplan.md nulmeting) |

---

### Deel 4: Aangepast ontwerp

| # | Eis                                                                 | Status  | Bewijslast | Wie | Notities                               |
| - | ------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------- |
| 1 | Aangepast ontwerp aanwezig voor geselecteerde verbeteringen         | ✅ Compliant | Refactoring-Onderbouwing.md | SimonEulenpesch | Aangepast ontwerp per verbetering (sectie 3) met UML-klassediagrammen voor/na (validator + IdentifierSourceResource); realisatie commit 303c735 |
| 2 | Ontwerp onderbouwd met ontwerpprincipes (bijv. SOLID, DRY, KISS)    | ✅ Compliant | Refactoring-Onderbouwing.md | SimonEulenpesch | SOLID (SRP), DRY, KISS, SoC, YAGNI benoemd en gekoppeld per verbetering (sectie 4) |
| 3 | Ontwerppatronen en/of refactoringpatronen benoemd en toegepast      | ✅ Compliant | Refactoring-Onderbouwing.md | SimonEulenpesch | Extract Method, Guard Clauses, Compose Method, Extract Constant (sectie 3+4) |
| 4 | Alternatieven overwogen en gemotiveerde keuze gemaakt (voor "Goed") | ✅ Compliant | Refactoring-Onderbouwing.md | SimonEulenpesch | Alternatieven per verbetering met motivatie (sectie 5) |

---

### Deel 5: Realisatie (PoC) & verantwoording

| # | Eis                                                                   | Status  | Bewijslast | Wie | Notities                                                 |
| - | --------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------------------------- |
| 1 | Verbeteringen gerealiseerd in een PoC dat overeenkomt met het ontwerp | ✅ Compliant | Refactoring-Onderbouwing.md | Rowen Albers | Refactoring gerealiseerd in commit 303c735 (main, 39 java-bestanden): Extract Method (validator, IdentifierSourceResource), Extract Constant, static-fix. Komt overeen met ontwerp in Refactoring-Onderbouwing.md |
| 2 | Gebruik van (AI-)tooling beschreven en verantwoord                    | ✅ Compliant | Refactoring-Onderbouwing.md | Rowen Albers | Gebruik van Claude voor refactoring en security-hardening/documentatie beschreven en verantwoord in sectie 7 van Refactoring-Onderbouwing.md |
| 3 | Kritische reflectie op toolinggebruik aanwezig (voor "Goed")          | ✅ Compliant | Refactoring-Onderbouwing.md | Rowen Albers | Kritische reflectie op Claude toegevoegd in sectie 7.2 (limieten legacy Spring ASM, controller parameter signatures behouden, python regex annotatiefout) |

---

### Deel 6: Validatie verbeteringen - testen & regressie

| # | Eis                                                                      | Status            | Bewijslast  | Wie             | Notities                                                                                                                                                                       |
| - | ------------------------------------------------------------------------ | ----------------- | ----------- | --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Tests na verbetering uitgevoerd en resultaten vastgelegd                 | ⚠️ Gedeeltelijk | Testplan.md | SimonEulenpesch | Voorlopige validatie op de toegevoegde validator-test (sectie 4.5): api 41 → 52, totaal 134 → 145, resultaten vastgelegd. Volledige validatie volgt na de PoC (Deel 3 t/m 5) |
| 2 | Aantoonbaar dat onderhoudbaarheid is verbeterd (metriek voor vs. na)     | ⚠️ Gedeeltelijk | Testplan.md | SimonEulenpesch | Voor/na op SequentialIdentifierGeneratorValidator: 0% → 71,9% line, 0% → 95,5% branch (JaCoCo). Eén gerichte klasse, niet de volledige PoC                                  |
| 3 | Aantoonbaar dat geen regressie is opgetreden (bestaande tests nog groen) | ⚠️ Gedeeltelijk | Testplan.md | SimonEulenpesch | Volledige suite lokaal groen na toevoeging (145 tests, 0 failures). Nog geen CI-run na merge                                                                                   |

---

### Samenvatting Opdrachtonderdeel 1

| Categorie                | Aantal |
| ------------------------ | ------ |
| ✅ Compliant             | 17     |
| ⚠️ Gedeeltelijk        | 3      |
| ❌ Open / Niet compliant | 0      |

### Openstaande actiepunten Opdrachtonderdeel 1

| Actie                                                                                         | Prioriteit | Wie             |
| --------------------------------------------------------------------------------------------- | ---------- | --------------- |
| Verse SonarCloud-meting op huidige main voor de na-waarden (coverage, CC, duplicatie, rating) | Hoog       |                 |
| Validatie Deel 6 volledig afronden met voor/na op metriekniveau (na-meting gereed)            | Hoog       | SimonEulenpesch |

### Wijzigingslog Opdrachtonderdeel 1

| Datum      | Versie | Wijziging                                                                                                                                                                               | Door            |
| ---------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-12 | 1.0    | Opdrachtonderdeel 1 toegevoegd aan globale checklist                                                                                                                                    | RafvanHooijdonk |
| 2026-06-12 | 1.1    | Deel 1 volledig compliant: NFR-document, SonarCloud CI, systematische analyse gedocumenteerd (eisen 1 t/m 4)                                                                            | RafvanHooijdonk |
| 2026-06-16 | 1.2    | Deel 2 volledig compliant: Groep_6_Testplan.md met teststrategie en nulmeting (134 tests, 132 groen, 5 testtypen)                                                                       | SimonEulenpesch |
| 2026-06-16 | 1.3    | Testplan uitgebreid met sectie 4.5 Verantwoording testdekking; gerichte unit-test SequentialIdentifierGeneratorValidator (0% → 71,9% line / 95,5% branch), suite 134 → 145 tests      | SimonEulenpesch |
| 2026-06-16 | 1.4    | Deel 6 op ⚠️ Gedeeltelijk: voorlopige voor/na-validatie via validator-test in Testplan 4.5 (geen regressie, suite 145 groen). Bewijslast-prefix Deel 2 gecorrigeerd (zonder Groep_6_) | SimonEulenpesch |
| 2026-06-16 | 1.5    | Deel 3 eisen 1+2 ✅ Compliant (geprioriteerde verbeteracties in Analyse-Onderhoudbaarheid 8.2), eis 3 ⚠️ Gedeeltelijk (testresultaten-koppeling mist). Deel 5 eis 1 ⚠️ Gedeeltelijk (refactoring gerealiseerd via commit 73d9b94), eis 2+3 Open | SimonEulenpesch |
| 2026-06-16 | 1.6    | Echte PoC-commit geïdentificeerd: 303c735 (15/06, "201 code quality issues"), niet 73d9b94. Deel 4 ✅ Compliant (Refactoring-Onderbouwing.md: ontwerp, principes, patronen, alternatieven, UML). Deel 3 eis 3 ✅ (testkoppeling in 8.2). Deel 5 eis 1 ✅ (realisatie gekoppeld aan ontwerp). Hersteld na merge-revert | SimonEulenpesch |
| 2026-06-16 | 1.7    | Deel 5 eis 2+3 ✅ Compliant (AI-verantwoording en kritische reflectie met betrekking tot Claude toegevoegd aan Refactoring-Onderbouwing.md) | Rowen Albers    |

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

| #  | Eis                                                                        | Status            | Bewijslast                                                             | Wie             | Notities                                                                                                                                                             |
| -- | -------------------------------------------------------------------------- | ----------------- | ---------------------------------------------------------------------- | --------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1  | Branch protection actief op `main`: alleen via PR, reviews verplicht     | ✅ Compliant      | Settings → Rules → "Protect main – NEN-7510 Ctrl 8.4/8.32" (Active) | RafvanHooijdonk | Ruleset volledig actief: PR verplicht, status checks, force push geblokkeerd, CodeQL scanning vereist. Repo is public; rulesets volledig afgedwongen op GitHub Free. |
| 2  | Alle CI-checks slagen voor merge (build, test, SAST)                       | ✅ Compliant      | `.github/workflows/ci-build-test.yml`                                | RafvanHooijdonk | Workflows draaien op echte idgen-module (`working-directory: openmrs-module-idgen`). Stub `pom.xml` niet langer gebruikt.                                        |
| 3  | **CodeQL** of gelijkwaardige SAST actief                             | ✅ Compliant      | `.github/workflows/sast-codeql.yml`                                  | RafvanHooijdonk | CodeQL scant echte idgen-broncode op push, PR en wekelijks schedule.                                                                                                 |
| 4  | **Secret Scanning** actief                                           | ✅ Compliant      | Settings → Security → Code security and analysis                     | RafvanHooijdonk | Secret Protection én Push protection actief (repo is public; gratis beschikbaar). Blokkeert commits met bekende secrets.                                            |
| 5  | **Dependabot** alerts + security updates actief                      | ✅ Compliant      | Settings → Advanced Security +`.github/dependabot.yml`              | RafvanHooijdonk | Monitort echte idgen-dependencies, wekelijkse updates.                                                                                                               |
| 6  | **Dependency Review Action** gekoppeld aan PR's                      | ✅ Compliant      | `.github/workflows/sca-dependency-review.yml`                        | RafvanHooijdonk | Actief op PR naar `main`, blokkeert HIGH/CRITICAL, weigert GPL-3.0 en AGPL-3.0.                                                                                    |
| 7  | **SBOM** wordt gegenereerd (CycloneDX of SPDX) en geanalyseerd (SCA) | ✅ Compliant      | `.github/workflows/sbom-cyclonedx.yml` + Actions artifacts           | RafvanHooijdonk | CycloneDX JSON via Anchore/Syft op `path: openmrs-module-idgen`. Bevat echte idgen-dependencies.                                                                   |
| 8  | **GitHub Environments** gedefinieerd met protection rules            | ✅ Compliant      | Settings → Environments                                               | RafvanHooijdonk | `production` (1 protection rule, 1 secret) en `test` (1 secret) aanwezig                                                                                         |
| 9  | Secrets gescheiden per environment                                         | ✅ Compliant      | Settings → Environments                                               | RafvanHooijdonk | `production` en `test` hebben elk eigen geïsoleerde secrets                                                                                                     |
| 10 | Pipeline-artifacts (rapporten, SBOM) worden bewaard                        | ⚠️ Gedeeltelijk | Actions → SBOM run → Artifacts                                       | RafvanHooijdonk | Retentie is 90 dagen (free plan maximum). Geconfigureerde 365 dagen wordt automatisch teruggebracht                                                                  |
| 11 | **README.md** beschrijft beleid en procedure (mini-ISMS)             | ✅ Compliant      | `README.md`                                                          | RafvanHooijdonk | Pipeline overzicht, verantwoordelijkheden, branch protection, environments, secrets, artifact bewaring                                                               |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 10     |
| ⚠️ Gedeeltelijk/Tijdelijk | 1      |
| ❌ Niet compliant           | 0      |

### Openstaande actiepunten

**Oplosbaar zonder plan-upgrade:**

Geen - alle oplosbare punten zijn verholpen. Branch protection en Secret Scanning zijn volledig actief (repo is public).

**Vereist GitHub plan-upgrade (betaald):**

| Actie                                     | Blokker              |
| ----------------------------------------- | -------------------- |
| Artifact retentie verhogen naar 365 dagen | Betaald plan vereist |

### Wijzigingslog Opdracht 1

| Datum      | Versie | Wijziging                                                                                                                         | Door            |
| ---------- | ------ | --------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Initiële checklist aangemaakt na volledige pipeline controle                                                                     | RafvanHooijdonk |
| 2026-06-03 | 1.1    | `sbom-cyclonedx.yml` gefixed (Maven stap verwijderd, exit code 1 opgelost)                                                      | RafvanHooijdonk |
| 2026-06-03 | 1.2    | `README.md` bijgewerkt met mini-ISMS beleid en procedures                                                                       | RafvanHooijdonk |
| 2026-06-03 | 1.3    | `SECURITY.md` bijgewerkt met rapportageproces en prioritering                                                                   | RafvanHooijdonk |
| 2026-06-03 | 1.4    | `ci-build-test.yml` workflow aangemaakt voor build & test checks                                                                | RafvanHooijdonk |
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
**Verantwoordelijke:** RowenAlbers (Delen 1, 2, 3, 4), SinanSagir (Delen 5, 6, 7), RafvanHooijdonk (Deel 8, Deel 9 eisen 1-5), SinanSagir (Deel 9 eis 6)
**Periode:** 2026-06
**Sprints:**

- **Sprint 3:** PoC Mitigatie, Attack Surface, Pentestrapportage voor mitigatie, DPIA-check (Taken 3.1, 3.2, 3.3, 3.4)
- **Sprint 4:** PoC afronden, Pentestrapportage na mitigatie, rapportages samenvoegen (Taken 4.2, 4.3)

### Deel 1: Attack Surface Mapping

| # | Eis                                                                                   | Status       | Bewijslast                                                                                                                                                                                                              | Wie          | Notities                                                       |
| - | ------------------------------------------------------------------------------------- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | -------------------------------------------------------------- |
| 1 | Alle HTTP-endpoints geidentificeerd (pad, methode, vereiste privileges)               | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Geïdentificeerd in sectie 4 van het rapport                   |
| 2 | Externe inputs gedocumenteerd (gebruikersinvoer, uploads, omgevingsvariabelen)        | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Gedocumenteerd in sectie 3 (register)                          |
| 3 | Diagram gemaakt met vertrouwensmodel (hoge-risico entry points gemarkeerd)            | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Systeemgrenzen & vertrouwensmodel met Mermaid in sectie 2      |
| 4 | Per entry point: inputvalidatie en autorisatiecheck gedocumenteerd, gaps beschreven   | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | 12 security gaps in kaart gebracht gekoppeld aan NEN-7510 8.25 |
| 5 | Deliverable: tabel Endpoint / Methode / Privilege / Inputvalidatie / Autorisatiecheck | ✅ Compliant | [Groep_6_Attack_Surface_Mapping.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Attack_Surface_Mapping.md) | Rowen Albers | Volledige endpoints/security mapping tabel in sectie 4         |

### Deel 2: Logging gap-analyse

| # | Eis                                                               | Status       | Bewijslast                                                                                                                                                                                                        | Wie          | Notities                                                                                   |
| - | ----------------------------------------------------------------- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | ------------------------------------------------------------------------------------------ |
| 1 | Bestaande log-statements in de module in kaart gebracht           | ✅ Compliant | [Groep_6_Logging_Gap_Analyse.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Logging_Gap_Analyse.md) | Rowen Albers | Alle log-statements in de Java-code geïnventariseerd                                      |
| 2 | Tabel aanwezig: Event / Gelogd? / Compliant met NEN-7510 8.15?    | ✅ Compliant | [Groep_6_Logging_Gap_Analyse.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Logging_Gap_Analyse.md) | Rowen Albers | Register tabel met 11 actieve log-statements in sectie 3                                   |
| 3 | Ontbrekende beveiligingsrelevante events geidentificeerd          | ✅ Compliant | [Groep_6_Logging_Gap_Analyse.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Logging_Gap_Analyse.md) | Rowen Albers | 6 kritieke ontbrekende events (zoals ID-generatie en export) beschreven in sectie 4        |
| 4 | Gecontroleerd of gevoelige data (BSN, wachtwoorden) in logs staat | ✅ Compliant | [Groep_6_Logging_Gap_Analyse.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Logging_Gap_Analyse.md) | Rowen Albers | Analyse van credentials in URL-queryparameters en BSN/ID strings in sectie 5               |
| 5 | Gap gedocumenteerd                                                | ✅ Compliant | [Groep_6_Logging_Gap_Analyse.md](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/docs/LU2%20-%20Kwaliteit%20en%20security%20-%20verbeteronderzoek%20security/Groep_6_Logging_Gap_Analyse.md) | Rowen Albers | Gap-tabel tegen NEN-7510 audit metadata (Wie, Wat, Wanneer, Waarop, Resultaat) in sectie 6 |

### Deel 3: Logging implementeren

| # | Eis                                                                                               | Status       | Bewijslast                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | Wie          | Notities                                                                               |
| - | ------------------------------------------------------------------------------------------------- | ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | -------------------------------------------------------------------------------------- |
| 1 | Audit logging toegevoegd aan: inloggen/uitloggen, lezen patiëntdossier, aanmaken/wijzigen record | ✅ Compliant | [BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/service/BaseIdentifierSourceService.java), [IdentifierSourceController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/IdentifierSourceController.java), [LogEntryController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/LogEntryController.java) | Rowen Albers | Geïmplementeerd in service en controllers                                             |
| 2 | Elk logbericht bevat: UserID, timestamp, event, uitkomst, resource-UUID (NEN-7510 8.15)           | ✅ Compliant | [BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/service/BaseIdentifierSourceService.java)                                                                                                                                                                                                                                                                                                                                                                                                                                   | Rowen Albers | Berichten bevatten UserID, timestamp (Log4j), event type, uitkomst en UUID van de bron |
| 3 | Geen BSN of medische data in logs                                                                 | ✅ Compliant | [BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/service/BaseIdentifierSourceService.java)                                                                                                                                                                                                                                                                                                                                                                                                                                   | Rowen Albers | Geen identifier-waarden of patiëntendata in logs; alleen metadata                     |

### Deel 4: Logging testen

| # | Eis                                              | Status       | Bewijslast                                                                                                                                                                                  | Wie          | Notities                                                                                               |
| - | ------------------------------------------------ | ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------ | ------------------------------------------------------------------------------------------------------ |
| 1 | JUnit-test: succesvolle toegang wordt gelogd     | ✅ Compliant | [LoggingAuditTest.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/test/java/org/openmrs/module/idgen/service/LoggingAuditTest.java) | Rowen Albers | `testSuccessfulAccessLogged` verifieert dat `READ_PATIENT_IDENTIFIER` met `SUCCESS` wordt gelogd |
| 2 | JUnit-test: mislukte toegang wordt gelogd        | ✅ Compliant | [LoggingAuditTest.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/test/java/org/openmrs/module/idgen/service/LoggingAuditTest.java) | Rowen Albers | `testFailedAccessLogged` verifieert dat `SAVE_IDENTIFIER_SOURCE` met `FAILURE` wordt gelogd      |
| 3 | JUnit-test (negatief): logbericht bevat geen BSN | ✅ Compliant | [LoggingAuditTest.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/test/java/org/openmrs/module/idgen/service/LoggingAuditTest.java) | Rowen Albers | `testNoBsnOrSensitiveDataInLogs` verifieert dat geen ID-waarden of 'BSN' in logs lekken              |
| 4 | Alle tests slagen (mvn test geeft groen)         | ✅ Compliant | [LoggingAuditTest.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/test/java/org/openmrs/module/idgen/service/LoggingAuditTest.java) | Rowen Albers | Alle 9 JUnit-tests in `LoggingAuditTest.java` slagen groen in Maven build                            |

### Deel 5: DPIA-check

| # | Eis                                                                             | Status       | Bewijslast    | Wie        | Notities                                                             |
| - | ------------------------------------------------------------------------------- | ------------ | ------------- | ---------- | -------------------------------------------------------------------- |
| 1 | Vastgesteld of verwerking van persoonsgegevens plaatsvindt in de module         | ✅ Compliant | DPIA-Check.md | SinanSagir | Patiënt-ID's, gebruikerslogs; 4 van 9 WP248-criteria van toepassing |
| 2 | Bepaald of een volledige DPIA vereist is (AVG art. 35 drempelcriteria getoetst) | ✅ Compliant | DPIA-Check.md | SinanSagir | DPIA verplicht: criteria 3, 4, 5, 7 van WP248 van toepassing         |
| 3 | Risico's t.a.v. privacy vastgelegd met mitigerende maatregelen                  | ✅ Compliant | DPIA-Check.md | SinanSagir | 5 privacy-risico's met scores, mitigaties en NEN-7510 koppeling      |
| 4 | DPIA-check gedocumenteerd in de repo                                            | ✅ Compliant | DPIA-Check.md | SinanSagir | Groep_6_DPIA-Check.md aangemaakt in security-folder                  |

---

### Deel 6: Penetration Tests - aantonen kwetsbaarheden

| # | Eis                                                                                          | Status       | Bewijslast                    | Wie        | Notities                                                                                |
| - | -------------------------------------------------------------------------------------------- | ------------ | ----------------------------- | ---------- | --------------------------------------------------------------------------------------- |
| 1 | Minimaal één kritische kwetsbaarheid (uit backlog Opdracht 4) geselecteerd voor pentest    | ✅ Compliant | Pentestrapport.md sectie 3    | SinanSagir | SCA-05 (commons-collections 3.2, CVE-2015-7501, CVSS 9.8) geselecteerd en gemotiveerd   |
| 2 | Pentest vóór mitigatie uitgevoerd en navologbaar gedocumenteerd (tool, commando's, output) | ✅ Compliant | Pentestrapport.md secties 4-5 | SinanSagir | OWASP Dependency-Check + ysoserial gadget chain gedocumenteerd in 3 fasen               |
| 3 | Misbruik van de kwetsbaarheid is aangetoond (screenshot/log als bewijs)                      | ✅ Compliant | Pentestrapport.md sectie 4    | SinanSagir | Dependency-Check rapport (dependency-check-report.html) + bom.json als technisch bewijs |
| 4 | Pentest-rapport vóór mitigatie opgeslagen in repo                                          | ✅ Compliant | Pentestrapport.md             | SinanSagir | Groep_6_Pentestrapport.md aangemaakt in security-folder                                 |

---

### Deel 7: Mitigatie & validatie

| # | Eis                                                                         | Status       | Bewijslast                          | Wie        | Notities                                                                        |
| - | --------------------------------------------------------------------------- | ------------ | ----------------------------------- | ---------- | ------------------------------------------------------------------------------- |
| 1 | Kwetsbaarheid gemitigeerd (code-aanpassing of configuratie) via PR          | ✅ Compliant | Pentestrapport.md sectie 8, pom.xml | SinanSagir | dependencyManagement commons-collections 3.2 → 3.2.2 in root pom.xml           |
| 2 | Gebruik van (AI-)tooling bij realisatie beschreven en verantwoord           | ✅ Compliant | Pentestrapport.md sectie 8.4        | SinanSagir | Claude (Sonnet 4.6) ondersteunend ingezet; inhoudelijke keuzes door SinanSagir  |
| 3 | Pentest ná mitigatie uitgevoerd; aangetoond dat securityrisico is verlaagd | ✅ Compliant | Pentestrapport.md sectie 9          | SinanSagir | mvn dependency:tree bevestigt 3.2.2; CVE-2015-7501 niet van toepassing op 3.2.2 |
| 4 | Pentest-rapport ná mitigatie opgeslagen in repo                            | ✅ Compliant | Pentestrapport.md                   | SinanSagir | Groep_6_Pentestrapport.md bevat voor- en na-mitigatie in één document         |
| 5 | Vergelijking voor/na gedocumenteerd (wat was het risico, wat is het nu)     | ✅ Compliant | Pentestrapport.md sectie 10         | SinanSagir | CVSS 9.8 Kritiek → N.v.t. na upgrade; NEN-7510 Ctrl 8.8 compliant              |

---

### Deel 8: Code Coverage

| # | Eis                                                        | Status       | Bewijslast                                                                            | Wie             | Notities                                                                                        |
| - | ---------------------------------------------------------- | ------------ | ------------------------------------------------------------------------------------- | --------------- | ----------------------------------------------------------------------------------------------- |
| 1 | JaCoCo geconfigureerd en geactiveerd (mvn jacoco:report)   | ✅ Compliant | `openmrs-module-idgen/api/pom.xml` + `quality-gate-sonarcloud.yml`                | RafvanHooijdonk | JaCoCo draait via `mvn verify` in quality-gate-sonarcloud.yml; rapporten gaan naar SonarCloud |
| 2 | Coverage % bepaald en gedocumenteerd (met onderbouwing)    | ✅ Compliant | `Groep_6_Analyse-Onderhoudbaarheid.md` + `Groep_6_Non-Functional-Requirements.md` | RafvanHooijdonk | 50% overall (api 54.3%, omod 46.9%); per-bestand breakdown inclusief 0%-klassen gedocumenteerd  |
| 3 | Coverage rapport opgeslagen als artifact in GitHub Actions | ✅ Compliant | `quality-gate-sonarcloud.yml` stap "Upload JaCoCo coverage reports"                 | RafvanHooijdonk | api + omod rapport geupload als artifact `jacoco-report-{run}`, 90 dagen retentie             |

### Deel 9: DAST - Dynamic Application Security Testing (OWASP ZAP)

> **Bron:** WS04B slide 16, WS05 slide 59, WS06 slide 18. Output telt als dynamisch testbewijs (rubricelement C) en als bijlage in het auditrapport (NEN-7510 8.29).

| # | Eis                                                                                                          | Status       | Bewijslast                                                    | Wie             | Notities                                                                                       |
| - | ------------------------------------------------------------------------------------------------------------ | ------------ | ------------------------------------------------------------- | --------------- | ---------------------------------------------------------------------------------------------- |
| 1 | OWASP ZAP geïnstalleerd en draaibaar (lokaal of via Docker)                                                 | ✅ Compliant | `run-zap.sh`, `.github/workflows/dast-owasp-zap.yml`      | RafvanHooijdonk | ZAP via `ghcr.io/zaproxy/zaproxy:stable`; lokaal script + GitHub Actions workflow aangemaakt |
| 2 | Draaiende OpenMRS-instantie beschikbaar als DAST-target (lokale of staging-omgeving)                         | ✅ Compliant | `docker-compose.yml`, `run-zap.sh`                        | RafvanHooijdonk | Bestaande docker-compose setup; target `http://localhost:8080/openmrs`                       |
| 3 | ZAP Spider uitgevoerd op de target (endpoints automatisch ontdekt)                                           | ✅ Compliant | `docs/dast/zap-report.html`, Actions run #6                 | RafvanHooijdonk | Spider inbegrepen in `zap-full-scan.py`; 1577 URLs gescand in run #6                         |
| 4 | ZAP Active Scan uitgevoerd (XSS, SQLi, CSRF, etc. getest)                                                    | ✅ Compliant | `docs/dast/zap-report.html`, Actions run #6                 | RafvanHooijdonk | Active scan inbegrepen in `zap-full-scan.py`; 1827 alerts gevonden in 19m 29s                |
| 5 | ZAP-rapport opgeslagen als artifact in de repo (audit trail)                                                 | ✅ Compliant | `docs/dast/zap-report/`, artifact `zap-report-6` (212 KB) | RafvanHooijdonk | HTML + JSON + XML rapport in `docs/dast/`; artifact 90 dagen bewaard; NEN-7510 8.29          |
| 6 | DAST-bevindingen beoordeeld en gekoppeld aan bestaande security backlog of als nieuwe finding gedocumenteerd | ✅ Compliant | Resolved_Alerts_DAST.md                                       | SinanSagir      | Alle 49 alerts getrieerd: 11 gefixt (SecurityHeadersFilter), 10 informational/reviewed, 28 buiten scope (Tomcat-eigen content, geen broncode in deze repo) |

---

### Samenvatting Opdracht 5

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 39     |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 0      |

### Openstaande actiepunten Opdracht 5

Geen openstaande actiepunten. Alle eisen zijn compliant. DAST-herscan na mitigatie (run-zap.sh) is een aanbevolen vervolgstap, zie Resolved_Alerts_DAST.md sectie 5.

### Wijzigingslog Opdracht 5

| Datum      | Versie | Wijziging                                                                                                                                               | Door            |
| ---------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 2026-06-03 | 1.0    | Opdracht 5 toegevoegd aan globale checklist                                                                                                             | RafvanHooijdonk |
| 2026-06-12 | 1.1    | Deel 5 (DPIA), Deel 6 (Pentest voor), Deel 7 (Mitigatie+validatie) toegevoegd; naam aangepast                                                           | RafvanHooijdonk |
| 2026-06-13 | 1.2    | Deel 1 (Attack Surface Mapping) ingevuld en op ✅ Compliant gezet                                                                                       | Rowen Albers    |
| 2026-06-12 | 1.2    | Deel 8 volledig compliant: JaCoCo actief, coverage gedocumenteerd, artifact upload toegevoegd                                                           | RafvanHooijdonk |
| 2026-06-13 | 1.3    | Deel 2 (Logging gap-analyse) ingevuld en op ✅ Compliant gezet                                                                                          | Rowen Albers    |
| 2026-06-13 | 1.4    | Deel 3 (Logging implementeren) ingevuld en op ✅ Compliant gezet                                                                                        | Rowen Albers    |
| 2026-06-13 | 1.5    | Deel 4 (Logging testen) ingevuld, tests groen gemaakt en op ✅ Compliant gezet                                                                          | Rowen Albers    |
| 2026-06-13 | 1.6    | Deel 5 (DPIA-check) compliant: AVG art. 35 drempeltoets, 5 privacy-risico's gedocumenteerd                                                              | SinanSagir      |
| 2026-06-13 | 1.7    | Deel 6 (Pentest voor) compliant: SCA-05 CVE-2015-7501 gedocumenteerd met aanvalsketen                                                                   | SinanSagir      |
| 2026-06-13 | 1.8    | Deel 7 (Mitigatie) compliant: commons-collections 3.2.2 in pom.xml, voor/na vergelijking                                                                | SinanSagir      |
| 2026-06-13 | 1.9    | Pentest-Voor.md en Pentest-Na.md samengevoegd tot Groep_6_Pentestrapport.md; AI-formulering gecorrigeerd                                                | SinanSagir      |
| 2026-06-15 | 2.0    | Deel 9 (DAST - OWASP ZAP) toegevoegd: 6 eisen, verantwoordelijke RafvanHooijdonk voor installatie/uitvoering                                            | RafvanHooijdonk |
| 2026-06-15 | 2.1    | Deel 9 eisen 1+2 compliant: run-zap.sh + dast-owasp-zap.yml workflow aangemaakt; eisen 3-5 wachten op uitvoering                                        | RafvanHooijdonk |
| 2026-06-15 | 2.2    | Deel 9 eisen 3+4+5 compliant: ZAP full scan uitgevoerd (1577 URLs, 1827 alerts, 19m 29s); rapporten in docs/dast/ en als artifact zap-report-6 (212 KB) | RafvanHooijdonk |
| 2026-06-16 | 2.3    | Deel 9 eis 6 compliant: alle 49 alerts uit docs/dast/zap-report/zap-report.json getrieerd in Groep_6_Resolved_Alerts_DAST.md (11 gefixt via nieuwe SecurityHeadersFilter in idgen-omod, 10 informational/reviewed, 28 buiten scope); Opdracht 5 nu volledig compliant | SinanSagir |

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

| # | Eis                                                                                                                                                                                                                                                                                                                                                                                                                               | Status  | Bewijslast | Wie | Notities                                    |
| - | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- | ---------- | --- | ------------------------------------------- |
| 1 | Geschreven voor niet-technische lezer (Raad van Bestuur), geen jargon                                                                                                                                                                                                                                                                                                                                                             | ❌ Open |            |     | Geen CVE-nummers of CVSS-scores in de tekst |
| 2 | Overall RAG-status aanwezig met toelichti�������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������� |         |            |     |                                             |
