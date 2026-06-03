# Compliance Checklist: OpenMRS Module

> **Repository:** AvansHogeschoolBreda/openmrsmodule
> **Project:** LU2: Verbeter-onderzoek software-kwaliteit (NEN-7510)

Dit document is de centrale checklist voor alle opdrachten binnen dit project.
Per opdracht worden de eisen, status, bewijslast en verantwoordelijke bijgehouden.

---

## Inhoudsopgave

- [Opdracht 1: Compliance Pipeline](#opdracht-1-compliance-pipeline)
- [Opdracht 2: Compliance Verslag](#opdracht-2-compliance-verslag)
- [Opdracht 3: Asset identificatie, Threat modeling &amp; Risico evaluatie](#opdracht-3-asset-identificatie-threat-modeling-risico-evaluatie)
- [Opdracht 4: Compliance Scanning &amp; Risk Assessment Report](#opdracht-4-compliance-scanning-risk-assessment-report)
- [Opdracht 5: Secure Coding &amp; Logging](#opdracht-5-secure-coding-logging)
- [Opdracht 6: Audit Reporting](#opdracht-6-audit-reporting)
- [Sprints](#sprints)

---

## Opdracht 1: Compliance Pipeline

**Bron:** [WS02: Hardening Dev Pipeline, slide 41](assets/presentaties/ICT-I2.4%20Security%20WS02%20-%20Hardening%20Dev%20Pipeline.pdf#page=41)
**Doel:** Aantoonbaar compliant CI/CD security pipeline opzetten conform NEN-7510.
**Verantwoordelijke:** RafvanHooijdonk
**Periode:** 2026-06

### Eisen

| #  | Eis                                                                        | Status                   | Bewijslast                                                | Wie | Notities                                                                                                                                                                                                                                          |
| -- | -------------------------------------------------------------------------- | ------------------------ | --------------------------------------------------------- | --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1  | Branch protection actief op `main`: alleen via PR, reviews verplicht   | ⚠️ Gedeeltelijk        | Settings → Branches →`main` rule aanwezig             | RafvanHooijdonk | Regel geconfigureerd (PR verplicht, 1 approval, status checks), maar**not enforced** op private repo zonder GitHub Team/Enterprise plan                                                                                                     |
| 2  | Alle CI-checks slagen voor merge (build, test, SAST)                       | ⚠️ Tijdelijk compliant | `.github/workflows/ci.yml` aanwezig                     | RafvanHooijdonk | CI workflow actief. Tijdelijke stub `pom.xml` toegevoegd zodat Maven build & test slagen. **Let op:** zodra de echte OpenMRS module wordt toegevoegd, moet `pom.xml` worden vervangen of overschreven door de module-eigen `pom.xml`. |
| 3  | **CodeQL** of gelijkwaardige SAST actief                             | ⚠️ Tijdelijk compliant | `.github/workflows/codeql.yml`                          | RafvanHooijdonk | CodeQL actief op push, PR en wekelijks schedule. Runs slagen, maar scant momenteel alleen een lege stub.                                                                                                                                          |
| 4  | **Secret Scanning** actief                                           | ❌ Niet compliant        | Settings → Advanced Security                             | RafvanHooijdonk | Niet beschikbaar op GitHub Free plan voor private repos. Vereist GitHub Advanced Security (betaald)                                                                                                                                               |
| 5  | **Dependabot** alerts + security updates actief                      | ⚠️ Tijdelijk compliant | Settings → Advanced Security +`.github/dependabot.yml` | RafvanHooijdonk | Alerts enabled, wekelijkse updates. Monitort momenteel alleen stub `pom.xml` (enkel JUnit). Pas volledig effectief met echte module-dependencies.                                                                                               |
| 6  | **Dependency Review Action** gekoppeld aan PR's                      | ⚠️ Tijdelijk compliant | `.github/workflows/dependency-review.yml`               | RafvanHooijdonk | Actief op PR naar `main`, blokkeert HIGH/CRITICAL, weigert GPL-3.0 en AGPL-3.0. Reviewt momenteel minimale stub-dependencies.                                                                                                                   |
| 7  | **SBOM** wordt gegenereerd (CycloneDX of SPDX) en geanalyseerd (SCA) | ⚠️ Tijdelijk compliant | `.github/workflows/sbom.yml` + Actions artifacts        | RafvanHooijdonk | CycloneDX JSON via Anchore/Syft op elke push naar `main`. SBOM bevat momenteel alleen stub-dependencies.                                                                                                                                        |
| 8  | **GitHub Environments** gedefinieerd met protection rules            | ✅ Compliant             | Settings → Environments                                  | RafvanHooijdonk | `production` (1 protection rule, 1 secret) en `test` (1 secret) aanwezig                                                                                                                                                                      |
| 9  | Secrets gescheiden per environment                                         | ✅ Compliant             | Settings → Environments                                  | RafvanHooijdonk | `production` en `test` hebben elk eigen geïsoleerde secrets                                                                                                                                                                                  |
| 10 | Pipeline-artifacts (rapporten, SBOM) worden bewaard                        | ⚠️ Gedeeltelijk        | Actions → SBOM run → Artifacts                          | RafvanHooijdonk | Retentie is 90 dagen (free plan maximum). Geconfigureerde 365 dagen wordt automatisch teruggebracht                                                                                                                                               |
| 11 | **README.md** beschrijft beleid en procedure (mini-ISMS)             | ✅ Compliant             | `README.md`                                             | RafvanHooijdonk | Pipeline overzicht, verantwoordelijkheden, branch protection, environments, secrets, artifact bewaring                                                                                                                                            |                                                    |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 3      |
| ⚠️ Gedeeltelijk/Tijdelijk | 7      |
| ❌ Niet compliant           | 1      |

### Openstaande actiepunten

**Oplosbaar zonder plan-upgrade:**

| Actie                                                                                                          | Prioriteit | Wie |
| -------------------------------------------------------------------------------------------------------------- | ---------- | --- |
| Echte OpenMRS module toevoegen en stub `pom.xml` vervangen door module-eigen `pom.xml` (fix eis #2 t/m #7) | Hoog       |     |

**Vereist GitHub plan-upgrade (Team of Enterprise):**

| Actie                                     | Blokker                                           |
| ----------------------------------------- | ------------------------------------------------- |
| Branch protection afdwingen op `main`   | GitHub Team/Enterprise vereist voor private repos |
| Secret Scanning activeren                 | GitHub Advanced Security vereist                  |
| Artifact retentie verhogen naar 365 dagen | Betaald plan vereist                              |

### Wijzigingslog Opdracht 1

| Datum      | Versie | Wijziging                                                                  | Door |
| ---------- | ------ | -------------------------------------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Initiële checklist aangemaakt na volledige pipeline controle              | RafvanHooijdonk |
| 2026-06-03 | 1.1    | `sbom.yml` gefixed (Maven stap verwijderd, exit code 1 opgelost)         | RafvanHooijdonk |
| 2026-06-03 | 1.2    | `README.md` bijgewerkt met mini-ISMS beleid en procedures                | RafvanHooijdonk |
| 2026-06-03 | 1.3    | `SECURITY.md` bijgewerkt met rapportageproces en prioritering            | RafvanHooijdonk |
| 2026-06-03 | 1.4    | `ci.yml` workflow aangemaakt voor build & test checks                    | RafvanHooijdonk |
| 2026-06-03 | 1.5    | `checklist.md` aangemaakt voor doorlopende compliance tracking           | RafvanHooijdonk |
| 2026-06-03 | 1.6    | Stub `pom.xml` toegevoegd, eis #2 bijgewerkt naar tijdelijk compliant    | RafvanHooijdonk |
| 2026-06-03 | 1.7    | Opdracht-foto toegevoegd, checklist hersteld na corruptie                  | RafvanHooijdonk |
| 2026-06-03 | 1.8    | Eis #3, #5, #6, #7 bijgewerkt naar tijdelijk compliant (geen echte module) | RafvanHooijdonk |
| 2026-06-03 | 1.9    | Checklist omgezet naar globaal formaat met opdrachten als secties          | RafvanHooijdonk |

---

## Opdracht 2: Compliance Verslag

**Bron:** [WS02: Hardening Dev Pipeline, slide 42](assets/presentaties/ICT-I2.4%20Security%20WS02%20-%20Hardening%20Dev%20Pipeline.pdf#page=42)

**Doel:** Aantonen dat de pipeline compliant is aan NEN-7510:2024-2 controls via een compliance verslag.
**Verantwoordelijke:** RafvanHooijdonk (met projectgroep)
**Periode:** 2026-06
**Oplevering:** Markdown of Word document, ingeleverd via de repo.

> Opdracht 1 hoeft nog niet 100% gereed te zijn voor deze opdracht.

### Eisen

| # | Eis                                                                                         | Status  | Bewijslast                              | Wie | Notities                                                          |
| - | ------------------------------------------------------------------------------------------- | ------- | --------------------------------------- | --- | ----------------------------------------------------------------- |
| 1 | Per relevante NEN-7510:2024-2 control beschreven wat de control vereist                     | ❌ Open | compliance verslag (nog aan te leveren) |     | Controls uit slide 9 als basis                                    |
| 2 | Per control beschreven hoe de pipeline hieraan voldoet (verwijzing naar bestand/instelling) | ❌ Open | compliance verslag (nog aan te leveren) |     | Concrete verwijzingen naar workflows, Settings, screenshots       |
| 3 | Per control beschreven welk restrisico er nog is en waarom                                  | ❌ Open | compliance verslag (nog aan te leveren) |     | Bekende beperkingen: branch protection, Secret Scanning, retentie |
| 4 | Structuur: tabel of genummerde secties per control                                          | ❌ Open | compliance verslag (nog aan te leveren) |     |                                                                   |
| 5 | Opgeleverd als Markdown of Word document in de repo                                         | ❌ Open | bestand in repo                         |     |                                                                   |

### Samenvatting

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 5      |

### Openstaande actiepunten

| Actie                                                             | Prioriteit | Wie |
| ----------------------------------------------------------------- | ---------- | --- |
| Compliance verslag schrijven op basis van NEN-7510:2024-2 slide 9 | Hoog       |     |
| Verslag opleveren als Markdown of Word in de repo                 | Hoog       |     |

### Wijzigingslog Opdracht 2

| Datum      | Versie | Wijziging                                   | Door |
| ---------- | ------ | ------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Opdracht 2 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Opdracht 3: Asset identificatie, Threat modeling & Risico evaluatie

**Bron:** [WS03: Healthcare Risk Assessment, slides 48-51](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=48)
**Doel:** Risico's in kaart brengen voor de OpenMRS module via asset identificatie, bow-tie analyse en risico evaluatie van de CI-CD pipeline.
**Verantwoordelijke:** Met projectgroep
**Periode:** 2026-06
**Oplevering:** Per deel een document of diagram, ingeleverd via de repo.

---

### Deel 1: Asset identificatie & Threat modeling

**Bron:** [WS03: Healthcare Risk Assessment, slide 49](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=49)

| # | Eis                                                                                               | Status  | Bewijslast | Wie | Notities                                           |
| - | ------------------------------------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------------------- |
| 1 | Documentatie van de OpenMRS module bestudeerd                                                     | ❌ Open |            |     |                                                    |
| 2 | Minimaal een aantal assets geidentificeerd (bijv. Patient Obs, User Credentials, System Settings) | ❌ Open |            |     |                                                    |
| 3 | BIV-analyse (CIA) uitgevoerd per asset                                                            | ❌ Open |            |     | Beschikbaarheid, Integriteit, Vertrouwelijkheid    |
| 4 | Gevoelige gegevens beschreven met referenties                                                     | ❌ Open |            |     |                                                    |
| 5 | Meest reele hazards per asset benoemd                                                             | ❌ Open |            |     | Welke hazards zijn het meest reeel voor de module? |
| 6 | Score schaal, risk appetite en grenswaarden vastgelegd                                            | ❌ Open |            |     |                                                    |

---

### Deel 2: Bow-tie van hazard

**Bron:** [WS03: Healthcare Risk Assessment, slide 50](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=50)

| # | Eis                                                         | Status  | Bewijslast | Wie | Notities |
| - | ----------------------------------------------------------- | ------- | ---------- | --- | -------- |
| 1 | Een hazard gekozen                                          | ❌ Open |            |     |          |
| 2 | Top-event gedefinieerd                                      | ❌ Open |            |     |          |
| 3 | Bow-tie gemaakt met preventieve en correctieve maatregelen  | ❌ Open |            |     |          |
| 4 | Relevante NEN-7510:2024-2 controls gekoppeld aan de bow-tie | ❌ Open |            |     |          |
| 5 | Escalation factors opgenomen                                | ❌ Open |            |     |          |
| 6 | Audit mindset verwerkt: niet gelogd = niet gebeurd          | ❌ Open |            |     |          |

---

### Deel 3: Risico evaluatie CI-CD

**Bron:** [WS03: Healthcare Risk Assessment, slide 51](assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf#page=51)

| # | Eis                                           | Status  | Bewijslast | Wie | Notities                                     |
| - | --------------------------------------------- | ------- | ---------- | --- | -------------------------------------------- |
| 1 | Risico matrix gemaakt voor de CI-CD pipeline  | ❌ Open |            |     | Op basis van CI-CD inrichting uit Opdracht 1 |
| 2 | Bow-tie(s) gemaakt voor CI-CD risico's        | ❌ Open |            |     |                                              |
| 3 | Escalation factors opgenomen in de bow-tie(s) | ❌ Open |            |     |                                              |

---

### Samenvatting Opdracht 3

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 15     |

### Openstaande actiepunten Opdracht 3

| Actie                                                       | Prioriteit | Wie |
| ----------------------------------------------------------- | ---------- | --- |
| Deel 1: assets en BIV-analyse uitvoeren                     | Hoog       |     |
| Deel 2: hazard kiezen en bow-tie maken                      | Hoog       |     |
| Deel 3: risico matrix en bow-ties voor CI-CD pipeline maken | Hoog       |     |

### Wijzigingslog Opdracht 3

| Datum      | Versie | Wijziging                                   | Door |
| ---------- | ------ | ------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Opdracht 3 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Opdracht 4: Compliance Scanning & Risk Assessment Report

**Bron:** [WS04A: Compliance Scanning, slide 53](assets/presentaties/ICT-I2.4%20Security%20WS04A%20-%20Compliance%20Scanning.pdf#page=53)
**Doel:** SAST/SCA scan uitvoeren op de OpenMRS module, resultaten verwerken in een geprioriteerde security backlog en een Risk Assessment Report.
**Verantwoordelijke:**
**Periode:** 2026-06
**Oplevering:** PDF + bronbestanden (Markdown, spreadsheet) in de repo.

> WS04B (Testing & Pentesting) heeft geen aparte opdracht. Die sessie is een refresher; de testoutput (unit tests, DAST, pentest) is bewijsmateriaal voor Opdracht 4 en Opdracht 6.

### Deel 1: Scan codebase

| # | Eis                                                        | Status  | Bewijslast | Wie | Notities                     |
| - | ---------------------------------------------------------- | ------- | ---------- | --- | ---------------------------- |
| 1 | SAST scan uitgevoerd op de module                          | ❌ Open |            |     | Bijv. Snyk CLI of IDE plugin |
| 2 | SCA scan uitgevoerd op de module                           | ❌ Open |            |     |                              |
| 3 | SBOM gegenereerd (bijv. CycloneDX)                         | ❌ Open |            |     |                              |
| 4 | Technisch SAST-bestand opgeslagen als output (audit trail) | ❌ Open |            |     |                              |
| 5 | Technisch SCA-bestand opgeslagen als output (audit trail)  | ❌ Open |            |     |                              |
| 6 | Technisch SBOM-bestand opgeslagen als output (audit trail) | ❌ Open |            |     |                              |

### Deel 2: Geprioriteerde security backlog

| # | Eis                                                                                       | Status  | Bewijslast | Wie | Notities                                                    |
| - | ----------------------------------------------------------------------------------------- | ------- | ---------- | --- | ----------------------------------------------------------- |
| 1 | Backlog bevat minimaal 8 bevindingen (4 SCA + 4 SAST)                                     | ❌ Open |            |     |                                                             |
| 2 | Elk item heeft: Finding ID, CVE/CWE, component, CVSS Base Score (geverifieerd via NVD)    | ❌ Open |            |     | Niet alleen Snyk-score overnemen                            |
| 3 | Elk item heeft contextuele score (bereikbaarheid + healthcare-impact)                     | ❌ Open |            |     |                                                             |
| 4 | Elk item gekoppeld aan minimaal een NEN-7510 control                                      | ❌ Open |            |     |                                                             |
| 5 | Elk item heeft: fix beschikbaar (ja/nee + versie), effort (S/M/L/XL), prioriteit, besluit | ❌ Open |            |     | Besluit: patchen / supprimeren (met rationale) / accepteren |
| 6 | False positives en risicoacceptaties gedocumenteerd                                       | ❌ Open |            |     |                                                             |

### Deel 3: Risk Assessment Report (RAR)

| # | Eis                                                                             | Status  | Bewijslast | Wie | Notities |
| - | ------------------------------------------------------------------------------- | ------- | ---------- | --- | -------- |
| 1 | Managementsamenvatting aanwezig (0.5-1 pagina, niet-technisch leesbaar)         | ❌ Open |            |     |          |
| 2 | Scope & Methodologie beschreven (welke module, tools, periode, wat NIET getest) | ❌ Open |            |     |          |
| 3 | Top-5 bevindingen beschreven (technisch + business + NEN-7510 koppeling)        | ❌ Open |            |     |          |
| 4 | Security backlog opgenomen in rapport                                           | ❌ Open |            |     |          |
| 5 | Kostenraming aanwezig (minimaal 3 kostenposten; fictief maar realistisch)       | ❌ Open |            |     |          |
| 6 | Conclusie & aanbevelingen aanwezig (go/no-go voor productie-deployment)         | ❌ Open |            |     |          |
| 7 | Rapport opgeleverd als PDF + bronbestanden in de repo                           | ❌ Open |            |     |          |

### Samenvatting Opdracht 4

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 19     |

### Openstaande actiepunten Opdracht 4

| Actie                                           | Prioriteit | Wie |
| ----------------------------------------------- | ---------- | --- |
| SAST en SCA scan draaien op de echte module     | Hoog       |     |
| Security backlog opstellen (min. 8 bevindingen) | Hoog       |     |
| Risk Assessment Report schrijven                | Hoog       |     |

### Wijzigingslog Opdracht 4

| Datum      | Versie | Wijziging                                   | Door |
| ---------- | ------ | ------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Opdracht 4 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Opdracht 5: Secure Coding & Logging

**Bron:** [WS05: Secure Coding, Privacy by Design, slide 52](assets/presentaties/ICT-I2.4%20Security%20WS05%20-%20Secure%20Coding%2C%20Privacy%20by%20Design.pdf#page=52)
**Doel:** Attack surface in kaart brengen, logging gap-analyse uitvoeren, compliant audit logging implementeren en testen, code coverage activeren in CI.
**Verantwoordelijke:**
**Periode:** 2026-06

### Deel 1: Attack Surface Mapping

| # | Eis                                                                                   | Status  | Bewijslast | Wie | Notities                                           |
| - | ------------------------------------------------------------------------------------- | ------- | ---------- | --- | -------------------------------------------------- |
| 1 | Alle HTTP-endpoints geidentificeerd (pad, methode, vereiste privileges)               | ❌ Open |            |     | Zoek op @GetMapping, @PostMapping, @RequestMapping |
| 2 | Externe inputs gedocumenteerd (gebruikersinvoer, uploads, omgevingsvariabelen)        | ❌ Open |            |     |                                                    |
| 3 | Diagram gemaakt met vertrouwensmodel (hoge-risico entry points gemarkeerd)            | ❌ Open |            |     |                                                    |
| 4 | Per entry point: inputvalidatie en autorisatiecheck gedocumenteerd, gaps beschreven   | ❌ Open |            |     | Koppeling aan NEN-7510 8.25                        |
| 5 | Deliverable: tabel Endpoint / Methode / Privilege / Inputvalidatie / Autorisatiecheck | ❌ Open |            |     |                                                    |

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

### Deel 5: Code Coverage

| # | Eis                                                        | Status  | Bewijslast | Wie | Notities |
| - | ---------------------------------------------------------- | ------- | ---------- | --- | -------- |
| 1 | JaCoCo geconfigureerd en geactiveerd (mvn jacoco:report)   | ❌ Open |            |     |          |
| 2 | Coverage % bepaald en gedocumenteerd (met onderbouwing)    | ❌ Open |            |     |          |
| 3 | Coverage rapport opgeslagen als artifact in GitHub Actions | ❌ Open |            |     |          |

### Samenvatting Opdracht 5

| Categorie                   | Aantal |
| --------------------------- | ------ |
| ✅ Compliant                | 0      |
| ⚠️ Gedeeltelijk/Tijdelijk | 0      |
| ❌ Open / Niet compliant    | 21     |

### Openstaande actiepunten Opdracht 5

| Actie                                           | Prioriteit | Wie |
| ----------------------------------------------- | ---------- | --- |
| Attack surface mapping uitvoeren                | Hoog       |     |
| Logging gap-analyse uitvoeren                   | Hoog       |     |
| Compliant audit logging implementeren en testen | Hoog       |     |
| JaCoCo activeren in CI                          | Hoog       |     |

### Wijzigingslog Opdracht 5

| Datum      | Versie | Wijziging                                   | Door |
| ---------- | ------ | ------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Opdracht 5 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Opdracht 6: Audit Reporting

**Bron:** [WS06: Audit Reporting, slide 44](assets/presentaties/ICT-I2.4%20Security%20WS06%20-%20Audit%20Reporting.pdf#page=44)
**Doel:** Volledig auditrapport opleveren met executive summary, traceability matrix en technisch rapport. Plus een responsible disclosure scenario uitwerken.
**Verantwoordelijke:**
**Periode:** 2026-06
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
| 3 | Status t.o.v. CRA en NEN-7510:2024-2 vermeld                          | ❌ Open |            |     |                                             |
| 4 | Top 3 risico's beschreven in zorg-impact (niet technische impact)     | ❌ Open |            |     |                                             |
| 5 | Geprioriteerde roadmap aanwezig (nu / deze sprint / later)            | ❌ Open |            |     |                                             |
| 6 | Maximaal 400 woorden                                                  | ❌ Open |            |     |                                             |

### Deliverable 2: Traceability Matrix

| # | Eis                                                                      | Status  | Bewijslast | Wie | Notities                                     |
| - | ------------------------------------------------------------------------ | ------- | ---------- | --- | -------------------------------------------- |
| 1 | Minimaal 5 NEN-7510:2024-2 controls opgenomen                            | ❌ Open |            |     | Aanbevolen: 8.28, 8.8, 8.15, 8.25, 5.35/5.36 |
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

| Datum      | Versie | Wijziging                                   | Door |
| ---------- | ------ | ------------------------------------------- | ---- |
| 2026-06-03 | 1.0    | Opdracht 6 toegevoegd aan globale checklist | RafvanHooijdonk |

---

## Sprints

De sprints zijn de uitvoeringsplanning. Per sprint staan de taken, verwachte output en eindcheck.
De gedetailleerde sprint-bestanden staan in `docs/sprints/`.

| Sprint   | Doel                                        | Bestand                                    | Status  |
| -------- | ------------------------------------------- | ------------------------------------------ | ------- |
| Sprint 1 | Omgeving inrichten & Gap-analyse            | [docs/sprints/sprint1.md](sprints/sprint1.md) | ❌ Open |
| Sprint 2 | Risico-analyse & Security Backlog           | [docs/sprints/sprint2.md](sprints/sprint2.md) | ❌ Open |
| Sprint 3 | Mitigatie, Pentest & Auditrapport (concept) | [docs/sprints/sprint3.md](sprints/sprint3.md) | ❌ Open |
| Sprint 4 | Definitief & Oplevering                     | [docs/sprints/sprint4.md](sprints/sprint4.md) | ❌ Open |

### Eindcheck Sprint 1

| # | Check                                                          | Status                   | Notities                                                                                         | Wie             |
| - | -------------------------------------------------------------- | ------------------------ | ------------------------------------------------------------------------------------------------ | --------------- |
| 1 | GitHub repository heeft branch protection en Dependabot actief | ⚠️ Gedeeltelijk        | Branch protection geconfigureerd maar niet afdwingbaar (Free plan). Dependabot en CodeQL actief. | RafvanHooijdonk |
| 2 | SBOM-bestand wordt als CI-artifact aangemaakt in Actions       | ⚠️ Tijdelijk compliant | `sbom.yml` actief en artifact aangemaakt. Draait op stub, niet op echte module.                | RafvanHooijdonk |
| 3 | Gap-analyse dekt minimaal 3 NEN-7510 controls met bewijs       | ❌ Open                  | `Groep_6_Gap-Analyse.md` is aangemaakt, maar nog niet ingevuld.                                |                 |
| 4 | Alle teamleden hebben een commit bijgedragen                   | ❌ Open                  | Niet verifieerbaar vanuit deze context.                                                          |                 |

### Eindcheck Sprint 2

| # | Check                                                                       | Status                   | Wie |
| - | --------------------------------------------------------------------------- | ------------------------ | --- |
| 1 | Risicomatrix met minimaal 5 risico's aanwezig                               | ❌ Open                  |     |
| 2 | Bow-tie voor het hoogste risico uitgewerkt                                  | ❌ Open                  |     |
| 3 | SAST- en SBOM-scan loopt automatisch in CI en output is opgeslagen          | ⚠️ Tijdelijk compliant |     |
| 4 | Security backlog heeft minimaal 5 bevindingen met CVSS + NEN-7510 koppeling | ❌ Open                  |     |
| 5 | Patchadvies is onderbouwd met CVE-data uit de SBOM                          | ❌ Open                  |     |

### Eindcheck Sprint 3

| # | Check                                                                                        | Status  | Wie |
| - | -------------------------------------------------------------------------------------------- | ------- | --- |
| 1 | PR met PoC-mitigatie gemerged (of review-ready)                                              | ❌ Open |     |
| 2 | Pentest voor en na mitigatie gedocumenteerd                                                  | ❌ Open |     |
| 3 | DPIA-check aanwezig                                                                          | ❌ Open |     |
| 4 | Concept-auditrapport heeft alle secties (ook al zijn ze nog niet volledig uitgeschreven)     | ❌ Open |     |
| 5 | Concept-presentatie heeft minimaal een structuur met titels per slide                        | ❌ Open |     |

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
