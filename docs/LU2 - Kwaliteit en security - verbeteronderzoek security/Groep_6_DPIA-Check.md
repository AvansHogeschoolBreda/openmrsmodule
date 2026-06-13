# DPIA-check: openmrs-module-idgen

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## Bronnen

- [AVG (GDPR): Verordening (EU) 2016/679](https://eur-lex.europa.eu/legal-content/NL/TXT/?uri=CELEX%3A32016R0679)
- [WP248 rev.01: Guidelines on Data Protection Impact Assessment (Art. 29 Working Party)](https://ec.europa.eu/newsroom/article29/items/611236)
- [Autoriteit Persoonsgegevens: DPIA uitvoeren](https://www.autoriteitpersoonsgegevens.nl/themas/beveiliging/data-protection-impact-assessment-dpia)
- [NEN-7510:2026 (informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
- [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md)
- [Groep_6_Logging_Gap_Analyse.md](Groep_6_Logging_Gap_Analyse.md)
- [Groep_6_Bow-Tie.md](Groep_6_Bow-Tie.md)

---

## 1. Scope

Dit document beoordeelt of de verwerking van persoonsgegevens door de `openmrs-module-idgen` een verplichte Data Protection Impact Assessment (DPIA) vereist conform AVG artikel 35.

Binnen scope: de module zelf, de gegenereerde identifiers, de auditlogs die door de module worden weggeschreven, en de endpoints waarop de module persoonsgegevens verwerkt.

Buiten scope: de OpenMRS core, de onderliggende databaseserver, netwerkcommunicatie buiten de module en deploymentinfrastructuur.

---

## 2. Methodologie

De pre-assessment volgt de negen drempelcriteria uit [WP248 rev.01](https://ec.europa.eu/newsroom/article29/items/611236), gepubliceerd door de Art. 29 Working Party. Wanneer twee of meer criteria van toepassing zijn, is een volledige DPIA verplicht conform AVG art. 35 lid 1 en lid 3.

De beoordeling is gebaseerd op:

1. Analyse van de module-broncode en de uitgevoerde Logging Gap-Analyse (Groep_6_Logging_Gap_Analyse.md).
2. De Asset-Identificatie en BIV-analyse (Groep_6_Asset-Identificatie.md).
3. De beveiligingsbevindingen uit de Security-Analyse (Groep_6_Security-Analyse.md).

---

## 3. Verwerkingsactiviteiten in de module

### 3.1 Welke persoonsgegevens verwerkt de module?

De `idgen`-module genereert en beheert patiëntidentificatienummers binnen OpenMRS. De volgende persoonsgegevens komen voor:

| Gegevenscategorie | Omschrijving | AVG-categorie |
|---|---|---|
| Patiëntidentifiers (PID) | Door de module gegenereerde nummers die direct verwijzen naar patiëntdossiers | Indirect bijzondere categorie (Art. 9): via de dossierrelatie worden gezondheidsinformatie en identifiers onlosmakelijk gekoppeld |
| Gebruikersinformatie in auditlogs | UserID en gebruikersnaam van zorgverleners, vastgelegd per audit-event via `Context.getAuthenticatedUser()` | Gewone persoonsgegevens (Art. 6) van medewerkers |
| Credentials in URL-parameters (vóór mitigatie) | Het endpoint `/exportIdentifiers.form` accepteerde `username` en `password` als queryparameter; deze kwamen daardoor in server-access logs terecht | Bijzonder gevoelig: inloggegevens |

### 3.2 Verwerkingsgrondslag

| Grondslag | Toelichting |
|---|---|
| AVG Art. 6 lid 1 sub c | Wettelijke verplichting: patiëntidentificatie is wettelijk vereist in de Nederlandse zorg |
| AVG Art. 9 lid 2 sub h | Gezondheidszorg: verwerking is noodzakelijk voor diagnose en zorgverlening |

De idgen-module verwerkt zelf geen directe gezondheidsgegevens (diagnoses, behandelingen). De gegenereerde identifiers zijn echter onlosmakelijk gekoppeld aan patiëntdossiers die wel bijzondere categoriegegevens (Art. 9) bevatten. Toegang tot een identifier is daarmee functioneel gelijk aan toegang tot de dossierrelatie.

---

## 4. AVG Art. 35 Drempeltoets

| # | Criterium (WP248 rev.01) | Van toepassing? | Motivatie |
|---|---|---|---|
| 1 | Profilering of scoring van personen | Nee | De module kent nummers toe. Er worden geen gedragsprofielen of scores over personen aangemaakt. |
| 2 | Geautomatiseerde besluitvorming met rechtsgevolg of aanzienlijke invloed | Nee | Er worden geen geautomatiseerde beslissingen over patiënten of medewerkers genomen. |
| 3 | Stelselmatige monitoring van betrokkenen | **Ja** | De geïmplementeerde NEN-7510 audit logging (Groep_6_Logging_Gap_Analyse.md sectie 8) legt stelselmatig vast wie welke patiëntidentifier wanneer heeft gegenereerd of gewijzigd. |
| 4 | Bijzondere categorieën of gerechtelijke gegevens (Art. 9) | **Ja** | Patiëntidentifiers zijn functioneel direct verbonden aan gezondheidsinformatie (Art. 9). Elk gegenereerd nummer verwijst naar een patiëntdossier met medische data. |
| 5 | Grootschalige verwerking | **Ja** | Ziekenhuissystemen en zorginstellingen die OpenMRS inzetten verwerken patiëntgegevens op grote schaal. Een instelling van gemiddelde omvang registreert tienduizenden tot miljoenen patiëntidentifiers. |
| 6 | Matching of combineren van datasets | Nee | De module koppelt geen externe datasets aan elkaar. |
| 7 | Kwetsbare betrokkenen | **Ja** | Patiënten zijn per definitie kwetsbare betrokkenen: zij bevinden zich in een afhankelijke positie ten opzichte van zorgverleners. |
| 8 | Innovatief of nieuw gebruik van technologieën | Nee | Idgen is een bestaande OpenMRS-module zonder nieuwe of experimentele technologie. |
| 9 | Overdracht van persoonsgegevens buiten de EU | Nee | Afhankelijk van deploymentomgeving. Bij een cloud-deployment buiten de EU is dit criterium alsnog van toepassing. |

**Uitkomst: 4 van 9 criteria zijn van toepassing (criteria 3, 4, 5 en 7).**

Conform WP248 rev.01 is bij twee of meer criteria een volledige DPIA verplicht. **Een DPIA is verplicht vóór productie-deployment van de `openmrs-module-idgen`.**

---

## 5. Privacy-risico's en mitigerende maatregelen

| # | Risico | Kans (1-5) | Impact (1-5) | Score | Status | Mitigatie | NEN-7510 |
|---|---|---|---|---|---|---|---|
| P1 | Credentials (username/password) lekken via URL-parameters naar server-access logs | 4 | 5 | 20 (kritiek) | Gedeeltelijk gemitigeerd | URL-credentials verwijderen; sessie-authenticatie of Basic Auth headers gebruiken (aanbeveling in Groep_6_Logging_Gap_Analyse.md sectie 7.3) | Ctrl 8.5, 8.15 |
| P2 | Bulk-export van patiëntidentifier-reeksen door onbevoegde gebruiker | 3 | 5 | 15 (hoog) | Open | RBAC afdwingen op export-endpoints; audit van elke export-actie (geïmplementeerd in `IdentifierSourceController`) | Ctrl 8.5, 8.15 |
| P3 | Auditlogs zijn te lang bewaard of onvoldoende beveiligd, waardoor medewerkerdata lekt | 3 | 3 | 9 (middel) | Open | Retentiebeleid opstellen; toegangsbeperking auditlogs tot autorisatiemanager of compliance-officer | Ctrl 8.15 |
| P4 | Patiëntidentifiers zijn te koppelen aan gezondheidsdata via de OpenMRS-database | 4 | 5 | 20 (kritiek) | Structureel residueel | Data minimization: auditlogs bevatten ResourceUUID van de bronconfigurator, nooit de gegenereerde identifier-waarde zelf. Negatieve JUnit-test bevestigt dit (Groep_6_Logging_Gap_Analyse.md sectie 9.1, test 3). | Ctrl 8.5, 8.25 |
| P5 | Ontbrekende audit trail maakt incidenten onreconstrueerbaar | 5 | 4 | 20 (kritiek) | Gemitigeerd | NEN-7510 8.15 audit logging geïmplementeerd voor alle kritieke events (READ, SAVE, PURGE, RETIRE, EXPORT). Tests groen. | Ctrl 8.15 |

### Privacy by Design principes

De volgende Privacy by Design principes (conform Cavoukian, 2009) zijn aantoonbaar toegepast:

| Principe | Implementatie |
|---|---|
| Data minimization | Auditlogs bevatten alleen UserID en ResourceUUID. De daadwerkelijk gegenereerde identifier-waarden staan nooit in de logs. |
| Doelbinding | Identifiers worden uitsluitend gegenereerd voor patiëntregistratie in de gekoppelde OpenMRS-instantie. |
| Integriteit en vertrouwelijkheid | Toegangsbeperking via het OpenMRS RBAC-systeem (rollen en privileges). |
| Traceerbaarheid | Audit logging van alle mutaties met Wie/Wat/Wanneer/Waarop/Resultaat conform NEN-7510 8.15. |

---

## 6. Residueel risico na mitigatie

Na de geïmplementeerde maatregelen (audit logging, data minimization in logs) resteren de volgende risico's:

1. **Credentials in URL (P1):** De aanbeveling uit de logging gap-analyse is nog niet in code doorgevoerd. Zolang `/exportIdentifiers.form` credentials als queryparameter accepteert, lopen deze alsnog in server-logs. Dit vereist een aparte code-aanpassing.

2. **Koppelbaarheid identifiers aan gezondheidsdata (P4):** Dit is een structureel residueel risico. Elke patiëntidentifier verwijst naar een dossier. Mitigatie op module-niveau is beperkt; aanvullend beleid bij de implementerende zorginstelling (toegangsbeperking op databaseniveau, pseudonimisering) is vereist.

3. **Ontbrekend retentiebeleid voor auditlogs (P3):** De module schrijft logs weg maar bepaalt geen retentieduur. De implementerende instelling moet een retentiebeleid vaststellen conform de Wet op de geneeskundige behandelingsovereenkomst (Wgbo: 20 jaar bewaarplicht voor dossiers; auditlogs minimaal 6 maanden conform NEN-7510).

---

## 7. Conclusie

De `openmrs-module-idgen` verwerkt persoonsgegevens in de zin van de AVG. De verwerking raakt bijzondere categorieën (gezondheidsinformatie via patiëntidentifiers), kwetsbare betrokkenen, vindt stelselmatig en grootschalig plaats. Vier van negen WP248-criteria zijn van toepassing.

**Een volledige DPIA is verplicht vóór productie-deployment conform AVG Art. 35.**

De meest urgente privacy-risico's (ontbrekende audit trail, data in logs) zijn gemitigeerd door de NEN-7510 8.15 implementatie. De aanbeveling is om vóór livegang:

1. Credentials uit URL-parameters te verwijderen (P1).
2. Een retentiebeleid voor auditlogs op te stellen (P3).
3. Een volledige DPIA uit te voeren, inclusief advies van de Functionaris Gegevensbescherming (FG) van de implementerende instelling.
