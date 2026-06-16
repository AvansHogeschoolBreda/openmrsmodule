# Analyse Onderhoudbaarheid: OpenMRS idgen module

**Groep: C6**

| Naam:             | Nummer: |
| ----------------- | ------- |
| Raf van Hooijdonk | 2230382 |
| Rowen Albers      | 2227982 |
| Simon Eulenpesch  | 2226731 |
| Sinan Sagir       | 2235816 |

---

## 1. Scope en methodiek

**Module:** OpenMRS ID Generation Module (idgen) v4.13.0
**Broncode:** `openmrs-module-idgen/api/src/main/java` en `omod/src/main/java`
**Meetdatum:** 12/06/2026
**Tools:** SonarCloud (statische analyse), JaCoCo (coverage), handmatige broncode-inspectie

> **Belangrijk: dit zijn baselinecijfers vóór de PoC.** Alle metrieken in dit document beschrijven de toestand op 12/06/2026. De code is daarna gerefactord in commit 303c735 (15/06/2026, 201 code quality issues). De huidige code op `main` is dus al verbeterd ten opzichte van deze cijfers; een verse SonarCloud-meting toont de na-waarden (Deel 6 in `docs/checklist.md`). Voorbeeld: de hieronder genoemde security rating C (hardcoded password in `IdgenModuleActivator`) is in de huidige code opgelost.

De analyse is uitgevoerd langs vier ISO 25010-kwaliteitskenmerken voor onderhoudbaarheid: analyseerbaarheid, wijzigbaarheid, testbaarheid en modulariteit. Per kenmerk zijn passende metrieken gemeten en gedocumenteerd.

**Databronnen:**

- SonarCloud Issues-pagina: volledig gescraped (alle 204 open issues)
- SonarCloud Measures-pagina: Coverage, Cognitive Complexity, Duplications, Code Smells, Technical Debt Ratio, LOC per bestand
- SonarCloud Overview-pagina: quality gate status, snapshot per kwaliteitsdimensie
- JaCoCo XML-rapporten gegenereerd via `mvn verify`
- Handmatige broncode-inspectie (import-telling, vertakkingsanalyse)

---

## 2. Projectomvang

| Metriek                              | api/src                               | omod/src                           | Totaal              |
| ------------------------------------ | ------------------------------------- | ---------------------------------- | ------------------- |
| Productiebestanden                   | 37                                    | 20                                 | 57                  |
| Testbestanden                        | n.v.t.                                | n.v.t.                             | 29 (aparte telling) |
| Lines of Code (SonarCloud)           | 2.098                                 | 2.214                              | 4.312               |
| Grootste bestand (LOC)               | `BaseIdentifierSourceService` (272) | `IdentifierSourceResource` (498) | zie per module      |
| Open issues                          | 94                                    | 110                                | 204                 |
| Nieuwe issues (new code)             | 34                                    | 31                                 | 65                  |
| Security vulnerability               | 1                                     | 0                                  | 1                   |
| Reliability issues                   | 2                                     | 0                                  | 2                   |
| Maintainability issues (code smells) | 91                                    | 110                                | 201                 |
| Security Hotspots (unreviewed)       | 0                                     | 0                                  | 0                   |
| Quality Gate Status                  | n.v.t.                                | n.v.t.                             | **Gefaald**   |
| Analyse-datum                        | 12/06/2026                            | 12/06/2026                         | 12/06/2026          |

> De Quality Gate faalt omdat "Coverage on New Code" (50%) onder de drempel van 60% valt.

---

## 3. Complexiteit (NFR-1, NFR-2)

### 3.1 Meetmethode

Cognitive Complexity is gemeten via SonarCloud (rule `java:S3776`). Dit is een modernere maatstaf dan cyclomatische complexiteit: het weegt nestingdiepte zwaarder en sluit eenvoudige lineaire patronen (bijv. switch op enum) uit. De SonarCloud-drempel is 15 per methode. Aanvullend zijn per klasse de totale Cognitive Complexity en LOC uitgelezen via de Measures-pagina.

