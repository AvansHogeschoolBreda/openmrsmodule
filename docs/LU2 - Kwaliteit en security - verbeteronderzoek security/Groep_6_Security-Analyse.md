# Security Analyse (SCA, SAST en SBOM)

**Groep: C6**

| Naam:             | Nummer: |
| ----------------- | ------- |
| Raf van Hooijdonk | 2230382 |
| Rowen Albers      | 2227982 |
| Simon Eulenpesch  | 2226731 |
| Sinan Sagir       | 2235816 |

---

## Bronnen

- [NEN-7510:2026 (informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
- [OWASP Top 10 (2021)](https://owasp.org/Top10/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [CycloneDX Maven Plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin)
- [Semgrep (OSS) Java ruleset p/java](https://semgrep.dev/p/java)
- [NIST National Vulnerability Database (NVD)](https://nvd.nist.gov/)
- [CWE (Common Weakness Enumeration)](https://cwe.mitre.org/)
- [Apache Struts 1 End-of-Life aankondiging](https://struts.apache.org/struts1eol-announcement.html)
- [Apache Log4j 1.x End-of-Life](https://logging.apache.org/log4j/1.2/)
- [NCSC Cybersecuritybeeld Nederland (CSBN) 2024](https://www.ncsc.nl/actueel/nieuws/2024/juni/18/cybersecuritybeeld-nederland-2024)
- [Verizon Data Breach Investigations Report (DBIR) 2024](https://www.verizon.com/business/resources/reports/dbir/)
- [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md) (assets, hazards, scoreschaal en risk appetite)
- [Groep_6_Risicomatrix.md](Groep_6_Risicomatrix.md) (CI/CD risico evaluatie)

---

## 1. Scope

Dit document beschrijft de uitgevoerde security analyse op de OpenMRS module **idgen** (ID Generation Module). De module genereert en beheert patiëntidentificatienummers binnen OpenMRS, onder andere via lokale identifier-bronnen en een remote identifier source die nummers over HTTP ophaalt.

Binnen scope:

- **SCA (Software Composition Analysis):** analyse van alle third-party dependencies op bekende kwetsbaarheden.
- **SAST (Static Application Security Testing):** statische analyse van de Java-broncode van de module.
- **SBOM (Software Bill of Materials):** machine-leesbaar overzicht van alle componenten.
- **Security backlog:** geprioriteerde lijst van bevindingen met CVE/CWE, CVSS en mitigatie.

Buiten scope: de OpenMRS core, de databaseserver, dynamische tests (DAST) en penetration tests. Die vallen onder Opdracht 4 Deel 3 (Risk Assessment Report) en Opdracht 6.

Deze analyse draait voor het eerst op de **echte module-code** en niet op de stub `pom.xml`. Daarmee vervalt de tijdelijke beperking die in Opdracht 1 voor SCA, SAST en SBOM gold.

---

## 2. Methodiek

De analyse volgt drie sporen die elkaar aanvullen:

1. **SCA** brengt risico in de dependency-keten in kaart. OWASP Dependency-Check koppelt elke library aan bekende CVE's via de NVD-feed.
2. **SBOM** legt de volledige componentenlijst vast als auditbaar artefact. Zonder SBOM is een dependency-analyse niet reproduceerbaar.
3. **SAST en code review** beoordelen de broncode zelf. Semgrep voert de geautomatiseerde scan uit; aanvullend is een handmatige security code review gedaan op de hoge-risico entry points (remote identifier fetching, logging, deserialisatie).

CVSS-scores in de security backlog zijn de NVD Base Scores (CVSS v3.1 waar beschikbaar, anders v2). Naast de base score is per bevinding een **contextuele score** bepaald op basis van bereikbaarheid in de module en de healthcare-impact, conform de scoreschaal in [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md) sectie 3.

---

## 3. Gebruikte tooling

| Tool                   | Type | Functie                                          | Bron                                                     |
| ---------------------- | ---- | ------------------------------------------------ | -------------------------------------------------------- |
| OWASP Dependency-Check | SCA  | Koppelt dependencies aan bekende CVE's via NVD   | [Link](https://owasp.org/www-project-dependency-check/)     |
| CycloneDX Maven Plugin | SBOM | Genereert machine-leesbare SBOM (CycloneDX 1.6)  | [Link](https://github.com/CycloneDX/cyclonedx-maven-plugin) |
| Semgrep (OSS)          | SAST | Statische code-analyse met de `p/java` ruleset | [Link](https://semgrep.dev/p/java)                          |

Semgrep OSS is gebruikt zonder login; daardoor draaien 60 community-regels en niet de 181 pro-regels. Dit is een bekende beperking en wordt in de interpretatie (sectie 7) meegenomen.

---

## 4. Uitgevoerde commando's en audit trail

De volgende commando's zijn uitgevoerd in de module-root `openmrs-module-idgen/`:

```bash
# SCA: dependency-analyse tegen de NVD-feed
mvn org.owasp:dependency-check-maven:check

# SBOM: geaggregeerde CycloneDX SBOM over alle submodules
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom

# SAST: statische code-analyse met de Java-ruleset
semgrep scan --config p/java .
```

Elk commando levert een technisch outputbestand op dat als audit trail in de repository is bewaard. "Niet gelogd is niet gebeurd": de bewijsbestanden maken de analyse herleidbaar en reproduceerbaar.

| Artefact                     | Tool                   | Inhoud                               | Locatie                                                             |
| ---------------------------- | ---------------------- | ------------------------------------ | ------------------------------------------------------------------- |
| dependency-check-report.html | OWASP Dependency-Check | SCA-rapport met CVE's per dependency | `openmrs-module-idgen/target/`, `api/target/`, `omod/target/` |
| bom.xml                      | CycloneDX              | SBOM in XML (CycloneDX 1.6)          | `openmrs-module-idgen/target/`                                    |
| bom.json                     | CycloneDX              | SBOM in JSON (CycloneDX 1.6)         | `openmrs-module-idgen/target/`                                    |
| sast-report.json             | Semgrep                | SAST-resultaten in JSON              | `openmrs-module-idgen/`                                           |

---

## 5. SCA: resultaten OWASP Dependency-Check

### 5.1 Samenvatting

Dependency-Check is succesvol uitgevoerd over de volledige dependency-tree van de module. De scan signaleert meerdere **CRITICAL** dependencies. Het betreft grotendeels verouderde libraries die via de legacy transitieve keten van het OpenMRS-platform binnenkomen. De meeste kwetsbaarheden zijn deserialisatie-gerelateerd (CWE-502) en hebben een publieke proof-of-concept.

### 5.2 Kwetsbare dependencies

| Component           | Versie        | Belangrijkste CVE | CWE     | CVSS (NVD) | Ernst     |
| ------------------- | ------------- | ----------------- | ------- | ---------- | --------- |
| struts-core         | 1.3.8         | CVE-2014-0114     | CWE-20  | 7.5 (v2)   | Hoog      |
| spring-core         | 3.0.5.RELEASE | CVE-2016-1000027  | CWE-502 | 9.8 (v3.1) | Kritiek   |
| xstream             | 1.4.3         | CVE-2013-7285     | CWE-502 | 9.8 (v3.1) | Kritiek   |
| postgresql (JDBC4)  | 9.0-801.jdbc4 | CVE-2018-10936    | CWE-295 | 4.2 (v3.0) | Gemiddeld |
| commons-collections | 3.2           | CVE-2015-7501     | CWE-502 | 9.8 (v3.1) | Kritiek   |
| log4j               | 1.2.15        | CVE-2019-17571    | CWE-502 | 9.8 (v3.1) | Kritiek   |

Struts 1 is sinds 2013 End-of-Life, log4j 1.x sinds 2015. Voor beide verschijnen geen security-patches meer. De overige libraries hebben wel een veilige versie beschikbaar (zie security backlog).

![1781050533766](image/Groep_6_Security-Analyse/1781050533766.png)

---

## 6. SBOM: CycloneDX

De CycloneDX Maven Plugin genereerde een geaggregeerde SBOM over alle submodules (api, omod, owa). De SBOM bevat **116 componenten** en is opgeslagen in zowel `bom.xml` als `bom.json` (CycloneDX specificatie 1.6).

| Eigenschap         | Waarde                            |
| ------------------ | --------------------------------- |
| Formaat            | CycloneDX 1.6                     |
| Aantal componenten | 116                               |
| Bestanden          | bom.xml, bom.json                 |
| Generatiemoment    | build-fase (`makeAggregateBom`) |

De SBOM is de machine-leesbare basis onder de SCA: elke kwetsbare library uit sectie 5 is herleidbaar tot een concrete `purl` in `bom.json`, bijvoorbeeld `pkg:maven/log4j/log4j@1.2.15` en `pkg:maven/com.thoughtworks.xstream/xstream@1.4.3`. Hiermee is het patchadvies traceerbaar naar de exacte component en versie.

![1781050499836](image/Groep_6_Security-Analyse/1781050499836.png)

---

## 7. SAST: resultaten Semgrep

De Semgrep-scan is succesvol uitgevoerd:

| Eigenschap        | Waarde                      |
| ----------------- | --------------------------- |
| Resultaat         | Scan completed successfully |
| Findings          | 0 (0 blocking)              |
| Regels uitgevoerd | 60 (community)              |
| Gescande targets  | 57                          |
| Parsed lines      | circa 100%                  |

Semgrep vond **geen directe automatische findings**. Dit betekent niet dat de module veilig is. Twee kanttekeningen:

1. **Beperkte ruleset.** Semgrep OSS draaide zonder login: 60 community-regels in plaats van het volledige pakket inclusief 181 pro-regels. Cross-file dataflow en supply-chain regels draaiden niet.
2. **SCA-risico valt buiten SAST.** De grootste risico's zitten in de dependencies (sectie 5), niet in eigen code. SAST detecteert dat per definitie niet.

Om die reden is aanvullend een **handmatige security code review** uitgevoerd op de hoge-risico entry points van de module: de remote identifier source (`RemoteIdentifierSourceProcessor`), de identifier-verwerking (`IdentifierSourceProcessor`) en de logging (`LogEntry`). De bevindingen daaruit staan als SAST-01 tot en met SAST-04 in de security backlog.

![1781050548418](image/Groep_6_Security-Analyse/1781050548418.png)

---

## 8. Security backlog

### 8.1 Overzichtstabel

Tien bevindingen: zes uit SCA (SCA-01 tot SCA-06) en vier uit SAST/code review (SAST-01 tot SAST-04).

| ID      | Bevinding                                             | Component / Locatie                         | CVE              | CWE     | CVSS (NVD) | Contextueel | NEN-7510   | Fix beschikbaar     | Effort | Prioriteit | Besluit                 |
| ------- | ----------------------------------------------------- | ------------------------------------------- | ---------------- | ------- | ---------- | ----------- | ---------- | ------------------- | ------ | ---------- | ----------------------- |
| SCA-01  | Verouderde Apache Struts dependency                   | struts-core 1.3.8                           | CVE-2014-0114    | CWE-20  | 7.5        | Hoog        | 8.8        | Nee (EOL)           | L      | Hoog       | Verwijderen / vervangen |
| SCA-02  | Verouderde Spring Core dependency                     | spring-core 3.0.5.RELEASE                   | CVE-2016-1000027 | CWE-502 | 9.8        | Kritiek     | 8.8        | Ja (5.3.x / 6.x)    | L      | Kritiek    | Patchen                 |
| SCA-03  | Kwetsbare XStream dependency                          | xstream 1.4.3                               | CVE-2013-7285    | CWE-502 | 9.8        | Kritiek     | 8.8        | Ja (1.4.21)         | M      | Kritiek    | Patchen                 |
| SCA-04  | Verouderde PostgreSQL JDBC driver                     | postgresql 9.0-801.jdbc4                    | CVE-2018-10936   | CWE-295 | 4.2        | Hoog        | 8.8        | Ja (42.7.x)         | S      | Hoog       | Patchen                 |
| SCA-05  | Onveilige commons-collections (deserialisatie-gadget) | commons-collections 3.2                     | CVE-2015-7501    | CWE-502 | 9.8        | Kritiek     | 8.8        | Ja (3.2.2 / 4.x)    | S      | Kritiek    | Patchen                 |
| SCA-06  | End-of-Life Log4j 1.x                                 | log4j 1.2.15                                | CVE-2019-17571   | CWE-502 | 9.8        | Kritiek     | 8.8        | Nee (EOL, migreren) | L      | Kritiek    | Vervangen               |
| SAST-01 | Improper Input Validation                             | RemoteIdentifierSourceProcessor             | n.v.t.           | CWE-20  | n.v.t.     | Hoog        | 8.25, 8.28 | Ja (code)           | M      | Hoog       | Patchen                 |
| SAST-02 | Improper Authentication                               | Remote identifier source                    | n.v.t.           | CWE-287 | n.v.t.     | Hoog        | 8.5        | Ja (code)           | M      | Hoog       | Patchen                 |
| SAST-03 | Insufficient Logging                                  | LogEntry / identifier-uitgifte              | n.v.t.           | CWE-778 | n.v.t.     | Hoog        | 8.15       | Ja (code)           | M      | Hoog       | Patchen                 |
| SAST-04 | Deserialization of Untrusted Data                     | XStream / log4j / commons-collections paden | n.v.t.           | CWE-502 | n.v.t.     | Kritiek     | 8.8, 8.25  | Ja (code + patch)   | M      | Kritiek    | Patchen                 |

### 8.2 Bevindingen in detail

#### SCA-01: Verouderde Apache Struts dependency

- **Component:** struts-core 1.3.8 (CVE-2014-0114, CWE-20).
- **Beschrijving:** Struts 1 is sinds 2013 End-of-Life. CVE-2014-0114 staat manipulatie van de `class`-property toe via onvoldoende invoervalidatie (ClassLoader-manipulatie), wat kan leiden tot remote code execution.
- **Risico:** Een aanvaller kan via gemanipuleerde requests interne objecteigenschappen aanpassen. Voor een EOL-component verschijnen geen patches meer.
- **Healthcare impact:** Compromittering van een module die patiëntidentifiers uitgeeft raakt direct de integriteit van patiëntkoppelingen. Een verkeerd toegekend identifier kan dossiers verwisselen.
- **Severity / prioriteit:** CVSS 7.5 (Hoog), contextueel Hoog. Prioriteit Hoog.
- **Mitigatie:** Struts 1 verwijderen. Moderne OpenMRS-modules gebruiken Spring MVC. Indien de transitieve afhankelijkheid via een ouder platform binnenkomt: platformversie verhogen of de afhankelijkheid uitsluiten.

#### SCA-02: Verouderde Spring Core dependency

- **Component:** spring-core 3.0.5.RELEASE (CVE-2016-1000027, CWE-502).
- **Beschrijving:** Spring 3.0.5 (2011) bevat een deserialisatie-kwetsbaarheid waarmee remote code execution mogelijk is wanneer onveilige data wordt gedeserialiseerd.
- **Risico:** Volledige systeemovername bij een bereikbaar deserialisatiepunt. CVSS 9.8.
- **Healthcare impact:** RCE op een component met databasetoegang geeft toegang tot patiëntgegevens (AVG Art. 9). Meldplicht AVG Art. 33 wordt actief bij exploitatie.
- **Severity / prioriteit:** CVSS 9.8 (Kritiek), contextueel Kritiek. Prioriteit Kritiek.
- **Mitigatie:** Upgraden naar een ondersteunde Spring-lijn (5.3.x of 6.x), afgestemd op de OpenMRS-platformversie.

#### SCA-03: Kwetsbare XStream dependency

- **Component:** xstream 1.4.3 (CVE-2013-7285, CWE-502).
- **Beschrijving:** XStream 1.4.3 deserialiseert XML zonder typebeperking. Een aanvaller kan via geprepareerde XML willekeurige objecten en daarmee code laten uitvoeren.
- **Risico:** Remote code execution via de XML-deserialisatiepaden. XStream wordt binnen OpenMRS gebruikt voor (de)serialisatie; de idgen remote identifier source verwerkt externe data.
- **Healthcare impact:** Identiek aan SCA-02: RCE met toegang tot patiëntdata.
- **Severity / prioriteit:** CVSS 9.8 (Kritiek), contextueel Kritiek. Prioriteit Kritiek.
- **Mitigatie:** Upgraden naar XStream 1.4.21 en een security-allowlist (`addPermission`) configureren zodat alleen verwachte typen worden gedeserialiseerd.

#### SCA-04: Verouderde PostgreSQL JDBC driver

- **Component:** postgresql 9.0-801.jdbc4 (CVE-2018-10936, CWE-295).
- **Beschrijving:** Deze driver dateert uit 2010 en is ruim vijftien jaar oud. Hij mist alle latere security-fixes. CVE-2018-10936 betreft onjuiste validatie waardoor een man-in-the-middle de TLS-verbinding kan onderscheppen.
- **Risico:** Onderschepping of manipulatie van het databaseverkeer tussen module en database. NVD CVSS 4.2 (Medium), contextueel verhoogd omdat het verkeer patiëntgegevens bevat.
- **Healthcare impact:** Een onderschepte databaseverbinding lekt of manipuleert patiëntgegevens in transit. Dit raakt zowel vertrouwelijkheid als integriteit.
- **Severity / prioriteit:** CVSS 4.2 (Gemiddeld), contextueel Hoog. Prioriteit Hoog.
- **Mitigatie:** Upgraden naar een ondersteunde driver (42.7.x) en TLS-certificaatvalidatie afdwingen.

#### SCA-05: Onveilige commons-collections

- **Component:** commons-collections 3.2 (CVE-2015-7501, CWE-502).
- **Beschrijving:** Commons-collections 3.2 bevat de beruchte `InvokerTransformer`-gadget die Java-deserialisatieaanvallen mogelijk maakt. Dit is een van de meest gebruikte RCE-gadgets.
- **Risico:** Remote code execution zodra een bereikbaar deserialisatiepunt onvertrouwde data verwerkt. CVSS 9.8.
- **Healthcare impact:** Volledige systeemovername met toegang tot patiëntdata.
- **Severity / prioriteit:** CVSS 9.8 (Kritiek), contextueel Kritiek. Prioriteit Kritiek.
- **Mitigatie:** Upgraden naar 3.2.2 (de kwetsbare gadget is daar standaard uitgeschakeld) of naar commons-collections4 4.x.

#### SCA-06: End-of-Life Log4j 1.x

- **Component:** log4j 1.2.15 (CVE-2019-17571, CWE-502).
- **Beschrijving:** Log4j 1.x is sinds augustus 2015 End-of-Life. CVE-2019-17571 betreft een deserialisatie-kwetsbaarheid in de `SocketServer`-klasse die RCE toelaat. Aanvullend zijn er CVE-2022-23305 (SQL-injectie via JDBCAppender, CWE-89) en CVE-2021-4104 (onveilige JMSAppender).
- **Risico:** RCE en SQL-injectie via de logging-laag. Voor een EOL-component verschijnen geen patches meer.
- **Healthcare impact:** De logging-laag verwerkt vaak gevoelige context. Compromittering raakt zowel de audit trail (NEN-7510 8.15) als de bredere systeemintegriteit.
- **Severity / prioriteit:** CVSS 9.8 (Kritiek), contextueel Kritiek. Prioriteit Kritiek.
- **Mitigatie:** Migreren naar Log4j 2.x (minimaal 2.17.1) of als tussenstap reload4j 1.2.25 als drop-in vervanging.

#### SAST-01: Improper Input Validation (CWE-20)

- **Locatie:** `RemoteIdentifierSourceProcessor` en de identifier-invoer.
- **Beschrijving:** De module haalt identifiers op bij een externe bron en verwerkt aangeleverde waarden. Onvoldoende validatie van externe respons en gebruikersinvoer maakt injectie of onverwachte verwerking mogelijk.
- **Risico:** Ongevalideerde invoer kan leiden tot foutieve identifiers, injectie of verstoring van de uitgifte. Semgrep OSS detecteerde dit niet automatisch (sectie 7).
- **Healthcare impact:** Foutieve of botsende patiëntidentifiers kunnen dossiers verwisselen, met direct risico voor patiëntveiligheid.
- **Severity / prioriteit:** Contextueel Hoog. Prioriteit Hoog.
- **Mitigatie:** Strikte allowlist-validatie op formaat en lengte van externe identifiers; afwijkende respons afwijzen en loggen.

#### SAST-02: Improper Authentication (CWE-287)

- **Locatie:** Remote identifier source en de aanroepende service-laag.
- **Beschrijving:** De koppeling met de externe identifier-bron en de bijbehorende endpoints moeten authenticatie en autorisatie afdwingen. Ontbrekende of zwakke authenticatie maakt ongeautoriseerde identifier-uitgifte mogelijk.
- **Risico:** Een ongeautoriseerde partij kan identifiers opvragen of de remote bron misbruiken.
- **Healthcare impact:** Ongeautoriseerde identifier-uitgifte ondermijnt de betrouwbaarheid van patiëntkoppelingen.
- **Severity / prioriteit:** Contextueel Hoog. Prioriteit Hoog.
- **Mitigatie:** Authenticatie afdwingen op de remote koppeling (token of mTLS) en RBAC-controle op alle uitgifte-acties (NEN-7510 8.5).

#### SAST-03: Insufficient Logging (CWE-778)

- **Locatie:** `LogEntry` en de identifier-uitgifte.
- **Beschrijving:** Beveiligingsrelevante gebeurtenissen (uitgifte van een identifier, mislukte remote-aanroep, autorisatiefout) worden onvoldoende vastgelegd. Zonder volledige audit trail geldt: niet gelogd is niet gebeurd.
- **Risico:** Incidenten zijn niet reconstrueerbaar; compliance-aantoonbaarheid ontbreekt.
- **Healthcare impact:** Bij een datalek kan niet worden aangetoond wie welke identifier wanneer heeft uitgegeven. Dit bemoeilijkt de meldplicht (AVG Art. 33).
- **Severity / prioriteit:** Contextueel Hoog. Prioriteit Hoog.
- **Mitigatie:** Audit logging toevoegen met UserID, timestamp, event, uitkomst en resource-UUID, conform NEN-7510 8.15. Geen BSN of medische data in logs.

#### SAST-04: Deserialization of Untrusted Data (CWE-502)

- **Locatie:** Deserialisatiepaden via XStream, log4j en commons-collections.
- **Beschrijving:** Dit is de code-zijde van de SCA-bevindingen SCA-02, SCA-03, SCA-05 en SCA-06. Waar de module externe of opgeslagen data deserialiseert, vormen de kwetsbare libraries een direct RCE-pad.
- **Risico:** Remote code execution via een geketende gadget. Dit is het hoogste technische risico in de module.
- **Healthcare impact:** Volledige systeemovername met toegang tot patiëntgegevens.
- **Severity / prioriteit:** Contextueel Kritiek. Prioriteit Kritiek.
- **Mitigatie:** Combineer het patchen van de libraries (sectie 5) met allowlist-deserialisatie en het vermijden van Java-native deserialisatie van onvertrouwde bronnen.

### 8.3 False positives en risicoacceptaties

- Semgrep leverde nul findings op; er zijn dus geen SAST-false-positives te documenteren.
- Geen enkele bevinding is geaccepteerd zonder mitigatie. De kritieke deserialisatie-bevindingen (SCA-02, SCA-03, SCA-05, SCA-06, SAST-04) zijn niet acceptabel zonder fix conform de risk appetite in [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md) sectie 3.4.
- De definitieve bereikbaarheidsbepaling per deserialisatiepad (welke paden daadwerkelijk onvertrouwde data verwerken) wordt in het Risk Assessment Report (Opdracht 4 Deel 3) afgerond.

---

## 9. Healthcare impact en risico's voor patiëntgegevens

De idgen-module zit op een gevoelig punt: ze kent patiëntidentifiers toe. Twee soorten schade zijn relevant.

1. **Integriteit van patiëntkoppelingen.** Een gemanipuleerde of foutieve identifier kan dossiers verwisselen. Dat is geen abstract IT-risico maar een direct patiëntveiligheidsrisico: een behandelaar baseert beslissingen op het verkeerde dossier.
2. **Vertrouwelijkheid van patiëntdata.** De kritieke deserialisatie-kwetsbaarheden (CVSS 9.8) geven bij exploitatie toegang tot het systeem en daarmee tot patiëntgegevens. Patiëntdata is bijzondere categorie persoonsgegevens (AVG Art. 9). Een lek activeert de meldplicht binnen 72 uur (AVG Art. 33) en mogelijk naar betrokkenen (Art. 34).

De zorgsector is een primair doelwit: DBIR 2024 plaatst healthcare in de top-5 meest aangevallen sectoren en NCSC CSBN 2024 noemt ransomware en supply-chain risico als groeiende dreiging.

---

## 10. NEN-7510:2026 relevantie

| Control   | Omschrijving                           | Betrokken bevindingen      |
| --------- | -------------------------------------- | -------------------------- |
| Ctrl 8.8  | Beheer van technische kwetsbaarheden   | SCA-01 t/m SCA-06, SAST-04 |
| Ctrl 8.5  | Veilige authenticatie                  | SAST-02                    |
| Ctrl 8.15 | Logging van beveiligingsgebeurtenissen | SAST-03, SCA-06            |
| Ctrl 8.25 | Veilige ontwikkellevenscyclus          | SAST-01, SAST-04           |
| Ctrl 8.28 | Veilig programmeren                    | SAST-01                    |

Ctrl 8.8 vereist tijdige detectie en beoordeling van technische kwetsbaarheden en passende maatregelen. De SCA, SBOM en deze backlog vormen samen het bewijs dat aan de detectie- en beoordelingseis wordt voldaan. De patchstap (mitigatie) volgt in het Patchadvies en het Risk Assessment Report.

---

## 11. Risico van legacy dependencies in zorgsoftware

De zwaarste risico's zitten niet in eigen code maar in de meegesleepte legacy keten. Struts 1 (2013 EOL), Log4j 1.x (2015 EOL), Spring 3.0.5 (2011), XStream 1.4.3 en commons-collections 3.2 zijn allemaal jaren oud en deels onhoudbaar.

Drie structurele problemen maken dit in zorgsoftware extra zwaar:

1. **Geen patches meer.** Voor EOL-componenten (Struts 1, Log4j 1.x) verschijnen geen fixes. Het risico is permanent tot de component is vervangen.
2. **Transitieve overerving.** Deze libraries komen via het oudere OpenMRS-platform binnen. Patchen vereist daarom coördinatie met de platformversie, niet alleen een losse bump in de module.
3. **Deserialisatie als rode draad.** Vier van de zes SCA-bevindingen zijn CWE-502. Een enkel bereikbaar deserialisatiepad maakt meerdere gadgets tegelijk exploiteerbaar.

In een zorgcontext betekent een uitgestelde patch dat een bekend, publiek exploiteerbaar RCE-pad open blijft staan op een systeem dat patiëntgegevens verwerkt. Dat is niet verenigbaar met de lage risk appetite voor vertrouwelijkheid van patiëntdata uit de asset-identificatie.

---

## 12. Conclusie

SAST, SCA en SBOM zijn alle drie succesvol uitgevoerd op de echte idgen-module en als auditbare bewijsbestanden vastgelegd (`dependency-check-report.html`, `bom.xml`, `bom.json`, `sast-report.json`).

De kernbevindingen:

1. **De SCA signaleert meerdere kritieke dependencies.** Spring Core, XStream, commons-collections en Log4j 1.x scoren CVSS 9.8 en zijn grotendeels deserialisatie-gerelateerd (CWE-502).
2. **De SBOM (116 componenten) maakt de analyse reproduceerbaar.** Elke kwetsbare library is herleidbaar tot een concrete `purl`.
3. **Semgrep vond geen automatische findings, maar dat is geen vrijbrief.** De ruleset was beperkt (OSS) en het grootste risico zit in de dependencies. De aanvullende code review leverde vier relevante bevindingen op (CWE-20, CWE-287, CWE-778, CWE-502).
4. **Legacy libraries vormen het grootste risico.** EOL-componenten zonder patchpad, binnengekomen via de transitieve keten, op een systeem dat patiëntidentifiers beheert.

Vervolgacties zijn nodig voor compliance (NEN-7510 Ctrl 8.8) en securityverbetering: een geprioriteerd Patchadvies opstellen, de bereikbaarheid per deserialisatiepad bevestigen, en de mitigaties valideren in het Risk Assessment Report (Opdracht 4 Deel 3). De kritieke bevindingen vereisen actie op korte termijn conform de termijnen in `SECURITY.md`.

---

## 13. Koppeling naar andere deliverables

| Deliverable                               | Koppeling                                                                             |
| ----------------------------------------- | ------------------------------------------------------------------------------------- |
| Groep_6_Asset-Identificatie.md            | Scoreschaal, risk appetite en hazards die deze bevindingen contextualiseren           |
| Groep_6_Risicomatrix.md                   | CI/CD-risico's die naast deze module-bevindingen staan                                |
| Opdracht 4 Deel 3: Risk Assessment Report | Deze backlog en SBOM worden opgenomen; bereikbaarheid en kostenraming volgen daar     |
| Patchadvies (Sprint 2 taak 2.6)           | SCA-bevindingen leveren de concrete versie-aanbevelingen voor het patchadvies         |
| Opdracht 6: Auditrapport                  | SBOM, SAST-output en security backlog zijn verplichte bijlagen (Deliverable 3, eis 7) |
