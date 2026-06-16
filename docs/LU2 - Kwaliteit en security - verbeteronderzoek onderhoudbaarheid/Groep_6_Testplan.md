# Testplan en testresultaten (vóór verbetering)

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## 1. Doel en scope

Dit document legt de teststrategie vast voor de `idgen`-module en documenteert de testresultaten in de uitgangssituatie, dus vóór de verbeteringen uit het PoC. Deze nulmeting dient als referentie. Na de verbeteringen wordt dezelfde testset opnieuw gedraaid om aan te tonen dat de onderhoudbaarheid is verbeterd en dat er geen regressie is opgetreden (zie Deel 6 in `docs/checklist.md`).

**Scope:** de Java-testsuite in `openmrs-module-idgen/api/src/test/java` en `openmrs-module-idgen/omod/src/test/java`.

**Niet in scope:** OpenMRS core, de databaseserver, JSX/JavaScript frontend en netwerk-infrastructuur. Deze vallen buiten de meetscope van de module, gelijk aan `Groep_6_Non-Functional-Requirements.md`.

De teststrategie sluit aan op de geprioriteerde verbeteracties in `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2 (met name actie 5 en 6: unit tests voor de validator en de nul-coverage klassen).

---

## 2. Teststrategie

### 2.1 Testniveaus en testtypen

De module bevat een gelaagde testsuite. Per testtype is een eigen OpenMRS-basisklasse in gebruik, wat het testniveau bepaalt.

| # | Testtype | Niveau | Basisklasse | Wat het test | Tooling |
|---|---|---|---|---|---|
| 1 | Unit test | Geïsoleerd, geen context | `junit.framework.TestCase` / plain JUnit 4 | Pure logica (Luhn-checksum, util-functies) | JUnit 4 |
| 2 | Component/integratietest | Spring-context + in-memory H2-database | `IdgenBaseTest` (verlengt `BaseModuleContextSensitiveTest`) | Service-laag, pool-beheer, audit logging, DB-interactie | JUnit 4 + OpenMRS testframework |
| 3 | REST-resourcetest | Resource-laag geïsoleerd | `BaseDelegatingResourceTest` | (De)serialisatie van REST-representaties | JUnit 4 + webservices.rest |
| 4 | REST web-/controllertest | Volledige web-context | `BaseModuleWebContextSensitiveTest` / `MainResourceControllerTest` | HTTP-endpoints, controllers, searchhandlers | JUnit 4 + Spring MockMVC |
| 5 | Integratietest (IT) | Spring-context + scheduler/taken | `IdgenBaseTest` | Geplande taken en pool-scheduling | JUnit 4 via Failsafe |

De aanwezigheid van vijf onderscheiden testtypen voldoet aan het rubric-criterium "meerdere testtypen" voor het niveau Goed.

### 2.2 Toolketen

| Tool | Rol | Configuratie |
|---|---|---|
| JUnit 4 | Testframework | Testbronnen in `api` en `omod` |
| Maven Surefire | Uitvoering unit/component-tests (`test`-fase) | `api/pom.xml`, `omod/pom.xml` |
| Maven Failsafe | Uitvoering integratietests (`*IT`, `integration-test`-fase) | Standaard OpenMRS parent-pom |
| JaCoCo | Code coverage tijdens `verify` | `api/pom.xml` jacoco-plugin, gerapporteerd aan SonarCloud |
| OpenMRS testframework | In-memory H2-database, Spring-context, fixtures | `IdgenBaseTest`, dataset-XML |

### 2.3 Scope-afbakening: unit-/component-tests versus integratietests

`mvn test` voert via Surefire alleen klassen met het achtervoegsel `*Test` uit (testtypen 1 t/m 4). De drie `*IT`-klassen draaien via Failsafe in de `integration-test`-fase en worden in deze nulmeting daarom niet uitgevoerd. Dit is een bewuste afbakening: de CI-pipeline (`ci-build-test.yml`) draait eveneens `mvn test`, zodat de nulmeting exact overeenkomt met wat de pipeline bewaakt.

---

## 3. Testinventaris

### 3.0 Herkomst van de tests

Het overgrote deel van de testsuite is overgeërfd uit de upstream OpenMRS idgen v4.13.0 broncode, in bulk in de repository geïmporteerd op 08/06/2026. Deze tests heeft groep C6 niet geschreven; ze horen bij de module zelf. Slechts twee testklassen zijn door de groep toegevoegd:

| Testklasse | Tests | Door | Wanneer | Voor |
|---|---|---|---|---|
| `LoggingAuditTest` | 9 | Rowen Albers | 13/06/2026 | Audit logging (Opdracht 5, Deel 4) |
| `SequentialIdentifierGeneratorValidatorTest` | 11 | Simon Eulenpesch | 16/06/2026 | Verantwoording testdekking (sectie 4.5) |

Alle overige testklassen in de tabellen hieronder zijn upstream-tests. Van de 134 tests in de nulmeting (sectie 4) zijn er dus 125 overgeërfd en 9 door de groep geschreven (`LoggingAuditTest`). De validator-test valt buiten de nulmeting en hoort bij sectie 4.5.

De repository telt na de toevoeging van de validator-test 31 testbronnen. Hiervan zijn 4 hulpbestanden (basisklasse, stub, hulptaak en een herbruikte domeinklasse) en geen zelfstandige testcases. De overige 27 zijn testklassen: 20 vormen de nulmeting in sectie 4, 3 zijn uitgesloten (zie sectie 5.3), 3 zijn `*IT` (Failsafe, sectie 2.3) en 1 is de C6-toegevoegde validator-test (sectie 4.5).

### 3.1 api-module (`api/src/test/java`)

| Testklasse | Type | Basisklasse | Uitgevoerd in `mvn test` |
|---|---|---|---|
| `IdgenUtilTest` | Unit | plain JUnit 4 | Ja |
| `LuhnMod10IdentifierValidatorTest` | Unit | plain JUnit 4 | Ja |
| `SequentialIdentifierGeneratorValidatorTest` | Unit (C6-toegevoegd) | plain JUnit 4 | Ja (na nulmeting, zie 4.5) |
| `IdentifierSourceServiceTest` | Component/integratie | `IdgenBaseTest` | Ja |
| `LoggingAuditTest` (C6-toegevoegd) | Component/integratie | `IdgenBaseTest` | Ja |
| `DuplicateIdentifiersPoolComponentTest` | Component/integratie | `IdgenBaseTest` | Ja |
| `RemoteWithLocalPoolIntegrationTest` | Component/integratie | `IdgenBaseTest` | Ja |
| `AutoGenerationOptionEditorTest` | Component/integratie | `IdgenBaseTest` | Ja |
| `RemoteIdentifierSourceProcessorTest` | Unit | `TestCase` | Nee (`@Ignore`, sectie 5.2) |
| `IdentifierSourceServiceLoadTest` | Loadtest | `IdgenBaseTest` | Nee (`@Ignore`, sectie 5.2) |
| `LocationBasedPrefixProviderTest` | Unit | n.v.t. | Nee (pom-exclude, sectie 5.3) |
| `SequentialIdentifierGeneratorTest` | Unit | n.v.t. | Nee (pom-exclude, sectie 5.3) |
| `IdentifierPoolSchedulerIT` | Integratie (IT) | `IdgenBaseTest` | Nee (Failsafe) |
| `SequentialIdentifierGeneratorIT` | Integratie (IT) | `IdgenBaseTest` | Nee (Failsafe) |
| `IdgenTaskIT` | Integratie (IT) | `IdgenBaseTest` | Nee (Failsafe) |

Hulpbestanden (geen testcase): `IdgenBaseTest` (basisklasse), `RemoteIdentifierSourceProcessorStub` (stub), `TestTask` (hulptaak), `CodedOrFreeText` (herbruikte domeinklasse).

### 3.2 omod-module (`omod/src/test/java`)

| Testklasse | Type | Basisklasse | Uitgevoerd in `mvn test` |
|---|---|---|---|
| `AutoGenerationOptionControllerTest` | REST web-/controllertest | `MainResourceControllerTest` | Ja |
| `IdentifierSourceRestControllerTest` | REST web-/controllertest | `MainResourceControllerTest` | Ja |
| `LogEntryControllerTest` | REST web-/controllertest | `MainResourceControllerTest` | Ja |
| `LogEntrySearchHandlerTest` | REST web-/controllertest | `MainResourceControllerTest` | Ja |
| `IdentifierSourceControllerTest` | REST web-/controllertest | `MainResourceControllerTest` | Ja |
| `IdentifierPoolResourceHandlerTest` | REST web-context | `BaseModuleWebContextSensitiveTest` | Ja |
| `RemoteIdentifierSourceResourceHandlerTest` | REST web-context | `BaseModuleWebContextSensitiveTest` | Ja |
| `SequentialIdentifierGeneratorResourceHandlerTest` | REST web-context | `BaseModuleWebContextSensitiveTest` | Ja |
| `AutoGenerationOptionResourceTest` | REST-resourcetest | `BaseDelegatingResourceTest` | Ja |
| `IdentifierSourceResourceTest` | REST-resourcetest | `BaseDelegatingResourceTest` | Ja |
| `LogEntryResourceTest` | REST-resourcetest | `BaseDelegatingResourceTest` | Ja |
| `IdentifierResourceTest` | REST web-/controllertest | `MainResourceControllerTest` | Nee (pom-exclude, sectie 5.3) |

---

## 4. Testresultaten (nulmeting)

### 4.1 Uitvoering

Uitgevoerd op 16/06/2026 met de CI-equivalente commando's (zie sectie 6). De `omod`-tests vereisen dat de `api`-artifact eerst gepackaged is; daarom wordt eerst `package -DskipTests` gedraaid en daarna `test`, identiek aan `ci-build-test.yml`.

### 4.2 Resultaat per module

| Module | Testklassen uitgevoerd | Tests run | Geslaagd | Failures | Errors | Skipped |
|---|---|---|---|---|---|---|
| api | 9 | 41 | 39 | 0 | 0 | 2 |
| omod | 11 | 93 | 93 | 0 | 0 | 0 |
| **Totaal** | **20** | **134** | **132** | **0** | **0** | **2** |

Build-resultaat: **BUILD SUCCESS**. Alle uitgevoerde tests slagen. De 2 skipped tests zijn `@Ignore`-gemarkeerd in de upstream-broncode (sectie 5.2).

### 4.3 Resultaat per testklasse (Surefire)

**api-module:**

| Testklasse | Tests | Failures | Errors | Skipped |
|---|---|---|---|---|
| `IdentifierSourceServiceTest` | 25 | 0 | 0 | 0 |
| `LoggingAuditTest` | 9 | 0 | 0 | 0 |
| `IdgenUtilTest` | 1 | 0 | 0 | 0 |
| `LuhnMod10IdentifierValidatorTest` | 1 | 0 | 0 | 0 |
| `AutoGenerationOptionEditorTest` | 1 | 0 | 0 | 0 |
| `DuplicateIdentifiersPoolComponentTest` | 1 | 0 | 0 | 0 |
| `RemoteWithLocalPoolIntegrationTest` | 1 | 0 | 0 | 0 |
| `RemoteIdentifierSourceProcessorTest` | 1 | 0 | 0 | 1 |
| `IdentifierSourceServiceLoadTest` | 1 | 0 | 0 | 1 |

**omod-module:**

| Testklasse | Tests | Failures | Errors | Skipped |
|---|---|---|---|---|
| `IdentifierSourceRestControllerTest` | 19 | 0 | 0 | 0 |
| `LogEntryControllerTest` | 17 | 0 | 0 | 0 |
| `LogEntrySearchHandlerTest` | 17 | 0 | 0 | 0 |
| `AutoGenerationOptionControllerTest` | 11 | 0 | 0 | 0 |
| `IdentifierPoolResourceHandlerTest` | 6 | 0 | 0 | 0 |
| `RemoteIdentifierSourceResourceHandlerTest` | 6 | 0 | 0 | 0 |
| `SequentialIdentifierGeneratorResourceHandlerTest` | 6 | 0 | 0 | 0 |
| `AutoGenerationOptionResourceTest` | 3 | 0 | 0 | 0 |
| `IdentifierSourceResourceTest` | 3 | 0 | 0 | 0 |
| `LogEntryResourceTest` | 3 | 0 | 0 | 0 |
| `IdentifierSourceControllerTest` | 2 | 0 | 0 | 0 |

De ruwe rapporten staan in `api/target/surefire-reports/` en `omod/target/surefire-reports/` (per klasse een `.txt`- en `.xml`-bestand). De CI bewaart deze als artifact `test-reports-{run}` (90 dagen) via `ci-build-test.yml`.

### 4.4 Koppeling aan coverage-baseline

De testsuite slaagt volledig maar dekt de codebase slechts gedeeltelijk: 50,0% line coverage (api 54,3%, omod 46,9%), gemeten met JaCoCo en gerapporteerd in SonarCloud op 12/06/2026. De kritieke logica is ondergetest: `SequentialIdentifierGeneratorValidator` heeft 0% coverage met 22 ongedekte condities. Zie `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 5 voor de volledige coverage-onderbouwing per bestand. Het wegwerken van deze testgaten is verbeteractie 5 en 6 in de prioritering.