### 3.2 Totale Cognitive Complexity per bestand (top 10)

| Klasse                                     | Module | LOC             | CC (totaal)   | CC/LOC | Beoordeling             |
| ------------------------------------------ | ------ | --------------- | ------------- | ------ | ----------------------- |
| `IdentifierSourceResource`               | omod   | 498             | **223** | 0.45   | Kritiek: God Class      |
| `IdentifierSourceController`             | omod   | 243             | 46            | 0.19   | Hoog risico             |
| `AutoGenerationOptionResource`           | omod   | 256             | 36            | 0.14   | Hoog risico             |
| `BaseIdentifierSourceService`            | api    | 272             | 32            | 0.12   | Hoog risico             |
| `SequentialIdentifierGeneratorValidator` | api    | 56              | 27            | 0.48   | Kritiek: hoge densiteit |
| `IdgenEditPatientIdentifiersController`  | omod   | 104             | 25            | 0.24   | Hoog risico             |
| `SequentialIdentifierGenerator`          | api    | 136             | 25            | 0.18   | Hoog risico             |
| `LuhnModNIdentifierValidator`            | api    | 87              | 23            | 0.26   | Hoog risico             |
| `AutoGenerationOptionController`         | omod   | 95              | 16            | 0.17   | Matig                   |
| `LogEntryResource`                       | omod   | 151             | 16            | 0.11   | Matig                   |
| **Totaal project**                   | beide  | **4.312** | **668** | 0.15   | n.v.t.                  |

### 3.3 Brain Methods (SonarCloud: Cognitive Complexity > 15 per methode)

| Klasse                                     | Methode (regel) | CC            | Max | LOC-methode | Nesting | Variabelen | Herstelkost |
| ------------------------------------------ | --------------- | ------------- | --- | ----------- | ------- | ---------- | ----------- |
| `IdentifierSourceResource`               | L142            | **101** | 15  | 165         | 3       | 37         | 1h 31min    |
| `IdentifierSourceResource`               | L318            | **106** | 15  | 144         | 4       | 39         | 1h 36min    |
| `SequentialIdentifierGeneratorValidator` | L41             | 27            | 15  | 56          | 3      | 8         | 17min       |
| `IdgenEditPatientIdentifiersController`  | L44             | 16            | 15  | 104         | 2      | 6         | 6min        |

`IdentifierSourceResource` bevat twee methoden die de CC-limiet met een factor 7 overschrijden. SonarCloud classificeert beide als "Brain Method": methoden zo complex dat ze vrijwel niet te begrijpen zijn zonder volledige kennis van de klasse. De klasse zelf heeft 498 LOC en een totale CC van 223, wat overeen komt met de zwaarste outlier in de Maintainability Overview (>5h technische schuld, ~450+ LOC op het scatterplot).

### 3.4 Onderbouwing

`IdentifierSourceResource` is de grootste enkelvoudige maintainability-risicofactor in de codebase. Een methode met CC 106 is in de praktijk onmogelijk volledig te unit-testen. Bij elke wijziging aan dit bestand is het risico op regressiefouten extreem hoog. Bovendien heeft het bestand 148 uncovered conditions (zie sectie 5), wat de testlast aantoont die de complexiteit creëert. Dit bestand is het primaire doel voor refactoring in de PoC.

---

## 4. Duplicatie (NFR-3)

### 4.1 Meetmethode

Gemeten via SonarCloud (Measures > Duplications, per bestand).

### 4.2 Projectbrede resultaten

| Metriek                     | api/src | omod/src | Totaal | NFR-drempel | Status       |
| --------------------------- | ------- | -------- | ------ | ----------- | ------------ |
| Duplicated Lines (%)        | 2.6%    | 10.8%    | 5.8%   | 3%          | Niet voldaan |
| Duplicated Lines (absoluut) | 112     | 293      | 405    | n.v.t.      | n.v.t.       |
| Duplicated Blocks           | 4       | 15       | 19     | n.v.t.      | n.v.t.       |
| Duplicated Files            | 3       | 8        | 11     | n.v.t.      | n.v.t.       |
| New code duplicatie         | 0.0%    | 0.0%     | 0.0%   | n.v.t.      | Voldaan      |

