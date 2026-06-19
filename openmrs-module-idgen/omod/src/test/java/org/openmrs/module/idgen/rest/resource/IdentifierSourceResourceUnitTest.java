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
package org.openmrs.module.idgen.rest.resource;

import org.junit.Test;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.validation.ValidationException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Pure unit tests (geen Spring-context) voor de context-onafhankelijke delen van de God Class
 * {@link IdentifierSourceResource}: representatiebeschrijvingen, creatable/updatable properties,
 * display, de pool-property-getters (zowel pool- als niet-pool-tak), newDelegate, de Swagger-modellen
 * en de validatie-fouttak van create. De create/update happy-paths vereisen de Context en blijven
 * voor de context-sensitieve tests.
 *
 * Doel: de ongedekte regels op nieuwe code in deze klasse wegwerken zodat de SonarCloud-quality-gate
 * de 80%-drempel haalt, en tegelijk een regressie-vangnet leveren voor de latere klassesplitsing
 * (zie Testplan sectie 8.5/8.7 en Refactoring-Onderbouwing 3.2).
 *
 * Toegevoegd door groep 6.
 */
public class IdentifierSourceResourceUnitTest {

	private final IdentifierSourceResource resource = new IdentifierSourceResource();
	private final DefaultRepresentation def = new DefaultRepresentation();
	private final FullRepresentation full = new FullRepresentation();
	private final RefRepresentation ref = new RefRepresentation();

	@Test
	public void representationDescriptions_shouldDifferPerRepresentation() {
		assertNotNull(resource.getRepresentationDescription(ref));
		assertNotNull(resource.getRepresentationDescription(def));
		assertNotNull(resource.getRepresentationDescription(full));
	}

	@Test
	public void creatableAndUpdatableProperties_shouldBeDefined() {
		assertNotNull(resource.getCreatableProperties());
		assertNotNull(resource.getUpdatableProperties());
	}

	@Test
	public void swaggerModels_shouldBeDefinedForEachRepresentation() {
		assertNotNull(resource.getGETModel(def));
		assertNotNull(resource.getGETModel(full));
		assertNotNull(resource.getGETModel(ref));
		assertNotNull(resource.getCREATEModel(def));
		assertNotNull(resource.getUPDATEModel(def));
	}

	@Test
	public void newDelegate_shouldReturnASequentialGenerator() {
		assertTrue(resource.newDelegate() instanceof SequentialIdentifierGenerator);
	}

	@Test
	public void getDisplayString_shouldRenderTypeNameAndClass() {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.setName("Bron A");
		assertTrue(resource.getDisplayString(gen).contains("Bron A"));
	}

	@Test
	public void hasTypesDefined_shouldBeTrue() {
		assertTrue(resource.hasTypesDefined());
	}

	@Test
	public void poolPropertyGetters_shouldReportSizesForAPool() {
		IdentifierPool pool = new IdentifierPool();
		// verse pool: lege maar niet-null collecties
		assertNotNull(resource.getIdentifiers(pool));
		assertTrue(resource.getUsedIdentifiers(pool) == 0);
		assertTrue(resource.getAvailableIdentifiers(pool) == 0);
	}

	@Test
	public void poolPropertyGetters_shouldReturnNullForNonPoolSources() {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		assertNull(resource.getIdentifiers(gen));
		assertNull(resource.getUsedIdentifiers(gen));
		assertNull(resource.getAvailableIdentifiers(gen));
	}

	@Test(expected = ValidationException.class)
	public void create_shouldRejectAPostBodyMissingTheRequiredFields() {
		// lege body: geen sourceType, name of identifierType -> validatie verzamelt fouten en gooit
		resource.create(new SimpleObject(), null);
	}
}
