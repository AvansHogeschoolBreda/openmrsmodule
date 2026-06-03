# Sprint 1: Omgeving inrichten & Gap-analyse

## Sprint-goal

"Onze GitHub-omgeving staat klaar en we hebben aantoonbaar gemaakt waar onze module staat ten opzichte van NEN-7510."

---

## Taken

| #   | Taak                                                                                                                                                                                                                                          | Verwachte output                                                                                    | Status                   | Notities                                                                                                                  | Wie             |
| --- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------------------- | --------------- |
| 5.1 | **Module-keuze vastleggen**: documenteer de gekozen module (naam, versie, link naar broncode) en motiveer de keuze (complexiteit, scope, kritieke functionaliteit)                                                                      | `README.md` of `docs/module-keuze.md`                                                           | ❌ Open                  | Bestand aangemaakt (`Groep_6_Module-Keuze.md`) maar nog niet ingevuld                                                   |                 |
| 5.2 | **Repository inrichten**: branch protection (main is protected), MFA voor alle leden, Dependabot alerts aan, CodeQL workflow actief                                                                                                     | GitHub Security-tab toont geen rode vlaggen                                                         | ⚠️ Tijdelijk compliant | Branch protection geconfigureerd (wacht op goedkeuring docent i.v.m. limitatie Free plan), Dependabot aan, CodeQL aan. MFA geconfigureerd en verplicht gesteld op organisatieniveau. | RafvanHooijdonk |
| 5.3 | **OTAP-omgeving opzetten**: docker-compose per omgeving (dev / test / prod), GitHub Environments met protection rules                                                                                                                   | `docker-compose.yml` + `.github/environments/`                                                  | ✅ Compliant           | GitHub Environments aangemaakt (production + test). docker-compose basis en dev/test/prod overrides zijn aangemaakt.      | RafvanHooijdonk |
| 5.4 | **SBOM genereren**: via `snyk sbom` of de GitHub SBOM-export in CycloneDX JSON-formaat                                                                                                                                                | `docs/sbom.cdx.json` als CI-artifact                                                              | ⚠️ Tijdelijk compliant | `sbom.yml` actief, CycloneDX artifact wordt aangemaakt in CI. Draait op stub pom.xml, niet op echte module.             | RafvanHooijdonk |
| 5.5 | **Gap-analyse uitvoeren**: vergelijk de huidige staat van de module met NEN-7510:2024-2 controls A.8.3 (toegangsbeveiliging), A.8.5 (authenticatie) en A.8.15 (logging). Noteer per control: aanwezig / gedeeltelijk / afwezig + bewijs | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Gap-Analyse.md`            | ❌ Open                  | Bestand aangemaakt met header en structuur. Nog in te vullen.                                                             |                 |
| 5.6 | **Mini-complianceverslag**: tabel: per NEN-7510 control, pipeline-maatregel, bewijs                                                                                                                                                     | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Mini-Complianceverslag.md` | ✅ Compliant             | Ingevuld: 5 controls (8.8, 8.15, 8.25, 8.28, 5.36) met vereiste, maatregel, bewijsverwijzing en restrisico per control. | SinanSagir      |

---

## Non-functionals

- De kwaliteitseisen t.a.v. security en maintainability vastleggen.
- Denken aan statische code-analyse (voor acceptatiecriteria).

---

## Eindcheck Sprint 1

- [x] Module-keuze gedocumenteerd en onderbouwd in Groep_6_Module-Keuze.md
- [ ] GitHub repository heeft branch protection en Dependabot actief
- [ ] SBOM-bestand wordt als CI-artifact aangemaakt in Actions
- [ ] Gap-analyse dekt minimaal 3 NEN-7510 controls met bewijs
- [ ] Alle teamleden hebben een commit bijgedragen
