# Non-Functional Requirements — Onderhoudbaarheid

**Project:** OpenMRS ID Generation Module (idgen)
**Groep:** C6 — Raf van Hooijdonk, Rowen Albers, Simon Eulenpesch, Sinan Sagir
**Versie:** 1.0
**Datum:** 2026-06-12
**Kader:** LU2 Verbeteronderzoek Onderhoudbaarheid — Opdrachtonderdeel 1

---

## 1. Doel en scope

Dit document legt de non-functional requirements (NFR's) vast voor de onderhoudbaarheid van de `idgen`-module. De eisen zijn gebaseerd op de ISO 25010-kwaliteitskenmerken voor onderhoudbaarheid (maintainability) en worden automatisch gemeten via SonarCloud, gekoppeld aan de CI-pipeline.

**Scope:** Java-broncode in `openmrs-module-idgen/` (backend). JSX/JavaScript frontend valt buiten de primaire meetscope, maar wordt kwalitatief beoordeeld.

**Niet in scope:** OpenMRS core, databaseserver, netwerk-infrastructuur.

---

## 2. Meetmethode en tooling

| Tool        | Rol                                                    | Integratie              |
| ----------- | ------------------------------------------------------ | ----------------------- |
| SonarCloud  | Statische analyse: bugs, code smells, duplicatie, security hotspots | GitHub Actions workflow `sonarcloud.yml` |
| JaCoCo      | Code coverage meten per Maven-build                    | `pom.xml` plugin + CI  |
| Maven Surefire | Testuitvoering en rapport genereren                 | `ci.yml` (bestaand)     |

De CI-pipeline **faalt** (exit code ≠ 0) als de SonarCloud Quality Gate niet slaagt. Dit is geconfigureerd via de `sonarcloud.yml` workflow met de stap `sonar:sonar` en de `Wait for Quality Gate`-actie.

---

## 3. Non-functional requirements

### 3.1 Cyclomatische complexiteit

| ID    | Eis                                                                          | Drempelwaarde | Meting       | Prioriteit |
| ----- | ---------------------------------------------------------------------------- | ------------- | ------------ | ---------- |
| NFR-1 | Gemiddelde cyclomatische complexiteit per methode mag niet hoger zijn dan 10 | ≤ 10          | SonarCloud   | Hoog       |
| NFR-2 | Geen enkele methode mag een complexiteit van 15 of hoger hebben              | < 15          | SonarCloud   | Hoog       |

**Onderbouwing:** De huidige module heeft een totale cyclomatische complexiteit van 949 over 151 bestanden (≈ 6,3 gemiddeld). Methoden met complexiteit ≥ 15 zijn moeilijk te testen en foutgevoelig. De drempelwaarde van 10 is een industrie-standaard (McCabe, 1976) en sluit aan bij de SonarCloud default quality gate.

---

### 3.2 Duplicatie

| ID    | Eis                                                       | Drempelwaarde | Meting     | Prioriteit |
| ----- | --------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-3 | Percentage gedupliceerde coderegels mag niet hoger zijn dan 5% | ≤ 5%     | SonarCloud | Middel     |

**Onderbouwing:** Duplicatie vergroot onderhoudslast: een bug in gedupliceerde code moet op meerdere plekken gerepareerd worden. De 5%-drempel is de SonarCloud-standaard voor de "Sonar Way" quality gate.

---

### 3.3 Code coverage

| ID    | Eis                                                              | Drempelwaarde | Meting         | Prioriteit |
| ----- | ---------------------------------------------------------------- | ------------- | -------------- | ---------- |
| NFR-4 | Line coverage van de Java-broncode moet minimaal 60% zijn        | ≥ 60%         | JaCoCo / SonarCloud | Hoog  |
| NFR-5 | Branch coverage moet minimaal 50% zijn                           | ≥ 50%         | JaCoCo / SonarCloud | Middel |

**Onderbouwing:** De huidige codebase heeft beperkte testdekking. Een minimum van 60% line coverage borgt dat kritieke paden (ID-generatie, validatie, pool-beheer) gedekt zijn door regressietests. Voor legacy-code met lage beginwaarde geldt dit als streefnorm na het PoC.

---

### 3.4 Code smells en technical debt

| ID    | Eis                                                                 | Drempelwaarde | Meting     | Prioriteit |
| ----- | ------------------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-6 | Nieuw geïntroduceerde code smells per PR mogen de rating niet onder B brengen | ≥ B (Maintainability Rating) | SonarCloud | Hoog |
| NFR-7 | Technical debt ratio (remediation cost / development cost) ≤ 10%   | ≤ 10%         | SonarCloud | Middel     |

**Onderbouwing:** SonarCloud's Maintainability Rating A–E geeft direct inzicht in de kwaliteit van nieuwe code. Door de rating op B of hoger te houden voor iedere PR wordt regressie in onderhoudbaarheid geblokkeerd.

---

### 3.5 Bugs en security hotspots (kwaliteitspoort)

| ID    | Eis                                                                  | Drempelwaarde | Meting     | Prioriteit |
| ----- | -------------------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-8 | Geen nieuwe bugs van type Blocker of Critical in nieuwe code         | 0             | SonarCloud | Hoog       |
| NFR-9 | Alle nieuwe security hotspots moeten zijn beoordeeld (reviewed)      | 0 unreviewed  | SonarCloud | Hoog       |

---

## 4. Quality Gate configuratie

De volgende SonarCloud quality gate-condities worden ingesteld (of gelden via "Sonar Way" default):

| Conditie                          | Operator | Drempelwaarde | Scope        |
| --------------------------------- | -------- | ------------- | ------------ |
| Coverage on New Code              | <        | 60%           | Nieuwe code  |
| Duplicated Lines on New Code      | >        | 5%            | Nieuwe code  |
| Maintainability Rating (New Code) | worse than | B           | Nieuwe code  |
| Reliability Rating (New Code)     | worse than | A           | Nieuwe code  |
| Security Rating (New Code)        | worse than | A           | Nieuwe code  |
| Security Hotspots Reviewed        | <        | 100%          | Nieuwe code  |

De quality gate is gekoppeld aan de `sonarcloud.yml` workflow. Als de quality gate faalt, faalt de CI-run, en de PR kan niet gemerged worden (branch protection).

---

## 5. Verantwoording toolkeuze: SonarCloud

| Criterium           | SonarCloud                                | Qodana                          |
| ------------------- | ----------------------------------------- | ------------------------------- |
| Prijs               | Gratis voor publieke repos                | Gratis voor open source (beperkt) |
| Maven-integratie    | Native (`sonar-maven-plugin`)             | Via Gradle/Maven mogelijk       |
| GitHub Actions      | Officiële action beschikbaar              | Docker-gebaseerd                |
| PR-decoratie        | Inline comments op PR                     | Rapport als artifact            |
| Quality Gate in CI  | Ingebouwd via `Wait for Quality Gate`     | Manueel uitleggen               |
| **Keuze**           | ✅ Geselecteerd                           | —                               |

SonarCloud is gekozen vanwege de naadloze GitHub-integratie, de officiële Maven-plugin en de mogelijkheid om de quality gate direct als CI-blocker in te stellen.

---

## 6. Baseline meting (vóór verbetering)

> **Actie:** Vóór aanvang van het PoC wordt een baseline-meting uitgevoerd op de `main`-branch. De resultaten worden hier ingevuld na de eerste SonarCloud-run.

| Metriek                          | Baseline (voor) | Doel (na PoC) |
| -------------------------------- | --------------- | ------------- |
| Gemiddelde cyclomatische complexiteit | — invullen —  | ≤ 10          |
| Percentage duplicatie            | — invullen —    | ≤ 5%          |
| Line coverage                    | — invullen —    | ≥ 60%         |
| Branch coverage                  | — invullen —    | ≥ 50%         |
| Maintainability Rating           | — invullen —    | ≥ B           |
| Aantal Blocker/Critical bugs     | — invullen —    | 0 (nieuw)     |

---

## 7. Wijzigingslog

| Datum      | Versie | Wijziging                                      | Door            |
| ---------- | ------ | ---------------------------------------------- | --------------- |
| 2026-06-12 | 1.0    | Initieel document aangemaakt                   | RafvanHooijdonk |
