# Asset identificatie & Threat modeling

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## 1. Scope en methodiek

### 1.1 Scope

Dit document beschrijft de asset identificatie en threat modeling voor de OpenMRS module die in dit project centraal staat. De module draait als onderdeel van het OpenMRS systeem, een open-source elektronisch patiëntendossier (EPD) ingezet in de zorg. Binnen scope vallen:

- De OpenMRS module zelf (broncode, REST API, dataverwerking)
- De ondersteunende CI/CD pipeline (GitHub Actions)
- De data die de module verwerkt en opslaat (patiëntdata, credentials, logs)
- De configuratie en secrets die de module en pipeline gebruiken

Buiten scope: de OpenMRS core (niet door ons ontwikkeld), de databaseserver zelf, en netwerkinfrastructuur van de zorginstelling.

### 1.2 Methodiek: STRIDE

Threat modeling is uitgevoerd conform de **STRIDE-methode** (Microsoft, 2006), een gestructureerde aanpak die dreigingen indeelt in zes categorieen:

| Letter | Dreiging | Kernvraag |
|---|---|---|
| S | Spoofing (identiteitsvervalsing) | Kan een aanvaller doen alsof hij een legitieme gebruiker is? |
| T | Tampering (manipulatie) | Kan een aanvaller data of code aanpassen? |
| R | Repudiation (ontkenning) | Kan een aanvaller acties ontkennen zonder sporen? |
| I | Information Disclosure (data-exposure) | Kan een aanvaller ongeautoriseerd informatie inzien? |
| D | Denial of Service (uitval) | Kan een aanvaller het systeem onbeschikbaar maken? |
| E | Elevation of Privilege (rechtenesclatie) | Kan een aanvaller meer rechten verkrijgen dan toegestaan? |

Elke geidentificeerde hazard in dit document is gekoppeld aan een of meer STRIDE-categorieen. Dit maakt de analyse reproduceerbaar en herleidbaar.

### 1.3 Relevante wet- en regelgeving

| Norm / Wet | Relevantiee |
|---|---|
| NEN-7510:2026 | Informatiebeveiliging in de zorg. Verplicht kader voor Nederlandse zorginstellingen. |
| AVG / GDPR Art. 9 | Patiëntdata is "bijzondere categorie persoonsgegevens". Verwerking vereist expliciete grondslag. |
| AVG Art. 32 | Passende technische en organisatorische maatregelen verplicht. |
| AVG Art. 33/34 | Meldplicht bij datalek binnen 72 uur (AP) en eventueel naar betrokkenen. |
| Wbp / UAVG | Nationale implementatie AVG voor zorgspecifieke verplichtingen. |

### 1.4 Gebruikte bronnen