### 4.5 Verantwoording testdekking

De nulmeting van 50,0% line coverage is bewust geen tekortkoming maar een verdedigbaar uitgangspunt. De verantwoording bestaat uit twee delen: waar het gat zit en waarom 70% de juiste streefnorm is in plaats van een hogere norm.

#### Waar de 50% zit: twee soorten ongedekte code

De ongedekte code valt uiteen in twee categorieën met een verschillende waarde-kostenverhouding.

**a) Bewust niet getest (lage waarde, hoge kosten):**

| Klasse | Reden geen test toegevoegd |
|---|---|
| `IdgenConstants` | Alleen constanten, 1 regel, geen logica om te valideren |
| `IdgenModuleActivator` | Lifecycle-hook, draait alleen in een echte module-context |
| `IdentifierTableHeaderExtension`, `AdminList` | UI-extensieklassen zonder bedrijfslogica |

Hier 100% halen betekent schijntests schrijven om een getal te halen. Dat verlaagt de onderhoudbaarheid in plaats van die te verhogen.

**b) Echt testgat dat telt (kritiek, ondergetest):**

| Klasse | Risico |
|---|---|
| `SequentialIdentifierGeneratorValidator` | Valideert ID-formaten; 0% coverage, 22 ongedekte condities |
| `IdentifierSourceResource` Brain Methods | 148 ongedekte condities door extreme complexiteit |

