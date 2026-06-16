# Aangepast ontwerp en refactoring-onderbouwing

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## 1. Doel en scope

Dit document beschrijft het aangepaste ontwerp voor de geselecteerde verbeteringen aan de `idgen`-module en onderbouwt de keuzes met ontwerpprincipes, refactoringpatronen en overwogen alternatieven. Het sluit aan op de geprioriteerde verbeteracties in `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2 en op de testopzet in `Groep_6_Testplan.md`.

De realisatie van deze ontwerpen is uitgevoerd als PoC in commit 303c735 (15/06/2026, "mitigated 201 code quality issues") en aanvullend 7d41fbc (diamond operators). Dit document beschrijft het ontwerp achter die realisatie.

**Scope:** de structurele code-aanpassingen voor onderhoudbaarheid (complexiteit, duplicatie, leesbaarheid). Buiten scope: security-hardening (CodeQL, commit 73d9b94) en de testtoevoegingen (zie `Groep_6_Testplan.md`).

---

## 2. Methodiek

De aanpak volgt de refactoring-discipline van Martin Fowler: kleine, gedragsbehoudende transformaties, telkens gedekt door de bestaande testsuite zodat regressie direct zichtbaar wordt. Per verbeteractie uit de prioritering is bepaald welk ontwerpprincipe wordt geschonden, welk refactoringpatroon dit herstelt en welke alternatieven zijn overwogen.

De geselecteerde verbeteringen (uit `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2):

| Actie | Verbetering | Ontwerpprobleem | Prioriteit |
|---|---|---|---|
| 1 | Multi-threading fix (static / geen gedeelde mutable state) | Race condition | Kritiek |
| 3 | Brain Methods opsplitsen | Te hoge cognitive complexity | Hoog |
| 4 | Magic strings naar constanten | Duplicatie (DRY-schending) | Hoog |
| 5 | Validator unit-testbaar maken | Testbaarheid + complexity | Hoog |
| 8, 9 | Diamond operator, `@Override` | Leesbaarheid, consistentie | Laag |

---

## 3. Aangepast ontwerp per verbetering

### 3.1 Validator: Extract Method en Guard Clause

**Huidig ontwerp (voor):** `SequentialIdentifierGeneratorValidator.validate()` was één methode met geneste `if/else`-blokken, inline validator-loading en lengtecontroles. Cognitive Complexity 27 (drempel 15). De geneste `else`-tak maakte de methode moeilijk te volgen en te testen.

**Aangepast ontwerp (na):** de methode is opgesplitst in drie kleinere methoden met één verantwoordelijkheid elk:

```
validate(o, errors)
  -> super.validate(o, errors)          // naam-validatie (basisklasse)
  -> guard: firstIdentifierBase vereist
  -> guard: identifierType vereist (early return)
  -> validateIdentifierType(source, firstId, errors)   // extracted
  -> checkLengthConstraints(source, firstId, errors)   // extracted
```

**UML-klassediagram (voor en na):**

```mermaid
classDiagram
    class IdentifierSourceValidator {
        +supports(Class) boolean
        +validate(Object, Errors) void
    }

    class Validator_VOOR["SequentialIdentifierGeneratorValidator (voor)"] {
        +supports(Class) boolean
        +validate(Object, Errors) void  «CC 27, alle logica inline»
    }

    class Validator_NA["SequentialIdentifierGeneratorValidator (na)"] {
        +supports(Class) boolean
        +validate(Object, Errors) void  «CC < 15, orkestreert»
        -validateIdentifierType(source, firstId, Errors) String
        -checkLengthConstraints(source, firstId, Errors) void
    }

    IdentifierSourceValidator <|-- Validator_VOOR
    IdentifierSourceValidator <|-- Validator_NA
```

In de voor-situatie zit alle validatielogica in één methode (`validate`). In de na-situatie orkestreert `validate` alleen nog en zijn de twee deeltaken afgesplitst naar private methoden, waardoor de cognitive complexity onder de drempel komt en de deeltaken los testbaar zijn.

**Toegepaste patronen:**
- **Extract Method** (Fowler): `validateIdentifierType()` en `checkLengthConstraints()` afgesplitst.
- **Replace Nested Conditional with Guard Clauses** (Fowler): de `else`-tak na de `identifierType == null`-controle is vervangen door een `return`, waardoor de happy path plat blijft.

**Ontwerpprincipes:** Single Responsibility (elke methode doet één ding) en KISS (de hoofdmethode leest nu als een checklist).

