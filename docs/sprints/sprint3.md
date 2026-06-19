# Sprint 3: Mitigatie, Pentest & Auditrapport (concept)

## Sprint-goal

"We hebben kwetsbaarheden aantoonbaar gemitigeerd en het auditrapport is inhoudelijk compleet."

---

## Taken

| #   | Taak                                                                                                                                                                                                                              | Verwachte output                                           | Status       | Wie        |
| --- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------- | ------------ | ---------- |
| 3.1 | **PoC: kwetsbaarheden mitigeren**: los minimaal 2 kwetsbaarheden uit de security backlog op in de broncode; maak een aparte branch en PR met beschrijving van de fix en de gebruikte (AI-)tooling                              | PR met diff + beschrijving AI-gebruik                     | ✅ Gedaan    | SinanSagir |
| 3.2 | **Pentestrapportage voor mitigatie**: documenteer voor minimaal 2 kwetsbaarheden het misbruik: aanvalsstap, bewijs (screenshot / curl-output / Burp-log)                                                                        | `docs/pentest/bevinding-[id]-voor.md`                     | ✅ Gedaan    | SinanSagir |
| 3.3 | **Pentestrapportage na mitigatie (hertest)**: toon aan dat de kwetsbaarheid na de fix niet meer reproduceerbaar is                                                                                                               | `docs/pentest/bevinding-[id]-na.md`                       | ✅ Gedaan    | SinanSagir |
| 3.4 | **DPIA-check**: beschrijf hoe de module omgaat met bijzondere persoonsgegevens (AVG art. 9); beoordeel of een volledige DPIA verplicht is (AVG art. 35); leg de spanning tussen logging en privacy uit                          | `docs/auditrapport/08-dpia-check.md`                      | ✅ Gedaan    | SinanSagir |
| 3.5 | **Auditrapport concept**: vul de volledige rapportstructuur in; nog niet alle secties definitief, maar de structuur staat en executive summary is geschreven                                                                     | `docs/auditrapport/00-auditrapport.md`                    | ✅ Gedaan    | Alle       |
| 3.6 | **Presentatiedeck concept**: maximaal 15 slides; gericht op fictief management + technisch publiek; dek de rubric-producten                                                                                                      | `docs/presentatie/` (PDF-export of `.pptx`)               | ✅ Gedaan    | Alle       |

---

## Non-functionals / code-kwaliteit

- Klassediagrammen voor EN na refactorings.
- Testen en testplan (onderbouwing manier en coverage van testen).

---

## Eindcheck Sprint 3

- [X] PR met PoC-mitigatie gemerged (of review-ready)
- [X] Pentest voor en na mitigatie gedocumenteerd
- [X] DPIA-check aanwezig
- [X] Concept-auditrapport heeft alle secties (ook al zijn ze nog niet volledig uitgeschreven)
- [X] Concept-presentatie heeft minimaal een structuur met titels per slide