### 4.3 Duplicatie per bestand (bestanden met gedupliceerde blokken)

| Bestand                                                                                                                                                                            | Module   | Dup. Blokken | Oorzaak                                                |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- | ------------ | ------------------------------------------------------ |
| `IdentifierSourceResource`                                                                                                                                                       | omod     | 4            | Magic strings + herhaalde request-parsing              |
| `RemoteIdentifierSourceResourceHandler`                                                                                                                                          | omod     | 3            | Magic strings (display, identifierType, password)      |
| `SequentialIdentifierGeneratorResourceHandler`                                                                                                                                   | omod     | 3            | Magic strings (baseCharacterSet, prefix, suffix, etc.) |
| `LuhnModNIdentifierValidator`                                                                                                                                                    | api      | 2            | Identieke validatielogica gekopieerd                   |
| `AutoGenerationOption`, `LogEntry`, `LogEntryResource`, `LogEntrySearchHandler`, `IdentifierResource`, `SequenceIdentifierResource`, `IdentifierPoolResourceHandler` | omod/api | elk 1        | Diverse herhaalde patronen                             |

### 4.4 Oorzaak: magic string literals in omod-laag

De dominante oorzaak van de 15 gedupliceerde blokken in omod/src is het herhaald gebruik van string literals zonder constante-definitie. Voorbeelden van de ergste overtreders:

| Bestand                                          | Literal                                                                    | Herhalingen | Herstelkost    |
| ------------------------------------------------ | -------------------------------------------------------------------------- | ----------- | -------------- |
| `AutoGenerationOptionResource`                 | `"source"`                                                               | 10x         | 22min          |
| `AutoGenerationOptionResource`                 | `"identifierType"`                                                       | 9x          | 20min          |
| `SequentialIdentifierGeneratorResourceHandler` | `"baseCharacterSet"`                                                     | 8x          | 18min          |
| `AutoGenerationOptionResource`                 | `"location"`, `"manualEntryEnabled"`, `"automaticGenerationEnabled"` | elk 8x      | elk 18min      |
| `IdentifierSourceResource`                     | `"description"`, `"identifierType"`                                    | 9x en 6x    | 20min en 14min |

### 4.5 Oorzaak: Luhn-validator duplicatie in api-laag

`LuhnMod10`, `LuhnMod25`, `LuhnMod30` zijn elk 7 LOC maar delegeren naar `LuhnModNIdentifierValidator`. De abstracte basisklasse bestaat al maar bevat nog 2 gedupliceerde blokken (87 LOC, CC 23). Dit kan via patroon-extrapolatie worden opgelost.

### 4.6 Onderbouwing

Gedupliceerde code betekent dat een bugfix op meerdere plaatsen doorgevoerd moet worden. In een medische context (patiënt-ID validatie) is dit een onderhoudsrisico. Extractie van magic strings naar constanten in de resource handlers lost zowel de duplicatie als de leesbaarheid in één stap op.

---

## 5. Code coverage (NFR-4, NFR-5)

### 5.1 Meetmethode

Gemeten via JaCoCo, gegenereerd tijdens `mvn verify`, gerapporteerd in SonarCloud (Measures > Coverage, per bestand).

### 5.2 Projectbrede resultaten

| Metriek                   | api/src            | omod/src           | Totaal | NFR-drempel | Status        |
| ------------------------- | ------------------ | ------------------ | ------ | ----------- | ------------- |
| Line coverage             | 54.3%              | 46.9%              | 50.0%  | 70%         | Niet voldaan  |
| Branch/condition coverage | n.v.t. (Free-plan) | n.v.t. (Free-plan) | n.v.t. | 50%         | Niet meetbaar |

