# Module-Keuze

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## Gekozen module

| Kenmerk | Details |
|---|---|
| **Naam** | OpenMRS ID Generation Module (idgen) |
| **Versie** | v4.9.0 (of vergelijkbare legacy/lesmateriaal-versie) |
| **Broncode** | [https://github.com/openmrs/openmrs-module-idgen](https://github.com/openmrs/openmrs-module-idgen) |
| **Primaire taal** | Java (60.6%), JSX/JavaScript (28.4%), XML & CSS (11.0%) |

---

## Motivatie voor de keuze

De keuze voor de **idgen**-module is gebaseerd op een zorgvuldige afweging van de complexiteit, de technologische scope en de kritieke healthcare-functionaliteit van de module in het kader van het LU2-verbetertraject (security & onderhoudbaarheid).

### 1. Complexiteit en Scope (Kwalitatief & Kwantitatief)
Op basis van de `scc` (Sloc, Cloc and Code) complexiteitsmetingen bevindt de `idgen` module zich in de ideale **"sweet spot"** voor dit onderzoek:

* **Systeemonderbouwing via vergelijking:**
  * **Te eenvoudig:** Modules zoals `oauth2login` (complexiteit 113) en `attachments` (complexiteit 258) bevatten te weinig codebase en logische vertakkingen om diepgaande security-issues of structurele maintainability-gebreken bloot te leggen.
  * **Te complex/omvangrijk:** Modules zoals `legacyui` (complexiteit 14.719) en `reporting` (complexiteit 8.880) zijn simpelweg te omvangrijk om binnen de doorlooptijd van één onderwijsperiode (sprint 1 t/m 4) grondig te analyseren, te reviewen en te patchen via een Proof of Concept.
  * **De idgen module:** Met **57 bestanden**, **4.312 regels code** (Java backend) en een **totale cognitive complexity van 668** biedt `idgen` een uitdagende, maar behapbare scope. Er is voldoende 'body' om een representatieve compliance scan (SAST/SCA) en onderhoudbaarheidsanalyse uit te voeren met reële bevindingen.

* **Technologische diversiteit:**
  De module bevat zowel backend-code (Java, Hibernate, Spring Framework) als frontend-componenten (JSX/React, JSP-pagina's, JavaScript en CSS). Dit stelt ons in staat om de applicatie over de hele stack te analyseren op kwetsbaarheden (o.a. data-in-transit, XSS, CSRF en database-queries).

### 2. Kritieke Zorgfunctionaliteit en Patiëntveiligheid (Healthcare Impact)
Binnen een Elektronisch Patiëntendossier (EPD) is de unieke identificatie van patiënten de meest fundamentele basisfunctionaliteit. 
* **Medische risico's:** Als de ID-generator faalt (bijvoorbeeld door race-conditions bij gelijktijdige registraties, SQL-injecties, of het genereren van duplicaten), kunnen patiëntendossiers door elkaar raken. Patiënt A kan dan de medische gegevens, medicatie-voorschriften of allergieën van Patiënt B gekoppeld krijgen. Dit leidt tot directe en levensbedreigende klinische risico's (patiëntverwisseling).
* **Privacy en Vertrouwelijkheid (BSN-achtige data):** De gegenereerde ID's worden direct gekoppeld aan gevoelige medische dossiers en persoonsgegevens. Een lek of ongeautoriseerde manipulatie in dit systeem heeft direct impact op de vertrouwelijkheid en integriteit van patiëntgegevens.

---

## Relevante functionaliteit en NEN-7510 Mapping

### Functionele componenten
De `idgen` module stelt OpenMRS in staat om op flexibele wijze unieke identificatienummers (zoals patiëntennummers) te genereren via verschillende bronnen:
1. **Sequential Identifier Generator:** Genereert opeenvolgende nummers op basis van een configureerbaar patroon (inclusief check-digit algoritmes zoals Luhn of Verhoeff).
2. **Local Pool:** Beheert een pool van vooraf gegenereerde of handmatig ingevoerde identificatienummers.
3. **Remote Identifier Source:** Vraagt identifiers op bij een externe web service (bijv. een centraal overheidsregister of een andere OpenMRS instance).
4. **Custom Generator:** Voert specifieke code uit voor het bepalen van de identifier.

### Direct toepasselijke NEN-7510:2026 Controls
Omdat de module direct invloed heeft op patiëntidentificatie en de integriteit van het ontwikkelproces, zijn de volgende drie normen uit de NEN-7510-2 (informatiebeveiliging in de zorg) direct van toepassing en geselecteerd voor dit verbetertraject:

| NEN-7510 Control | Omschrijving control | Directe toepassing op `idgen` |
|---|---|---|
| **Control 8.8** *(Beheer van technische kwetsbaarheden)* | Het verkrijgen van tijdige informatie over technische kwetsbaarheden in gebruikte systemen en software, en het nemen van passende maatregelen. | De `idgen` module maakt gebruik van diverse externe libraries voor database-koppelingen en REST-APIs. Het tijdig identificeren en mitigeren van kwetsbaarheden in deze dependencies (via Dependabot en Dependency Review) is cruciaal om misbruik te voorkomen. |
| **Control 8.15** *(Audit logging)* | Het registreren van gebeurtenissen die van belang zijn voor de informatiebeveiliging (zoals activiteiten, fouten en uitzonderingen) en het bewaren hiervan. | Het registreren, wijzigen of exporteren van ID-bronnen, evenals de uitputting van een ID-pool en het handmatig wijzigen van reeksen, moet audit-proof gelogd worden om onregelmatigheden en misbruik te kunnen traceren. |
| **Control 5.36** *(Conformiteit aan beleidsregels)* | Het regelmatig beoordelen van de naleving van het informatiebeveiligingsbeleid en de geldende normen binnen de organisatie. | De ontwikkeling en het beheer van de `idgen` module moeten aantoonbaar getoetst worden aan de beveiligingsrichtlijnen van het project (zoals het mini-ISMS in de README, SECURITY.md en de centrale checklist.md). |
