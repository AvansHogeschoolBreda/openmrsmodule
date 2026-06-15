# Security Policy

## Ondersteunde versies

Dit project is een verbeteronderzoek repository in het kader van LU2: Verbeteronderzoek software-kwaliteit (NEN-7510). Er is één actieve branch (`main`) die wordt ondersteund.

| Branch / Versie | Ondersteund |
|---|---|
| `main` | ✅ |
| Andere branches | ❌ |

---

## Kwetsbaarheid melden (Responsible Disclosure)

Heb je een beveiligingsprobleem gevonden? Meld het vertrouwelijk via:

- **GitHub Security Advisory:** Gebruik "Report a vulnerability" op de [Security-tab](../../security/advisories/new).
- **E-mail:** Neem contact op met de repository owner via [@RafvanHooijdonk](https://github.com/RafvanHooijdonk).

Meld **geen** kwetsbaarheden via publieke GitHub Issues.

## Wat kun je verwachten?

| Stap | Termijn |
|---|---|
| Bevestiging van ontvangst | Binnen 3 werkdagen |
| Initiële beoordeling (CRITICAL/HIGH) | Binnen 5 werkdagen |
| Fix of mitigatie (CRITICAL) | Streefdoel 14 dagen |
| Fix of mitigatie (HIGH/MEDIUM) | Streefdoel 30 dagen |

---

## Prioritering

| Ernst | CVSS Score | Actie |
|---|---|---|
| CRITICAL | 9.0–10.0 | Directe actie, hotfix op `main` |
| HIGH | 7.0–8.9 | Oplossen in eerstvolgende sprint |
| MEDIUM | 4.0–6.9 | Gepland in backlog |
| LOW | 0.1–3.9 | Ter beoordeling, lage prioriteit |

---

## Automatische detectie & tooling

| Tool | Type | Wat het doet | Status | NEN-7510 |
|---|---|---|---|---|
| CodeQL | SAST | Statische code-analyse op elke push en PR | ✅ Actief | 8.8, 8.29 |
| Dependabot | SCA | Automatische alerts en updates voor kwetsbare dependencies | ✅ Actief | 8.8 |
| Dependency Review | SCA | Blokkeert PRs met HIGH/CRITICAL kwetsbaarheden of verboden licenties | ✅ Actief | 8.8 |
| CycloneDX SBOM | SBOM | Software Bill of Materials bij elke push naar `main` (116 componenten) | ✅ Actief | 8.8, 5.22 |
| SonarCloud | Kwaliteitspoort | Quality gate + JaCoCo code coverage; blokkeert merge bij falen | ✅ Actief | 8.25, 8.28 |
| OWASP ZAP | DAST | Dynamic Application Security Testing op draaiende OpenMRS | ✅ Actief | 8.29 |
| Secret Scanning | Secrets | Detectie van gelekte secrets in code; push protection blokkeert commits | ✅ Actief (Secret Protection + Push Protection) | 8.28 |

---

## Uitgevoerde security-activiteiten

Alle documenten staan in `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/`:

| Activiteit | Document | NEN-7510 |
|---|---|---|
| Gap-analyse NEN-7510 controls | `Groep_6_Gap-Analyse.md` | 8.3, 8.5, 8.15 |
| Mini-complianceverslag pipeline | `Groep_6_Mini-Complianceverslag.md` | 8.8, 8.15, 5.36 |
| Asset-identificatie + threat modeling | `Groep_6_Asset-Identificatie.md` | 8.8, 8.29 |
| Risicomatrix CI/CD + bow-tie H8 | `Groep_6_Risicomatrix.md` | 8.8, 8.29 |
| Bow-tie analyse H10 (hardcoded secrets) | `Groep_6_Bow-Tie.md` | 8.24, 5.17, 8.8 |
| SAST + SCA + SBOM scan | `Groep_6_Security-Analyse.md` | 8.8, 8.29 |
| Security backlog (10 bevindingen) | `Groep_6_Security-Analyse.md` | 8.8 |
| Risk Assessment Report | `Groep_6_Risk-Assessment-Report.md` | 8.29 |
| Attack Surface Mapping | `Groep_6_Attack_Su