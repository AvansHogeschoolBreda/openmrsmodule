# Mini-Complianceverslag

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## Inleiding

Dit verslag toont per NEN-7510:2026 control aan hoe de CI/CD security pipeline van de OpenMRS module repository hieraan voldoet. Per control staat beschreven wat de norm vereist, welke pipeline-maatregel dit afdekt, en welk restrisico er nog bestaat.

**Module:** OpenMRS ID Generation Module (idgen)
**Repository:** AvansHogeschoolBreda/openmrsmodule
**Periode:** 2026-06
**Onderzochte controls:** 8.8, 8.15, 5.36

---

## Overzicht

| NEN-7510:2026 Control | Pipeline-maatregel | Status |
|---|---|---|
| 8.8 Beheer van technische kwetsbaarheden | Dependabot, Dependency Review, CodeQL | ⚠️ Tijdelijk compliant |
| 8.15 Logging | SBOM-artifact, CI-run logs, SECURITY.md rapportageproces | ⚠️ Tijdelijk compliant |
| 5.36 Conformiteit aan beleidsregels | README.md (mini-ISMS), SECURITY.md, docs/checklist.md | ✅ Compliant |

---

## 1. Control 8.8: Beheer van technische kwetsbaarheden

**Wat de control vereist:**
Tijdig informatie verkrijgen over technische kwetsbaarheden in gebruikte systemen en software. De blootstelling beoordelen en passende maatregelen nemen voor de vastgestelde risico's.

**Hoe de pipeline hieraan voldoet:**

| Maatregel | Bestand / Instelling | Toelichting |
|---|---|---|
| Dependabot alerts + automatische updates | `.github/dependabot.yml` | Wekelijks (maandag 06:00) updates voor Maven-dependencies en GitHub Actions. Alerts staan aan in Settings. |
| Dependency Review | `.github/workflows/dependency-review.yml` | Blokkeert PRs naar `main` bij HIGH of CRITICAL kwetsbaarheden. Weigert GPL-3.0 en AGPL-3.0 licenties. |
| CodeQL SAST | `.github/workflows/codeql.yml` | Statische code-analyse op elke push, PR en wekelijks schema. Detecteert kwetsbaarheden in Java code. |

**Restrisico:**
De pipeline werkt momenteel op een stub `pom.xml` met alleen JUnit 5. Dependabot, Dependency Review en CodeQL analyseren daardoor minimale of geen echte module-dependencies. Pas volledig effectief wanneer de echte OpenMRS module en bijbehorende `pom.xml` zijn toegevoegd.

---

## 2. Control 8.15: Logging

**Wat de control vereist:**
Logbestanden die activiteiten, uitzonderingen, fouten en andere relevante beveiligingsgebeurtenissen vastleggen worden aangemaakt, bewaard en beschermd. Logbestanden worden niet ongeautoriseerd gewijzigd.

**Hoe de pipeline hieraan voldoet:**

| Maatregel | Bestand / Instelling | Toelichting |
|---|---|---|
| SBOM-artifact | `.github/workflows/sbom.yml` | CycloneDX JSON SBOM wordt gegenereerd bij elke push naar `main` en bewaard als Actions-artifact (90 dagen). |
| CI-run logs | GitHub Actions (alle workflows) | Build-, test- en scanresultaten zijn bewaard als CI-run logs. Niet te wijzigen na afloop. |
| Kwetsbaarheidsrapportage | `SECURITY.md` | Beschrijft het proces voor het melden en afhandelen van kwetsbaarheden, inclusief termijnen per ernst. |

**Restrisico:**
Artifact-retentie is beperkt tot 90 dagen (GitHub Free plan maximum). De geconfigureerde 365 dagen wordt automatisch teruggebracht. Applicatie-level audit logging binnen de module zelf is nog niet geimplementeerd (zie Opdracht 5 voor logging-implementatie).

---

## 3. Control 5.36: Conformiteit aan beleidsregels voor informatiebeveiliging

**Wat de control vereist:**
De naleving van het informatiebeveiligingsbeleid en onderwerpspecifieke beleidsregels, regels en normen van de organisatie wordt regelmatig beoordeeld en gedocumenteerd.

**Hoe de pipeline hieraan voldoet:**

| Maatregel | Bestand / Instelling | Toelichting |
|---|---|---|
| Mini-ISMS | `README.md` | Bevat beveiligingsbeleid, verantwoordelijkheden, branch protection procedure, environments, secrets-beheer en bekende beperkingen. |
| Security Policy | `SECURITY.md` | Beschrijft rapportagekanalen, termijnen per ernst en overzicht van actieve security tools met status. |
| Compliance-tracker | `docs/checklist.md` | Centrale registratie van alle eisen per opdracht, status, bewijslast en wijzigingslog. |

**Restrisico:**
Er is geen formeel periodiek reviewproces ingericht voor het beleid. Compliance-tracking is handmatig via `docs/checklist.md`. Een geautomatiseerde beoordeling of extern auditproces ontbreekt.

---

## Conclusie

Drie NEN-7510:2026 controls zijn onderzocht in relatie tot de CI/CD pipeline.

Control 5.36 (conformiteit aan beleid) is volledig compliant: beleid is gedocumenteerd in README.md en SECURITY.md, en compliance wordt bijgehouden in checklist.md.

Controls 8.8 (kwetsbaarheidsbeheer) en 8.15 (logging) zijn tijdelijk compliant. De technische maatregelen zijn actief en correct geconfigureerd, maar draaien op een stub-project. Ze worden volledig effectief zodra de echte OpenMRS module is toegevoegd.

Het grootste restrisico is de stub `pom.xml`. Vervangen door de module-eigen `pom.xml` lost de meeste tijdelijke beperkingen in een keer op.