### 5.3 Coverage per bestand: kritieke klassen (0% coverage)

| Bestand                                    | Module | Coverage       | Uncovered Lines | Uncovered Cond. |
| ------------------------------------------ | ------ | -------------- | --------------- | --------------- |
| `SequentialIdentifierGeneratorValidator` | api    | **0.0%** | 29              | **22**    |
| `IdentifierSourceEditor`                 | api    | **0.0%** | 14              | 4               |
| `IdentifierTableHeaderExtension`         | omod   | **0.0%** | 22              | 14              |
| `RemoteIdentifierSourceValidator`        | api    | **0.0%** | 6               | 2               |
| `IdentifierSourceValidator`              | api    | **0.0%** | 6               | 2               |
| `IdgenModuleActivator`                   | api    | **0.0%** | 9               | 0               |
| `IdgenConstants`                         | api    | **0.0%** | 1               | 0               |
| `ExceptionUtils`                         | api    | **0.0%** | 6               | 10              |
| `AdminList`                              | omod   | **0.0%** | 8               | 0               |

`SequentialIdentifierGeneratorValidator` heeft 0% coverage met 22 uncovered conditions; dit is de validator die ID-formaten valideert. In een medisch systeem is dit een ernstig testgat.

### 5.4 Coverage per bestand: laagste non-zero coverage

| Bestand                                   | Module | Coverage | Uncovered Lines | Uncovered Cond. |
| ----------------------------------------- | ------ | -------- | --------------- | --------------- |
| `IdgenEditPatientIdentifiersController` | omod   | 2.2%     | 63              | 28              |
| `IdentifierResource`                    | omod   | 2.8%     | 31              | 4               |
| `AutoGenerationOptionController`        | omod   | 3.3%     | 38              | 20              |
| `SequenceIdentifierResource`            | omod   | 3.8%     | 19              | 6               |
| `LocationBasedSuffixProvider`           | api    | 5.6%     | 22              | 12              |
| `LogEntryController`                    | omod   | 8.3%     | 18              | 4               |
| `IdentifierSourceResource`              | omod   | 58.4%    | 93              | **148**   |

`IdentifierSourceResource` heeft ondanks een relatief hoge coverage 148 uncovered conditions, direct gevolg van de extreem complexe Brain Methods.

### 5.5 Bestanden met 100% coverage

Zeven bestanden bereiken 100% coverage: `IdentifierSource`, `IdentifierSourceProcessor`, `LuhnMod10IdentifierValidator`, `LuhnMod25IdentifierValidator`, `LuhnMod30IdentifierValidator`, `PrefixProvider`, `SuffixProvider`. Dit zijn echter kleine interface- of wrapper-klassen (4 tot 7 LOC).

### 5.6 Onderbouwing

Het globale beeld is dat de kritieke logica (validator, controller, resource) vrijwel ongetest is, terwijl eenvoudige interfaces volledig gedekt zijn. Om van 50% naar 70% te komen moeten primair de nul-coverage klassen aangepakt worden, met name `SequentialIdentifierGeneratorValidator` (de ID-formaat-validator met 22 uncovered conditions).

---

## 6. Koppeling en cohesie (coupling/cohesion)

### 6.1 Meetmethode

Gemeten via het aantal imports per klasse als proxy voor efferente koppeling (fan-out). SonarCloud Free-plan biedt geen directe koppelingsmetrieken.

### 6.2 Resultaten

| Klasse                                  | Module | LOC | Imports (proxy) | Beoordeling    |
| --------------------------------------- | ------ | --- | --------------- | -------------- |
| `HibernateIdentifierSourceDAO`        | api    | 231 | 28              | Hoog gekoppeld |
| `BaseIdentifierSourceService`         | api    | 272 | 27              | Hoog gekoppeld |
| `IdentifierSourceService` (interface) | api    | 119 | 19              | Matig          |
| `IdentifierSourceDAO` (interface)     | api    | 55  | 13              | Acceptabel     |
| `SequentialIdentifierGenerator`       | api    | 136 | 9               | Acceptabel     |