- [OpenMRS Data Model](https://wiki.openmrs.org/display/docs/Data+Model)
- [OpenMRS REST API documentatie](https://rest.openmrs.org/)
- [OpenMRS Security & Authentication](https://wiki.openmrs.org/display/docs/Security+and+Authentication)
- [OpenMRS Audit Log Module](https://wiki.openmrs.org/display/docs/Audit+Log+Module)
- [NEN-7510:2026 (informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
- [OWASP Top 10 (2021)](https://owasp.org/Top10/)
- [OWASP Top 10 CI/CD Security Risks](https://owasp.org/www-project-top-10-ci-cd-security-risks/)
- [NCSC Cybersecuritybeeld Nederland (CSBN) 2024](https://www.ncsc.nl/actueel/nieuws/2024/juni/18/cybersecuritybeeld-nederland-2024)
- [Verizon Data Breach Investigations Report (DBIR) 2024](https://www.verizon.com/business/resources/reports/dbir/)
- [CWE (Common Weakness Enumeration)](https://cwe.mitre.org/)
- [WS03: Healthcare Risk Assessment (ICT-I2.4 Security)](../assets/presentaties/ICT-I2.4%20Security%20WS03%20-%20Healthcare%20Risk%20Assessment.pdf)

---

## 2. Threat actors

Voordat assets en hazards worden beoordeeld, worden de realistische threat actors voor een zorgsysteem als OpenMRS vastgesteld. De kansscores van hazards zijn mede gebaseerd op de activiteit van deze actoren.

| ID | Actor | Motivatie | Capaciteit | Relevantie voor OpenMRS |
|---|---|---|---|---|
| TA1 | Externe aanvaller (cybercrimineel) | Financieel gewin via ransomware of dataverkoop | Hoog (georganiseerde groepen, toolkits beschikbaar) | Hoog: zorginstellingen zijn frequent doelwit. DBIR 2024: healthcare is top-5 aangevallen sector. |
| TA2 | Insider (medewerker met kwade opzet) | Datadiefstal, sabotage, wraak | Gemiddeld (directe toegang, kennis van systeem) | Hoog: insiders hebben legitieme toegang tot patiëntdata. NCSC CSBN 2024 noemt insider threats als groeiend risico. |
| TA3 | Insider (onbewuste fout) | Geen: per ongeluk | Laag (fout, niet opzettelijk) | Hoog: menselijke fouten (hardcoded secrets, verkeerde configuratie) zijn de meest voorkomende oorzaak van datalekken (DBIR 2024: 68% van breaches heeft menselijk element). |
| TA4 | Supply chain aanvaller | Toegang via gecompromitteerde dependency of tool | Hoog (gerichte aanvallen op open-source ecosysteem) | Gemiddeld: OpenMRS gebruikt Java/Maven dependencies. Log4Shell (2021) toonde aan hoe breed dit risico is. |
| TA5 | Script kiddie / opportunist | Reputatie, nieuwsgierigheid | Laag (gebruikt bestaande exploits) | Gemiddeld: als het systeem publiek bereikbaar is, zijn geautomatiseerde scans en brute-force aanvallen constant aanwezig. |

---

## 3. Scoreschaal, risk appetite en grenswaarden

Alle risico's worden gescoord op kans en impact (beide 1-5). Scores zijn onderbouwd met sectordata uit DBIR 2024 en NCSC CSBN 2024.

### 3.1 Kansschaal

| Score | Label | Omschrijving | Sectoronderbouwing |
|---|---|---|---|
| 1 | Zeldzaam | Minder dan 1x per jaar in vergelijkbare systemen | Geen bekende exploits, geen sector-incidenten |
| 2 | Onwaarschijnlijk | Circa 1x per jaar in de sector | Incidenten gedocumenteerd maar zeldzaam |
| 3 | Mogelijk | Maandelijks in de zorgsector | NCSC CSBN 2024: ransomware en credential attacks frequent in zorg |
| 4 | Waarschijnlijk | Wekelijks of actief geexploiteerd | DBIR 2024: brute-force en credential stuffing in top-3 aanvalsvectoren |
| 5 | Bijna zeker | Actieve exploit beschikbaar, systeem is direct doelwit | CVE met publieke PoC, systeem actief gescand |

### 3.2 Impactschaal

| Score | Label | Omschrijving | Juridische consequentie |
|---|---|---|---|
| 1 | Verwaarloosbaar | Geen verstoring, geen data-exposure, intern oplosbaar | Geen |
| 2 | Laag | Beperkte verstoring, geen persoonsgegevens gelekt | Geen meldplicht |
| 3 | Gemiddeld | Tijdelijke uitval of beperkte data-exposure zonder directe schade | Mogelijk intern onderzoek vereist |
| 4 | Hoog | Patiëntdata gelekt, behandeling vertraagd, meldplicht actief | AVG Art. 33: meldplicht AP binnen 72 uur |
| 5 | Kritiek | Massale data-exposure, patiëntveiligheid in gevaar, reputatieschade | AVG Art. 33 + 34: meldplicht AP en betrokkenen; NEN-7510 ernstig incident |

### 3.3 Risicoscore

Risicoscore = Kans x Impact. Maximale score: 25.

### 3.4 Risk appetite en grenswaarden

| Kleur | Score | Betekenis | Verplichte actie |
|---|---|---|---|
| Groen | 1-4 | Acceptabel risico | Monitoren; jaarlijkse herbeoordeling |
| Oranje | 5-12 | Verhoogd risico | Mitigerende maatregel verplicht binnen 3 maanden |
| Rood | 13-25 | Onacceptabel risico | Onmiddellijke actie verplicht; escalatie naar management |

De organisatie hanteert een **lage risk appetite** voor vertrouwelijkheid van patiëntgegevens, conform NEN-7510 en AVG Art. 9. Elk risico met impact 4 of 5 op een asset met Vertrouwelijkheidsclassificatie "Kritiek" of "Hoog" krijgt automatisch prioriteit, ongeacht de totaalscore.

---

## 4. Asset identificatie

### 4.1 Overzichtstabel

| Asset ID | Asset naam | Categorie | Vertrouwelijkheid | Integriteit | Beschikbaarheid | Hoogste BIV | NEN-7510 Primaire Control |
|---|---|---|---|---|---|---|---|
| A1 | Patiëntobservaties (obs) | Patiëntdata | Kritiek | Kritiek | Hoog | Kritiek | 5.33, 8.3 |
| A2 | Gebruikersreferenties (credentials) | Authenticatie | Kritiek | Hoog | Hoog | Kritiek | 8.5, 5.16 |
| A3 | Audit logs | Logging | Gemiddeld | Kritiek | Hoog | Kritiek | 8.15, 8.17 |
| A4 | Module broncode | Softwareasset | Gemiddeld | Hoog | Gemiddeld | Hoog | 8.8, 8.29 |
| A5 | CI/CD pipeline configuratie | Infrastructuur | Hoog | Kritiek | Hoog | Kritiek | 8.8, 8.9 |
| A6 | Secrets en API-sleutels | Configuratie | Kritiek | Kritiek | Hoog | Kritiek | 8.24, 5.17 |
| A7 | SBOM en dependency-informatie | Softwareasset | Gemiddeld | Gemiddeld | Laag | Gemiddeld | 8.8 |
| A8 | Systeminstellingen (module config) | Configuratie | Gemiddeld | Hoog | Hoog | Hoog | 8.9, 8.6 |

---

## 5. BIV-analyse per asset

Per asset worden classificaties toegelicht, gevoelige gegevens benoemd, bestaande controls beschreven en het residuele risico vastgesteld.

---

### A1: Patiëntobservaties (obs)

Patiëntobservaties zijn de kern van het EPD. Zij bevatten meetwaarden (bloeddruk, gewicht, laboratoriumuitslagen), diagnoses en medicatiegegevens die direct worden gebruikt bij behandelbeslissingen.

**Vertrouwelijkheid: Kritiek**
Patiëntdata valt onder AVG Art. 9 (bijzondere categorie: gezondheidsdata). Verwerking vereist expliciete grondslag. Ongeautoriseerde toegang leidt tot meldplicht bij de Autoriteit Persoonsgegevens (AVG Art. 33) en mogelijk naar betrokkenen (Art. 34). OpenMRS slaat observaties op in de `obs` tabel, gekoppeld aan `patient_id` en `concept_id` (OpenMRS Data Model). De combinatie van deze velden maakt re-identificatie mogelijk, ook zonder naam.

**Integriteit: Kritiek**
Gemanipuleerde observatiedata (tampered of corrupted) kan leiden tot een verkeerde diagnose of behandeling en direct gevaar voor de patient. De module schrijft observaties via de REST API. Buiten rolgebaseerde autorisatie (RBAC) heeft OpenMRS geen ingebouwde write-protection op veldniveau.

**Beschikbaarheid: Hoog**
Uitval verhindert invoer van nieuwe meetwaarden. In een klinische setting is dit direct operationeel risico. Tijdelijke uitval (minuten) is acceptabel; langdurige uitval (uren) vereist papieren noodprocedures.

**Gevoelige velden:** `obs.value_numeric`, `obs.value_text`, `obs.value_coded`, `obs.concept_id` gecombineerd met `patient_id` en `encounter_id`.

**Bestaande controls:**
- RBAC: rolgebaseerde toegangscontrole per gebruiker (NEN-7510 Ctrl 5.16)
- HTTPS op de REST API (transport-encryptie)
- Audit logging via OpenMRS Audit Log Module (NEN-7510 Ctrl 8.15)

**Residueel risico:** Toegangscontrole op applicatieniveau, maar geen database-level encryptie (at-rest). Een aanvaller met directe databasetoegang omzeilt de RBAC.

**Referenties:** [OpenMRS Data Model](https://wiki.openmrs.org/display/docs/Data+Model), NEN-7510:2026 Ctrl 5.33 en 8.3, AVG Art. 9, OWASP A01:2021 (Broken Access Control).

---

### A2: Gebruikersreferenties (credentials)

Gebruikersreferenties bestaan uit gebruikersnamen, wachtwoorden (bcrypt hash, OpenMRS standaard) en sessiestokens. OpenMRS ondersteunt RBAC: rollen bepalen welke observaties een gebruiker kan inzien of aanpassen.

**Vertrouwelijkheid: Kritiek**
Gelekte credentials geven directe toegang tot patiëntdata. Een gecompromitteerd admin-account geeft volledige systeemtoegang inclusief het aanpassen van RBAC-rollen.

**Integriteit: Hoog**
Gemanipuleerde credentials of RBAC-configuratie geven een aanvaller permanente toegang, ook na wachtwoordresets van andere accounts. RBAC-integriteit is ook NEN-7510 Ctrl 5.16-relevant.

**Beschikbaarheid: Hoog**
Als het authenticatiesysteem uitvalt, is het gehele EPD geblokkeerd voor alle gebruikers.

**Gevoelige velden:** `users.password` (bcrypt hash), sessiestokens in HTTP-cookies of Authorization-headers.

**Bestaande controls:**
- Bcrypt password hashing (NEN-7510 Ctrl 8.24)
- Sessiebeheer via OpenMRS core
- HTTPS transport-encryptie

**Residueel risico:** Geen rate limiting of account lockout geconfigureerd in de standaard OpenMRS installatie. Brute-force aanvallen op het login-endpoint zijn daardoor niet geblokkeerd (CWE-307: Improper Restriction of Excessive Authentication Attempts).

**Referenties:** [OpenMRS Security & Authentication](https://wiki.openmrs.org/display/docs/Security+and+Authentication), NEN-7510:2026 Ctrl 8.5 en 5.16, AVG Art. 32, OWASP A07:2021 (Identification and Authentication Failures), [CWE-307](https://cwe.mitre.org/data/definitions/307.html).

---

### A3: Audit logs

Audit logs registreren wie welke actie heeft uitgevoerd op welk moment. Zij zijn essentieel voor forensisch onderzoek, incident response en aantoonbaarheid van NEN-7510-compliance. Het principe: "niet gelogd = niet gebeurd".

**Vertrouwelijkheid: Gemiddeld**
Logs bevatten gebruikersnamen en actiecodes maar geen directe klinische patiëntdata. Combinatie van `patient_id`, `action` en `user_id` kan echter indirect gevoelig zijn (bv. welke arts keek naar welke patient).

**Integriteit: Kritiek**
Als logs worden aangepast of verwijderd, verliest de organisatie de mogelijkheid om incidenten te reconstrueren. Gemanipuleerde logs zijn waardeloos als juridisch bewijsmateriaal (NEN-7510 Ctrl 8.17: bescherming van logbestanden). Dit is ook relevant voor AVG Art. 33: een lek dat niet in de logs staat, is moeilijk te bewijzen en te melden.

**Beschikbaarheid: Hoog**
Uitval van logging heeft geen directe impact op patienten, maar verhindert compliance-aantoonbaarheid en realtime incident detection.

**Gevoelige velden:** `audit_log.user_id`, `audit_log.action`, `audit_log.object_type`, `audit_log.object_uuid`, `audit_log.date_created`.

**Bestaande controls:**
- OpenMRS Audit Log Module (NEN-7510 Ctrl 8.15)
- Logs worden geschreven door de applicatielaag, niet direct door eindgebruikers

**Residueel risico:** Logs worden opgeslagen in dezelfde database als patiëntdata. Een admin met database-toegang kan logregels verwijderen of aanpassen zonder dat dit zelf gelogd wordt. Er is geen aparte, write-protected logopslag (bv. WORM-storage of externe SIEM).

**Referenties:** NEN-7510:2026 Ctrl 8.15 en 8.17, [OpenMRS Audit Log Module](https://wiki.openmrs.org/display/docs/Audit+Log+Module), OWASP A09:2021 (Security Logging and Monitoring Failures).

---

### A4: Module broncode

De broncode bevat de businesslogica, REST endpoints, dataverwerking en validatieregels van de module. De code wordt beheerd in de private GitHub repository.

**Vertrouwelijkheid: Gemiddeld**
De repo is privaat. Blootstelling van de broncode vergroot het aanvalsoppervlak (aanvaller kent de exacte validatielogica en kan gerichte exploits schrijven). Security through obscurity is geen maatregel op zichzelf, maar exposure vermijden is verdediging in de diepte.

**Integriteit: Hoog**
Gemanipuleerde broncode via een supply chain aanval (gecompromitteerde dependency of kwaadaardige commit) kan backdoors of kwetsbaarheden introduceren. Dit wordt gemitigeerd door branch protection, verplichte PR-reviews en CodeQL SAST.

**Beschikbaarheid: Gemiddeld**
Verlies van de code is herstelbaar via git history en eventuele backups. Geen directe patiëntimpact. Tijdelijk verlies van de codebase verhindert alleen nieuwe deployments.

**Gevoelige elementen:** Eventuele hardcoded credentials of configuratiewaarden (moeten er niet in zitten, maar zijn een bekend risico). Logica die aanvallers kunnen reverse-engineeren.

**Bestaande controls:**
- Branch protection op `main` (geconfigureerd, niet afgedwongen op Free plan)
- Verplichte PR-reviews
- CodeQL SAST op elke push (NEN-7510 Ctrl 8.8)
- Dependabot automatische dependency updates

**Residueel risico:** Branch protection is niet volledig afdwingbaar op GitHub Free (private repo). Een repo-eigenaar kan direct naar `main` pushen. Zie checklist eis #1.

**Referenties:** NEN-7510:2026 Ctrl 8.8 en 8.29, OWASP A08:2021 (Software and Data Integrity Failures), CWE-506 (Embedded Malicious Code).

---

### A5: CI/CD pipeline configuratie

De CI/CD pipeline (GitHub Actions workflows) bepaalt hoe code gebouwd, getest en gedeployed wordt. Een gecompromitteerde pipeline kan kwaadaardige code in productie brengen of secrets exfiltreren.

**Vertrouwelijkheid: Hoog**
Workflow-YAML-bestanden zijn zichtbaar voor iedereen met repo-toegang. Secrets worden via GitHub Environments beheerd en zijn niet zichtbaar in logs. Workflow-logs zijn echter wel zichtbaar en kunnen per ongeluk gevoelige informatie bevatten.

**Integriteit: Kritiek**
Een gemanipuleerde workflow kan: (1) SAST-checks omzeilen, (2) kwaadaardige dependencies injecteren, (3) secrets exfiltreren naar een externe server. Dit is OWASP CI/CD Security Risk #4 (Poisoned Pipeline Execution). De pipeline is de laatste verdedigingslinie voor productie.

**Beschikbaarheid: Hoog**
Uitval verhindert deployments en vertraagt het patchen van kwetsbaarheden.

**Gevoelige elementen:** GitHub secrets (environment variabelen), deployment tokens, verwijzingen naar interne systemen in workflow-configuratie.

**Bestaande controls:**
- GitHub Environments met protection rules (production: 1 rule)
- Secrets gescheiden per environment
- SAST (CodeQL), Dependency Review, SBOM-generatie als verplichte stappen

**Residueel risico:** Workflow-bestanden kunnen worden aangepast via een PR. Een kwaadaardige PR kan een workflow introduceren die secrets logt. De Dependency Review blokkeert HIGH/CRITICAL packages maar controleert niet op kwaadaardige code in de dependency zelf.

**Referenties:** NEN-7510:2026 Ctrl 8.8 en 8.9, OWASP Top 10 CI/CD Security Risks (CICD-SEC-4): https://owasp.org/www-project-top-10-ci-cd-security-risks/, CWE-506.

---

### A6: Secrets en API-sleutels

Secrets omvatten database-wachtwoorden, API-sleutels voor externe diensten en deployment tokens. In de repository worden deze opgeslagen als encrypted GitHub Secrets per environment.

**Vertrouwelijkheid: Kritiek**
Een gelekte secret geeft een aanvaller directe toegang tot het systeem of externe diensten, zonder verdere authenticatie. Secrets in git-history zijn permanent blootgesteld, ook na verwijdering (git history is doorzoekbaar).

**Integriteit: Kritiek**
Als secrets worden vervangen door een aanvaller, kunnen productiedeployments worden gestuurd naar een kwaadaardig systeem (man-in-the-middle op deployniveau).

**Beschikbaarheid: Hoog**
Als secrets verloren gaan of rotatie mislukt, kan de pipeline niet deployen naar productie.

**Gevoelige elementen:** Database credentials, deployment tokens, externe API-sleutels. Deze mogen nooit in broncode of logs verschijnen.

**Bestaande controls:**
- GitHub Encrypted Secrets per environment (NEN-7510 Ctrl 8.24)
- Secrets worden niet gelogd door GitHub Actions (automatisch gemaskeerd)
- Gescheiden secrets per environment (production / test)

**Residueel risico:** GitHub Secret Scanning is niet beschikbaar op het Free plan voor private repos. Een per ongeluk gecommit secret wordt niet automatisch gedetecteerd. Handmatige detectie na het feit is de enige optie.

**Referenties:** NEN-7510:2026 Ctrl 8.24 en 5.17, OWASP A02:2021 (Cryptographic Failures), CWE-321 (Use of Hard-coded Cryptographic Key), OWASP CI/CD Risk #6 (Insufficient Credential Hygiene).

---

### A7: SBOM en dependency-informatie

De Software Bill of Materials (SBOM) bevat een overzicht van alle dependencies met versienummers en licenties. Gegenereerd via de `sbom.yml` workflow (CycloneDX JSON-formaat).

**Vertrouwelijkheid: Gemiddeld**
De SBOM onthult de volledige dependency-stack inclusief versienummers. Dit vergemakkelijkt gerichte aanvallen als een kwetsbare versie aanwezig is (aanvaller weet exact welke CVE van toepassing is). Intern is de SBOM echter noodzakelijk voor vulnerability management.

**Integriteit: Gemiddeld**
Een gemanipuleerde SBOM kan kwetsbaarheden verbergen, waardoor patchadvies incorrect wordt. Minder kritiek dan patiëntdata maar relevant voor supply chain security.

**Beschikbaarheid: Laag**
Uitval van SBOM-generatie heeft geen directe impact op patienten. Het verhindert wel vulnerability tracking en compliceert audits.

**Bestaande controls:**
- CycloneDX SBOM-generatie via Anchore/Syft bij elke push naar `main`
- SBOM opgeslagen als CI-artifact (90 dagen retentie)
- Dependency Review Action blokkeert HIGH/CRITICAL dependencies bij PRs

**Residueel risico:** De SBOM monitort momenteel alleen een stub `pom.xml` met minimale dependencies. Pas volledig effectief bij de echte module.

**Referenties:** CycloneDX standaard: https://cyclonedx.org/, NEN-7510:2026 Ctrl 8.8, OWASP A06:2021 (Vulnerable and Outdated Components).

---

### A8: Systeminstellingen (module configuratie)

Systeminstellingen bevatten configuratiewaarden die het gedrag van de module bepalen: welke concepten actief zijn, welke REST-endpoints beschikbaar zijn, logging-niveau en foutmeldingsdetail.

**Vertrouwelijkheid: Gemiddeld**
Configuratiewaarden zijn geen patiëntdata. Debug-instellingen of endpoint-configuratie kunnen aanvalsmogelijkheden onthullen als ze in foutmeldingen of API-responses lekken.

**Integriteit: Hoog**
Gemanipuleerde configuratie kan: logging uitschakelen, verkeerde concepten activeren, of debug-modus in productie inschakelen. Dit heeft directe impact op de betrouwbaarheid van het systeem en de audit trail.

**Beschikbaarheid: Hoog**
Corrupte of ontbrekende configuratie verhindert het opstarten van de module. Dit leidt tot uitval van het EPD-onderdeel.

**Bestaande controls:**
- Configuratie beheerd via OpenMRS Admin interface met RBAC
- Configuratiewijzigingen gelogd via Audit Log Module

**Residueel risico:** Geen versiebeheersysteem voor configuratiewijzigingen. Rollback na een verkeerde configuratiewijziging vereist handmatige interventie.

**Referenties:** NEN-7510:2026 Ctrl 8.9 en 8.6, OpenMRS Administration: https://wiki.openmrs.org/display/docs/Administration.

---

## 6. Hazard analyse

### 6.1 Hazards per asset

Per hazard worden het dreigingstype (STRIDE), de betrokken threat actors, CWE-nummer (waar van toepassing) en een toelichting gegeven.

| Asset | Hazard ID | Hazard | STRIDE | Threat Actor | CWE | Toelichting |
|---|---|---|---|---|---|---|
| A1 | H1 | Ongeautoriseerde toegang via gecompromitteerd account | S, I | TA1, TA2 | CWE-284 | Gestolen credentials (phishing, credential stuffing) geven toegang tot alle observaties van een patient. Zorg is top-doelwit voor credential-based aanvallen (DBIR 2024). |
| A1 | H2 | SQL-injectie via REST API | T, I | TA1, TA5 | CWE-89 | Onvoldoende invoervalidatie in de module kan leiden tot directe databasequery-manipulatie. OWASP A03:2021. |
| A2 | H3 | Brute-force aanval op login-endpoint | S, D | TA1, TA5 | CWE-307 | Geen rate limiting of account lockout in standaard OpenMRS configuratie. DBIR 2024: brute-force in top-3 aanvalsvectoren. |
| A2 | H4 | Credential stuffing via gelekte databases | S, I | TA1 | CWE-1391 | Hergebruik van wachtwoorden door zorgmedewerkers. HaveIBeenPwned registreerde 10+ miljard unieke credentials in 2024. |
| A3 | H5 | Log tampering door insider met admin-rechten | T, R | TA2 | CWE-117 | Admin kan logregels verwijderen of aanpassen in dezelfde database zonder secundaire detectie. |
| A3 | H6 | Log injection via kwaadaardige invoer | T, R | TA1, TA5 | CWE-117 | Aanvaller injecteert logregels via gemanipuleerde API-invoer om de audit trail te verstoren of te vervalsen. |
| A4 | H7 | Supply chain aanval via gecompromitteerde dependency | T, E | TA4 | CWE-506 | Een gecompromitteerde Maven-library introduceert backdoor-code. Precedent: Log4Shell CVE-2021-44228, XZ Utils CVE-2024-3094. |
| A5 | H8 | Workflow poisoning via kwaadaardige PR | T, E | TA1, TA2 | CWE-506 | Aanvaller opent een PR met aangepaste workflow-YAML die SAST omzeilt of secrets exfiltreert. OWASP CICD-SEC-4. |
| A5 | H9 | Secrets exfiltratie via workflow-logs | I | TA1, TA3 | CWE-532 | Secrets worden per ongeluk naar logs geschreven via `echo`, `env` of foutmeldingen. Onopzettelijk (TA3) of opzettelijk (TA1). |
| A6 | H10 | Hardcoded secret in broncode | I | TA3, TA1 | CWE-321 | Developer commit per ongeluk een wachtwoord of token naar de repo. Zonder Secret Scanning (niet beschikbaar op Free plan) blijft dit ongedetecteerd. |
| A7 | H11 | Kwetsbare dependency niet gesignaleerd | I | TA4 | CWE-1035 | SBOM-generatie faalt of CVE niet gekoppeld aan gebruikte versie. Resultaat: geen patchadvies voor een exploitable library. |
| A8 | H12 | Debug-modus actief in productie | I | TA3 | CWE-209 | Configuratiefout laat stack traces of interne systeeminformatie lekken via API-foutmeldingen. OWASP A05:2021. |

### 6.2 Hazard scorering

Kans en impact gescoord op basis van de schalen in sectie 3, onderbouwd met sectordata.

| Hazard ID | Kans (1-5) | Onderbouwing kans | Impact (1-5) | Onderbouwing impact | Score | Kleur |
|---|---|---|---|---|---|---|
| H1 | 3 | NCSC CSBN 2024: phishing en credential theft frequent in zorg | 5 | Directe toegang tot patiëntdata, AVG Art. 33 meldplicht | 15 | Rood |
| H2 | 2 | SQL-injectie minder frequent door frameworks, maar aanwezig in legacy modules | 5 | Volledige database-exposure mogelijk | 10 | Oranje |
| H3 | 4 | DBIR 2024: brute-force top-3 aanvalsvector, geautomatiseerde tools breed beschikbaar | 4 | Account compromise, toegang tot patiëntdata | 16 | Rood |
| H4 | 3 | HaveIBeenPwned: 10B+ gelekte credentials; hergebruik wachtwoorden common | 4 | Account compromise, patiëntdata exposure | 12 | Oranje |
| H5 | 2 | Insider tampering vereist bewuste actie en verhoogde toegang | 5 | Vernietiging van forensisch bewijsmateriaal, compliance-verlies | 10 | Oranje |
| H6 | 2 | Log injection vereist specifieke kennis van het systeem | 3 | Audit trail verstoord, moeilijker incident response | 6 | Oranje |
| H7 | 2 | Supply chain aanvallen nemen toe (NCSC CSBN 2024), maar gerichte aanvallen op OpenMRS-specifieke modules zijn zeldzaam | 5 | Backdoor in productie, volledig systeemcompromis | 10 | Oranje |
| H8 | 2 | Vereist write-toegang tot de repo; PR-reviews zijn een barriere | 5 | Pipeline gecompromitteerd, secrets gelekt, kwaadaardige deployments | 10 | Oranje |
| H9 | 3 | Menselijke fout frequent (TA3); per ongeluk `echo $SECRET` in debug-stap | 4 | Secret zichtbaar in public/private logs, directe credential exposure | 12 | Oranje |
| H10 | 3 | DBIR 2024: 68% van breaches heeft menselijk element; hardcoded secrets top-10 misvatting | 5 | Direct credential exposure in git history; permanent zonder secret rotation | 15 | Rood |
| H11 | 3 | SBOM-generatie faalt bij stub pom.xml; CVE-koppeling afhankelijk van tooling | 3 | Kwetsbare dependency ongepatched, maar afhankelijk van specifieke CVE | 9 | Oranje |
| H12 | 3 | Configuratiefouten common bij deployment; debug-modus vergeten uit te zetten | 3 | Interne informatie gelekt, aanvaller krijgt extra context | 9 | Oranje |

### 6.3 Rode hazards en prioritering

Hazards met score 13+ (rood): **H3 (16), H1 (15), H10 (15)**.

| Prioriteit | Hazard | Score | Reden |
|---|---|---|---|
| 1 | H3: Brute-force op login-endpoint | 16 | Hoogste score, direct exploiteerbaar zonder speciale kennis, geautomatiseerde tools breed beschikbaar |
| 2 | H1: Ongeautoriseerde toegang via gecompromitteerd account | 15 | Hoge kans (phishing actief in zorg), maximale impact (patiëntdata + meldplicht) |
| 3 | H10: Hardcoded secret in broncode | 15 | Hoge kans (menselijke fout), maximale impact (credential permanent gelekt in git history) |

**Geselecteerde hazard voor bow-tie analyse (Deel 2): H10 (Hardcoded secret in broncode)**

Keuze boven H3 (hogere score) omdat H10 beter aansluit bij de CI/CD context van dit project en de beschikbare controls (Secret Scanning ontbreekt op Free plan, Dependabot dekt dit niet). H10 illustreert ook hoe een menselijke fout (TA3) en een aanvaller (TA1) dezelfde kwetsbaarheid kunnen benutten, en hoe preventieve barrières in de pipeline (pre-commit hooks, SAST) en herstelbarrières (secret rotation, git history rewrite) samenwerken. H3 wordt meegenomen in de risicomatrix (Deel 2, `04-risicomatrix.md`).

---

## 7. STRIDE-overzicht per asset

| Asset | S (Spoofing) | T (Tampering) | R (Repudiation) | I (Info Disc.) | D (DoS) | E (Priv. Esc.) | Primaire STRIDE |
|---|---|---|---|---|---|---|---|
| A1: Patiëntobs | Hoog (H1) | Hoog (H2) | Gemiddeld | Kritiek (H1, H2) | Laag | Laag | I, T |
| A2: Credentials | Kritiek (H3, H4) | Gemiddeld | Laag | Kritiek | Hoog (H3) | Hoog | S, I |
| A3: Audit logs | Laag | Kritiek (H5, H6) | Kritiek (H5) | Gemiddeld | Laag | Laag | T, R |
| A4: Broncode | Laag | Hoog (H7) | Laag | Gemiddeld | Laag | Hoog (H7) | T, E |
| A5: CI/CD | Laag | Kritiek (H8) | Laag | Hoog (H9) | Gemiddeld | Kritiek (H8) | T, E |
| A6: Secrets | Hoog (H10) | Hoog | Laag | Kritiek (H10) | Gemiddeld | Hoog | I, S |
| A7: SBOM | Laag | Gemiddeld (H11) | Laag | Gemiddeld | Laag | Laag | T |
| A8: Config | Laag | Hoog (H12) | Laag | Gemiddeld (H12) | Hoog | Laag | T, I |

---

## 8. Conclusie

De meest kritieke assets zijn **A1 (patiëntobservaties)**, **A2 (credentials)**, **A5 (CI/CD pipeline)** en **A6 (secrets)**. Alle vier hebben een Vertrouwelijkheids- of Integriteitsclassificatie van "Kritiek" en zijn direct gekoppeld aan patiëntveiligheid of systeemintegriteit.

De drie rode hazards (H3, H1, H10) vereisen onmiddellijke actie conform de risk appetite in sectie 3. De bow-tie analyse voor H10 wordt uitgewerkt in `Groep_6_Bow-Tie.md`. De volledige risicomatrix voor alle hazards staat in `Groep_6_Risicomatrix.md`.

Bestaande controls (RBAC, HTTPS, CodeQL, Dependabot, audit logging) reduceren het risico maar laten significante residuele risico's open, met name op het gebied van rate limiting (H3), Secret Scanning (H10) en write-protected logopslag (H5). Deze residuele risico's worden meegenomen in de prioritering van mitigaties in Opdracht 4 en 5.
