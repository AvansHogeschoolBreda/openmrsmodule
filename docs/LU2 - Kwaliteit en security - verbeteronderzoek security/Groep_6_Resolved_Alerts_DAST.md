# Overzicht van de 49 OWASP ZAP DAST-bevindingen en mitigaties

**Groep: C6**

| Naam: | Nummer: |
|---|---|
| Raf van Hooijdonk | 2230382 |
| Rowen Albers | 2227982 |
| Simon Eulenpesch | 2226731 |
| Sinan Sagir | 2235816 |

---

## 1. Scope en bronbestand

Dit document verwerkt de 49 alerts uit de OWASP ZAP full scan van 15/06/2026 (`@generated: Mon, 15 Jun 2026 21:36:33`), opgeslagen als `docs/dast/zap-report/zap-report.json` (en `.html`/`.xml`). De scan draaide tegen `http://localhost:8080`, de volledige Tomcat-instantie uit `docker-compose.yml` (image `openmrs/openmrs-reference-application-distro:2.12.2`), niet uitsluitend tegen de idgen-module.

Een kopie van dit rapport stond ook los in de repository-root (`zap-report.json`, identiek aan het reeds gearchiveerde bestand). Die kopie is verwijderd omdat het bewijslast-exemplaar al in `docs/dast/zap-report/` stond (CLAUDE.md: geen losse rapportbestanden in de root).

### Wat hoort wel/niet bij de idgen-module

ZAP test de hele Tomcat-server, inclusief de standaard webapps die met Tomcat worden meegeleverd (`/examples`, `/docs`, `/manager`, `/host-manager`) en de Tomcat root-context (`/`). Geen van die paden heeft broncode in deze repository: ze zijn onderdeel van de Tomcat-distributie binnen het upstream Docker-image, niet van de idgen-module. Alleen bevindingen onder `/openmrs/**` raken code of configuratie die via dit project beheerd wordt.

Van de 49 alerts vallen 28 volledig buiten die scope (categorie **O**, zie sectie 4). Voor de overige 21 is per instance gecontroleerd welk deel onder `/openmrs/**` valt; alleen dat deel is meegenomen in de fix.

## 2. Methodiek

