# Sprint 4: Definitief & Oplevering

## Sprint-goal

"Alle producten zijn definitief, peer-reviewed binnen het team en ingeleverd."

**Deadline:** vrijdag einde week 8

---

## Taken

| #   | Taak                              | Toelichting                                                                                               | Status       | Wie        |
| --- | --------------------------------- | --------------------------------------------------------------------------------------------------------- | ------------ | ---------- |
| 4.1 | **Auditrapport definitief maken** | Alle secties uitgeschreven, bronnenlijst compleet, consistent genummerd                                   | ✅ Gedaan    | Alle       |
| 4.2 | **PoC afronden**                  | Code gemerged, CI groen, README bijgewerkt met instructies om de PoC te reproduceren                     | ✅ Gedaan    | Alle       |
| 4.3 | **Pentestrapportage samenvoegen** | Eén coherent document met methodologie, bevindingen, bewijs en hertestresultaten                         | ✅ Gedaan    | SinanSagir |
| 4.4 | **SBOM-sectie finaliseren**       | SBOM in rapport + updateadvies definitief, CVE-referenties gecontroleerd                                  | ✅ Gedaan    | SimonEulenpesch |
| 4.5 | **Presentatie oefenen**           | Minimaal één volledige doorloop, timingcheck (~20 min), rolverdeling vastgelegd                          | ✅ Gedaan    | Alle       |
| 4.6 | **GitHub-repository opschonen**   | README up-to-date, mapstructuur logisch, geen gevoelige data in repo (secrets check)                    | ✅ Gedaan    | Alle       |
| 4.7 | **Eindcheck rubric**              | Loop de rubric langs: zijn alle vijf producten (A-E) aantoonbaar aanwezig in GitHub?                     | ✅ Gedaan    | SinanSagir |

---

## Non-functionals / kwaliteit

- Aantonen dat alles nog werkt (regressie) en dat de kwaliteit verbeterd is (metrics + rapportage).

---

## Oplevering

Lever in via GitHub:

1. Tag de definitieve versie: `git tag v1.0-audit` en push
2. Zorg dat de docent toegang heeft tot de repository

---

## Eindcheck Sprint 4

- [X] Repository is getagd als `v1.0-audit`
- [X] Alle vijf rubric-producten zijn aanwezig en vindbaar in `/docs/`
- [X] CI-pipeline is groen op de main-branch
- [X] Geen hardcoded secrets aanwezig (secret scanning geeft geen alerts)
- [X] Presentatie is definitief en als PDF opgeslagen in de repository