**Alternatief overwogen:** de validatie volledig in een aparte `ValidationChain`/Chain of Responsibility gieten. Afgewezen: voor drie regels introduceert dat meer abstractie dan het oplost (YAGNI). Extract Method haalt de complexiteit onder de drempel met minimale structuur.

**Effect:** de afgesplitste methoden zijn los testbaar. De toegevoegde `SequentialIdentifierGeneratorValidatorTest` (11 tests) brengt de klasse van 0% naar 71,9% line coverage (zie `Groep_6_Testplan.md` sectie 4.5).

### 3.2 IdentifierSourceResource: Brain Methods opsplitsen

**Huidig ontwerp (voor):** twee methoden (regel 142 en 318) met Cognitive Complexity 101 en 106, elk 144 tot 165 regels, tot 39 lokale variabelen. SonarCloud classificeerde beide als Brain Method.

**Aangepast ontwerp (na):** de methoden zijn opgeknipt in kleinere private hulpmethoden. Het bestand bevat na de refactoring 17 private methoden (commit 303c735, 351 gewijzigde regels), elk met een afgebakende deeltaak (parsen van een deelrequest, opbouwen van een representatie, enz.).

**Schematisch UML (conceptueel; exacte methodenamen in de broncode):**

```mermaid
classDiagram
    class IdentifierSourceResource_VOOR["IdentifierSourceResource (voor)"] {
        +brainMethod_L142() «CC 101, 165 regels»
        +brainMethod_L318() «CC 106, 144 regels»
    }

    class IdentifierSourceResource_NA["IdentifierSourceResource (na)"] {
        +publicMethod_L142() «orkestreert»
        +publicMethod_L318() «orkestreert»
        -helper_1() ".. tot .."
        -helper_n() «17 private hulpmethoden»
    }

    IdentifierSourceResource_VOOR ..> IdentifierSourceResource_NA : Extract Method
```

De twee Brain Methods zijn opgesplitst: de publieke methoden orkestreren nog en de deeltaken zijn naar private hulpmethoden verplaatst. Dit verlaagt de cognitive complexity per methode zonder de publieke REST-API te wijzigen.

**Toegepaste patronen:**
- **Extract Method** (Fowler): herhaaldelijk toegepast om samenhangende blokken uit de Brain Methods te halen.
- **Compose Method** (Kerievsky): de overgebleven hoofdmethode bestaat na refactoring vrijwel alleen uit aanroepen op hetzelfde abstractieniveau.

**Ontwerpprincipes:** Single Responsibility en Separation of Concerns; de complexiteit is verdeeld over benoembare eenheden in plaats van geconcentreerd in één methode.

**Alternatief overwogen:** de klasse opsplitsen in meerdere resource-klassen (volledige architecturele splitsing). Afgewezen voor deze PoC: dat raakt de OpenMRS REST-contracten en vergt regressietesten buiten de scope. Extract Method verlaagt de complexity zonder de publieke API te breken; de volledige splitsing is genoteerd als vervolgactie.

**Validatie:** exacte CC-reductie per methode volgt uit een verse SonarCloud-meting (Deel 6). De analyse-baseline (CC 101/106) is van 12/06, vóór de refactoring.

### 3.3 Magic strings: Extract Constant

**Huidig ontwerp (voor):** string-literals zoals `"identifierType"`, `"source"`, `"[AUDIT] UserID: "` en `"SYSTEM"` stonden 4 tot 10 keer herhaald per bestand (rule `java:S1192`). Dit is de hoofdoorzaak van de 10,8% duplicatie in de omod-laag.

**Aangepast ontwerp (na):** de literals zijn geëxtraheerd naar `private static final`-constanten. Bevestigd in onder andere `BaseIdentifierSourceService` (`AUDIT_USER_PREFIX`, `SYSTEM_USER`) en de resource handlers.

**Toegepaste patronen:**
- **Extract Constant** (Fowler / Replace Magic Literal).