Per alert is gecontroleerd:
1. Welke instances (URI's) onder `/openmrs/**` vallen versus Tomcat-eigen content.
2. Of er een structurele oorzaak in module-code of -configuratie bestaat.
3. Of een module-niveau fix mogelijk is, gezien de module draait op een upstream Docker-image waarvan `server.xml`/`context.xml` niet door deze repo wordt beheerd.

Resultaat: drie categorieën.

| Categorie | Betekenis | Aantal |
|---|---|---|
| **F** - Fixed | Genuine `/openmrs` bevinding, opgelost via code/config in deze repo | 11 |
| **I** - Informational, reviewed | Bevinding raakt `/openmrs`, maar ZAP classificeert het als Informational (geen kwetsbaarheid); beoordeeld en geen fix vereist | 10 |
| **O** - Out of scope | Uitsluitend Tomcat-eigen content (`/examples`, `/docs`, `/manager`, `/host-manager`, root `/`); geaccepteerd risico, geen broncode in deze repo | 28 |

NEN-7510 koppeling: Ctrl 8.29 (Beveiligingstests) voor de scan zelf, Ctrl 8.8 (Kwetsbaarheidsbeheer) voor de triage en mitigatie. Relevante OWASP Top 10 (2021) categorieën: A05 (Security Misconfiguration) voor de ontbrekende headers, A01 (Broken Access Control) voor de onveilige HTTP-methoden.

## 3. Implementatie (categorie F)

Alle 11 categorie-F bevindingen worden gemitigeerd door één nieuwe servlet `Filter`, toegevoegd aan de idgen-omod en geregistreerd via `config.xml`. OpenMRS-modules kunnen filters bijdragen aan de webapp-context via `<filter>`/`<filter-mapping>` in `config.xml`; dit vereist `configVersion` 1.2 of hoger.

### Before: `openmrs-module-idgen/omod/src/main/resources/config.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module configVersion="1.0">
	...
	<require_version>1.11.3, 1.10.2 - 1.10.*, 1.9.9 - 1.9.*</require_version>

	<extension>
	...
```

### After: `openmrs-module-idgen/omod/src/main/resources/config.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module configVersion="1.2">
	...
	<require_version>1.11.3, 1.10.2 - 1.10.*, 1.9.9 - 1.9.*</require_version>

	<!-- Mitigeert ZAP-bevindingen 10038/10020/10021/10063/90004/90028/10054/10036 -->
	<filter>
		<filter-name>idgenSecurityHeadersFilter</filter-name>
		<filter-class>@MODULE_PACKAGE@.web.filter.SecurityHeadersFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>idgenSecurityHeadersFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<extension>
	...
```

### After (nieuw bestand): `openmrs-module-idgen/omod/src/main/java/org/openmrs/module/idgen/web/filter/SecurityHeadersFilter.java`

```java
package org.openmrs.module.idgen.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityHeadersFilter implements Filter {

	private static final String REST_PATH_MARKER = "/ws/rest/";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isBlockedMethod(httpRequest.getMethod().toUpperCase(Locale.ROOT), httpRequest.getRequestURI())) {
			httpResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		applySecurityHeaders(httpResponse);

		chain.doFilter(request, new SameSiteCookieResponseWrapper(httpResponse));
	}

	private boolean isBlockedMethod(String method, String requestUri) {
		if ("TRACE".equals(method) || "CONNECT".equals(method) || "PUT".equals(method)) {
			return true;
		}
		if ("DELETE".equals(method)) {
			return !requestUri.contains(REST_PATH_MARKER);
		}
		return false;
	}

	private void applySecurityHeaders(HttpServletResponse response) {
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Content-Security-Policy",
		    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
		            + "style-src 'self' 'unsafe-inline'; img-src 'self' data:; frame-ancestors 'none'");
		response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
		response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
		response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
		response.setHeader("Cross-Origin-Embedder-Policy", "credentialless");
		// Tomcat only adds its own "Server" header at the connector level when none is set yet,
		// so an empty value here is the only way to mask the version from a module-level filter.
		response.setHeader("Server", "");
	}

	@Override
	public void destroy() {
	}

	// Decorates cookies the application sets explicitly. Tomcat writes its own JSESSIONID
	// cookie through the raw connector response before this wrapper sees it, so that one
	// still needs a Rfc6265CookieProcessor entry in the server's context.xml.
	private static final class SameSiteCookieResponseWrapper extends HttpServletResponseWrapper {

		SameSiteCookieResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void addHeader(String name, String value) {
			super.addHeader(name, decorateIfSetCookie(name, value));
		}

		@Override
		public void setHeader(String name, String value) {
			super.setHeader(name, decorateIfSetCookie(name, value));
		}

		private String decorateIfSetCookie(String name, String value) {
			if ("Set-Cookie".equalsIgnoreCase(name) && value != null
			        && !value.toLowerCase(Locale.ROOT).contains("samesite")) {
				return value + "; SameSite=Lax";
			}
			return value;
		}
	}
}
```

Bijbehorende test: `openmrs-module-idgen/omod/src/test/java/org/openmrs/module/idgen/web/filter/SecurityHeadersFilterTest.java` (6 testgevallen: headers gezet, PUT overal geblokkeerd, DELETE geblokkeerd buiten `/ws/rest/`, DELETE toegestaan binnen `/ws/rest/`, TRACE/CONNECT geblokkeerd, SameSite toegevoegd aan een handmatig gezette Set-Cookie header).

### Waarom PUT overal geblokkeerd is, en DELETE alleen buiten `/ws/rest/`

`grep` op `openmrs-module-idgen/omod/src/main/java` bevestigt dat geen enkele controller of REST-resource in deze module PUT gebruikt; idgen volgt de OpenMRS REST-conventie (POST voor create/update). DELETE wordt niet expliciet in idgen's eigen klassen aangeroepen, maar wordt geërfd van de `webservices.rest`-basisklassen (`MetadataDelegatingCrudResource`) voor retire/purge op `/ws/rest/**`. Vandaar de uitzondering voor dat pad.

### Build- en testresultaat

| Stap | Commando | Resultaat |
|---|---|---|
| Build (volledige reactor) | `mvn -B clean package -DskipTests` (in `openmrs-module-idgen/`) | **Geslaagd** - `idgen`, `idgen-api`, `idgen-omod` allen BUILD SUCCESS |
| Unit tests nieuwe filter | `SecurityHeadersFilterTest` (6 testgevallen) | **Geslaagd** - 6/6 OK |
| Volledige `mvn test` (reactor) | n.v.t. lokaal | Kon lokaal niet draaien: de surefire-configuratie van deze repo (`--add-opens` JVM-flags in `api/pom.xml` en `omod/pom.xml`) vereist Java 11, zoals ook in `.github/workflows/ci-build-test.yml`. Lokaal is alleen Java 8 (1.8.0_481) beschikbaar. De nieuwe testklasse is daarom rechtstreeks via `JUnitCore` gecompileerd en gedraaid (omzeilt enkel de surefire-argLine, niet de testlogica) en gaf 6/6 groen. `mvn test` op de volledige reactor draait automatisch op Java 11 in CI bij de volgende push/PR op deze branch. |

DAST-herscan (run-zap.sh tegen een draaiende docker-compose-instantie) is niet uitgevoerd in deze sessie: er is geen Docker-omgeving beschikbaar. Aanbevolen vervolgstap: `docker-compose up -d` + `./run-zap.sh` herhalen na merge, en het nieuwe rapport naast dit document leggen als "na mitigatie"-bewijs, analoog aan de pentest voor/na-aanpak in `Groep_6_Pentestrapport.md`.

### Breaking changes

**Ja, potentieel, met onderbouwing waarom het risico klein is:**

- **PUT wordt overal binnen `/openmrs/**` geblokkeerd (405).** Geen breaking change voor idgen zelf (zie grep-resultaat hierboven), maar als een andere module of een toekomstige integratie wel PUT gebruikt, breekt die. Niet aangetroffen in deze module.
- **DELETE wordt geblokkeerd buiten `/ws/rest/**`.** Geen impact op idgen's REST-resources (die vallen onder `/ws/rest/`); zou wel een niet-REST DELETE-gebruiker breken als die ooit wordt toegevoegd.
- **`X-Frame-Options: DENY`** breekt het embedden van OpenMRS-pagina's in een `<iframe>` (bijv. een extern dashboard dat de UI inlijst). Niet aangetroffen in deze module of de bekende OWA's; indien dit ooit nodig is moet de waarde naar `SAMEORIGIN` voor het specifieke pad.
- **CSP met `'unsafe-inline' 'unsafe-eval'`** is bewust ruim gehouden omdat de legacy OpenMRS-UI (Dojo/jQuery, inline `<script>`-blokken in JSP's) anders zou breken. Dit is een tradeoff: de header is aanwezig (lost de ZAP-bevinding op) maar biedt minder XSS-bescherming dan een strikte CSP. Vastgelegd als restrisico in sectie 5.
- **`SameSite=Lax` op handmatig gezette cookies.** Lax is het browser-default sinds Chrome 80 en breekt in de praktijk geen top-level navigatie; alleen cross-site `POST`-flows zouden de cookie missen. Niet aangetroffen in deze module.

## 4. Volledige bevindingentabel (49 alerts)

| # | Plugin ID | Alert | Risk (Confidence) | CWE | Instances (totaal/in scope) | Categorie | Status / Oplossing |
|---|---|---|---|---|---|---|---|
| 1 | 40018 | SQL Injection | High (Low) | 89 | 2 / 0 | O | Tomcat-voorbeeld-servlets (`CookieExample`, `SessionExample`), geen broncode in deze repo. Geaccepteerd risico. |
| 2 | 10202 | Absence of Anti-CSRF Tokens | Medium (Low) | 352 | 5 / 0 | O | Tomcat `/examples/jsp/security` formulieren. Geaccepteerd risico. |
| 3 | 20012 | Anti-CSRF Tokens Check | Medium (Medium) | 352 | 4 / 0 | O | Tomcat `/examples` servlets. Geaccepteerd risico. |
| 4 | 90022 | Application Error Disclosure | Medium (Medium) | 550 | 5 / 0 | O | Tomcat `/docs/config/*.html` documentatiepagina's. Geaccepteerd risico. |
| 5 | 10038 | Content Security Policy (CSP) Header Not Set | Medium (High) | 693 | 5 / 1 | **F** | `/openmrs/initialsetup` instance gefixt via `Content-Security-Policy`-header (sectie 3). Overige 4 instances zijn Tomcat root/`docs`, buiten scope. |
| 6 | 30002 | Format String Error | Medium (Medium) | 134 | 2 / 0 | O | Tomcat `/examples/servlets/servlet/CookieExample`. Geaccepteerd risico. |
| 7 | 90028 | Insecure HTTP Method - DELETE | Medium (Medium) | 749 | 1 / 1 | **F** | Volledig gefixt: DELETE geblokkeerd buiten `/ws/rest/**` (sectie 3). |
| 8 | 90028 | Insecure HTTP Method - PUT | Medium (Medium) | 749 | 714 / 2 | **F** | 2 instances onder `/openmrs/**` gefixt (PUT nu overal binnen de module geblokkeerd). Overige 712 instances zijn fuzzing tegen de Tomcat root-context (`/$/...`, `/**/...`), buiten het bereik van een module-filter; buiten scope. |
| 9 | 10020 | Missing Anti-clickjacking Header | Medium (Medium) | 1021 | 5 / 1 | **F** | `/openmrs/initialsetup` gefixt via `X-Frame-Options: DENY`. Overige 4 instances Tomcat root/`docs`, buiten scope. |
| 10 | 10051 | Relative Path Confusion | Medium (Medium) | 20 | 8 / 0 | O | Tomcat `/docs/config/*.html` ankerlinks. Geaccepteerd risico. |
| 11 | 3 | Session ID in URL Rewrite | Medium (High) | 598 | 1 / 0 | O | Tomcat `/examples/jsp/security` `j_security_check;jsessionid=...`. Geaccepteerd risico. |
| 12 | 10099 | Source Code Disclosure - ActiveVFP | Medium (Medium) | 540 | 1 / 0 | O | Tomcat `/docs/ssi-howto.html`. Geaccepteerd risico. |
| 13 | 10099 | Source Code Disclosure - SQL | Medium (Medium) | 540 | 9 / 0 | O | Tomcat `/docs/*`. Geaccepteerd risico. |
| 14 | 10099 | Source Code Disclosure - Servlet | Medium (Medium) | 540 | 2 / 0 | O | Tomcat `/docs/aio.html`, `/examples/jsp/jsptoserv/*.java.html`. Geaccepteerd risico. |
| 15 | 10105 | Weak Authentication Method | Medium (Medium) | 326 | 10 / 0 | O | Tomcat `/host-manager/*` (Basic Auth over HTTP). Geaccepteerd risico. |
| 16 | 90022 | Application Error Disclosure | Low (Medium) | 550 | 1 / 0 | O | Tomcat `/examples/jsp/error/err.jsp`. Geaccepteerd risico. |
| 17 | 10010 | Cookie No HttpOnly Flag | Low (Medium) | 1004 | 1 / 0 | O | Tomcat `/examples/servlets/servlet/CookieExample`. Geaccepteerd risico. |
| 18 | 90027 | Cookie Slack Detector | Low (Low) | 205 | 4 / 0 | O | Tomcat `/examples/jsp/`, `/examples/servlets/`. Geaccepteerd risico. |
| 19 | 10054 | Cookie without SameSite Attribute | Low (Medium) | 1275 | 4 / 1 | **F** (deels) | `/openmrs/initialsetup`: best-effort fix via response-wrapper (sectie 3) voor cookies die de applicatie zelf zet. De container-eigen `JSESSIONID`-cookie wordt door Tomcat op connector-niveau geschreven, buiten bereik van een module-filter; vereist `Rfc6265CookieProcessor` in `context.xml` van het upstream image. Restrisico, zie sectie 5. Overige 3 instances Tomcat `/examples`, buiten scope. |
| 20 | 90004 | Cross-Origin-Embedder-Policy Header Missing or Invalid | Low (Medium) | 693 | 4 / 1 | **F** | `/openmrs/initialsetup` gefixt via `Cross-Origin-Embedder-Policy: credentialless`. Overige 3 instances Tomcat root/`docs`, buiten scope. |
| 21 | 90004 | Cross-Origin-Opener-Policy Header Missing or Invalid | Low (Medium) | 693 | 4 / 1 | **F** | `/openmrs/initialsetup` gefixt via `Cross-Origin-Opener-Policy: same-origin`. Overige 3 instances Tomcat root/`docs`, buiten scope. |
| 22 | 90004 | Cross-Origin-Resource-Policy Header Missing or Invalid | Low (Medium) | 693 | 4 / 1 | **F** | `/openmrs/initialsetup` gefixt via `Cross-Origin-Resource-Policy: same-origin`. Overige 3 instances Tomcat root/`docs`, buiten scope. |
| 23 | 10110 | Dangerous JS Functions | Low (Low) | 749 | 2 / 0 | O | Tomcat `/examples/websocket*/snake.*`. Geaccepteerd risico. |
| 24 | 110009 | Full Path Disclosure | Low (Low) | 209 | 12 / 0 | O | Server-filesystempaden (`/bin/jsvc`, `/etc/init.d`, ...), Tomcat/OS-niveau. Geaccepteerd risico. |
| 25 | 10009 | In Page Banner Information Leak | Low (High) | 497 | 5 / 0 | O | Tomcat `META-INF/context.xml`, `WEB-INF/web.xml` banners. Geaccepteerd risico. |
| 26 | 10023 | Information Disclosure - Debug Error Messages | Low (Medium) | 1295 | 1 / 0 | O | Tomcat `/docs/changelog.html`. Geaccepteerd risico. |
| 27 | 10063 | Permissions Policy Header Not Set | Low (Medium) | 693 | 5 / 1 | **F** | `/openmrs/initialsetup` gefixt via `Permissions-Policy`-header. Overige 4 instances Tomcat root/`docs`, buiten scope. |
| 28 | 2 | Private IP Disclosure | Low (Medium) | 497 | 5 / 0 | O | Tomcat `/docs/config/*.html` voorbeeld-IP's. Geaccepteerd risico. |
| 29 | 10036 | Server Leaks Version Information via "Server" Header | Low (High) | 497 | 4 / 2 | **F** (best effort) | `/openmrs/` en `/openmrs/initialsetup` gefixt door `Server`-header te overschrijven met een lege waarde (sectie 3); afhankelijk van de Tomcat-connectorconfiguratie van het upstream image, vandaar "best effort". Overige 2 instances (`/robots.txt`, `/sitemap.xml`) zijn Tomcat root, buiten scope. |
| 30 | 10021 | X-Content-Type-Options Header Missing | Low (Medium) | 693 | 5 / 1 | **F** | `/openmrs/initialsetup` gefixt via `X-Content-Type-Options: nosniff`. Overige 4 instances Tomcat root/`docs`, buiten scope. |
| 31 | 10111 | Authentication Request Identified | Informational (Low) | -1 | 2 / 0 | O | Tomcat `/examples/jsp/security/protected/j_security_check`. Informatief, geen actie. |
| 32 | 10094 | Base64 Disclosure | Informational (Medium) | 319 | 12 / 0 | O | Tomcat `/docs/*` (base64 in voorbeeldpagina's). Geaccepteerd risico. |
| 33 | 10019 | Content-Type Header Missing | Informational (Medium) | 345 | 2 / 0 | O | Tomcat `/docs/appdev/sample/sample.war`, `/examples/async/async2`. Geaccepteerd risico. |
| 34 | 10029 | Cookie Poisoning | Informational (Low) | 565 | 2 / 0 | O | Tomcat `/examples/servlets/servlet/CookieExample`. Geaccepteerd risico. |
| 35 | 90027 | Cookie Slack Detector | Informational (Low) | 205 | 1 / 0 | O | Tomcat `/examples/jsp/security/protected/`. Geaccepteerd risico. |
| 36 | 10058 | GET for POST | Informational (High) | 16 | 5 / 0 | O | Tomcat `/examples/jsp/security/protected/j_security_check`. Geaccepteerd risico. |
| 37 | 10024 | Information Disclosure - Sensitive Information in URL | Informational (Medium) | 598 | 1 / 0 | O | Tomcat `/examples/jsp/cal/cal1.jsp` (ZAP's eigen testparameters in de URL). Geaccepteerd risico. |
| 38 | 10027 | Information Disclosure - Suspicious Comments | Informational (Medium) | 615 | 8 / 1 | **I** | `/openmrs/initialsetup`-instance beoordeeld: het gevonden "verdachte" patroon is de JS-commentaarregel `// Updates the current progress with the new percentage value from the server` - ZAP's regex matcht op het woord "FROM", geen SQL-fragment. **False positive**, geen actie nodig. Overige 7 instances Tomcat `/docs/*`, buiten scope. |
| 39 | 10109 | Modern Web Application | Informational (Medium) | -1 | 5 / 1 | **I** | Informatieve detectie ("dit is een moderne webapp"), geen kwetsbaarheid. Geen actie nodig. |
| 40 | 10049 | Non-Storable Content | Informational (Medium) | 524 | 3 / 2 | **I** | Informatieve cache-observatie op `/openmrs`, `/openmrs/`. Geen actie nodig; caching-gedrag is functioneel correct voor dynamische pagina's. |
| 41 | 90005 | Sec-Fetch-Dest Header is Missing | Informational (High) | 352 | 3 / 3 | **I** | Sec-Fetch-* zijn request-headers die moderne browsers zelf meesturen; dit is een optionele defense-in-depth-suggestie (server zou erop kunnen filteren), geen vastgestelde kwetsbaarheid. Buiten scope van deze fix-ronde, genoteerd als mogelijke toekomstige hardening in sectie 5. |
| 42 | 90005 | Sec-Fetch-Mode Header is Missing | Informational (High) | 352 | 3 / 3 | **I** | Zie #41. |
| 43 | 90005 | Sec-Fetch-Site Header is Missing | Informational (High) | 352 | 3 / 3 | **I** | Zie #41. |
| 44 | 90005 | Sec-Fetch-User Header is Missing | Informational (High) | 352 | 3 / 3 | **I** | Zie #41. |
| 45 | 10112 | Session Management Response Identified | Informational (Medium) | -1 | 3 / 1 | **I** | Informatieve detectie van sessiebeheer, geen kwetsbaarheid. Geen actie nodig. |
| 46 | 10049 | Storable and Cacheable Content | Informational (Medium) | 524 | 5 / 0 | O | Tomcat root/`docs` statische content. Geaccepteerd risico. |
| 47 | 10049 | Storable but Non-Cacheable Content | Informational (Medium) | 524 | 1 / 1 | **I** | Informatieve cache-observatie op `/openmrs/initialsetup`. Geen actie nodig. |
| 48 | 10104 | User Agent Fuzzer | Informational (Medium) | 0 | 5 / 1 | **I** | ZAP test verschillende User-Agent-headers; geen verschil in respons aangetroffen op `/openmrs/`. Geen actie nodig. |
| 49 | 10031 | User Controllable HTML Element Attribute (Potential XSS) | Informational (Low) | 20 | 4 / 0 | O | Tomcat `/examples/jsp/colors/colrs.jsp`, `/examples/jsp/jsp2/el/*.jsp`. Geaccepteerd risico. |

**Samenvatting:** 11 gefixt (F), 10 informational/reviewed zonder fix nodig (I), 28 buiten scope (O). Totaal 49.

## 5. Restrisico en vervolgstappen

| Restrisico | Toelichting | Aanbeveling |
|---|---|---|
| `JSESSIONID`-cookie krijgt geen `SameSite`-attribuut | Tomcat schrijft deze cookie op connector-niveau, vóór onze response-wrapper. Vereist `Rfc6265CookieProcessor` met `sameSiteCookies="lax"` in `context.xml` van de Tomcat-laag. | Niet oplosbaar binnen de module; aan te dragen bij wie het Docker-image/`context.xml` van `openmrs/openmrs-reference-application-distro` beheert. |
| `Server`-header overschrijven is best effort | Werkt alleen als de connector nog geen waarde heeft gezet vóór onze filter draait; niet 100% gegarandeerd op elke Tomcat-configuratie. | Herbevestigen met een herscan (`run-zap.sh`) na deployment. |
| CSP staat `'unsafe-inline' 'unsafe-eval'` toe | Nodig om de legacy Dojo/jQuery-UI niet te breken; vermindert de XSS-beschermende waarde van de CSP. | Op termijn vervangen door nonce-based CSP zodra de UI niet meer op inline scripts leunt (technische schuld, vergelijkbaar met de bekende Java 11-testuitsluitingen). |
| Sec-Fetch-* headers niet gevalideerd door de server (#41-44) | Optionele defense-in-depth tegen CSRF; geen vastgestelde kwetsbaarheid. | Kan worden opgepakt als aanvullende CSRF-hardening in een volgende sprint; niet vereist voor deze mitigatieronde. |
| Tomcat-voorbeeldwebapps (`/examples`, `/docs`, `/manager`, `/host-manager`) blijven actief | 28 bevindingen (categorie O) komen hieruit. Verwijderen vereist een aangepast Docker-image/`Dockerfile` in plaats van het upstream `openmrs-reference-application-distro`-image; buiten scope van de idgen-module-opdracht. | Voor een productie-deployment: deze webapps uit het image verwijderen of een hardened image gebruiken. Aangedragen als aanbeveling voor het auditrapport (Opdracht 6) en `Groep_6_Security-Analyse.md`. |
| Geen DAST-herscan na mitigatie | Geen Docker-omgeving beschikbaar in deze sessie om `docker-compose up -d` + `./run-zap.sh` te draaien. | Herscan uitvoeren zodra deze branch gemerged is; resultaat naast dit document leggen, analoog aan de voor/na-aanpak van `Groep_6_Pentestrapport.md`. |

## 6. Koppeling naar bestaande deliverables

- **`docs/checklist.md`**, Opdracht 5 Deel 9, eis #6 ("DAST-bevindingen beoordeeld en gekoppeld aan bestaande security backlog of als nieuwe finding gedocumenteerd"): afgehandeld door dit document.
- **`Groep_6_Security-Analyse.md`**: de Tomcat-hardening aanbeveling (sectie 5 van dit document) is relevant voor een eventuele aanvulling op de security backlog daar.
- **`docs/dast/zap-report/README.md`**: verwijst nu naar dit document als triage van de bevindingen.
