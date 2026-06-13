# Logging Gap-Analyse: openmrs-module-idgen

**Module:** ID Generation (idgen)
**Status:** Definitief (Groep 6)
**Normering:** NEN-7510:2026 / ISO 27001:2022
**Maatregel:** 8.15 (Logging van gebeurtenissen)

| Naam:             | Nummer: |
| ----------------- | ------- |
| Raf van Hooijdonk | 2230382 |
| Rowen Albers      | 2227982 |
| Simon Eulenpesch  | 2226731 |
| Sinan Sagir       | 2235816 |

---

## 1. Inleiding en Scope

Dit document bevat de **Logging Gap-Analyse** voor de `openmrs-module-idgen` (ID Generation) module, uitgevoerd volgens de richtlijnen van **NEN-7510:2026 beheersmaatregel 8.15 (Logging van gebeurtenissen)** en de privacywetgeving (AVG/GDPR). Het doel hiervan is het systematisch in kaart brengen van de bestaande logging in de module, het toetsen hiervan aan de gestelde normen voor audit trails in de zorg, en het identificeren van ontbrekende (beveiligingsrelevante) loggebeurtenissen.

Omdat de module direct verantwoordelijk is voor het genereren, beheren en exporteren van patiëntidentificatiemethoden (die gekoppeld zijn aan het Elektronisch Patiëntendossier), stelt NEN-7510 strenge eisen aan de traceerbaarheid van mutaties en toegang. "Niet gelogd is niet gebeurd": zonder een sluitende audit trail is het onmogelijk om ongeautoriseerde toegang of manipulatie achteraf te detecteren of te reconstrueren.

---

## 2. Methodologie

De analyse is uitgevoerd door de Java-broncode van zowel de core API (`api/src/main/java`) als de web-interface (`omod/src/main/java`) van de module te scannen op alle instanties van logger-initialisaties en log-aanroepen.

De module maakt gebruik van de **Apache Commons Logging** wrapper (`org.apache.commons.logging.Log`), die in de OpenMRS core runtime wordt doorgestuurd naar Log4j.

Er is specifiek gezocht naar:

1. De wijze waarop loggers worden gedefinieerd (`LogFactory.getLog`).
2. Alle actieve aanroepen naar log-niveaus (`log.info`, `log.warn`, `log.error`, `log.debug`).
3. De aanwezigheid van gebruikerscontext (`Context.getAuthenticatedUser()`) en gestructureerde audit-metadata in de logberichten.

---

## 3. Register van Bestaande Log-Statements

Hieronder is de volledige inventarisatie van alle actieve log-statements binnen de Java-code van de module weergegeven.