**Ontwerpprincipes:** DRY (Don't Repeat Yourself); één definitie, één plek om te wijzigen. In een medische context (patiënt-ID-validatie) verlaagt dit het risico dat een wijziging op één plek vergeten wordt.

**Alternatief overwogen:** een enum of een centrale `Constants`-klasse voor alle veldnamen. Afgewezen: de literals zijn klasse-lokaal van betekenis; per klasse een constante houdt de cohesie hoog en voorkomt een god-constantenklasse.

### 3.4 Multi-threading: gedeelde mutable state wegnemen

**Huidig ontwerp (voor):** `LocationBasedPrefixProvider` en `LocationBasedSuffixProvider` muteerden een instantie-`Set` vanuit een niet-statische methode (rule `java:S2696`). In een multi-threaded OpenMRS-omgeving (meerdere gelijktijdige HTTP-requests) leidt dit tot race conditions bij ID-generatie.

**Aangepast ontwerp (na):** de betreffende methode is static gemaakt of de gedeelde `Set` is verwijderd, zodat er geen gedeelde veranderlijke toestand meer is.

**Ontwerpprincipes:** thread-safety door immutability / geen gedeelde state; dit is tevens een correctheidsfix, niet alleen onderhoudbaarheid.

**Alternatief overwogen:** synchronisatie (`synchronized` of een concurrent collectie). Afgewezen: locking voegt complexiteit en contention toe; de state was niet nodig op instantieniveau, dus wegnemen is eenvoudiger en sneller (KISS).

### 3.5 Leesbaarheid: diamond operator en @Override

**Aangepast ontwerp:** `new ArrayList<String>()` vervangen door `new ArrayList<>()` (commit 7d41fbc, ~35 plekken) en ontbrekende `@Override`-annotaties toegevoegd (commit 303c735).

**Ontwerpprincipes:** KISS en consistentie; `@Override` maakt de intentie expliciet en laat de compiler contractbreuken vangen. Dit zijn kleine, automatisch toepasbare verbeteringen met laag risico.

---

## 4. Overzicht toegepaste principes en patronen

| Refactoringpatroon | Bron | Toegepast op | Verbeteractie |
|---|---|---|---|
| Extract Method | Fowler | Validator, `IdentifierSourceResource` | 3, 5 |
| Replace Nested Conditional with Guard Clauses | Fowler | Validator | 5 |
| Compose Method | Kerievsky | `IdentifierSourceResource` | 3 |
| Extract Constant | Fowler | Resource handlers, services | 4 |
| Remove shared mutable state | n.v.t. (concurrency) | Location-based providers | 1 |

| Ontwerpprincipe | Waar toegepast |
|---|---|
| Single Responsibility (SOLID) | Validator, Brain Methods opsplitsen |
| DRY | Magic strings naar constanten |
| KISS | Guard clauses, geen onnodige abstractie, diamond operator |
| Separation of Concerns | Brain Methods naar deeltaken |
| YAGNI | Geen Chain of Responsibility / volledige klassesplitsing in deze PoC |

---

## 5. Alternatieven en gemotiveerde keuzes (samenvatting)

| Verbetering | Gekozen aanpak | Overwogen alternatief | Motivatie keuze |
|---|---|---|---|
| Validator-complexity | Extract Method + Guard Clause | Chain of Responsibility | Minder abstractie, complexity onder drempel (YAGNI) |
| Brain Methods | Extract Method binnen klasse | Klasse opsplitsen in meerdere resources | Behoudt publieke REST-API, geen contractbreuk in PoC |
| Magic strings | Constante per klasse | Centrale enum/Constants-klasse | Behoudt cohesie, vermijdt god-klasse |
| Multi-threading | State wegnemen / static | Synchronisatie | Eenvoudiger, geen lock-contention (KISS) |

---

## 6. Koppeling naar realisatie en validatie

- **Prioritering en onderbouwing:** `Groep_6_Analyse-Onderhoudbaarheid.md` sectie 8.2 (Deel 3).
- **Realisatie (PoC):** commit 303c735 (39 java-bestanden) en 7d41fbc (diamond operators), op `main` (Deel 5).
- **Validatie:** de bestaande testsuite blijft groen na de refactoring (geen regressie); de voor/na op metriekniveau volgt uit een verse SonarCloud-meting (Deel 6 in `docs/checklist.md`).

---

## 7. Bronnen

- [Martin Fowler - Refactoring: Improving the Design of Existing Code (2018)](https://martinfowler.com/books/refactoring.html)
- [Refactoring catalogus - Extract Function/Method](https://refactoring.com/catalog/extractFunction.html)
- [Refactoring catalogus - Replace Nested Conditional with Guard Clauses](https://refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html)
- [Joshua Kerievsky - Refactoring to Patterns (Compose Method)](https://www.industriallogic.com/xp/refactoring/composeMethod.html)
- [SonarSource - Cognitive Complexity](https://www.sonarsource.com/docs/CognitiveComplexity.pdf)
- [ISO 25010:2011 - Maintainability](https://www.iso.org/standard/35733.html)