De 50% is dus niet "we testen slecht", maar "de dekking zit op de verkeerde plek": triviale wrappers zijn 100% gedekt, de risicovolle logica niet. Deze categorie is precies verbeteractie 5, 6 en 3 in `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2.

#### Waarom de streefnorm 70% is en niet hoger

| Argument | Onderbouwing |
|---|---|
| Coverage is een proxy, geen doel | 100% dekking bewijst niet dat code correct is; het meet alleen welke regels zijn uitgevoerd, niet of de assertions zinvol zijn. Hoge verplichte targets leiden aantoonbaar tot lege tests die het getal oppoetsen. |
| Afnemend rendement | De eerste 70% dekt de kernpaden (ID-generatie, validatie, pool-beheer). De laatste 30% zit in onpraktisch te testen Hibernate-DAO's en Spring/Context-koppeling, die zware mock-infra vereisen voor weinig waarde. |
| Refactoring komt eerst | Een methode met CC 106 (`IdentifierSourceResource`) is niet volledig unit-testbaar. Eerst splitsen (actie 3), dan testen. |
| De regressiepoort zit op nieuwe code | De quality gate dwingt 60% af op nieuwe code, niet op de hele legacy-codebase. Eigen wijzigingen worden streng bewaakt terwijl legacy-schuld gefaseerd wordt afgelost. |
| Scope van het onderzoek | Dit is een verbeteronderzoek op een bestaande module, geen herschrijving. 70% overall is realistisch na het PoC. |

Externe ijking: de brede industrie-richtlijn ligt rond 70 tot 80% als pragmatisch optimum. Google noemt 60% acceptabel, 75% prijzenswaardig en 90% voorbeeldig maar niet vereist. 70% zit in die verdedigbare middenband en sluit aan op ISO 25010 testbaarheid: risicogestuurd testen, niet uitputtend.

#### Bewijs: voor en na op het kritieke testgat

Om aan te tonen dat het relevante gat te dichten is, is een gerichte unit-test toegevoegd voor `SequentialIdentifierGeneratorValidator` (de validator met 0% coverage). De test staat in `SequentialIdentifierGeneratorValidatorTest` en bevat 11 testgevallen op de verplichte-velden- en lengtebranches. Het is een pure unit-test zonder Spring-context of database.

**Coverage van de validator-klasse (voor en na):**

Beide metingen zijn met JaCoCo 0.8.11 op 16/06/2026 uitgevoerd. "Voor" is de volledige geërfde api-suite zonder de toegevoegde test; "na" is dezelfde suite met de test erbij. Zo is het verschil uitsluitend toe te schrijven aan de nieuwe test.

| Metriek | Voor (geërfde suite) | Na (+ validator-test) |
|---|---|---|
| Line coverage | 0,0% (0 van 32 regels) | 71,9% (23 van 32 regels) |
| Branch/condition coverage | 0,0% (0 van 22) | 95,5% (21 van 22) |
| Method coverage | 0,0% (0 van 6) | 100% (6 van 6) |

SonarCloud rapporteerde op 12/06/2026 eveneens 0% voor deze klasse (29 ongedekte regels; SonarCloud telt regels iets anders dan JaCoCo, vandaar 29 versus 32). Beide tools bevestigen dus dat geen enkele bestaande test deze validator raakte.

**Effect op de suite:**

| Metriek | Nulmeting | Na gerichte aanvulling |
|---|---|---|
| api tests | 41 | 52 |
| Totaal tests | 134 | 145 |
| Validator line coverage | 0,0% | 71,9% |

De resterende 1 ongedekte branch en 9 ongedekte regels zitten in `validateIdentifierType()` (regels 68 tot 82), de tak die via `Context.loadClass` een externe validator-class laadt. Die tak vereist een OpenMRS-context en hoort daarom in een context-sensitieve test (`IdgenBaseTest`), niet in een pure unit-test. Dit is een bewuste afbakening en stageert het validatiewerk van Deel 6.

Deze voor/na laat zien dat de 50% geen structurele blokkade is: het echte testgat is met een gerichte, goedkope test van 0% naar bijna volledige branch-dekking te brengen, terwijl de triviale klassen onaangeroerd blijven. Precies dat is de kern van de verantwoording.

#### Meetmethode (reproduceerbaar)

De statische `argLine` in `api/pom.xml` (nodig voor de Java 17 `--add-opens`) schaduwt de JaCoCo-agent van `prepare-agent`. Voor een losse meting wordt de agent expliciet meegegeven:

```bash
mvn -B clean test -pl api -Dtest=SequentialIdentifierGeneratorValidatorTest \
  "-DargLine=-javaagent:<jacoco-agent>.jar=destfile=<pad>/api/target/jacoco.exec"
