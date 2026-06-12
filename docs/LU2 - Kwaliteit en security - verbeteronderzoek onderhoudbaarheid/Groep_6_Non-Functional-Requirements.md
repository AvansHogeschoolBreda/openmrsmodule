# Non-Functional Requirements: Onderhoudbaarheid

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## 1. Doel en scope

Dit document legt de non-functional requirements (NFR's) vast voor de onderhoudbaarheid van de `idgen`-module. De eisen zijn gebaseerd op de ISO 25010-kwaliteitskenmerken voor onderhoudbaarheid (maintainability) en worden automatisch gemeten via SonarCloud, gekoppeld aan de CI-pipeline.

**Scope:** Java-broncode in `openmrs-module-idgen/` (backend). JSX/JavaScript frontend valt buiten de primaire meetscope, maar wordt kwalitatief beoordeeld.

**Niet in scope:** OpenMRS core, databaseserver, netwerk-infrastructuur.

---

## 2. Meetmethode en tooling

| Tool           | Rol                                                                 | Integratie                                 |
| -------------- | ------------------------------------------------------------------- | ------------------------------------------ |
| SonarCloud     | Statische analyse: bugs, code smells, duplicatie, security hotspots | GitHub Actions workflow `sonarcloud.yml` |
| JaCoCo         | Code coverage meten per Maven-build                                 | `pom.xml` plugin + CI                    |
| Maven Surefire | Testuitvoering en rapport genereren                                 | `ci.yml` (bestaand)                      |

De CI-pipeline **faalt** (exit code ≠ 0) als de SonarCloud Quality Gate niet slaagt. Dit is geconfigureerd via de `sonarcloud.yml` workflow met de stap `sonar:sonar` en de `Wait for Quality Gate`-actie.

---

## 3. Non-functional requirements

### 3.1 Cyclomatische complexiteit

| ID    | Eis                                                                          | Drempelwaarde | Meting     | Prioriteit |
| ----- | ---------------------------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-1 | Gemiddelde cyclomatische complexiteit per methode mag niet hoger zijn dan 10 | ≤ 10         | SonarCloud | Hoog       |
| NFR-2 | Geen enkele methode mag een complexiteit van 15 of hoger hebben              | < 15          | SonarCloud | Hoog       |

**Onderbouwing:** De huidige module heeft een totale cyclomatische complexiteit van 949 over 151 bestanden (≈ 6,3 gemiddeld). Methoden met complexiteit ≥ 15 zijn moeilijk te testen en foutgevoelig. De drempelwaarde van 10 is een industrie-standaard (McCabe, 1976) en sluit aan bij de SonarCloud default quality gate.

---

### 3.2 Duplicatie

| ID    | Eis                                                            | Drempelwaarde | Meting     | Prioriteit |
| ----- | -------------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-3 | Percentage gedupliceerde coderegels mag niet hoger zijn dan 3% | ≤ 3%         | SonarCloud | Middel     |

**Onderbouwing:** Duplicatie vergroot onderhoudslast: een bug in gedupliceerde code moet op meerdere plekken gerepareerd worden. De 5%-drempel is de SonarCloud-standaard voor de "Sonar Way" quality gate.

---

### 3.3 Code coverage

| ID    | Eis                                                       | Drempelwaarde | Meting              | Prioriteit |
| ----- | --------------------------------------------------------- | ------------- | ------------------- | ---------- |
| NFR-4 | Line coverage van de Java-broncode moet minimaal 70% zijn | ≥ 70%        | JaCoCo / SonarCloud | Hoog       |
| NFR-5 | Branch coverage moet minimaal 50% zijn                    | ≥ 50%        | JaCoCo / SonarCloud | Middel     |

**Onderbouwing:** De huidige codebase heeft beperkte testdekking. Een minimum van 60% line coverage borgt dat kritieke paden (ID-generatie, validatie, pool-beheer) gedekt zijn door regressietests. Voor legacy-code met lage beginwaarde geldt dit als streefnorm na het PoC.

---

### 3.4 Code smells en technical debt

| ID    | Eis                                                                            | Drempelwaarde                 | Meting     | Prioriteit |
| ----- | ------------------------------------------------------------------------------ | ----------------------------- | ---------- | ---------- |
| NFR-6 | Nieuw geïntroduceerde code smells per PR mogen de rating niet onder B brengen | ≥ B (Maintainability Rating) | SonarCloud | Hoog       |
| NFR-7 | Technical debt ratio (remediation cost / development cost) ≤ 10%              | ≤ 10%                        | SonarCloud | Middel     |

**Onderbouwing:** SonarCloud's Maintainability Rating (A t/m E) geeft direct inzicht in de kwaliteit van nieuwe code. Door de rating op B of hoger te houden voor iedere PR wordt regressie in onderhoudbaarheid geblokkeerd.

---

### 3.5 Bugs en security hotspots (kwaliteitspoort)

| ID    | Eis                                                             | Drempelwaarde | Meting     | Prioriteit |
| ----- | --------------------------------------------------------------- | ------------- | ---------- | ---------- |
| NFR-8 | Geen nieuwe bugs van type Blocker of Critical in nieuwe code    | 0             | SonarCloud | Hoog       |
| NFR-9 | Alle nieuwe security hotspots moeten zijn beoordeeld (reviewed) | 0 unreviewed  | SonarCloud | Hoog       |

---

## 4. Quality Gate configuratie

De volgende SonarCloud quality gate-condities worden ingesteld (of gelden via "Sonar Way" default):

| Conditie                          | Operator   | Drempelwaarde | Scope       |
| --------------------------------- | ---------- | ------------- | ----------- |
| Coverage on New Code              | <          | 60%           | Nieuwe code |
| Duplicated Lines on New Code      | >          | 5%            | Nieuwe code |
| Maintainability Rating (New Code) | worse than | B             | Nieuwe code |
| Reliability Rating (New Code)     | worse than | A             | Nieuwe code |
| Security Rating (New Code)        | worse than | A             | Nieuwe code |
| Security Hotspots Reviewed        | <          | 100%          | Nieuwe code |

De quality gate is gekoppeld aan de `sonarcloud.yml` workflow. Als de quality gate faalt, faalt de CI-run, en de PR kan niet gemerged worden (branch protection).

---

## 5. Verantwoording toolkeuze: SonarCloud

| Criterium          | SonarCloud                              | Qodana                            |
| ------------------ | --------------------------------------- | --------------------------------- |
| Prijs              | Gratis voor publieke repos              | Gratis voor open source (beperkt) |
| Maven-integratie   | Native (`sonar-maven-plugin`)         | Via Gradle/Maven mogelijk         |
| GitHub Actions     | Officiële action beschikbaar           | Docker-gebaseerd                  |
| PR-decoratie       | Inline comments op PR                   | Rapport als artifact              |
| Quality Gate in CI | Ingebouwd via `Wait for Quality Gate` | Manueel uitleggen                 |
| **Keuze**    | ✅                                      | ❌                                |

SonarCloud is gekozen vanwege de naadloze GitHub-integratie, de officiële Maven-plugin en de mogelijkheid om de quality gate direct als CI-blocker in te stellen.

---

## 6. Baseline meting (vóór verbetering)

> Baseline gemeten op 12/06/2026 via SonarCloud (eerste groene run). Zie ook `Groep_6_Analyse-Onderhoudbaarheid.md` voor de volledige onderbouwing per metriek.

| Metriek                        | Baseline (voor) | Doel (na PoC) | Haalbaarheid                              |
| ------------------------------ | --------------- | ------------- | ----------------------------------------- |
| Percentage duplicatie          | 5.8%            | ≤ 3%         | Refactoring gedupliceerde klassen         |
| Line coverage                  | 50.0%           | ≥ 70%        | Extra unit tests op kernlogica            |
| Maintainability Rating         | A               | A (behouden)  | Geen regressie introduceren               |
| Security Rating                | C               | A             | De 1 open security issue oplossen         |
| Reliability Rating             | A               | A (behouden)  | Geen regressie introduceren               |
| Aantal open issues             | 204             | ≤ 150        | ~54 code smells wegwerken via refactoring |
| Security Hotspots (unreviewed) | 0               | 0             | Blijft 0 door quality gate                |

