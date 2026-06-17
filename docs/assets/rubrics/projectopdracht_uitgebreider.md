# Projectopdracht LU2

De projectopdracht voor LU2 bestaat uit twee onderdelen.

Je voert met je groep een onderbouwd, weloverwogen verbetervoorstel in bestaande code van OpenMRS uit.  
Je analyseert ook de security en de compliance met NEN7510 van het gekozen onderdeel.

Deze pagina beschrijft de details van deze twee opdrachtonderdelen. 

## Uitgangspunt

Voor de uitvoering van deze opdrachten kies je als groep een project met betrekking tot OpenMRS. Dit kan een OpenMRS module zijn, of de core van het systeem. Een aantal modules zijn op BrightSpace beschikbaar; dit zijn oudere versies waar zeker een aantal verbeterpunten in zitten.

Je kunt ook zelf een OpenMRS module kiezen. Let dan wel op dat je niet een te eenvoudig project kiest. De verbeteronderzoeken (die hieronder beschreven staan) moeten namelijk voldoende inhoud hebben. Twijfel je, neem dan contact op met de docent.

Het project dat je gekozen hebt ga je in de weken die voor LU2 staan onderzoeken, en je gaat vooral zaken vastleggen. Je gaat in dit project geen nieuwe functionaliteit toevoegen, maar je gaat wel duidelijke verbeteringen doorvoeren. 

Na de eerste week van LU2 moet jullie keuze vastgelegd zijn. Dit kun je hier doen [opens in new window]. 

Wijzig deze keuze niet zonder goede reden en zonder in overleg met een docent te gaan.

## Opdrachtonderdeel 1: Verbeteronderzoek onderhoudbaarheid

Om Opdrachtonderdeel 1 te voltooien ga je in het gekozen OpenMRS project verbeteringen doorvoeren. Dit doe je met gebruik van de in onderdeel 2 beschreven ontwikkelomgeving. Je bepaalt non-functional requirements, legt deze vast en richt tooling in (b.v. Qodana of SonarCloud) die dit meet en bij 'niet voldoen' het CI proces laat falen.

Hiervoor gelden de volgende criteria:
* Je hebt de resultaten van een systematische analyse op onderhoudbaarheid duidelijk en bruikbaar vastgelegd.
* Je hebt relevante tests opgesteld, uitgevoerd en bijbehorende testresultaten duidelijk en bruikbaar vastgelegd.
* Je hebt geprioriteerde verbeteringen voor onderhoudbaarheid vastgelegd en onderbouwd. De eerder uitgevoerde analyse en vastgelegde testresultaten zijn verwerkt in de onderbouwing.
* Je hebt een duidelijk en bruikbaar aangepast ontwerp voor geselecteerde verbeteringen en onderbouwt deze met ontwerpprincipes, ontwerppatronen en refactoringpatronen.
* In een PoC heb je de verbeteringen gerealiseerd overeenkomstig met het ontwerp en verantwoord hoe de realisatie tot stand is gekomen met betrekking tot het gebruik van (AI-)tooling.
* Je hebt met testen na verbetering van de uitgewerkte adviezen aangetoond hoe onderhoudbaarheid verbeterd is en er geen regressie heeft opgetreden.
* Je presenteert je resultaten op professionele wijze.

## Opdrachtonderdeel 2: Verbeteronderzoek security & compliance

Je richt een softwareontwikkelplatform (b.v. GitHub) waar je met je projectgroep aan het gekozen project kunt werken. Dit platform richt je in zodat software ontwikkeld wordt conform de NEN-7510:2024 norm.

De code van het gekozen OpenMRS project upload je daar. In een aantal sprints lopend van week 5 t/m week 8 maak je de staat van het project inzichtelijk en voert een aantal verbeteringen door die het gekozen project laten voldoen aan gekozen NEN-7510:2024 controls. Dit maak je aantoonbaar door een audit rapport op te leveren met inhoud die herleidbaar is naar de inrichting van het ontwikkelplatform en het verbeterde PoC.

