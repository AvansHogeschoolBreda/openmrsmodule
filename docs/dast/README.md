# DAST – OWASP ZAP Full Scan

> **Doel:** Dynamisch aanvallen op de draaiende OpenMRS-instantie simuleren (NEN-7510 8.29).  
> **Tool:** OWASP ZAP via Docker  
> **Bewijslast:** `zap-report.html` en `zap-report.json` in deze map

---

## Lokaal uitvoeren

### Stap 1 – Start OpenMRS

Vanuit de project-root:

```bash
docker-compose up -d
```

Wacht tot OpenMRS volledig opgestart is. Dit kan 3–8 minuten duren bij een eerste boot.
Controleer via: http://localhost:8080/openmrs/

### Stap 2 – Run ZAP

```bash
./run-zap.sh
```

Het script draait ZAP via Docker en slaat de rapporten op in `docs/dast/`.

### Stap 3 – Bekijk rapport

Open `docs/dast/zap-report.html` in een browser.

---

## Via GitHub Actions

De workflow `.github/workflows/dast-owasp-zap.yml` kan handmatig getriggerd worden:

1. Ga naar **Actions** → **DAST – OWASP ZAP Full Scan**
2. Klik **Run workflow**
3. Na afloop: download het artifact `zap-report-<run-nummer>`

---

## Output bestanden

| Bestand            | Inhoud                                   |
| ------------------ | ---------------------------------------- |
| `zap-report.html`  | Leesbaar HTML-rapport voor het auditrapport |
| `zap-report.json`  | Machine-leesbare JSON (audit trail)      |
| `zap-report.xml`   | XML-export (OWASP-formaat)               |

---

## NEN-7510 koppeling

| Control | Titel                  | Hoe dit rapport bijdraagt                             |
| ------- | ---------------------- | ----------------------------------------------------- |
| 8.29    | Beveiligingstests      | DAST-output als aantoonbaar testbewijs vóór productie |
| 8.8     | Kwetsbaarheidsbeheer   | Dynamisch gevonden kwetsbaarheden identificeren       |
