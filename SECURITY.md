# Security Policy

## Ondersteunde versies

Dit project is een proof-of-concept (PoC) repository in het kader van LU2 – Verbeter-onderzoek
software-kwaliteit (NEN-7510). Er is momenteel één actieve branch (`main`) die wordt ondersteund
met security updates.

| Branch / Versie | Ondersteund |
|-----------------|-------------|
| `main`          | ✅          |
| Andere branches | ❌          |

## Kwetsbaarheid melden

Heb je een beveiligingsprobleem gevonden in deze repository? Meld het dan vertrouwelijk via één
van de volgende kanalen:

- **GitHub Security Advisory:** Gebruik de knop "Report a vulnerability" op de
  [Security-tab](../../security/advisories/new) van deze repository.
- **E-mail:** Neem direct contact op met de repository owner via het GitHub-profiel
  [@RafvanHooijdonk](https://github.com/RafvanHooijdonk).

## Wat kun je verwachten?

| Stap | Termijn |
|------|---------|
| Bevestiging van ontvangst | Binnen 3 werkdagen |
| Initiële beoordeling (CRITICAL/HIGH) | Binnen 5 werkdagen |
| Fix of mitigatie (CRITICAL) | Zo snel mogelijk, streefdoel 14 dagen |
| Fix of mitigatie (HIGH/MEDIUM) | Streefdoel 30 dagen |

## Prioritering

Kwetsbaarheden worden beoordeeld op basis van CVSS-score:

| Ernst | CVSS Score | Actie |
|-------|-----------|-------|
| CRITICAL | 9.0 – 10.0 | Directe actie, hotfix op `main` |
| HIGH | 7.0 – 8.9 | Oplossen in eerstvolgende sprint |
| MEDIUM | 4.0 – 6.9 | Gepland in backlog |
| LOW | 0.1 – 3.9 | Ter beoordeling, lage prioriteit |

## Automatische detectie

Dit project maakt gebruik van de volgende geautomatiseerde security-tools:

- **CodeQL** – SAST-analyse op elke push en PR (NEN-7510 Ctrl 8.8)
- **Dependabot** – Automatische alerts en updates voor kwetsbare dependencies
- **Dependency Review** – Blokkeert PRs met HIGH/CRITICAL kwetsbaarheden
- **SBOM (CycloneDX)** – Software Bill of Materials wordt gegenereerd bij elke push naar `main`

Zie ook de [Mini-ISMS sectie in de README](README.md) voor een volledig overzicht van het
beveiligingsbeleid en de pipeline-procedures.
