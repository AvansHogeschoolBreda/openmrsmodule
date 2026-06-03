# OpenMRS Module – Security Pipeline (NEN-7510)

## LU 2 Verbeter-onderzoek software-kwaliteit

De inhoud van deze leeruitkomst is als volgt.

Je onderzoekt een legacysysteem met betrekking tot kwaliteitsverbeteringen op het gebied van
onderhoudbaarheid en security. Je ontwikkelt proof-of-concepts (PoC) ter ondersteuning van je
onderzoeksresultaten. De resultaten en aanbevelingen deel je op professionele en overdraagbare
wijze, en verdedig je in een individueel interview.

Met een gestructureerd software-assessment analyseer je onderhoudbaarheid van het systeem.
Op basis van je bevindingen ontwerp en implementeer je een deel van de voorgestelde verbeteringen
in de vorm van een PoC, inclusief onderbouwing van de gemaakte keuzes. Met een herassessment
van het PoC toon je de kwaliteitsverbeteringen aan.

Op het gebied van security voer je een systematische analyse uit met betrekking tot nationaal
opgestelde normen/wetgeving en stelt een geprioriteerde aanpak op ter verbetering. Je analyseert
de afhankelijkheden die het systeem heeft met 3rd party softwarelibraries en -frameworks en
adviseert over updates ter verbetering van security. Door middel van (AI-)tooling voer je security
code reviews uit en toon je met een PoC systematisch en onderbouwd de effectiviteit van toegepaste
mitigaties voor de meest kritische technische kwetsbaarheden aan.

---

## Mini-ISMS – Beveiligingsbeleid en Procedures

### Doel
Dit document beschrijft het beveiligingsbeleid en de CI/CD-procedures voor de OpenMRS module
repository, conform NEN-7510 (informatiebeveiliging in de zorg).

### Verantwoordelijkheden
| Rol | Verantwoordelijkheid |
|-----|----------------------|
| Repository owner | Beheer van branch protection, environments en secrets |
| Developer | Aanmaken van feature branches, openen van pull requests |
| Reviewer | Code review en goedkeuring van pull requests vóór merge |

### Branch Protection
- Alle wijzigingen op `main` verlopen uitsluitend via een **Pull Request**.
- Minimaal **1 goedkeuring** is verplicht voordat een PR gemerged mag worden.
- Verouderde goedkeuringen worden automatisch ingetrokken bij nieuwe commits.
- Status checks (CI-workflows) moeten slagen vóór merge.

### CI/CD Pipeline Overzicht
| Workflow | Trigger | Doel |
|----------|---------|------|
| `codeql.yml` | Push/PR op `main`, wekelijks | SAST – statische code-analyse (NEN-7510 Ctrl 8.8) |
| `dependency-review.yml` | PR op `main` | SCA – controle op kwetsbare/verboden licenties in dependencies |
| `sbom.yml` | Push op `main`, release | Generatie van CycloneDX SBOM (Software Bill of Materials) |
| `dependabot.yml` | Wekelijks (maandag 06:00) | Automatische dependency updates voor Maven en GitHub Actions |

### Security Controls
- **SAST (CodeQL):** Elke push en PR wordt geanalyseerd op bekende kwetsbaarheden in Java-code.
- **Dependency Review:** Pull requests worden geblokkeerd bij HIGH/CRITICAL kwetsbaarheden of verboden licenties (GPL-3.0, AGPL-3.0).
- **Dependabot:** Automatische security updates en versie-updates voor dependencies.
- **SBOM:** Bij elke push naar `main` wordt een Software Bill of Materials gegenereerd in CycloneDX-formaat en opgeslagen als pipeline-artifact (90 dagen retentie).
- **Secrets beheer:** Secrets zijn gescheiden per environment (`production`, `test`). Productie-secrets zijn alleen toegankelijk in de `production` environment, die voorzien is van een protection rule.

### Environments
| Environment | Protection Rule | Gebruik |
|-------------|----------------|---------|
| `production` | Ja (1 rule) | Productie-deployments, beveiligd met goedkeuringsregel |
| `test` | Nee | Test-deployments |

### Incident & Vulnerability Rapportage
Beveiligingslekken worden gerapporteerd via het proces beschreven in [`SECURITY.md`](SECURITY.md).
Dependabot-alerts en CodeQL-bevindingen worden beoordeeld door de repository owner en
zo snel mogelijk verholpen op basis van prioriteit (CRITICAL > HIGH > MEDIUM > LOW).

### Artifact Bewaring
Pipeline-artifacts (SBOM-rapporten) worden 90 dagen bewaard in GitHub Actions.
