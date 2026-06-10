# Patchadvies afhankelijkheden (SBOM, CVE en CVSS)

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

### Bronnenlijst
* [NEN-7510:2026 (Informatiebeveiliging in de zorg)](https://www.nen.nl/nen-7510)
* [NIST National Vulnerability Database (NVD)](https://nvd.nist.gov/)
* [OWASP Top 10 (2021) - A06: Vulnerable and Outdated Components](https://owasp.org/Top10/A06_2021-Vulnerable_and_Outdated_Components/)
* [CycloneDX Maven Plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin)
* [Apache Struts 1 End-of-Life Announcement](https://struts.apache.org/struts1eol-announcement.html)
* [Apache Log4j 1.x End-of-Life Announcement](https://logging.apache.org/log4j/1.2/)

---

## 1. Scope en Inleiding

Dit document bevat het geprioriteerde patchadvies voor de **OpenMRS ID Generation Module (idgen)** versie 4.9.0. Het advies is gebaseerd op de machine-leesbare Software Bill of Materials (SBOM) met 116 componenten (gegenereerd via de CycloneDX Maven Plugin in het CycloneDX 1.6 XML/JSON formaat) en de resultaten van de Software Composition Analysis (SCA) via OWASP Dependency-Check.

Het doel van dit document is het bieden van een concrete en direct toepasbare roadmap voor het ontwikkelteam om bekende kwetsbaarheden (CVE's) in externe libraries te elimineren. Dit borgt compliance met **[NEN-7510:2026 Ctrl 8.8 (Beheer van technische kwetsbaarheden)](https://www.nen.nl/nen-7510)**.

---

## 2. Risico-analyse afhankelijkheden (SCA Overzicht)

De SCA-scan heeft zes kwetsbare dependencies geïdentificeerd in de transitieve keten. Onderstaande tabel toont de prioritering op basis van de CVSS Base Score (NVD) en de contextuele risicoscore (kans x impact) conform de scoreschaal uit [Groep_6_Asset-Identificatie.md](Groep_6_Asset-Identificatie.md).

| ID | Component | Versie | Belangrijkste CVE | CWE | CVSS Base | Contextuele Ernst | NEN-7510 | Status |
|---|---|---|---|---|---|---|---|---|
| **SCA-02** | spring-core | 3.0.5.RELEASE | [CVE-2016-1000027](https://nvd.nist.gov/vuln/detail/CVE-2016-1000027) | [CWE-502](https://cwe.mitre.org/data/definitions/502.html) | 9.8 | Kritiek | Ctrl 8.8 | Kwetsbaar |
| **SCA-03** | xstream | 1.4.3 | [CVE-2013-7285](https://nvd.nist.gov/vuln/detail/CVE-2013-7285) | [CWE-502](https://cwe.mitre.org/data/definitions/502.html) | 9.8 | Kritiek | Ctrl 8.8 | Kwetsbaar |
| **SCA-05** | commons-collections | 3.2 | [CVE-2015-7501](https://nvd.nist.gov/vuln/detail/CVE-2015-7501) | [CWE-502](https://cwe.mitre.org/data/definitions/502.html) | 9.8 | Kritiek | Ctrl 8.8 | Kwetsbaar |
| **SCA-06** | log4j | 1.2.15 | [CVE-2019-17571](https://nvd.nist.gov/vuln/detail/CVE-2019-17571) | [CWE-502](https://cwe.mitre.org/data/definitions/502.html) | 9.8 | Kritiek | Ctrl 8.8 | Kwetsbaar (EOL) |
| **SCA-01** | struts-core | 1.3.8 | [CVE-2014-0114](https://nvd.nist.gov/vuln/detail/CVE-2014-0114) | [CWE-20](https://cwe.mitre.org/data/definitions/20.html) | 7.5 | Hoog | Ctrl 8.8 | Kwetsbaar (EOL) |
| **SCA-04** | postgresql (JDBC) | 9.0-801.jdbc4 | [CVE-2018-10936](https://nvd.nist.gov/vuln/detail/CVE-2018-10936) | [CWE-295](https://cwe.mitre.org/data/definitions/295.html) | 4.2 | Hoog | Ctrl 8.8 | Kwetsbaar |

---

## 3. Geprioriteerde Patch Roadmap

De patches zijn ingedeeld in drie fasen op basis van risico, complexiteit en de levenscyclus van de libraries.

### Fase 1: Onmiddellijke Actie (Sprint 3)
*Focus: Kritieke RCE-kwetsbaarheden die direct exploiteerbaar zijn en libraries die relatief eenvoudig te updaten zijn.*

1. **XStream (SCA-03):** 
   * *Actie:* Upgraden van versie 1.4.3 naar **1.4.21+**.
   * *Onderbouwing:* XStream deserialiseert XML-invoer zonder typebeperking. Omdat de module externe identifier-bronnen via XML kan verwerken, is dit een directe RCE-vector. Versie 1.4.21 introduceert een veilige standaardconfiguratie met allowlists.
2. **Commons Collections (SCA-05):**
   * *Actie:* Upgraden van versie 3.2 naar **3.2.2** of migreren naar **commons-collections4 v4.x**.
   * *Onderbouwing:* Versie 3.2 bevat de beruchte `InvokerTransformer` deserialisatie-gadget. Upgraden naar 3.2.2 deactiveert deze gadget standaard, wat de exploiteerbaarheid direct opheft.
3. **Log4j 1.x (SCA-06):**
   * *Actie:* Volledig migreren naar **Log4j 2.x** (minimaal 2.17.1+) of als drop-in vervanging **Reload4j 1.2.25** configureren.
   * *Onderbouwing:* Log4j 1.x is sinds 2015 End-of-Life en bevat meerdere onopgeloste deserialisatie-kwetsbaarheden (`SocketServer`). Reload4j is een actieve fork die de compatibiliteit behoudt maar alle CVE's heeft gepatcht.

### Fase 2: Korte Termijn (Sprint 4)
*Focus: Complexe backend-upgrades en transitieve EOL-dependencies.*

4. **Spring Core (SCA-02):**
   * *Actie:* Upgraden naar **Spring Core 5.3.x** of **6.x** (afhankelijk van de OpenMRS platformversie).
   * *Onderbouwing:* Spring 3.0.5 is EOL sinds 2014. De kwetsbaarheid in `HttpInvokerServiceExporter` stelt aanvallers in staat om willekeurige code uit te voeren. Deze upgrade is complex omdat het impact heeft op de kern van de module en mogelijk grotere aanpassingen vereist in de code en configuratie.
5. **Apache Struts 1 (SCA-01):**
   * *Actie:* Struts 1 dependency volledig uitsluiten (`<exclusions>`) of vervangen door Spring MVC controllers.
   * *Onderbouwing:* Struts 1 is EOL sinds 2013. De ClassLoader-manipulatie (CVE-2014-0114) kan leiden tot RCE. Omdat moderne OpenMRS-modules gebruikmaken van Spring MVC, is de Struts-dependency waarschijnlijk overtollig en kan deze veilig worden uitgesloten.

### Fase 3: Middellange Termijn (Post-Oplevering)
*Focus: Medium-risico kwetsbaarheden en database-koppelingen.*

6. **PostgreSQL JDBC Driver (SCA-04):**
   * *Actie:* Upgraden van 9.0-801.jdbc4 naar **42.7.x**.
   * *Onderbouwing:* De verouderde driver valideert TLS-certificaten onvoldoende, waardoor een Man-in-the-Middle (MitM) aanvaller database-credentials of patiëntgegevens in transit kan onderscheppen. Dit is een medium base score, maar contextueel hoog vanwege de meldplicht bij het lekken van patiëntdata.

---

## 4. Afweging van Impact en Verwachte Risicoreductie

Door het uitvoeren van dit patchadvies wordt de blootstelling aan bekende kwetsbaarheden (OWASP A06:2021) drastisch gereduceerd. De onderstaande tabel toont de risico-evaluatie voor en na de voorgestelde mitigaties.

| Bevinding | Initieel Risico | Kans (voor) | Impact (voor) | Residueel Risico | Kans (na) | Impact (na) | Risicoreductie |
|---|---|---|---|---|---|---|---|
| **SCA-02** (Spring) | **15 (Rood)** | 3 | 5 | **5 (Oranje)** | 1 | 5 | **-10 (Zeer hoog)** |
| **SCA-03** (XStream) | **15 (Rood)** | 3 | 5 | **5 (Oranje)** | 1 | 5 | **-10 (Zeer hoog)** |
| **SCA-05** (Commons) | **15 (Rood)** | 3 | 5 | **5 (Oranje)** | 1 | 5 | **-10 (Zeer hoog)** |
| **SCA-06** (Log4j 1) | **15 (Rood)** | 3 | 5 | **5 (Oranje)** | 1 | 5 | **-10 (Zeer hoog)** |
| **SCA-01** (Struts) | **10 (Oranje)** | 2 | 5 | **0 (Groen)** | 0 | 0 | **-10 (Volledig)** |
| **SCA-04** (Postgres) | **8 (Oranje)** | 2 | 4 | **4 (Groen)** | 1 | 4 | **-4 (Halvering)** |

*Toelichting:* De impact van een database-lek blijft bij RCE-kwetsbaarheden altijd 5 (Kritiek) of 4 (Hoog) omdat de data inherent gevoelig is. Echter, door de kwetsbaarheid te patchen, daalt de kans (probability) naar 1 (zeldzaam) of 0 (indien verwijderd), waardoor het totale risico afneemt tot een acceptabel niveau.

---

## 5. Concrete Implementatie-instructies

### 5.1 Maven Dependency Updates (pom.xml)

Voeg de volgende dependency-updates toe aan de `pom.xml` van de `idgen` module om de kwetsbare libraries te overschrijven of te upgraden:

```xml
<dependencyManagement>
    <dependencies>
        <!-- SCA-03: Upgrade XStream naar veilige v1.4.21 -->
        <dependency>
            <groupId>com.thoughtworks.xstream</groupId>
            <artifactId>xstream</artifactId>
            <version>1.4.21</version>
        </dependency>

        <!-- SCA-05: Upgrade commons-collections naar v3.2.2 -->
        <dependency>
            <groupId>commons-collections</groupId>
            <artifactId>commons-collections</artifactId>
            <version>3.2.2</version>
        </dependency>

        <!-- SCA-06: Vervang Log4j 1.x door Reload4j v1.2.25 -->
        <dependency>
            <groupId>ch.qos.reload4j</groupId>
            <artifactId>reload4j</artifactId>
            <version>1.2.25</version>
        </dependency>
        
        <!-- SCA-04: Upgrade PostgreSQL driver naar v42.7.3 -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <groupId>postgresql</groupId>
            <version>42.7.3</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 5.2 Transitieve EOL Dependencies Uitsluiten

Indien Struts 1 of Log4j 1.x transitief via andere OpenMRS core libraries binnenkomt, sluit deze dan expliciet uit in de dependency configuratie:

```xml
<dependency>
    <groupId>org.openmrs.api</groupId>
    <artifactId>openmrs-api</artifactId>
    <version>${openmrsVersion}</version>
    <exclusions>
        <!-- SCA-01: Exclude Struts 1.x -->
        <exclusion>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts-core</artifactId>
        </exclusion>
        <!-- SCA-06: Exclude Log4j 1.x -->
        <exclusion>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 5.3 XStream Beveiliging in Java Code

Bij het gebruik van XStream 1.4.21+ moet een expliciete type-allowlist worden geconfigureerd in de Java code waar deserialisatie plaatsvindt (bijv. in `RemoteIdentifierSourceProcessor.java`):

```java
XStream xstream = new XStream();
// Schakel alle types standaard uit
XStream.setupDefaultSecurity(xstream);
// Laat alleen specifieke types en de eigen module models toe
xstream.allowTypes(new Class[] {
    org.openmrs.module.idgen.RemoteIdentifierSource.class,
    org.openmrs.module.idgen.IdentifierTemplate.class,
    java.lang.String.class,
    java.util.List.class
});
```

---

## 6. Relatie met andere deliverables

* **`Groep_6_Security-Analyse.md`**: Dit patchadvies is direct gebaseerd op de kwetsbaarheden (SCA-01 t/m SCA-06) die zijn gedetecteerd en geanalyseerd in [Groep_6_Security-Analyse.md](Groep_6_Security-Analyse.md).
* **`Groep_6_Risk-Assessment-Report.md`**: De prioritering en fasering van deze roadmap komen overeen met de conclusies en aanbevelingen in [Groep_6_Risk-Assessment-Report.md](Groep_6_Risk-Assessment-Report.md) (Sectie 7).
* **`bom.json` / `bom.xml`**: De geanalyseerde afhankelijkheden en versienummers komen rechtstreeks uit de gegenereerde CycloneDX Software Bill of Materials (SBOM) in `openmrs-module-idgen/target/`.
