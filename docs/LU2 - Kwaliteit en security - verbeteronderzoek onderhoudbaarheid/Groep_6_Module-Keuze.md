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
  * **De idgen module:** Met **151 bestanden**, **14.152 regels code** en een **cyclomatische complexiteit van 949** (waarvan 716 in de Java backend) biedt `idgen` een uitdagende, maar behapbare scope. Er is voldoende 'body' om een representatieve compliance scan (SAST/SCA) uit te voeren met reële bevindingen.

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

### Direct toepasselijke NEN-7510:2024-2 Controls
Omdat de module direct invloed heeft op patiëntidentificatie en integriteit, zijn de volgende normen uit de NEN-7510-2 (informatiebeveiliging in de zorg) direct van toepassing:

| NEN-7510 Control | Omschrijving control | Directe toepassing op `idgen` |
|---|---|---|
| **Control 8.15** *(Audit logging)* | Vastleggen van gebeurtenissen (inclusief gebruikers-ID's, tijdstippen, aard van de gebeurtenissen). | Elke wijziging in de configuratie van een ID-bron, uitputting van een ID-pool, of het handmatig wijzigen van de actuele tellerstand (sequence start) *moet* audit-proof gelogd worden. |
| **Control 8.3** & **8.5** *(Toegangsbeveiliging en Authenticatie)* | Beperken van toegangsrechten en veilige authenticatie. | Alleen geautoriseerde rollen (bijv. systeembeheerders) mogen ID-bronnen aanmaken of wijzigen. De authenticatietokens/API-keys voor de *Remote Identifier Source* moeten veilig opgeslagen en getransporteerd worden (data-in-transit encryptie via TLS). |
| **Control 8.25** *(Inputvalidatie / Gebruikersinvoer)* | Controleren van invoergegevens om fouten en injecties te voorkomen. | Bij het configureren van nieuwe generators (zoals regex-patronen en database-query parameters) is strikte inputvalidatie noodzakelijk om SQL/HQL-injecties en logische crashes te voorkomen. |
| **Control 8.28** *(Veilige ontwikkeling)* | Toepassen van regels voor veilige software-ontwikkeling. | De codebase moet vrij zijn van bekende OWASP Top 10 kwetsbaarheden (zoals concurrency race-conditions bij ID-generatie of XSS-lekken in de JSX/JSP beheer-UI). |