mvn -B jacoco:report -pl api "-Djacoco.dataFile=<pad>/api/target/jacoco.exec"
```

Het rapport komt in `api/target/site/jacoco/` (HTML + XML). In de CI levert de reguliere `verify`-stap de coverage aan SonarCloud.

#### Bronnen testdekking

- [Martin Fowler - Test Coverage](https://martinfowler.com/bliki/TestCoverage.html)
- [Google Testing Blog - Code Coverage Best Practices](https://testing.googleblog.com/2020/08/code-coverage-best-practices.html)
- [ISO 25010:2011 - Maintainability / Testability](https://www.iso.org/standard/35733.html)

---

## 5. Bevindingen en testgaten

### 5.1 Sterke punten

- De testsuite is groen: 0 failures en 0 errors over 132 uitgevoerde tests.
- Meerdere testniveaus zijn aanwezig, van pure unit tot volledige web-context.
- De service-laag (`IdentifierSourceServiceTest`, 25 tests) en de audit logging (`LoggingAuditTest`, 9 tests) zijn relatief goed gedekt.

### 5.2 Genegeerde tests (`@Ignore`)

| Testklasse | Reden (uit broncode) | Gevolg |
|---|---|---|
| `RemoteIdentifierSourceProcessorTest` | "We don't guarantee that this endpoint will always be up": de test roept een externe endpoint aan en is flaky | Geen netwerk-afhankelijke uitvoering in CI |
| `IdentifierSourceServiceLoadTest` | "Can't run this in the same test as the other IdentifierSourceService tests": loadtest met 500 threads, vereist isolatie | Geen prestatiemeting in de reguliere build |

Beide zijn upstream-keuzes uit de idgen-broncode, geen aanpassing van groep C6.

### 5.3 Uitgesloten tests (pom-exclude)

Drie tests zijn via een Surefire-exclude uitgeschakeld wegens incompatibiliteit met de Java-versie van de buildomgeving (Java 11 module-systeem), niet wegens een fout in onze code:

| Testklasse | Module | Configuratie | Oorzaak |
|---|---|---|---|
| `LocationBasedPrefixProviderTest` | api | `api/pom.xml` | PowerMock 1.x incompatibel met Java 11 module-systeem |
| `SequentialIdentifierGeneratorTest` | api | `api/pom.xml` | PowerMock 1.x incompatibel met Java 11 module-systeem |
| `IdentifierResourceTest` | omod | `omod/pom.xml` | `char[]`-cast vanuit `String` faalt op Java 11 |

Deze uitsluitingen zijn vastgelegd als technische schuld in `Groep_6_Non-Functional-Requirements.md`.

### 5.4 Testgaten voor het PoC

| Testgat | Bestand | Verbeteractie |
|---|---|---|
| Validator ongetest (0% coverage, 22 condities) | `SequentialIdentifierGeneratorValidator` | Actie 5 |
| Nul-coverage klassen | `IdentifierSourceEditor`, `RemoteIdentifierSourceValidator`, `IdentifierSourceValidator` | Actie 6 |
| Brain Methods nauwelijks testbaar (148 ongedekte condities) | `IdentifierSourceResource` | Actie 3 (refactoring maakt testen mogelijk) |

De koppeling bevinding naar verbetering is traceerbaar via `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2.

