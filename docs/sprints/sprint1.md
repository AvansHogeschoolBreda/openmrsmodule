# Sprint 1: Omgeving inrichten & Gap-analyse

## Sprint-goal

"Onze GitHub-omgeving staat klaar en we hebben aantoonbaar gemaakt waar onze module staat ten opzichte van NEN-7510."

---

## Taken

| #   | Taak                                                                                                                                                                                                                                        | Verwachte output                                                                                    | Status                   | Notities                                                                                                                                                                             | Wie                      |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------ |
| 5.1 | **Module-keuze vastleggen**: documenteer de gekozen module (naam, versie, link naar broncode) en motiveer de keuze (complexiteit, scope, kritieke functionaliteit)                                                                    | `README.md` of `docs/module-keuze.md`                                                           | ✅ Compliant             | Keuze vastgelegd en gemotiveerd in Groep_6_Module-Keuze.md. Bevat motivatie en 3 geselecteerde NEN-7510 controls.                                                                    | Rowen Albers             |
| 5.2 | **Repository inrichten**: branch protection (main is protected), MFA voor alle leden, Dependabot alerts aan, CodeQL workflow actief                                                                                                   | GitHub Security-tab toont geen rode vlaggen                                                         | ✅ Compliant | Branch protection volledig actief via ruleset "Protect main – NEN-7510 Ctrl 8.4/8.32" (repo is public; rulesets volledig afgedwongen). Dependabot aan, CodeQL aan. MFA geconfigureerd en verplicht gesteld op organisatieniveau. | RafvanHooijdonk          |
| 5.3 | **OTAP-omgeving opzetten**: docker-compose per omgeving (dev / test / prod), GitHub Environments met protection rules                                                                                                                 | `docker-compose.yml` + `.github/environments/`                                                  | ✅ Compliant             | GitHub Environments aangemaakt (production + test). docker-compose basis en dev/test/prod overrides zijn aangemaakt.                                                                 | RafvanHooijdonk          |
| 5.4 | **SBOM genereren**: via CycloneDX Maven Plugin (`makeAggregateBom`) in CycloneDX JSON-formaat                                                                                                                                        | `bom.xml` + `bom.json` als CI-artifact                                                            | ✅ Compliant            | `sbom-cyclonedx.yml` actief op echte idgen-module. CycloneDX 1.6, 116 componenten. Bewaard als artifact `sbom-{run}` (90 dagen). Snyk niet gebruikt; CycloneDX Maven Plugin ingezet. | RafvanHooijdonk          |
| 5.5 | **Gap-analyse uitvoeren**: vergelijk de huidige staat van de module met NEN-7510:2026 controls A.8.3 (toegangsbeveiliging), A.8.5 (authenticatie) en A.8.15 (logging). Noteer per control: aanwezig / gedeeltelijk / afwezig + bewijs | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Gap-Analyse.md`            | ✅ Compliant             | Ingevuld met 3 controls en concrete bewijzen uit de code en het ontwerp.                                                                                                             | Rowen Albers             |
| 5.6 | **Mini-complianceverslag**: tabel: per NEN-7510 control, pipeline-maatregel, bewijs                                                                                                                                                   | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Mini-Complianceverslag.md` | ✅ Compliant             | Ingevuld: 3 controls (8.8, 8.15, 5.36) met vereiste, maatregel, bewijsverwijzing en restrisico per control.                                                                          | SinanSagir, Rowen Albers |

---

## Non-functionals

- De kwaliteitseisen t.a.v. security en maintainability vastleggen.
- Denken aan statische code-analyse (voor acceptatiecriteria).

---

## Eindcheck Sprint 1

- [X] Module-keuze gedocumenteerd en onderbouwd in Groep_6_Module-Keuze.md
- [X] GitHub repository heeft branch protection en Dependabot actief
- [X] SBOM-bestand wordt als CI-artifact aangemaakt in Actions
- [X] Gap-analyse dekt minimaal 3 NEN-7510