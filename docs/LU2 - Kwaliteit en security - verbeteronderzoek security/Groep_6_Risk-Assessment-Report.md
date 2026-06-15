# Risk Assessment Report (RAR)

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

### Bronnenlijst
* [NEN-7510:2026 (Informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
* [OWASP Top 10 (2021)](https://owasp.org/Top10/)
* [NIST National Vulnerability Database (NVD)](https://nvd.nist.gov/)
* [MITRE CWE (Common Weakness Enumeration)](https://cwe.mitre.org/)
* [NCSC Cybersecuritybeeld Nederland (CSBN) 2024](https://www.ncsc.nl/actueel/nieuws/2024/juni/18/cybersecuritybeeld-nederland-2024)
* [Verizon Data Breach Investigations Report (DBIR) 2024](https://www.verizon.com/business/resources/reports/dbir/)
* [Autoriteit Persoonsgegevens: Verwerking van gezondheidsgegevens (AP)](https://autoriteitpersoonsgegevens.nl/nl/onderwerpen/algemene-informatie-avg/bijzondere-persoonsgegevens)
* [Autoriteit Persoonsgegevens: Meldplicht Datalekken (AP)](https://autoriteitpersoonsgegevens.nl/nl/onderwerpen/algemene-informatie-avg/datalekken)

---

## 1. Managementsamenvatting

Dit Risk Assessment Report beschrijft de actuele beveiligingsstatus van de OpenMRS ID Generation Module (idgen) versie 4.9.0. Het rapport is opgesteld voor het management en de producteigenaren om een weloverwogen besluit te kunnen nemen over de ingebruikname van deze module in een productieomgeving. Uit de gecombineerde resultaten van de Software Composition Analysis (SCA), Static Application Security Testing (SAST) en handmatige code reviews blijkt dat de huidige versie van de module fundamenteel onveilig is. De module verwerkt patiëntgegevens. Dit zijn bijzondere persoonsgegevens onder [Artikel 9 van de Algemene Verordening Gegevensbescherming (AVG)](https://autoriteitpersoonsgegevens.nl/nl/onderwerpen/algemene-informatie-avg/bijzondere-persoonsgegevens). De beveiliging hiervan moet voldoen aan de hoogste eisen van de [NEN-7510:2026 normering](https://www.nen.nl/nen-7510).

De uitvoering van de veiligheidsscans resulteerde in de identificatie van tien afzonderlijke kwetsbaarheden. Vijf van deze kwetsbaarheden hebben het risiconiveau Kritiek gekregen. De codebase leunt zwaar op verouderde third-party libraries die het einde van hun levenscyclus hebben bereikt. Componenten zoals Log4j 1.x en verouderde versies van het Spring Framework bevatten publiek bekende kwetsbaarheden die actieve exploitatie mogelijk maken. Een kwaadwillende actor kan via deze componenten volledige controle over de server verkrijgen. Dit concept staat bekend as Remote Code Execution (RCE). Bij een dergelijke systeemovername zijn alle onderliggende patiëntendossiers direct toegankelijk voor de aanvaller.

De impact van een succesvolle aanval op deze module is catastrofaal voor de zorginstelling. Naast de directe risico's voor de patiëntveiligheid door mogelijke manipulatie van medische gegevens, leidt een datalek van deze omvang tot aanzienlijke reputatieschade. Bovendien activeert een dergelijk lek direct de meldplicht bij de Autoriteit Persoonsgegevens conform [AVG Artikel 33](https://autoriteitpersoonsgegevens.nl/nl/onderwerpen/algemene-informatie-avg/datalekken). Incidenten uit het recente verleden bij vergelijkbare zorgorganisaties tonen aan dat het niet naleven van informatiebeveiligingsrichtlijnen kan resulteren in hoge boetes en ingrijpende maatregelen vanuit de Inspectie Gezondheidszorg en Jeugd.

Op basis van de geconstateerde kwetsbaarheden en het ontbreken van basale beveiligingsmaatregelen zoals afdoende inputvalidatie en sluitende audit logs, geven wij een negatief advies voor productie-deployment. De module mag onder geen beding patiëntdata verwerken totdat de kritieke bevindingen uit de security backlog zijn gemitigeerd. Het risico overschrijdt de vastgestelde risicobereidheid van de organisatie ruimschoots.

---

## 2. Scope en Methodologie

De scope van deze risicobeoordeling beperkt zich specifiek tot de broncode en de externe afhankelijkheden van de OpenMRS ID Generation Module (idgen). Deze module is verantwoordelijk voor het genereren en toewijzen van unieke identificatienummers aan patiënten binnen het OpenMRS ecosysteem. De onderliggende infrastructuur, de OpenMRS core applicatie en de databaseservers vallen buiten de scope van dit specifieke rapport. Dynamische applicatietests (DAST via OWASP ZAP) en actieve penetratietesten zijn uitgevoerd in Opdracht 5. De DAST-rapportage is beschikbaar in `docs/dast/` en de penetratietest is gedocumenteerd in `Groep_6_Pentestrapport.md`.

De beoordeling is uitgevoerd aan de hand van een gestructureerde methodologie. De identificatie van dreigingen is gebaseerd op het STRIDE-model. Er zijn scans uitgevoerd op de daadwerkelijke modulecode en niet langer op placeholder bestanden. De analyse bestaat uit de volgende drie pijlers:
1. Ten eerste is een Software Composition Analysis uitgevoerd met OWASP Dependency-Check. Dit proces koppelt alle gebruikte externe libraries aan de NIST National Vulnerability Database (NVD) om bekende kwetsbaarheden te identificeren.
2. Ten tweede is een Software Bill of Materials (SBOM) gegenereerd. Dit is een machinaal leesbare inventarisatie van alle 116 componenten in het CycloneDX 1.6 formaat. Dit document borgt de reproduceerbaarheid van de analyse en voldoet aan de eisen van [NEN-7510:2026 Ctrl 8.8 (Beheer van technische kwetsbaarheden)](https://www.nen.nl/nen-7510).
3. Ten derde is een Static Application Security Testing (SAST) analyse uitgevoerd met Semgrep. Omdat de automatische regels van de open-source versie van Semgrep beperkt zijn, is deze scan aangevuld met een uitgebreide handmatige security code review op de meest risicovolle onderdelen van de broncode.

De weging van de geïdentificeerde risico's is gebaseerd op een kwantitatieve schaal van 1 tot 25. De waarschijnlijkheid van een aanval en de uiteindelijke impact zijn beide gescoord op een schaal van 1 tot 5. Deze scores zijn gekalibreerd met behulp van sector-specifieke dreigingsinformatie uit het [Cybersecuritybeeld Nederland (CSBN) 2024](https://www.ncsc.nl/actueel/nieuws/2024/juni/18/cybersecuritybeeld-nederland-2024) en het [Verizon Data Breach Investigations Report (DBIR) 2024](https://www.verizon.com/business/resources/reports/dbir/). Risico's met een score van 13 of hoger vallen in de rode categorie en vereisen onmiddellijke mitigerende maatregelen.

### 2.1 Threat Actoren

In lijn met de dreigingsanalyse zijn de volgende drie primaire threat actoren geïdentificeerd als de meest reële gevaren voor de systemen:
* **Cybercrimineel (TA1):** Gemotiveerd door financieel gewin door middel van gijzelsoftware (ransomware) of de diefstal en doorverkoop van medische dossiers. Deze actor beschikt over geavanceerde aanvalsmogelijkheden (high capability) en maakt actief gebruik van geautomatiseerde scans om bekende kwetsbaarheden te exploiteren.
* **Onbewuste Insider (TA3):** Zorgmedewerkers of ontwikkelaars die onopzettelijk menselijke fouten maken. Dit omvat onder andere het per ongeluk commiten van hardcoded secrets, misconfiguraties in de pipeline of het uitschakelen van noodzakelijke validatie (high likelihood, low capability).
* **Supply Chain Attacker (TA4):** Externe actoren die proberen toegang te krijgen tot het systeem door kwaadaardige code te injecteren in upstream open-source afhankelijkheden (high capability, medium relevance).

---

## 3. Systeemcontext en Bedrijfsimpact

Om de impact van de gevonden kwetsbaarheden goed te kunnen wegen, is inzicht in de werking van de module noodzakelijk. De idgen module stelt zorgverleners in staat om patiënten uniek te identificeren via vier mechanismen: een sequentiële generator, een lokale pool van nummers, een externe remote identifier source en een op maat gemaakte generator. De betrouwbaarheid van deze identificatienummers is van levensbelang. Een fout in de toewijzing kan ertoe leiden dat medische observaties aan het verkeerde patiëntendossier worden gekoppeld. Dit raakt direct de integriteit van de patiëntdata en vormt een primair risico voor de patiëntveiligheid.

Wanneer een aanvaller misbruik maakt van de kwetsbaarheden in de module en toegang krijgt tot de database, heeft deze direct toegang tot gevoelige medische dossiers en het datamodel van OpenMRS. Dit omvat met name:
* De obs tabel (patiëntobservaties), waarin medische meetwaarden en klinische data zoals `obs.value_numeric` en `obs.concept_id` direct gekoppeld zijn aan de patiënt via `obs.person_id` (aangezien `patient_id` in de `patient` tabel gekoppeld is aan de `person_id` in de overkoepelende `person` tabel). Een lek in deze tabel compromitteert de privacy en integriteit van het medisch dossier fundamenteel.
* De patient_identifier tabel, waarin de door deze module gegenereerde identifiers worden opgeslagen (met name de kolommen `patient_identifier.identifier` en `patent_identifier.identifier_type` gekoppeld aan `patient_identifier.patient_id`). Manipulatie van deze tabel door een aanvaller kan leiden tot patiëntenverwisseling, waardoor behandelaars beslissingen baseren op foutieve medische dossiers.

Daarnaast communiceert de module via REST API endpoints. Deze endpoints zijn direct benaderbaar over het netwerk. Wanneer deze endpoints onvoldoende zijn beveiligd, ontstaat er een digitaal aanvalsoppervlak dat kwaadwillenden kunnen misbruiken om de onderliggende database te benaderen. Het ontbreken van adequate audit logging binnen deze module maakt het bovendien nagenoeg onmogelijk om na een incident te reconstrueren welke gegevens door onbevoegden zijn ingezien of gemanipuleerde. Dit schendt het basisprincipe van verantwoordingsplicht uit de AVG.

---

## 4. Top-5 Bevindingen en Risico's

De onderstaande vijf kwetsbaarheden vormen de grootste dreiging voor de vertrouwelijkheid en integriteit van het systeem. Deze bevindingen vereisen onmiddellijke actie.

**1. Deserialization of Untrusted Data via applicatielogica (SAST-04)**
* **Beschrijving:** Deze kwetsbaarheid bevindt zich in de Java broncode van de module. De applicatie maakt gebruik van onveilige deserialisatiepaden via de XStream en Log4j libraries. Deserialisatie is het proces waarbij data over een netwerk wordt omgezet naar Java-objecten. Omdat de invoer niet afdoende wordt gevalideerd, kan een aanvaller gemanipuleerde data meesturen die de server dwingt om kwaadaardige code uit te voeren.
* **Risico & Consequentie:** Dit leidt tot een volledige compromittering van de server. De bedrijfsimpact is maximaal. Aanvallers kunnen patiëntgegevens exfiltreren of manipuleren. Dit is een directe schending van [NEN-7510:2026 Ctrl 8.28 (Veilig programmeren)](https://www.nen.nl/nen-7510) en [NEN-7510:2026 Ctrl 8.8 (Beheer van technische kwetsbaarheden)](https://www.nen.nl/nen-7510).
* **Classificatie:** [CWE-502 (Deserialization of Untrusted Data)](https://cwe.mitre.org/data/definitions/502.html). De CVSS-score is contextueel beoordeeld als Kritiek (RCE).

**2. Kwetsbare XStream dependency (SCA-03)**
* **Beschrijving:** Uit de Software Composition Analysis blijkt dat de module XStream versie 1.4.3 gebruikt. Deze versie bevat een bekende kwetsbaarheid waarbij de library XML-data deserialiseert zonder enige vorm van typebeperking.
* **Risico & Consequentie:** Een kwaadwillende actor kan via de API voor externe identificatienummers schadelijke XML payloads insturen. Wanneer de module deze XML verwerkt, wordt de payload uitgevoerd. Dit resulteert wederom in Remote Code Execution. Omdat deze library diep in de applicatie is verweven, brengt dit de continuïteit van de zorginstelling in direct gevaar.
* **Classificatie:** [CVE-2013-7285](https://nvd.nist.gov/vuln/detail/CVE-2013-7285) / [CWE-502](https://cwe.mitre.org/data/definitions/502.html). CVSS base score van 9.8. Prioriteit Kritiek conform [NEN-7510:2026 Ctrl 8.8](https://www.nen.nl/nen-7510).

**3. Verouderd Spring Core Framework (SCA-02)**
* **Beschrijving:** De module is gebouwd op Spring Core versie 3.0.5.RELEASE. Deze versie stamt uit 2011 en is zwaar verouderd. De kwetsbaarheid bevindt zich in de `HttpInvokerServiceExporter` component.
* **Risico & Consequentie:** Als deze component onvertrouwde data verwerkt, kan een aanvaller op afstand code uitvoeren. Het gebruik van componenten die het einde van hun levensduur hebben bereikt is een onacceptabel risico in een zorgomgeving. Componenten zonder actieve beveiligingsupdates kunnen niet worden beschermd tegen nieuwe aanvalstechnieken.
* **Classificatie:** [CVE-2016-1000027](https://nvd.nist.gov/vuln/detail/CVE-2016-1000027) / [CWE-502](https://cwe.mitre.org/data/definitions/502.html). CVSS base score van 9.8. Belemmert compliance met [NEN-7510:2026 Ctrl 8.8](https://www.nen.nl/nen-7510).

**4. Onveilige commons-collections library (SCA-05)**
* **Beschrijving:** De afhankelijkheden van de module bevatten commons-collections versie 3.2. Deze specifieke versie is berucht vanwege de `InvokerTransformer` gadget. Dit is een van de meest misbruikte componenten voor Java deserialisatie-aanvallen.
* **Risico & Consequentie:** Zodra een aanvaller een ingang vindt om data naar deze library te sturen, is systeemovername een feit. De impact op de organisatie omvat massale datalekken en mogelijke gijzeling van het systeem door ransomware.
* **Classificatie:** [CVE-2015-7501](https://nvd.nist.gov/vuln/detail/CVE-2015-7501) / [CWE-502](https://cwe.mitre.org/data/definitions/502.html). CVSS base score van 9.8. Mitigatie is vereist conform [NEN-7510:2026 Ctrl 8.8](https://www.nen.nl/nen-7510).

**5. End-of-Life Log4j 1.x (SCA-06)**
* **Beschrijving:** De applicatie maakt voor logregistratie gebruik van Log4j versie 1.2.15. Deze versie is sinds 2015 end-of-life (EOL). Dit betekent dat er geen beveiligingsupdates meer verschijnen. De specifieke versie bevat een lek in de `SocketServer` klasse.
* **Risico & Consequentie:** Als een aanvaller verbinding kan maken met de applicatie en specifieke logregels kan triggeren, ontstaat er wederom een mogelijkheid voor Remote Code Execution. Het permanente karakter van dit risico maakt vervanging van de library noodzakelijk.
* **Classificatie:** [CVE-2019-17571](https://nvd.nist.gov/vuln/detail/CVE-2019-17571) / [CWE-502](https://cwe.mitre.org/data/definitions/502.html). CVSS base score van 9.8. Vervanging is verplicht conform [NEN-7510:2026 Ctrl 8.8](https://www.nen.nl/nen-7510).

---

## 5. Volledige Security Backlog

Naast de top-5 bevindingen zijn er nog vijf andere risico's geïdentificeerd tijdens de analyse. Het is van essentieel belang dat ook deze kwetsbaarheden structureel worden opgelost. Onderstaande tabel toont de volledige backlog, geprioriteerd op basis van ernst.

| Bevinding ID | Korte omschrijving | CVE / CWE | CVSS Score | Prioriteit | Mitigatie Strategie |
| ------ | ------ | ------ | ------ | ------ | ------ |
| **SCA-02** | Verouderde Spring Core dependency (v3.0.5) | [CVE-2016-1000027](https://nvd.nist.gov/vuln/detail/CVE-2016-1000027) | 9.8 (Kritiek) | Kritiek | Updaten naar een ondersteunde versie van Spring Framework |
| **SCA-03** | Kwetsbare XStream dependency (v1.4.3) | [CVE-2013-7285](https://nvd.nist.gov/vuln/detail/CVE-2013-7285) | 9.8 (Kritiek) | Kritiek | Updaten naar de nieuwste veilige versie van XStream (v1.4.21+) |
| **SCA-05** | Onveilige commons-collections (v3.2) | [CVE-2015-7501](https://nvd.nist.gov/vuln/detail/CVE-2015-7501) | 9.8 (Kritiek) | Kritiek | Updaten naar v3.2.2 of v4.x |
| **SCA-06** | End-of-Life Log4j 1.x | [CVE-2019-17571](https://nvd.nist.gov/vuln/detail/CVE-2019-17571) | 9.8 (Kritiek) | Kritiek | Volledig migreren naar Log4j 2 of Reload4j |
| **SAST-04** | Deserialization of Untrusted Data | [CWE-502](https://cwe.mitre.org/data/definitions/502.html) | Contextueel Kritiek | Kritiek | Veilige deserialisatie implementeren met strikte allowlists |
| **SCA-01** | Verouderde Apache Struts dependency | [CVE-2014-0114](https://nvd.nist.gov/vuln/detail/CVE-2014-0114) | 7.5 (Hoog) | Hoog | Component volledig verwijderen of vervangen door modern alternatief |
| **SCA-04** | Verouderde PostgreSQL JDBC driver | [CVE-2018-10936](https://nvd.nist.gov/vuln/detail/CVE-2018-10936) | 4.2 (Medium) | Hoog | Driver updaten om Man-in-the-Middle aanvallen te voorkomen |
| **SAST-01** | Improper Input Validation | [CWE-20](https://cwe.mitre.org/data/definitions/20.html) | Contextueel Hoog | Hoog | Strikte server-side validatie toepassen op externe ID bronnen |
| **SAST-02** | Improper Authentication | [CWE-287](https://cwe.mitre.org/data/definitions/287.html) | Contextueel Hoog | Hoog | Autorisatiechecks toevoegen aan alle openbare API endpoints |
| **SAST-03** | Insufficient Logging | [CWE-778](https://cwe.mitre.org/data/definitions/778.html) | Contextueel Hoog | Hoog | Audit logging inbouwen conform NEN-7510:2026 control 8.15 |

De kwetsbaarheden in de categorie Hoog leveren aanzienlijke risico's op voor de organisatie. De Improper Input Validation in de API kan leiden tot injectie-aanvallen. Het ontbreken van goede authenticatie zorgt ervoor dat ongeautoriseerde gebruikers toegang kunnen forceren. Het gebrek aan logging betekent dat er geen sluitende audit trail is, waardoor forensisch onderzoek na een incident onmogelijk wordt. De verouderde PostgreSQL driver biedt mogelijkheden voor aanvallers om het databaseverkeer te onderscheppen. Deze actiepunten moeten in de opvolgende sprints worden geadresseerd.

---

## 6. Financiële Impact en Kostenraming

Om de OpenMRS idgen module te transformeren naar een veilige en NEN-7510 compliant applicatie, zijn investeringen vereist in tijd, tooling en externe validatie. De onderstaande kostenraming is opgesteld om het management inzicht te geven in de benodigde middelen. De bedragen zijn indicatieve schattingen.

**Kostenpost 1: Ontwikkelcapaciteit en Refactoring**
De noodzaak om End-of-Life componenten zoals Log4j 1.x en Spring 3.0.5 te vervangen, vereist aanzienlijke wijzigingen in de kern van de applicatie. Ook het oplossen van de onveilige deserialisatie-logica neemt veel ontwikkeltijd in beslag. Dit vereist tevens het herschrijven van de bijbehorende unit tests.
* **Inschatting:** 160 ontwikkeluren tegen een intern tarief van €100 per uur.
* **Geraamde kosten:** €16.000.

**Kostenpost 2: CI/CD Tooling Licenties**
De huidige GitHub Actions pipeline draait op een gratis account. Branch protection is volledig actief via een ruleset (repo is public). Secret Scanning is beschikbaar op public repos maar ontbreekt voor private repos zonder GitHub Advanced Security. Om alle technische barrières op te heffen (met name Secret Scanning op private repos en artifact-retentie boven 90 dagen), is een upgrade naar GitHub Advanced Security noodzakelijk.
* **Inschatting:** Licentiekosten voor een ontwikkelteam van vijf personen voor het eerste jaar.
* **Geraamde kosten:** €3.000.

**Kostenpost 3: Externe Penetratietest**
Wanneer de code is herschreven en de kwetsbaarheden theoretisch zijn opgelost, moet de effectiviteit van deze maatregelen in de praktijk worden aangetoond. [NEN-7510:2026 Ctrl 8.29 (Onafhankelijke beoordeling van informatiebeveiliging)](https://www.nen.nl/nen-7510) eist dat beveiligingsfuncties onafhankelijk worden getest. Een formele, handmatige grey-box penetratietest door een onafhankelijke externe partij is cruciaal om te bewijzen dat alle Remote Code Execution vectoren daadwerkelijk zijn gesloten.
* **Inschatting:** Inhuur van gecertificeerde pentesters voor een duur van vier werkdagen inclusief rapportage.
* **Geraamde kosten:** €6.500.

**Totale geraamde investering:** €25.500. Dit bedrag staat in schril contrast met de mogelijke boetes onder de Algemene Verordening Gegevensbescherming (AVG). Deze boetes kunnen bij ernstige nalatigheid in de zorgsector oplopen tot 4 procent van de wereldwijde jaaromzet of maximaal 20 miljoen euro. De investering is derhalve proportioneel en noodzakelijk voor de continuïteit van de organisatie.

---

## 7. Conclusie en Aanbeveling

Het definitieve oordeel van dit Risk Assessment Report is een expliciete **NO-GO** voor de deployment van de OpenMRS idgen module in de huidige staat. De applicatie bevat fundamentele ontwerpfouten en leunt op een verouderde keten van afhankelijkheden. Het samenstel van deze factoren resulteert in vijf actieve kwetsbaarheden die geclassificeerd zijn als Kritiek. Het risico op een grootschalig datalek van patiëntgegevens is onacceptabel hoog.

**Aanbeveling en Roadmap:**
Wij adviseren het ontwikkelteam om per direct de functionele doorontwikkeling van deze module te staken. De volledige capaciteit van de komende sprint moet worden ingezet om de vijf kritieke bevindingen uit de security backlog op te lossen.
* **Korte termijn (Sprint 3):** Start met het patchen van de XStream en Commons Collections libraries. Ontwerp veilige deserialisatie-mechanismen in de Java code en elimineer het gebruik van Log4j 1.x.
* **Middellange termijn (Sprint 4):** Voer de in kostenpost 2 voorgestelde licentie-upgrade uit om de CI/CD pipeline compliant te maken met Secret Scanning. Implementeer waterdichte audit logging voor elke actie die patiëntgegevens raakt, conform [NEN-7510:2026 Ctrl 8.15](https://www.nen.nl/nen-7510).
* **Lange termijn:** Voer na de refactoring een dynamische applicatiescan en de externe penetratietest uit. Pas nadat de onafhankelijke auditors hebben bevestigd dat de kritieke bevindingen niet langer exploiteerbaar zijn, kan de Go/No-Go beslissing opnieuw in overweging worden genomen.

---

## 8. Relatie met andere deliverables

Dit document maakt integraal deel uit van het security-verbetertraject en staat in directe verbinding met de overige deliverables:
* **`Groep_6_Asset-Identificatie.md`**: Dit rapport gebruikt de in [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md) geïdentificeerde assets (met name A1 en A2), threat actoren (TA1, TA3, TA4) en de vastgestelde risico-appetite en waarschijnlijkheid/impact-schalen.
* **`Groep_6_Security-Analyse.md`**: De hier gepresenteerde security backlog en CVE/CWE-bevindingen zijn direct gebaseerd op de automatische en handmatige analyses gedocumenteerd in [Groep_6_Security-Analyse.md](Groep_6_Security-Analyse.md).
* **`Groep_6_Risicomatrix.md`**: De risico's rondom het ontwikkelingsproces en de CI/CD pipeline die dit RAR beïnvloeden, zijn afgestemd met [Groep_6_Risicomatrix.md](Groep_6_Risicomatrix.md).
* **`Groep_6_Audit-Rapport.md`**: De bevindingen en het go/no-go advies uit dit Risk Assessment Report dienen als primaire input voor het uiteindelijke [Groep_6_Audit-Rapport.md](Groep_6_Audit-Rapport.md).
