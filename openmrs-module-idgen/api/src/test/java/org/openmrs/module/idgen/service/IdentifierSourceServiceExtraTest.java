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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Aanvullende context-sensitieve tests voor {@link BaseIdentifierSourceService} die de bestaande
 * {@link IdentifierSourceServiceTest} niet raakt: de pool-, autogeneratie- en zoek-methoden plus
 * generatie via een PatientIdentifierType. Deze tests dekken indirect ook de processors, de
 * generator en de DAO.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdentifierSourceServiceExtraTest extends IdgenBaseTest {

	private IdentifierSourceService service;

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		service = Context.getService(IdentifierSourceService.class);
	}

	@Test
	public void getIdentifierSourceTypes_shouldReturnTheThreeSupportedTypes() {
		assertEquals(3, service.getIdentifierSourceTypes().size());
	}

	@Test
	public void getProcessor_shouldReturnAProcessorForASequentialSource() {
		IdentifierSource source = service.getIdentifierSource(1);
		IdentifierSourceProcessor processor = service.getProcessor(source);
		assertNotNull(processor);
	}

	@Test
	public void generateIdentifier_shouldGenerateFromASequentialSource() {
		String id = service.generateIdentifier(service.getIdentifierSource(7), "test");
		// Bron 7 heeft prefix "MRS" en base 0-9
		assertTrue(id.startsWith("MRS"));
	}

	@Test
	public void generateIdentifier_shouldDrawFromAnIdentifierPool() {
		IdentifierPool pool = (IdentifierPool) service.getIdentifierSource(3);
		String id = service.generateIdentifier(pool, "from pool");
		assertNotNull(id);
	}

	@Test
	public void generateIdentifier_shouldUseAutoGenerationOptionForType() {
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierType(1);
		String id = service.generateIdentifier(type, "auto");
		// Optie 1 koppelt type 1 aan bron 1 met automatische generatie aan
		assertNotNull(id);
	}

	@Test
	public void getAvailableIdentifiers_andQuantity_shouldReflectThePool() {
		IdentifierPool pool = (IdentifierPool) service.getIdentifierSource(3);
		List<PooledIdentifier> available = service.getAvailableIdentifiers(pool, 3);
		assertEquals(3, available.size());
		assertEquals(5, service.getQuantityInPool(pool, true, false));
		assertEquals(0, service.getQuantityInPool(pool, false, true));
	}

	@Test
	public void addIdentifiersToPool_shouldGrowThePool() {
		IdentifierPool pool = (IdentifierPool) service.getIdentifierSource(4);
		int before = service.getQuantityInPool(pool, false, false);
		service.addIdentifiersToPool(pool, Arrays.asList("X-1", "X-2"));
		assertEquals(before + 2, service.getQuantityInPool(pool, false, false));
	}

	@Test
	public void saveAndPurgeAutoGenerationOption_shouldRoundTrip() {
		PatientIdentifierType type = Context.getPatientService().getPatientIdentifierType(1);
		AutoGenerationOption option = new AutoGenerationOption(type);
		option.setSource(service.getIdentifierSource(1));
		AutoGenerationOption saved = service.saveAutoGenerationOption(option);
		assertNotNull(saved.getId());
		service.purgeAutoGenerationOption(saved);
	}

	@Test
	public void sequenceValue_shouldBeSavedAndRead() {
		SequentialIdentifierGenerator seq = (SequentialIdentifierGenerator) service.getIdentifierSource(1);
		service.saveSequenceValue(seq, 25L);
		assertEquals(Long.valueOf(25L), service.getSequenceValue(seq));
	}

	@Test(expected = APIException.class)
	public void saveIdentifierSource_shouldRejectWhenNameIsNull() {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.setName(null);
		service.saveIdentifierSource(gen);
	}
}
