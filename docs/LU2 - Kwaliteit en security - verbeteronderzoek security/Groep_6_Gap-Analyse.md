# Gap-Analyse NEN-7510:2026

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## Inleiding

Dit document bevat de Gap-analyse voor de **OpenMRS ID Generation Module (idgen)** versie 4.9.0. Deze analyse vergelijkt de huidige staat van de module met drie cruciale controls uit de [NEN-7510:2026 normering](https://www.nen.nl/nen-7510) voor informatiebeveiliging in de zorg. Het doel is om vast te stellen welke beveiligingsgaps er bestaan binnen de modulecode en het ontwerp, zodat deze gericht gemitigeerd kunnen worden in de daaropvolgende ontwikkelfasen (Sprint 3 en 4).

**Scope van het onderzoek:**
* **Module:** OpenMRS ID Generation Module (idgen)
* **Versie:** v4.9.0
* **Onderzochte controls:** NEN-7510:2026 Control 8.3 (Informatietoegang), Control 8.5 (Veilige authenticatie) en Control 8.15 (Logging van gebeurtenissen).
* **Periode:** Juni 2026

---

## Analyse

Onderstaande tabel toont de status, omschrijving en het concrete bewijs van de onderzochte controls binnen de module.

| NEN-7510:2026 Control | Omschrijving | Status | Bewijs (code of logica) | Notities |
|---|---|---|---|---|
| **Control 8.3**<br>*(Informatietoegang)* | Toegang tot informatie en systemen is beperkt conform het toegangsbeleid (RBAC). | **Gedeeltelijk** | In `RemoteIdentifierSourceProcessor.java` ontbreken expliciete privilegechecks (`Context.requirePrivilege`) bij het opvragen en verwerken van identifiers via de API. | De OpenMRS core dwingt weliswaar algemene privileges af, maar de module specifieke koppelingen en endpoints controleren onvoldoende op rolgebaseerde rechten (zie `SAST-02` in het security backlog). |
| **Control 8.5**<br>*(Veilige authenticatie)* | Gebruikersauthenticatie moet veilig verlopen om toegang tot systemen te beheren. | **Gedeeltelijk** | Er is geen rate-limiting mechanism of account lockout policy geconfigureerd op de login- en API-endpoints van de module. | OpenMRS core slaat wachtwoorden veilig op met bcrypt hashing, maar is door het ontbreken van login-beveiligingen gevoelig voor brute-force aanvallen en credential stuffing (zie hazard `H3` uit de asset-identificatie). |
| **Control 8.15**<br>*(Logging van gebeurtenissen)* | Logbestanden die activiteiten en beveiligingsrelevante gebeurtenissen vastleggen worden aangemaakt en beschermd. | **Gedeeltelijk / Afwezig** | Code review van de `IdentifierSourceProcessor.java` en de database-interacties toont aan dat er geen audit log wordt weggeschreven bij kritieke acties zoals het opraken van een ID-pool of mislukte verbindingen met externe identifier-bronnen. | Zonder applicatie-level audit logging is het onmogelijk om na een incident te reconstrueren wie welke identifiers wanneer heeft aangemaakt of aangepast. Dit schendt de principes van de AVG (zie `SAST-03` / `CWE-778`). |

---

## Conclusie

Uit deze Gap-analyse komen drie significante kwetsbaarheden naar voren die de compliancy van de module aan de NEN-7510:2026 in de weg staan:

1. **Gebrek aan audit logging op applicatieniveau (Control 8.15):** De module registreert cruciale acties (zoals ID-uitgifte of mislukte API-aanroepen) momenteel niet in een beveiligd audit log. Dit is de grootste gap, aangezien er geen sluitende audit trail is.
2. **Onvoldoende toegangscontrole op module-endpoints (Control 8.3):** Niet alle specifieke endpoints in de module dwingen de juiste RBAC-permissies af, waardoor ongeautoriseerde toegang mogelijk is.
3. **Kwetsbaarheid voor brute-force aanvallen (Control 8.5):** Er is geen actieve bescherming op netwerk- of applicatieniveau tegen geautomatiseerde inlogpogingen.

**Opvolging:**
Deze bevindingen zijn opgenomen in de geprioriteerde security backlog van de [Groep_6_Security-Analyse.md](Groep_6_Security-Analyse.md) (bevindingen `SAST-01` t/m `SAST-03`) en dienen als directe input voor de Proof of Concept (PoC) mitigaties en de implementatie van compliant audit logging in Sprint 3 (Opdracht 5).
