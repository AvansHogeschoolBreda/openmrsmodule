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
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Pure unit tests voor de context-onafhankelijke delen van {@link AutoGenerationOptionResource}:
 * representatiebeschrijvingen, Swagger-modellen, creatable/updatable properties, display en het
 * niet-ondersteunde delete-contract. create/update/save/getByUniqueId vereisen de Context.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class AutoGenerationOptionResourceUnitTest {

	private final AutoGenerationOptionResource resource = new AutoGenerationOptionResource();
	private final DefaultRepresentation def = new DefaultRepresentation();
	private final FullRepresentation full = new FullRepresentation();
	private final RefRepresentation ref = new RefRepresentation();

	@Test
	public void newDelegate_shouldReturnEmptyOption() {
		assertNotNull(resource.newDelegate());
	}

	@Test
	public void representationDescriptions_shouldBeDefinedPerRepresentation() {
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
	public void swaggerModels_shouldBeDefined() {
		assertNotNull(resource.getGETModel(def));
		assertNotNull(resource.getGETModel(full));
		assertNotNull(resource.getGETModel(ref));
		assertNotNull(resource.getCREATEModel(def));
		assertNotNull(resource.getUPDATEModel(def));
	}

	@Test
	public void getDisplayString_shouldRenderWithoutContext() {
		assertNotNull(resource.getDisplayString(new AutoGenerationOption()));
	}

	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void delete_shouldNotBeSupported() {
		resource.delete(new AutoGenerationOption(), "reason", null);
	}
}