---

## 6. Reproduceerbaarheid

### 6.1 Omgeving

| Onderdeel | Lokale meting (16/06/2026) | CI (`ci-build-test.yml`) |
|---|---|---|
| Maven | 3.9.16 | 3.x (runner) |
| JDK | Temurin 17.0.19 | Temurin 11 |
| Compile target | Java 1.8 (`maven-compiler-plugin`) | Java 1.8 |
| Werkmap | `openmrs-module-idgen` | `openmrs-module-idgen` |

De compile-target is in beide gevallen Java 1.8, vastgelegd in `pom.xml`. De lokale meting draaide op JDK 17 en de CI op JDK 11; in beide gevallen is het resultaat groen met dezelfde drie pom-excludes.

### 6.2 Commando's

```bash
cd openmrs-module-idgen
mvn -B clean package -DskipTests   # api-artifact bouwen (omod-tests hebben dit nodig)
mvn -B test                        # unit- en component-tests draaien (Surefire)
```

Voor coverage (JaCoCo, zoals de pipeline meet):

```bash
mvn -B clean verify                # genereert jacoco.exec en surefire-reports
```

### 6.3 Outputlocaties

| Output | Locatie |
|---|---|
| Surefire-rapporten (api) | `api/target/surefire-reports/` |
| Surefire-rapporten (omod) | `omod/target/surefire-reports/` |
| JaCoCo-coverage | `api/target/jacoco.exec`, gerapporteerd in SonarCloud |
| CI-artifact | Actions run, artifact `test-reports-{run}` (90 dagen) |

