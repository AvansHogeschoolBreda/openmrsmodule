# Bow-tie analyse: H10 Hardcoded secret in broncode

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## Bronnen

- [NEN-7510:2026 (informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
- [OWASP Top 10 CI/CD Security Risks (CICD-SEC)](https://owasp.org/www-project-top-10-ci-cd-security-risks/)
- [CWE-321: Use of Hard-coded Cryptographic Key](https://cwe.mitre.org/data/definitions/321.html)
- [CWE-532: Insertion of Sensitive Information into Log File](https://cwe.mitre.org/data/definitions/532.html)
- [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)
- [GitHub: Removing sensitive data from a repository](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/removing-sensitive-data-from-a-repository)
- [AVG Art. 33 (GDPR Art. 33): Meldplicht datalekken](https://eur-lex.europa.eu/legal-content/NL/TXT/?uri=CELEX:32016R0679)
- [Detect-secrets tool (Yelp)](https://github.com/Yelp/detect-secrets)
- [NCSC Cybersecuritybeeld Nederland (CSBN) 2024](https://www.ncsc.nl/actueel/nieuws/2024/juni/18/cybersecuritybeeld-nederland-2024)
- [Verizon Data Breach Investigations Report (DBIR) 2024](https://www.verizon.com/business/resources/reports/dbir/)
- [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md) (hazard-definitie en scorering H10)

---

## 1. Scope

Dit document werkt de bow-tie analyse uit voor hazard **H10: Hardcoded secret in broncode**. H10 is geselecteerd als bow-tie onderwerp in `Groep_6_Asset-Identificatie.md` (sectie 6.3) omdat het:

1. Een score van 15 heeft (rood), wat onmiddellijke actie verplicht.
2. Direct verband houdt met de CI/CD-context van dit project.
3. Illustreert hoe een menselijke fout (TA3) en een aanvaller (TA1) dezelfde kwetsbaarheid benutten via verschillende oorzaken.
4. Aantoont hoe preventieve barrières in de pipeline samenwerken met herstelbarrières na het incident.

Buiten scope: de volledige hazard-analyse voor alle assets. Die staat in `Groep_6_Asset-Identificatie.md`. De CI/CD-specifieke risicomatrix en bow-tie voor H8 staan in `Groep_6_Risicomatrix.md`.

---

## 2. Methodiek

Een bow-tie analyse brengt oorzaken, het top-event en gevolgen samen in één overzicht. Het top-event is het exacte kantelpunt: daarvoor zijn preventieve barrières mogelijk, daarna alleen herstelbarrières. Escalation factors verzwakken barrières.

De koppeling aan NEN-7510:2026 controls maakt de analyse traceerbaar naar het compliance-kader van dit project.

---

## 3. Hazard en top-event

| Veld | Waarde |
|---|---|
| Hazard ID | H10 |
| Hazard | Hardcoded secret in broncode |
| Asset | A6: Secrets en API-sleutels |
| STRIDE | I (Information Disclosure), S (Spoofing) |
| CWE | CWE-321 (Use of Hard-coded Cryptographic Key) |
| OWASP | CICD-SEC-6 (Insufficient Credential Hygiene) |
| NEN-7510 | Ctrl 8.24, 5.17, 8.8, 8.15, 6.8 |
| Risicoscore | 15 (Rood) |

**Top-event:** Een secret (wachtwoord, API-sleutel, deployment token) staat in de git history van de repository en is toegankelijk voor elke partij met leestoegang tot de repository of de git history.

Het top-event is het kantelpunt: het secret is gecommit. Daarvoor waren preventieve maatregelen mogelijk. Daarna zijn alleen noch herstelmaatregelen mogelijk.

---

## 4. Bow-tie diagram

```
PREVENTIEF                        TOP-EVENT              HERSTEL

[O1] Dev hardcodes  --[PB1: Pre-commit hook]--\
[O2] .env gecommit  --[PB2: .gitignore]--------+--> SECRET IN  --[HB1: Secret rotatie]----> [G1] Ongeauth. DB-toegang
[O3] Debug in code  --[PB3: PR-review]---------/    GIT HISTORY --[HB2: Git history rewrite]-> [G2] CI/CD gecompromitteerd
[O4] Copy-paste     --[PB4: CodeQL SAST]------/     (kantelpunt)--[HB3: Audit log review]---> [G3] AVG Art. 33 meldplicht
                                                                --[HB4: Incident response]--> [G4] Reputatieschade
```

PB = Preventieve barrière (links van het top-event)
HB = Herstelbarrière (rechts van het top-event)

---

## 5. Oorzaken

| ID | Oorzaak | Threat Actor | Toelichting |
|---|---|---|---|
| O1 | Developer typt credential direct in broncode | TA3 (onbewuste fout) | Tijdelijk voor test, vergeet te verwijderen. DBIR 2024: 68% van breaches heeft menselijk element. |
| O2 | .env of configuratiebestand per ongeluk gecommit | TA3 | Ontbrekende of onjuiste .gitignore-regel. |
| O3 | Secret geprint in debug-output vastgelegd in broncode | TA3 | `System.out.println` of `console.log` met credential-waarde. |
| O4 | Copy-paste fout: credential vanuit CI-configuratie naar broncode | TA3, TA2 | Deployment token of database-credential handmatig overgezet bij het debuggen van een workflow. |

---

## 6. Preventieve barrières

Preventieve barrières bevinden zich tussen de oorzaken en het top-event. Ze proberen te voorkomen dat het secret in de git history terechtkomt.

| ID | Barrière | Werking | NEN-7510 | Huidig actief? |
|---|---|---|---|---|
| PB1 | Pre-commit hook (detect-secrets) | Scant elke commit automatisch op credential-patronen voor de commit plaatsvindt. Blokkeert de commit als een patroon overeenkomt. | Ctrl 8.8 | Nee: niet geconfigureerd in dit project |
| PB2 | .gitignore voor .env en configuratiebestanden | Verhindert dat gevoelige configuratiebestanden worden gestaaged. | Ctrl 5.17 | Gedeeltelijk: .gitignore aanwezig maar niet gevalideerd op volledigheid |
| PB3 | Verplichte PR-review voor merge naar main | Tweede persoon controleert de code inclusief eventuele credentials. | Ctrl 5.36 | Geconfigureerd maar niet volledig afdwingbaar (GitHub Free plan) |
| PB4 | CodeQL SAST-scan op elke push | Detecteert CWE-321 (hardcoded cryptographic keys) en vergelijkbare patronen in de Java-code. | Ctrl 8.8 | Actief (tijdelijk compliant: draait op stub pom.xml) |

---

## 7. Escalation factors (preventief)

Escalation factors verzwakken een preventieve barrière. Als een escalation factor actief is, werkt de bijbehorende barrière minder goed of helemaal niet.

| ID | Escalation factor | Betrokken barrière | Impact |
|---|---|---|---|
| EF1 | GitHub Secret Scanning niet beschikbaar op Free plan voor private repos | PB4 | Als PB1 (pre-commit hook) wordt overgeslagen, is er geen automatisch vangnet. CodeQL detecteert niet alle secret-patronen en is bovendien tijdelijk compliant. |
| EF2 | Branch protection niet volledig afdwingbaar op GitHub Free | PB3 | Repo-eigenaar kan direct naar main pushen, waardoor PB3 (PR-review) wordt omzeild. |
| EF3 | Lange secret rotatie-cyclus | Alle preventieve barrières | Als secrets jaren geldig blijven zonder rotatie, vergroot de window of exposure bij een lek: het secret werkt lang na de compromittering. |
| EF4 | Git history is permanent en breed bewaard | PB1, PB2, PB3, PB4 | Als geen enkele barrière het secret tegenhoudt, is het daarna persistent aanwezig in forks, caches en gecachede zoekresultaten, ook na een force-push. |

---

## 8. Gevolgen

| ID | Gevolg | Ernst | Toelichting |
|---|---|---|---|
| G1 | Ongeautoriseerde toegang tot productiedatabase | Kritiek | Aanvaller gebruikt het secret direct. Patiëntdata (A1) direct bereikbaar. AVG Art. 9 (bijzondere categorie) geschonden. |
| G2 | CI/CD pipeline gecompromitteerd | Kritiek | Deployment token misbruikt voor kwaadaardige deployments of exfiltratie van andere secrets. |
| G3 | AVG Art. 33 meldplicht actief | Hoog | Datalek moet binnen 72 uur gemeld worden bij de Autoriteit Persoonsgegevens. Bij hoog risico voor betrokkenen ook AVG Art. 34. |
| G4 | Reputatieschade en juridische aansprakelijkheid | Hoog | Boete tot 4% van de jaaromzet mogelijk (AVG Art. 83). Vertrouwensschade bij patiënten en zorginstelling. |

---

## 9. Herstelbarrières

Herstelbarrières bevinden zich tussen het top-event en de gevolgen. Ze beperken de schade nadat het secret al in de git history staat.

| ID | Barrière | Werking | NEN-7510 | Huidig actief? |
|---|---|---|---|---|
| HB1 | Onmiddellijke secret rotatie en revocatie | Het gecompromitteerde secret intrekken in GitHub Environments en vervangen door een nieuw secret. Beperkt de window of opportunity voor de aanvaller direct. | Ctrl 8.24, 5.17 | Handmatig mogelijk via GitHub Settings |
| HB2 | Git history rewrite via BFG Repo-Cleaner | Secret verwijderen uit alle vorige commits via BFG Repo-Cleaner of `git filter-repo`, gevolgd door een force-push naar alle branches. Verwijdert het secret uit de doorzoekbare git history. | Ctrl 8.8 | Niet geautomatiseerd; handmatige procedure vereist |
| HB3 | Audit log review | GitHub Actions logs en access logs controleren op ongeautoriseerd gebruik van het gecompromitteerde secret. Bepaalt of de aanvaller het secret al heeft gebruikt en welke systemen geraakt zijn. | Ctrl 8.15 | Actief: GitHub Actions logs beschikbaar (90 dagen retentie op Free plan) |
| HB4 | Incident response en AVG-meldplicht | Beoordelen of patiëntdata gelekt is. Bij bevestiging: meldplicht AVG Art. 33 (72 uur bij AP) en bij hoog risico voor betrokkenen ook Art. 34. Intern incident-rapport opstellen. | Ctrl 6.8 | Procedure beschreven in SECURITY.md |

---

## 10. Escalation factors (correctief)

| ID | Escalation factor | Betrokken barrière | Impact |
|---|---|---|---|
| EF5 | Git history is permanent: forks en externe caches | HB2 | BFG Repo-Cleaner verwijdert het secret uit de eigen repository. Forks en gecachede versies (bv. GitHub's zoekindex) kunnen het secret nog bevatten. HB2 is daardoor nooit volledig effectief. |
| EF6 | 90 dagen log-retentie op Free plan | HB3 | Als het incident pas na 90 dagen wordt ontdekt, zijn de relevante GitHub Actions logs verlopen. Forensisch onderzoek is dan onmogelijk. |
| EF7 | Geen automatische melding bij ongeautoriseerd secret-gebruik | HB4 | GitHub notificeert niet automatisch bij misbruik van een gecompromitteerd secret. Detectie is reactief en afhankelijk van handmatige monitoring. |

---

## 11. NEN-7510:2026 control-koppeling

| Control | Omschrijving | Rol in deze bow-tie |
|---|---|---|
| Ctrl 5.17 | Authenticatiegeheimen: beleid voor beheer van wachtwoorden en tokens | Preventief: secrets mogen nooit in broncode staan; rotatie-beleid verplicht (PB2) |
| Ctrl 8.24 | Gebruik van cryptografie en sleutelbeheer | Herstel: secret rotatie en revocatie na compromittering (HB1) |
| Ctrl 8.8 | Beheer van technische kwetsbaarheden | Preventief: SAST-scan en pre-commit hooks detecteren CWE-321 (PB1, PB4); herstel: git history rewrite (HB2) |
| Ctrl 8.15 | Logging van informatiebeveiligingsgebeurtenissen | Herstel: audit log review bepaalt of het secret misbruikt is (HB3) |
| Ctrl 6.8 | Rapportage van informatiebeveiligingsgebeurtenissen | Herstel: incident response en AVG-meldplicht (HB4) |

---

## 12. Audit mindset: niet gelogd = niet gebeurd

Als het gecompromitteerde secret door een aanvaller wordt gebruikt, maar de applicatie en pipeline loggen geen secret-gebruik of deployment-activiteit, kan het incident niet worden gereconstrueerd. Twee concrete risico's:

1. GitHub Actions logs bewaren deployment-activiteit slechts 90 dagen op het Free plan. Een laat ontdekt incident verliest zijn bewijsmateriaal (EF6).
2. Secret-gebruik via de REST API wordt niet door GitHub gelogd op applicatieniveau. Zonder applicatie-level access logging is ongeautoriseerd gebruik onzichtbaar.

HB3 (audit log review) en HB4 (incident response) zijn alleen effectief als er logs beschikbaar zijn op het moment van ontdekking. Dit is de reden waarom NEN-7510 Ctrl 8.15 (audit logging) een kernmaatregel is, uitgewerkt in Opdracht 5.

---

## 13. Koppeling naar andere deliverables

| Deliverable | Koppeling |
|---|---|
| Groep_6_Asset-Identificatie.md | Hazard H10, scorering (15, rood) en keuze voor deze bow-tie zijn vastgelegd in sectie 6.3 |
| Groep_6_Risicomatrix.md | H10 is opgenomen in de CI/CD risicomatrix; bow-tie voor H8 staat in dat document |
| Opdracht 4: Security backlog | H10 levert bevinding F-H10 in de security backlog (CWE-321, CVSS contextueel, NEN-7510 Ctrl 8.24) |
| Opdracht 5: Secure Coding & Logging | Audit mindset sectie 12 is directe input voor de logging gap-analyse |
| Opdracht 6: Auditrapport | Dit bow-tie diagram is bijlage in het auditrapport (Deliverable 3, eis 7) |