### 6.3 Onderbouwing

`HibernateIdentifierSourceDAO` en `BaseIdentifierSourceService` zijn sterk gekoppeld aan OpenMRS-interne klassen (Hibernate SessionFactory, Context, Spring). Dit is deels onvermijdelijk in een OpenMRS-module, maar `BaseIdentifierSourceService` heeft 272 LOC met CC 32, een klassiek God Class-antipatroon. Een splitsing in een aparte query-klasse en service-klasse zou koppeling en complexiteit beiden verlagen.

---

## 7. Code smells en technische schuld (NFR-6, NFR-7)

### 7.1 Overzicht per module

| Metriek                               | api/src | omod/src | Totaal         | Beoordeling       |
| ------------------------------------- | ------- | -------- | -------------- | ----------------- |
| Maintainability Rating (overall code) | A       | A        | **A**    | Voldoet aan NFR-6 |
| Technical Debt Ratio (overall code)   | n.v.t. | n.v.t.  | **1.4%** | Voldoet aan NFR-7 |
| Code smells (totaal)                  | 91      | 110      | 201            | Verbeterpunt      |
| Code smells (nieuwe code)             | 34      | 31       | 65             | Verbeterpunt      |
| Vulnerability                         | 1       | 0        | 1              | Zie 7.5           |
| Reliability issues                    | 2       | 0        | 2              | Zie 7.6           |

### 7.2 Technische schuld op nieuwe code

| Metriek                            | api/src     | omod/src | Totaal                  |
| ---------------------------------- | ----------- | -------- | ----------------------- |
| Toegevoegde technische schuld      | 3h 50min    | 1h 41min | **5h 31min**      |
| Technical Debt Ratio (new code)    | 23.3%       | 0.0%     | **23.3%**         |
| Maintainability Rating (new code)  | **D** | A        | **D**             |
| Totale inspanning alle open issues | n.v.t.     | n.v.t.  | **3 dagen 7 uur** |

De overall Technical Debt Ratio van **1.4%** voldoet aan NFR-7 (drempel 10%). Op nieuwe code echter is de ratio 23.3% (api/src); toekomstige PRs moeten actief code smells voorkomen.

### 7.3 Code smells per bestand (top 10)

| Bestand                                          | Module | Code Smells  | Technische schuld ratio | Meest voorkomende issues                                 |
| ------------------------------------------------ | ------ | ------------ | ----------------------- | -------------------------------------------------------- |
| `IdentifierSourceResource`                     | omod   | **30** | 2.0%                    | Magic strings, Brain Methods, merge if-statements        |
| `IdentifierSourceController`                   | omod   | 12           | 1.8%                    | Generic exceptions, deprecated API, nested try           |
| `AutoGenerationOptionResource`                 | omod   | 11           | 1.5%                    | Magic strings, diamond operator                          |
| `IdgenEditPatientIdentifiersController`        | omod   | 10           | 0.6%                    | Diamond operator, Brain Method                           |
| `IdentifierPoolResourceHandler`                | omod   | 9            | 1.3%                    | Magic strings, diamond operator                          |
| `SequentialIdentifierGeneratorResourceHandler` | omod   | 9            | 2.2%                    | Magic strings, diamond operator                          |
| `BaseIdentifierSourceService`                  | api    | 8            | 0.7%                    | Generic exceptions, diamond operator, java.time          |
| `IdgenUtil`                                    | api    | 7            | 3.5%                    | Generic exceptions, unnecessary cast, try-with-resources |
| `SequentialIdentifierGenerator`                | api    | 7            | 2.2%                    | Variable hiding, merge if-statements, deprecated API     |
| `IdentifierPool`                               | api    | 6            | 1.9%                    | Generic exceptions, diamond operator                     |

### 7.4 Top-5 issue categorieën (op basis van volledige issues-scan)