---

## 7. Conclusie

De nulmeting toont een groene testsuite van 132 geslaagde tests, zonder failures of errors. De suite bevat vijf testtypen; vier daarvan worden in de nulmeting uitgevoerd, de integratietests (`*IT`) draaien via Failsafe en vallen buiten `mvn test` (zie sectie 2.3). De suite is breed maar ondiep: de coverage blijft op 50,0% en de meest complexe en veiligheidskritische klassen (de validator en `IdentifierSourceResource`) zijn onvoldoende gedekt. Sectie 4.5 toont met een gerichte unit-test al aan dat dit gat te dichten is: de validator ging van 0% naar 71,9% line coverage zonder de triviale klassen aan te raken. Dit is de meetbare uitgangssituatie waartegen de verbeteringen uit het PoC worden afgezet. De validatie na verbetering (Deel 6) hergebruikt exact deze commando's en rapporten om verbetering en het uitblijven van regressie aan te tonen.

---

## 8. Bronnen

- [JUnit 4](https://junit.org/junit4/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Maven Failsafe Plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)
- [JaCoCo - Java Code Coverage Library](https://www.jacoco.org/jacoco/)
- [OpenMRS - Unit Testing conventions](https://wiki.openmrs.org/display/docs/Unit+Testing)
- [SonarCloud - AvansHogeschoolBreda/openmrsmodule](https://sonarcloud.io/project/overview?id=AvansHogeschoolBreda_openmrsmodule)
