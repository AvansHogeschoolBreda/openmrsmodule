/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.service;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.LogEntry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Dekt de randmethoden van {@link BaseIdentifierSourceService} die de bestaande
 * {@link IdentifierSourceServiceTest} en {@link IdentifierSourceServiceExtraTest} niet raken:
 * de type-groepering, het filteren van types op autogeneratie-optie, generatie via type/locatie
 * met en zonder optie, log-opvraging en pool-refill. Doel: de ongedekte regels op nieuwe code
 * wegwerken zodat de SonarCloud-quality-gate de 80%-drempel haalt (zie Testplan sectie 8.5/8.7).
 *
 * De groeperende en filterende methoden vereisen consistente data (alle bronnen op een bestaand
 * type, hooguit een optie per type), daarom laden die tests ServiceCoverageTestData.xml in plaats
 * van de inconsistente upstream TestData.xml.
 *
 * Toegevoegd door groep 6.
 */
public class BaseIdentifierSourceServiceCoverageTest extends IdgenBaseTest {

	private IdentifierSourceService getService() {
		return Context.getService(IdentifierSourceService.class);
	}

	@Test
	public void getIdentifierSourcesByType_shouldGroupSourcesPerIdentifierType() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/ServiceCoverageTestData.xml");
		Map<PatientIdentifierType, List<IdentifierSource>> grouped = getService().getIdentifierSourcesByType(false);
		assertNotNull(grouped);
		assertFalse(grouped.isEmpty());
		PatientIdentifierType type1 = Context.getPatientService().getPatientIdentifierType(1);
		assertTrue(grouped.containsKey(type1));
		assertFalse(grouped.get(type1).isEmpty());
	}

	@Test
	public void getPatientIdentifierTypesByAutoGenerationOption_shouldFilterOnTheOptionFlags() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/ServiceCoverageTestData.xml");
		PatientIdentifierType type1 = Context.getPatientService().getPatientIdentifierType(1);
		// optie 3001 op type 1 staat op manual=true, auto=true
		List<PatientIdentifierType> matching = getService().getPatientIdentifierTypesByAutoGenerationOption(true, true);
		assertTrue(matching.contains(type1));
		// andere vlaggencombinatie matcht niet, dus type 1 valt af (dekt de false-tak)
		List<PatientIdentifierType> nonMatching = getService().getPatientIdentifierTypesByAutoGenerationOption(false, false);
		assertFalse(nonMatching.contains(type1));
	}

	@Test
	public void generateIdentifier_byType_shouldReturnNullWhenNoAutoGenerationOptionExists() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		// type 50 heeft wel een bron maar geen autogeneratie-optie
		PatientIdentifierType type50 = Context.getPatientService().getPatientIdentifierType(50);
		assertNull(getService().generateIdentifier(type50, "geen optie"));
	}

	@Test
	public void generateIdentifier_byTypeAndLocation_shouldReturnNullWhenNoOptionAtAll() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		// type 50 heeft geen enkele optie, dus ook niet voor een locatie
		PatientIdentifierType type50 = Context.getPatientService().getPatientIdentifierType(50);
		Location location2 = Context.getLocationService().getLocation(2);
		assertNull(getService().generateIdentifier(type50, location2, "geen optie"));
	}

	@Test
	public void generateIdentifier_byTypeAndLocation_shouldGenerateFromTheMatchingOption() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		// optie 3 koppelt type 2 + locatie 2 aan pool-bron 3 met automatische generatie aan
		PatientIdentifierType type2 = Context.getPatientService().getPatientIdentifierType(2);
		Location location2 = Context.getLocationService().getLocation(2);
		String id = getService().generateIdentifier(type2, location2, "via locatie-optie");
		assertNotNull(id);
	}

	@Test
	public void getLogEntries_shouldReturnEntriesForASource() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		IdentifierSource source = getService().getIdentifierSource(1);
		List<LogEntry> entries = getService().getLogEntries(source, null, null, null, null, null);
		assertNotNull(entries);
		assertFalse(entries.isEmpty());
	}

	@Test
	public void checkAndRefillIdentifierPool_shouldRefillUntilMinimumIsReached() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		// pool 3 heeft een sequentiele bron en min-pool 100 met 5 beschikbaar, dus refill wordt getriggerd
		IdentifierPool pool = (IdentifierPool) getService().getIdentifierSource(3);
		getService().checkAndRefillIdentifierPool(pool);
		assertTrue(getService().getQuantityInPool(pool, true, false) >= pool.getMinPoolSize());
	}
}