| Categorie (SonarCloud rule)                          | Aantal | Ernst          | Eenvoudig te fixen?                      |
| ---------------------------------------------------- | ------ | -------------- | ---------------------------------------- |
| Magic string literals (`java:S1192`)               | ~35    | Critical/Major | Ja: constant extraheren                  |
| Diamond operator `<>` (`java:S2293`)             | ~35    | Minor          | Ja: automatisch refactorbaar             |
| Generic exception (`java:S112`)                    | ~15    | Major          | Deels: specifieke exception-klasse nodig |
| Brain Method / Cognitive Complexity (`java:S3776`) | 3      | Critical       | Nee: architecturele refactoring vereist  |
| Unused private fields/variables (`java:S1068`)     | ~8     | Major          | Ja: verwijderen                          |
| Lege methode-bodies (`java:S1186`)                 | 4      | Critical       | Ja: comment of implementatie             |
| Multi-threading (`java:S2696`)                     | 4      | Critical       | Ja: methode static maken                 |
| `@Override` ontbreekt (`java:S1161`)             | 6      | Major          | Ja: annotatie toevoegen                  |
| `java.time` API (`java:S6813`)                   | ~10    | Info           | Ja: Date vervangen door LocalDate        |
| `Thread.sleep()` in tests (`java:S2925`)         | 3      | Major          | Ja: testclock injecteren                 |

### 7.5 Security vulnerability

De 1 vulnerability (rating C) betreft `IdgenModuleActivator.java` L50: `'PASSWORD' detected in this expression, review this potentially hard-coded password`. SonarCloud classificeert dit als Major vulnerability (rule: `java:S2068`). Het veld `REGISTRY_API_PASSWORD` bevat een hardcoded credential. Het is tevens een ongebruikt veld (`java:S1068`), dus de fix is verwijdering. Dit lost de security rating C op.

### 7.6 Reliability issues

Beide reliability issues bevinden zich in testcode: `IdentifierSourceServiceTest.java` L340 en L357 `Do not use the system clock in tests` (Info-niveau). Ze veroorzaken potentieel testflakiness bij tijdgevoelige scenario's. Fix: injecteren van een `java.time.Clock` via constructor.

### 7.7 Multi-threading risico (Critical severity)

`LocationBasedPrefixProvider` (L73, L78) en `LocationBasedSuffixProvider` (L73, L78) bevatten elk twee Critical code smells: `Make the enclosing method "static" or remove this set`. Een non-static methode past een instantievariabele van het type `Set` aan zonder synchronisatie. In een multi-threaded OpenMRS-omgeving (meerdere HTTP-requests tegelijk) leidt dit tot race conditions bij ID-generatie met locatie-gebaseerde prefixen of suffixen. De technische schuld ratio van beide bestanden is 3.6%, de hoogste in de codebase. Fix: methode static maken of het `Set`-veld verwijderen.

---

## 8. Samenvatting en prioritering

### 8.1 Volledige metrics-tabel

| Metriek                          | Baseline (12/06/2026)         | Doel (na PoC) | Status       |
| -------------------------------- | ----------------------------- | ------------- | ------------ |
| Quality Gate                     | **Gefaald**             | Geslaagd      | Niet voldaan |
| Line coverage (totaal)           | 50.0%                         | 70%           | Niet voldaan |
| Line coverage (api)              | 54.3%                         | 70%           | Niet voldaan |
| Line coverage (omod)             | 46.9%                         | 70%           | Niet voldaan |
| Duplicated Lines (%)             | 5.8%                          | 3%            | Niet voldaan |
| Duplicated Lines (omod)          | 10.8%                         | < 5%          | Niet voldaan |
| Duplicated Blocks                | 19                            | < 5           | Niet voldaan |
| Cognitive Complexity (totaal)    | 668                           | < 400         | Niet voldaan |
| Brain Methods (CC > 15)          | 3 methoden (CC 101, 106, 27)  | 0             | Niet voldaan |
| Security Rating                  | **C** (1 vulnerability) | A             | Niet voldaan |
| Maintainability Rating (overall) | **A**                   | A             | Voldaan      |
| Technical Debt Ratio (overall)   | **1.4%**                | < 10%         | Voldaan      |
| Reliability Rating               | **A**                   | A             | Voldaan      |
| Security Hotspots (unreviewed)   | **0**                   | 0             | Voldaan      |
| Open issues (totaal)             | 204                           | < 150         | Niet voldaan |

