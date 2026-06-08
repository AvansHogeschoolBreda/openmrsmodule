# Sprint 2: Risico-analyse & Security Backlog

## Sprint-goal

"We weten welke risico's en kwetsbaarheden er in onze module zitten en hebben ze geprioriteerd."

---

## Taken

| #   | Taak                                                                                                                                                                                                                                 | Verwachte output                                          | Status  | Wie |
| --- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------- | ------- | --- |
| 2.1 | **Asset-identificatie**: breng in kaart welke kroonjuwelen de module verwerkt (patiëntdata, credentials, audit logs, etc.)                                                                                                         | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Asset-Identificatie.md`                         | ✅ Compliant | RafvanHooijdonk |
| 2.2 | **Risicomatrix opstellen**: minimaal 5 dreigingen (kans x impact), CIA-triad-classificatie, kleurcodering (rood/oranje/groen)                                                                                                      | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Risicomatrix.md` | ✅ Compliant | SinanSagir |
| 2.3 | **Bow-tie analyse**: voor het risico met de hoogste score: top event, minimaal 3 preventieve barrières en 2 herstelbarrières, gekoppeld aan NEN-7510:2026 controls                                                               | `docs/LU2 - Kwaliteit en security - verbeteronderzoek security/Groep_6_Bow-Tie.md`      | ✅ Compliant | SinanSagir |
| 2.4 | **SAST & SCA scan uitvoeren**: `snyk test` (SCA), `snyk code test` (SAST) en `snyk sbom` als onderdeel van de CI-pipeline; sla ruwe output op                                                                                    | CI-workflow + artifact `snyk-results.json`               | ❌ Open |     |
| 2.5 | **Security backlog samenstellen**: minimaal 5 bevindingen; kolommen: Finding ID, CVE/CWE, CVSS-score, Contextuele score, NEN-7510 control, Prioriteit (H/M/L), Status, False positive-rationale (indien van toepassing)          | `docs/auditrapport/06-security-backlog.md`               | ❌ Open |     |
| 2.6 | **Patchadvies schrijven**: op basis van de SBOM en CVE/CVSS-scores: welke libraries updaten, in welke volgorde, verwachte risicoreductie                                                                                           | `docs/auditrapport/07-patchadvies.md`                    | ❌ Open |     |

---

## Non-functionals

- 'Identify': software kwaliteit in kaart brengen.
- Rapport code-kwaliteit, onderbouwing en plan opstellen.

---

## Eindcheck Sprint 2

- [X] Risicomatrix met minimaal 5 risico's aanwezig
- [X] Bow-tie voor het hoogste risico uitgewerkt
- [ ] SAST- en SBOM-scan loopt automatisch in CI en output is opgeslagen
- [ ] Security backlog heeft minimaal 5 bevindingen met CVSS + NEN-7510 koppeling
- [ ] Patchadvies is onderbouwd met CVE-data uit de SBOM
