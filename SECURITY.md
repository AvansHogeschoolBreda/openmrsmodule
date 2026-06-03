# Security Policy

## Ondersteunde versies

Dit project is een proof-of-concept repository in het kader van LU2: Verbeter-onderzoek
software-kwaliteit (NEN-7510). Er is momenteel één actieve branch (`main`) die wordt ondersteund.

| Branch / Versie | Ondersteund |
| --------------- | ----------- |
| `main`          | ✅          |
| Andere branches | ❌          |

## Kwetsbaarheid melden

Heb je een beveiligingsprobleem gevonden? Meld het vertrouwelijk via:

- **GitHub Security Advisory:** Gebruik "Report a vulnerability" op de [Security-tab](../../security/advisories/new).
- **E-mail:** Neem contact op met de repository owner via [@RafvanHooijdonk](https://github.com/RafvanHooijdonk).

## Wat kun je verwachten?

| Stap                                        | Termijn                                  |
| ------------------------------------------- | ---------------------------------------- |
| Bevestiging van ontvangst                   | Binnen 3 werkdagen                       |
| Initiële beoordeling (CRITICAL/HIGH)        | Binnen 5 werkdagen                       |
| Fix of mitigatie (CRITICAL)                 | Streefdoel 14 dagen                      |
| Fix of mitigatie (HIGH/MEDIUM)              | Streefdoel 30 dagen                      |

## Prioritering

| Ernst    | CVSS Score  | Actie                                       |
| -------- | ----------- | ------------------------------------------- |
| CRITICAL | 9.0: 10.0  | Directe actie, hotfix op `main`            |
| HIGH     | 7.0: 8.9   | Oplossen in eerstvolgende sprint            |
| MEDIUM   | 4.0: 6.9   | Gepland in backlog                          |
| LOW      | 0.1: 3.9   | Ter beoordeling, lage prioriteit            |

## Automatische detectie

| Tool               | Wat het doet                                                                 | Status                  |
| ------------------ | ---------------------------------------------------------------------------- | ----------------------- |
| CodeQL             | SAST-analyse op elke push en PR (NEN-7510 Ctrl 8.8)                         | ⚠️ Tijdelijk compliant |
| Dependabot         | Automatische alerts en updates voor kwetsbare dependencies                   | ⚠️ Tijdelijk compliant |
| Dependency Review  | Blokkeert PRs met HIGH/CRITICAL kwetsbaarheden of verboden licenties         | ⚠️ Tijdelijk compliant |
| SBOM (CycloneDX)   | Software Bill of Materials bij elke push naar `main`                        | ⚠️ Tijdelijk compliant |
| Secret Scanning    | Detectie van gelekte secrets in code                                         | ❌ Niet beschikbaar     |

Tijdelijk compliant = tools draaien correct, maar analyseren momenteel een stub project zonder
echte OpenMRS module code. Zodra de echte module is toegevoegd worden deze volledig effectief.

Secret Scanning is niet beschikbaar op het GitHub Free plan voor private repositories.

Zie `docs/checklist.md` voor de volledige compliance status en openstaande actiepunten.
Zie `README.md` voor het beveiligingsbeleid en pipeline-overzicht.