### 8.2 Geprioriteerde verbeteracties voor PoC

| #  | Actie                                        | Bestanden                                                             | Impact               | Inspanning          | Prioriteit        |
| -- | -------------------------------------------- | --------------------------------------------------------------------- | -------------------- | ------------------- | ----------------- |
| 1  | Multi-threading fix: static methode          | `LocationBasedPrefixProvider`, `LocationBasedSuffixProvider`      | Veiligheid productie | Klein               | **Kritiek** |
| 2  | Security fix: hardcoded password verwijderen | `IdgenModuleActivator`                                              | Rating C -> A        | Klein               | **Kritiek** |
| 3  | Brain Methods refactoren                     | `IdentifierSourceResource` L142, L318                               | CC 223 -> < 50       | Groot (3h+)         | **Hoog**    |
| 4  | Magic strings extraheren als constanten      | Resource handlers (omod)                                              | Dup 10.8% -> < 2%    | Middel              | **Hoog**    |
| 5  | Unit tests schrijven voor validator          | `SequentialIdentifierGeneratorValidator`                            | Coverage +%          | Middel              | **Hoog**    |
| 6  | Unit tests voor nul-coverage klassen         | `IdentifierSourceEditor`, `RemoteIdentifierSourceValidator`, etc. | Coverage +%          | Middel              | **Hoog**    |
| 7  | Luhn-validators samenvoegen                  | `LuhnMod10`, `LuhnMod25`, `LuhnMod30`                           | Dup api -2 blokken   | Klein               | **Middel**  |
| 8  | Diamond operator toevoegen (~35 stuks)       | Verspreid                                                             | Issues -35           | Klein (automatisch) | **Laag**    |
| 9  | `@Override` annotaties toevoegen           | 6 bestanden                                                           | Issues -6            | Klein               | **Laag**    |
| 10 | `java.time` API migratie                   | ~10 bestanden                                                         | Modernisering        | Middel              | **Laag**    |

De prioritering is direct herleidbaar naar de meetresultaten: acties 1 t/m 4 volgen uit de complexiteits-, duplicatie- en security-bevindingen (secties 3, 4 en 7), en acties 5 en 6 volgen uit de testresultaten in `Groep_6_Testplan.md` (nulmeting: validator 0% coverage, meerdere nul-coverage klassen). De NFR-doelen staan in `Groep_6_Non-Functional-Requirements.md`. De realisatie is uitgevoerd als PoC in commit 303c735 (15/06/2026, 201 code quality issues), met de ontwerpkeuzes onderbouwd in `Groep_6_Refactoring-Onderbouwing.md`.

---

## 9. Bronnen

- [ISO 25010:2011 - Systems and software quality models](https://www.iso.org/standard/35733.html)
- [SonarCloud - AvansHogeschoolBreda/openmrsmodule](https://sonarcloud.io/project/overview?id=AvansHogeschoolBreda_openmrsmodule)
- [McCabe, T.J. (1976). A Complexity Measure. IEEE Transactions on Software Engineering](https://doi.org/10.1109/TSE.1976.233837)
- [Martin Fowler - Refactoring: Improving the Design of Existing Code (2018)](https://martinfowler.com/books/refactoring.html)
- [JaCoCo - Java Code Coverage Library](https://www.jacoco.org/jacoco/)
- [SonarSource - Cognitive Complexity: A new way of measuring understandability (2023)](https://www.sonarsource.com/docs/CognitiveComplexity.pdf)
