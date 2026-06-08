# Rubric LU2 Verbeteronderzoek Security

Beroepsproduct v1.0
Module: ATIx IN-B2.4 Softwarearchitectuur & -kwaliteit 2025-26 P4

---

## Criteria

### Security audit: wetgeving & normen (/ 20)

| Onvoldoende (0 pt) | Voldoende (11 pt) | Goed (20 pt) |
|---|---|---|
| De resultaten van de audit t.a.v. NEN7510 ontbreken of zijn onduidelijk en niet bruikbaar vastgelegd. Er is geen onderbouwd advies opgenomen. | De gap naar volledige implementatie van relevante NEN7510-2 controls is vastgelegd. Er is een advies opgenomen voor verbetering van de compliancy op basis van een lijst non-compliances die is geprioriteerd op basis van risico-inschatting. | Bevat alle onderdelen van "Voldoende" en de audit is grondig, herleidbaar en onderbouwd met relevante bronnen en normen. De prioritering en aanpak zijn expliciet gemotiveerd. |

---

### Secure pipelines (/ 15)

| Onvoldoende (0 pt) | Voldoende (8 pt) | Goed (15 pt) |
|---|---|---|
| De pipelines zijn niet secure ingericht of de verschillende OTAP-omgevingen zijn onvoldoende gescheiden. Documentatie is onvoldoende. | De pipelines zijn secure ingericht inclusief scheidingen naar de OTAP-omgevingen. Documentatie verantwoordt de gemaakte keuzes en geeft inzicht in de toegepaste veiligheidsmaatregelen. | Bevat alle onderdelen van "Voldoende" en er is verantwoorde keuze gemaakt voor niet-herleidbare data in de verschillende OTAP-omgevingen. |

---

### Advies updates: SBOM, CVE en CVSS (/ 15)

| Onvoldoende (0 pt) | Voldoende (8 pt) | Goed (15 pt) |
|---|---|---|
| De analyse van afhankelijkheden ontbreekt of is onvolledig en niet bruikbaar vastgelegd. Er is geen of een onvoldoende onderbouwd advies over updates van libraries op basis van CVE's en CVSS-scores. | Er is een duidelijke en (machine)bruikbare SBOM gegenereerd die de relevante afhankelijkheden van het systeem beschrijft. Er is een bruikbaar en onderbouwd advies opgesteld over updates van afhankelijkheden op basis van SBOM, CVE's en CVSS-scores. | Bevat alle onderdelen van "Voldoende" en het advies bevat een duidelijke prioritering, afweging van impact en risico's en concrete aanbevelingen voor implementatie. |

---

### Security code review & kwetsbaarheden (/ 15)

| Onvoldoende (0 pt) | Voldoende (9 pt) | Goed (15 pt) |
|---|---|---|
| Security code reviews ontbreken of zijn onvoldoende uitgevoerd. Kwetsbaarheden zijn niet duidelijk vastgelegd of onderbouwd. | Security code reviews zijn uitgevoerd met (AI-)tooling. Kwetsbaarheden zijn duidelijk en bruikbaar vastgelegd, onderbouwd en geprioriteerd. Risico's bij niet oplossen zijn beschreven op basis van valide bronnen. | Bevat alle onderdelen van "Voldoende" en de analyse is diepgaand, met goed onderbouwde risico-inschattingen en duidelijke relatie tussen kwetsbaarheden en systeemgebruik. |

---

### Penetration tests: aantonen kwetsbaarheden (/ 15)

| Onvoldoende (0 pt) | Voldoende (8 pt) | Goed (15 pt) |
|---|---|---|
| Penetration tests ontbreken of zijn niet navolgbaar gedocumenteerd. Misbruik van kwetsbaarheden is niet aantoonbaar gemaakt. | Penetration tests zijn uitgevoerd en navolgbaar gedocumenteerd, waarbij misbruik van de meest kritische kwetsbaarheden is aangetoond. | Bevat alle onderdelen van "Voldoende" en de tests zijn systematisch opgezet, reproduceerbaar en geven diepgaand inzicht in exploitatie en impact. |

---

### Mitigatie & validatie verbeteringen (/ 20)

| Onvoldoende (0 pt) | Voldoende (11 pt) | Goed (20 pt) |
|---|---|---|
| Kwetsbaarheden zijn niet gemitigeerd of de mitigaties zijn niet aantoonbaar effectief. Verantwoording van realisatie ontbreekt. | Kwetsbaarheden zijn gemitigeerd en met penetration tests is aangetoond dat securityrisico's zijn verlaagd. De realisatie is verantwoord, inclusief gebruik van (AI)tooling. | Bevat alle onderdelen van "Voldoende" en de verbetering is kwantitatief en reproduceerbaar aangetoond. Er is een kritische reflectie op de gekozen mitigaties en tooling. |

---

## Totaal: / 100

| Onvoldoende | Voldoende | Goed |
|---|---|---|
| 0 punten minimum | 55 punten minimum | 100 punten minimum |