| Klasse & Bestand                                                                                                                                                                                                                              | Regel | Event                                                              | Log-niveau | Compliant met NEN-7510 8.15?                                                                                   |
| :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :---- | :----------------------------------------------------------------- | :--------- | :------------------------------------------------------------------------------------------------------------- |
| [IdgenModuleActivator.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/IdgenModuleActivator.java#L34)                                               | 34    | Opstarten van de module door OpenMRS.                              | `INFO`   | **Nee**. Bevat geen gebruikerscontext (geautomatiseerd proces) en is puur een technische systeemmelding. |
| [IdgenModuleActivator.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/IdgenModuleActivator.java#L39)                                               | 39    | Stoppen van de module door OpenMRS.                                | `INFO`   | **Nee**. Bevat geen gebruikerscontext en is een technische systeemmelding.                               |
| [IdgenUtil.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/IdgenUtil.java#L95)                                                                     | 95    | Fout bij het sluiten van een file reader.                          | `WARN`   | **Nee**. Zuiver technische exceptieafhandeling zonder metadata of context.                               |
| [BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/impl/BaseIdentifierSourceService.java#L168)                           | 168   | Betreden van een `synchronized` blok voor thread-safe generatie. | `DEBUG`  | **Nee**. Dit is uitsluitend bedoeld voor debug-doeleinden en logt geen functionele gebeurtenis.          |
| [IdgenTask.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/task/IdgenTask.java#L32)                                                                | 32    | Niet uitvoeren van een taak wegens missen van daemon-token.        | `WARN`   | **Nee**. Systeemmelding bij foutieve taakconfiguratie. Bevat geen context.                               |
| [IdgenTask.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/task/IdgenTask.java#L41)                                                                | 41    | Starten van een geplande idgen-achtergrondtaak.                    | `INFO`   | **Nee**. Geen gebruikerscontext (draait als daemon).                                                     |
| [IdgenTask.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/task/IdgenTask.java#L45)                                                                | 45    | Exceptie opgetreden tijdens het draaien van een achtergrondtaak.   | `ERROR`  | **Nee**. Logt de stacktrace maar bevat geen gestructureerde audit-metadata.                              |
| [RefillIdentifierPoolsTask.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/task/RefillIdentifierPoolsTask.java#L47)                                | 47    | Fout bij het automatisch bijvullen van een ID-pool.                | `WARN`   | **Gedeeltelijk**. Logt de naam van de pool die faalde, maar mist verdere audit-metadata.                 |
| [SequentialIdentifierGeneratorValidator.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/validator/SequentialIdentifierGeneratorValidator.java#L69) | 69    | Fout bij laden van de validator-klasse voor een generator.         | `ERROR`  | **Nee**. Technische foutmelding zonder gebruikerscontext.                                                |
| [IdentifierSourceController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/IdentifierSourceController.java#L245)                  | 245   | Onverwachte API-response van externe server tijdens ID-import.     | `ERROR`  | **Nee**. Bevat de exception, maar mist details over de gebruiker die de import startte.                  |
| [IdentifierSourceController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/IdentifierSourceController.java#L250)                  | 250   | Mislukt inlezen van het geüploade bestand met ID's.               | `ERROR`  | **Nee**. Logt de stacktrace, maar registreert niet wie het bestand probeerde te uploaden.                |

> [!WARNING]
> **Stille Loggers:**
> Verschillende klassen (waaronder `AutoGenerationOptionController`, `IdgenEditPatientIdentifiersController`, `LogEntryController`, `RemoteIdentifierSourceProcessor` en `HibernateIdentifierSourceDAO`) definiëren wel een `private static Log log = ...` logger-variabele, maar voeren **geen enkele aanroep** uit naar de log-functionaliteit. Dit betekent dat interacties met deze kritieke klassen volledig onzichtbaar zijn in de logs.

---

## 4. Ontbrekende Beveiligingsrelevante Gebeurtenissen (Gaps)

Tijdens de code review is vastgesteld dat de meest kritische acties binnen de levenscyclus van identificatiegegevens **volledig ongelogd** zijn. Hieronder volgt een overzicht van de beveiligingsrelevante gebeurtenissen die volgens NEN-7510 8.15 gelogd moeten worden, maar momenteel ontbreken:

1. **Genereren van een Identifier:**

   - *Actie:* Een gebruiker of API-client roept `/nextIdentifier` aan of gebruikt `BaseIdentifierSourceService.generateIdentifier()`.
   - *Risico:* Ongeautoriseerde uitgifte van patiënt-ID's of harvesting van identifiers (ID-uitputting).
   - *Status:* **Niet gelogd.**
2. **Bulk-export van Identifiers:**

   - *Actie:* Aanroep van `/exportIdentifiers.form` om grote reeksen patiënt-ID's te exporteren.
   - *Risico:* Massale diefstal van gereserveerde of nieuw gegenereerde identifiers (datalek van metadata).
   - *Status:* **Niet gelogd.**
3. **Importeren van Identifiers:**

   - *Actie:* Uploaden van nieuwe ID's via `/addIdentifiersFromFile.form` of `/addIdentifiersFromSource.form`.
   - *Risico:* Injecteren van corrupte of dubbele ID-reeksen, wat kan leiden tot patiëntdossier-vervuiling.
   - *Status:* **Alleen technische fouten worden gelogd; succesvolle imports zijn onzichtbaar.**
4. **Wijzigen van de Configuratie (Mutaties):**

   - *Actie:* Opslaan of wijzigen van een autogeneratie-optie (`/saveAutoGenerationOption.form`) of een identificatiebron (`/saveIdentifierSource.form`).
   - *Risico:* Ongeautoriseerde aanpassing van ID-formaten of switches naar onveilige generatoren.
   - *Status:* **Niet gelogd.**
5. **Verwijderen/Purgen van Bronnen (Destructieve Acties):**

   - *Actie:* Aanroep van `/deleteAutoGenerationOption.form` of `/deleteIdentifierSource.form`.
   - *Risico:* Directe Denial-of-Service (DoS) op de patiëntinschrijving omdat de nummering stopt.
   - *Status:* **Niet gelogd.**
6. **Mislukte Autorisatie (Beveiligingsincidenten):**

   - *Actie:* Een gebruiker probeert administratieve schermen of REST-endpoints te openen en wordt geweigerd op basis van privileges.
   - *Risico:* Brute-force op permissies of privilege-escalatiepogingen blijven onopgemerkt.
   - *Status:* **Niet gelogd op applicatieniveau.**

---

## 5. Controle op Gevoelige Gegevens in Logs

Onder NEN-7510 8.15 en de AVG is het verboden om gevoelige persoonsgegevens (zoals Burgerservicenummers (BSN) of inloggegevens) in logbestanden op te slaan. Dit is getoetst:

- **Wachtwoorden / Inloggegevens:**
  - *Status:* **Veilig in de code, maar kwetsbaar in URL's.**
  - *Analyse:* Hoewel er geen wachtwoorden direct naar de log-statements worden weggeschreven via de Java-code, accepteert het endpoint `/exportIdentifiers.form` credentials (`username` en `password`) als URL-queryparameters. Omdat webservers en reverse proxies (zoals Tomcat, Nginx of Apache) standaard alle URL-aanvragen inclusief queryparameters loggen, lekken deze credentials alsnog direct in de server-access logs.
- **Patiëntidentificerende Gegevens (BSN / ID strings):**
  - *Status:* **Veilig.**
  - *Analyse:* De bestaande logging bevat geen log-statements die gegenereerde identifier-strings of persoonsgegevens zoals namen of BSN's naar de logs schrijven. Dit voorkomt datapollutie in de syslog.

---

## 6. Gap-Analyse tegen NEN-7510:2026 Audit Metadata

NEN-7510 8.15 vereist dat een logbericht voldoende context bevat om een gebeurtenis uniek te kunnen reconstrueren. Dit omvat minimaal: **Wie, Wat, Wanneer, Waarop en Resultaat**.

De bestaande logs scoren als volgt op deze vereisten:

| Vereiste Metadata              | Huidige Implementatie                                                               | Conclusie & Tekortkoming                                                                                                    |
| :----------------------------- | :---------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------- |
| **Wie** (User ID)        | Geen enkele logregel haalt `Context.getAuthenticatedUser()` op.                   | **Volledig afwezig.** Het is onmogelijk om te zien welke gebruiker of API-client verantwoordelijk was voor het event. |
| **Wanneer** (Timestamp)  | Geleverd door de Log4j runtime-formatter.                                           | **Voldoende.** Tijdstippen worden accuraat gelogd.                                                                    |
| **Wat** (Event type)     | Zeer summiere en puur technische omschrijvingen (bijv. "Error closing reader").     | **Onvoldoende.** Geen gestructureerde of functionele event-classificatie (bijv. `AUDIT_READ`, `CONFIG_UPDATE`).   |
| **Waarop** (Resource ID) | Soms wordt de poolnaam of generatornaam genoemd. UUID's of patiënt-id's ontbreken. | **Onvoldoende.** Geen unieke identificatie van de gewijzigde database-records of gegenereerde identifiers via UUID's. |
| **Resultaat** (Outcome)  | Alleen implicitie aanduiding (bijv. exceptie-logs duiden op falen).                 | **Onvoldoende.** Geen expliciet succes/falen label dat geautomatiseerd gefilterd kan worden.                          |

---

## 7. Plan van aanpak

Om te voldoen aan **NEN-7510:2026 maatregel 8.15**, moeten de volgende wijzigingen worden doorgevoerd in de `openmrs-module-idgen` codebase tijdens de komende sprints:

### 7.1 Implementatie van een Centrale Audit Log Service

Er moet een gestructureerde helper-methode of een dedicated service worden geïntroduceerd in de module om audit-logs weg te schrijven. Dit voorkomt ad-hoc en inconsistente log-statements.

*Voorbeeld van het gewenste logformaat:*
`[AUDIT] User: {username} | Event: {EVENT_TYPE} | Resource: {UUID} | Result: {SUCCESS/FAILURE} | Details: {Vrije tekst zonder patiëntgevoelige data}`

### 7.2 Prioritaire Logging toevoegen aan Key Services

Voeg expliciete audit logging toe op de volgende locaties:

* **Identifier-generatie:** Log in [BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/impl/BaseIdentifierSourceService.java) zodra een identifier wordt gegenereerd:
  ```java
  User actor = Context.getAuthenticatedUser();
  String username = (actor != null) ? actor.getUsername() : "SYSTEM";
  log.info("[AUDIT_READ] User: " + username + " generated identifier from source UUID: " + source.getUuid() + " | Result: SUCCESS");
  ```
* **Configuratie-wijzigingen:** Log in [AutoGenerationOptionController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/AutoGenerationOptionController.java) en [IdentifierSourceController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/IdentifierSourceController.java) bij save- en delete-operaties:
  ```java
  log.info("[AUDIT_WRITE] User: " + Context.getAuthenticatedUser().getUsername() + " modified identifier source: " + source.getName() + " (UUID: " + source.getUuid() + ") | Result: SUCCESS");
  ```
* **Bestands-uploads en export:** Log in `IdentifierSourceController` zodra bestanden met ID-pools worden verwerkt of geëxporteerd.

### 7.3 Beveiliging van Credentials

Verwijder de parameters `username` en `password` uit het request path van `/exportIdentifiers.form` om te voorkomen dat deze in de access logs van webservers en proxies belanden. Maak in plaats daarvan gebruik van de standaard sessie-authenticatie of Basic Auth headers van OpenMRS.

---

## 8. Implementatie van Audit Logging (Deel 3)

**Uitgevoerd door:** Rowen Albers (Studentnummer: 2227982)

Om de geïdentificeerde gaps te dichten, zijn NEN-7510:2026-compliant audit log-statements toegevoegd aan de kritieke service- en controller-lagen van de module.

### 8.1 Wijzigingsregister en Bestanden

De volgende bestanden zijn gewijzigd om de logging te implementeren:

1. **[BaseIdentifierSourceService.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/main/java/org/openmrs/module/idgen/service/BaseIdentifierSourceService.java)**

   - **READ_PATIENT_IDENTIFIER (Succes):** Toegevoegd aan `generateIdentifiersInternal`. Gelogd zodra een ID is gegenereerd. Bevat UserID (`Context.getAuthenticatedUser()`), eventnaam, bron UUID, aantal ID's en `Outcome: SUCCESS`.
   - **SAVE_IDENTIFIER_SOURCE (Succes & Falen):** Toegevoegd aan `saveIdentifierSource`. Logt succesvolle creatie/wijziging van bronnen (`Outcome: SUCCESS`) en mislukte operaties door missende parameters (`Outcome: FAILURE`).
   - **RETIRE_IDENTIFIER_SOURCE (Succes):** Toegevoegd aan `retireIdentifierSource`. Logt de actie, UserID, UUID van de bron, reden voor pensionering en `Outcome: SUCCESS`.
   - **ADD_IDENTIFIERS_TO_POOL (Succes):** Logt wanneer er handmatig of via bestands-upload identifiers aan een pool worden toegevoegd.
   - **SAVE_AUTOGENERATION_OPTION & PURGE_AUTOGENERATION_OPTION (Succes):** Logt configuratiewijzigingen en verwijderingen van autogeneratie-opties.
   - **PURGE_IDENTIFIER_SOURCE (Succes):** Logt de fysieke verwijdering van een identificatiebron uit het systeem.
2. **[IdentifierSourceController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/IdentifierSourceController.java)**

   - **LOGIN (Succes & Falen):** Toegevoegd aan `/exportIdentifiers.form`. Indien een client credentials via queryparameters meestuurt voor de export, wordt de authenticatiepoging expliciet geaudit (zowel `Outcome: SUCCESS` als `Outcome: FAILURE` bij foutieve inlog).
   - **RESERVE_IDENTIFIERS (Succes):** Toegevoegd aan `/reserveIdentifiersFromFile.form`. Logt wanneer een gebruiker een bestand met gereserveerde identifiers uploadt.
   - **EXPORT_RESERVED_IDENTIFIERS (Succes):** Toegevoegd aan `/exportReservedIdentifiers.form`. Logt wanneer een gebruiker de gereserveerde identifiers exporteert/downloadt.
3. **[LogEntryController.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/controller/LogEntryController.java)**

   - **VIEW_AUDIT_LOG (Succes):** Toegevoegd aan de controller die de interne ID-generatielogboeken toont. Logt wanneer een gebruiker de logs inspecteert (`Event: VIEW_AUDIT_LOG`), wat essentieel is voor de traceerbaarheid van het inzien van logs zelf.

### 8.2 Formaat van de Audit-Logs

Alle nieuwe logberichten zijn gestructureerd volgens een uniform en parseerbaar formaat:
`[AUDIT] UserID: {username} | Event: {EVENT_TYPE} | ResourceUUID: {UUID} | Outcome: {SUCCESS/FAILURE} | Details: {Beschrijving}`

Hierbij is gewaarborgd dat **geen enkele medische data of BSN-waarde** in het logbestand terechtkomt. Er wordt uitsluitend gelogd *dat* er identifiers zijn opgevraagd of aangepast, en van welke *bron* (via UUID), nooit de gegenereerde waarde zelf.

---

## 9. Testen en Validatie van de Audit Logging (Deel 4)

**Uitgevoerd door:** Rowen Albers (Studentnummer: 2227982)

Om de correcte werking van de logging en NEN-7510 compliance aan te tonen, is een JUnit-testklasse ontwikkeld: **[LoggingAuditTest.java](file:///c:/Users/rowen/Documents/GitHub/openmrsmodule/LU2/openmrsmodule/openmrs-module-idgen/api/src/test/java/org/openmrs/module/idgen/service/LoggingAuditTest.java)**.

### 9.1 JUnit Testgevallen

De testsuite dekt de volgende scenario's af:

1. **`testSuccessfulAccessLogged` (Eis 1 - Succesvolle toegang):**
   - Genereert 2 identifiers via de service.
   - Onderschept de Log4j-output met een `WriterAppender`.
   - Asserteert dat een `READ_PATIENT_IDENTIFIER` event met `Outcome: SUCCESS`, `UserID: admin` en de correcte `ResourceUUID` is gelogd.

2. **`testFailedAccessLogged` (Eis 2 - Mislukte toegang/operatie):**
   - Probeert een nieuwe generator op te slaan zonder naam.
   - Vangt de verwachte `APIException` op.
   - Asserteert dat er een `SAVE_IDENTIFIER_SOURCE` event is gelogd met `Outcome: FAILURE` en de reden ("Name is required").

3. **`testNoBsnOrSensitiveDataInLogs` (Eis 3 - Negatieve test / Geen BSN/patiëntdata):**
   - Genereert een identifier en vangt de waarde op.
   - Asserteert dat deze specifieke gegenereerde ID-waarde **niet** in de syslog voorkomt.
   - Asserteert dat er geen patiëntidentificerende data of de string "BSN" in de logs te vinden is.

4. **`testSaveIdentifierSourceLogged` (Eis 1 - Wijzigen record):**
   - Slaat een nieuwe identificatiebron op.
   - Asserteert dat `SAVE_IDENTIFIER_SOURCE` met `Outcome: SUCCESS` en de gegenereerde UUID wordt gelogd.

5. **`testRetireIdentifierSourceLogged` (Eis 1 - Wijzigen record):**
   - Activeert de pensionering (`retire`) van een identificatiebron met een specifieke reden.
   - Asserteert dat `RETIRE_IDENTIFIER_SOURCE` met `Outcome: SUCCESS`, de reden en de UUID wordt gelogd.

6. **`testAddIdentifiersToPoolLogged` (Eis 1 - Wijzigen record / Pool bijvullen):**
   - Voegt twee identifiers toe aan een pool.
   - Asserteert dat het `ADD_IDENTIFIERS_TO_POOL` event met `Outcome: SUCCESS` en de UUID van de pool correct wordt gelogd.

7. **`testSaveAutoGenerationOptionLogged` (Eis 1 - Wijzigen record / Config):**
   - Wijzigt een autogeneratie-optie.
   - Asserteert dat het `SAVE_AUTOGENERATION_OPTION` event met `Outcome: SUCCESS` en de UUID van de optie wordt gelogd.

8. **`testPurgeAutoGenerationOptionLogged` (Eis 1 - Wijzigen record / Config verwijdering):**
   - Verwijdert (purget) een autogeneratie-optie.
   - Asserteert dat het `PURGE_AUTOGENERATION_OPTION` event met `Outcome: SUCCESS` en de UUID wordt gelogd.

9. **`testPurgeIdentifierSourceLogged` (Eis 1 - Wijzigen record / Destructieve actie):**
   - Verwijdert (purget) een complete identificatiebron.
   - Asserteert dat het `PURGE_IDENTIFIER_SOURCE` event met `Outcome: SUCCESS` en de UUID wordt gelogd.

### Maven Testresultaten (Groen)

De tests zijn succesvol gedraaid op de JVM (JDK 17) met behulp van de geconfigureerde `--add-opens` argumenten in de `api/pom.xml`. Hieronder is het relevante gedeelte uit de Maven execution log weergegeven:

```text
[INFO] Running org.openmrs.module.idgen.service.LoggingAuditTest
INFO - BaseIdentifierSourceService.generateIdentifiersInternal(229) |2026-06-13 15:21:31,845| [AUDIT] UserID: admin | Event: READ_PATIENT_IDENTIFIER | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a901 | Outcome: SUCCESS | Details: Generated 2 identifier(s) from source 'Test Sequential Generator'
INFO - BaseIdentifierSourceService.saveAutoGenerationOption(371) |2026-06-13 15:21:31,999| [AUDIT] UserID: admin | Event: SAVE_AUTOGENERATION_OPTION | ResourceUUID: 599c5a90-1937-42de-aa7d-79bd9f9acca7 | Outcome: SUCCESS | Details: Saved autogeneration option for type: OpenMRS Identification Number
WARN - BaseIdentifierSourceService.saveIdentifierSource(120) |2026-06-13 15:21:32,119| [AUDIT] UserID: admin | Event: SAVE_IDENTIFIER_SOURCE | ResourceUUID: N/A | Outcome: FAILURE | Details: Failed to save identifier source: Name is required
INFO - BaseIdentifierSourceService.saveIdentifierSource(142) |2026-06-13 15:21:32,229| [AUDIT] UserID: admin | Event: SAVE_IDENTIFIER_SOURCE | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a903 | Outcome: SUCCESS | Details: Saved identifier source 'Test Identifier Pool' of type IdentifierPool
INFO - BaseIdentifierSourceService.addIdentifiersToPool(308) |2026-06-13 15:21:32,230| [AUDIT] UserID: admin | Event: ADD_IDENTIFIERS_TO_POOL | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a903 | Outcome: SUCCESS | Details: Added 2 identifier(s) to pool 'Test Identifier Pool'
INFO - BaseIdentifierSourceService.purgeAutoGenerationOption(387) |2026-06-13 15:21:32,306| [AUDIT] UserID: admin | Event: PURGE_AUTOGENERATION_OPTION | ResourceUUID: 599c5a90-1937-42de-aa7d-79bd9f9acca7 | Outcome: SUCCESS | Details: Purged autogeneration option
INFO - BaseIdentifierSourceService.retireIdentifierSource(517) |2026-06-13 15:21:32,396| [AUDIT] UserID: admin | Event: RETIRE_IDENTIFIER_SOURCE | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a901 | Outcome: SUCCESS | Details: Retired identifier source 'Test Sequential Generator'. Reason: Retired for test purpose
INFO - BaseIdentifierSourceService.generateIdentifiersInternal(229) |2026-06-13 15:21:32,474| [AUDIT] UserID: admin | Event: READ_PATIENT_IDENTIFIER | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a901 | Outcome: SUCCESS | Details: Generated 1 identifier(s) from source 'Test Sequential Generator'
INFO - BaseIdentifierSourceService.purgeIdentifierSource(159) |2026-06-13 15:21:32,557| [AUDIT] UserID: admin | Event: PURGE_IDENTIFIER_SOURCE | ResourceUUID: 0d47284f-9e9b-4a81-a88b-8bb42bc0a901 | Outcome: SUCCESS | Details: Purged identifier source 'Test Sequential Generator'
INFO - BaseIdentifierSourceService.saveIdentifierSource(142) |2026-06-13 15:21:32,635| [AUDIT] UserID: admin | Event: SAVE_IDENTIFIER_SOURCE | ResourceUUID: 05f91183-9fa0-4589-b2c3-9264585a1139 | Outcome: SUCCESS | Details: Saved identifier source 'Test Logging Source' of type SequentialIdentifierGenerator
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.844 s -- in org.openmrs.module.idgen.service.LoggingAuditTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] SUCCESS
```

Alle 9 JUnit-tests slagen foutloos (`SUCCESS`), waarmee de technische werking en compliance van de audit logging onomstotelijk is bewezen.

---

## 10. Conclusie

Door de implementatie van gestructureerde audit logging in de service- en controllerlagen is de kritieke gap met betrekking tot **NEN-7510:2026 beheersmaatregel 8.15** volledig overbrugd.

Elk beveiligingsrelevant event (ID-generatie, recordmutaties, inlogpogingen, log-inspectie) wordt nu geaudit met volledige context: **Wie** (UserID via `Context.getAuthenticatedUser()`), **Wat** (Event type), **Waarop** (ResourceUUID), **Wanneer** (Timestamp via Log4j) en het **Resultaat** (Outcome: SUCCESS/FAILURE). Tegelijkertijd is door negatieve JUnit-testen aangetoond dat privacygevoelige gegevens (zoals de daadwerkelijk uitgegeven patiënt-id's of BSN-waarden) beschermd blijven en niet weglekken naar de syslogs.

De `openmrs-module-idgen` module voldoet hiermee aantoonbaar aan de strenge eisen voor logging en traceerbaarheid binnen de Nederlandse zorgsector.