Voor deze opdracht gelden de volgende criteria:
* Je heb de resultaten van een audit van het systeem ten aanzien van NEN7510 duidelijk en bruikbaar vastgelegd. De resultaten bevatten ook een advies over een aanpak voor verbeterde security van het systeem op basis van deze lijst met risico-inschatting geprioriteerde kwetsbaarheden.
* Je hebt secure pipelines ingericht met deployment naar verschillende omgevingen uit OTAP.
* Op basis van de SBOM en de CVE's met bijbehorende CVSS-scores heb je un onderbouwd en bruikbaar advies opgesteld over updates naar nieuwere versies van de libraries waar het systeem van afhankelijk is.
* Je voert security code reviews uit en je hebt geprioriteerde technische kwetsbaarheden duidelijk en bruikbaar vastgelegd en onderbouwd. Je beschrijft op basis van valide bronnen welke risico's bestaan bij gebruik van het systeem als kwetsbaarheden niet opgelost worden.
* Met navolgbaar gedocumenteerde penetration tests toon je het misbruik aan van de meest kritische kwetsbaarheden.
* Je mitigeert deze kwetsbaarheden en toont met penetration tests aan dat de securityrisico's verlaagd zijn. Je verantwoordt hoe de realisatie van de mitigaties tot stand is gekomen met betrekking tot het gebruik van (AI-)tooling.
* Je presenteert je resultaten op professionele wijze.

## Toetsing en beoordeling

De toetsing en beoordeling van deze LU bestaat uit 3 toetsonderdelen:
1. Een uitgewerkt verbeteronderzoek (beroepsproduct) naar de onderhoudbaarheid van het systeem,
2. Een uitgewerkt verbeteronderzoek (beroepsproduct) naar de securityaspecten van het systeem, en
3. Een individueel criteriumgericht interview (CGI).

Voor ieder van deze drie toetsonderdelen moet je een score van minimaal 55 (van totaal 100) punten behalen voor een voldoende resultaat op de leeruitkomst.

### Toetsonderdeel 1: Verbeteronderzoek onderhoudbaarheid

Het beroepsproduct is de uitwerking die je met je groep hebt gemaakt van de opdracht. Die uitwerking presenteer je met je groep voor twee docenten.

#### Inleveren en beoordeling
Het beroepsproduct lever je in via de inleverlinks voor het beroepsproduct van LU. Bij de inleverlinks vind je de rubric die we gebruiken bij de beoordeling. Je vindt daar ook informatie over de onderdelen die je inlevert.

#### Richtlijnen voor de presentatie
We publiceren zo snel mogelijk een planning voor de presentaties van iedere groep. Zorg dat je op het tijdstip van jullie presentatie met alle groepsleden in het aangegeven lokaal aanwezig bent.

Jullie presenteren en demonstreren het beroepsproduct op een heldere en gestructureerde manier. Je verantwoordt in de presentatie dat jullie beroepsproduct aan de eisen van de opdracht en aan de minimale criteria (kolom voldoende) van de rubric voldoet. 

Voor de presentatie en demonstratie samen is 30 minuten beschikbaar. Je kunt die tijd zelf indelen. Niet alle groepsleden hoeven het woord te krijgen.

Daarna is er 15 minuten tijd voor verdiepende vragen door de docenten.

Beoordeling vindt plaats aan de hand van de rubric bij het beroepsproduct. De beoordeling van het beroepsproduct geldt voor de hele groep.

### Toetsonderdeel 2: Verbeteronderzoek security

Hiervoor gelden dezelfde richtlijnen als voor toetsonderdeel 1. Het beroepsproduct is de uitwerking die je met je groep hebt gemaakt van de opdracht. Die uitwerking presenteer je ook hier met je groep voor twee docenten.

#### Inleveren en beoordeling
Het beroepsproduct lever je in via de inleverlinks voor het beroepsproduct van LU. Bij de inleverlinks vind je de rubric die we gebruiken bij de beoordeling. Je vindt daar ook informatie over de onderdelen die je inlevert.

#### Presentatie
Voor de presentatie gelden dezelfde criteria als bij toetsonderdeel 1.

### Toetsonderdeel 3: Criterium gericht interview

In een vraaggesprek verantwoord je de resultaten van de systematische analyse, de duurzaamheid van het ontwerp en samenhang daarvan met de analyse, en aangetoonde verbeteringen.

Voor de resultaten van het verbeteronderzoek op het gebied van security verantwoord je de vertaling van auditbevindingen naar risico’s en aangetoonde verbetermaatregelen.

In een vraaggesprek verantwoordt ieder teamlid individueel de gemaakte keuzes van de ontwikkelde uitbreiding. Zo laat je zien dat je een beeld hebt van de verbeteringen, en van de eventuele tekortkomingen, blinde vlekken en mogelijke alternatieven, die je met je team in de bestaande software hebt aangebracht. Je verantwoordt ook de uitwerking van de security en compliance.

Je gebruikt in dit gesprek correcte vaktaal, betrekt relevante concepten en communiceert volgens logische redeneringen. En je reflecteert op jouw handelen in het project.

#### Beoordeling
Voor het CGI hoef je niets in te leveren. Bij de inleverlinks vind je wel een item voor het CGI, met daaraan gekoppeld de rubric die we gebruiken bij de beoordeling. De docent gebruikt dit voor het vastleggen van de beoordeling.