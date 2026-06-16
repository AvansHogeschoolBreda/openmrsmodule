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
package org.openmrs.module.idgen.rest.resource.handler;

import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pure unit tests voor de drie REST subclass-handlers. De context-onafhankelijke methoden
 * (representatiebeschrijving, Swagger-modellen, creatable/updatable properties, type-naam en
 * display) worden gedekt; save() en getAllByType() vereisen de OpenMRS Context en vallen erbuiten.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class ResourceHandlersUnitTest {

	private final DefaultRepresentation def = new DefaultRepresentation();
	private final FullRepresentation full = new FullRepresentation();
	private final RefRepresentation ref = new RefRepresentation();

	@Test
	public void sequentialHandler_shouldExposeMetadataDescriptionsAndModels() {
		SequentialIdentifierGeneratorResourceHandler h = new SequentialIdentifierGeneratorResourceHandler();
		assertEquals("2.2", h.getResourceVersion());
		assertNotNull(h.newDelegate());
		assertNotNull(h.getRepresentationDescription(def));
		assertNotNull(h.getRepresentationDescription(full));
		assertNotNull(h.getRepresentationDescription(ref));
		assertNotNull(h.getCreatableProperties());
		assertNotNull(h.getUpdatableProperties());
		assertNotNull(h.getTypeName());
		assertNotNull(h.getGETModel(def));
		assertNotNull(h.getGETModel(full));
		assertNotNull(h.getGETModel(ref));
		assertNotNull(h.getCREATEModel(def));

		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.setName("Seq");
		gen.setIdentifierType(new PatientIdentifierType());
		assertTrue(h.getDisplayString(gen).contains("Seq"));
	}

	@Test
	public void poolHandler_shouldExposeMetadataDescriptionsAndModels() {
		IdentifierPoolResourceHandler h = new IdentifierPoolResourceHandler();
		assertEquals("2.2", h.getResourceVersion());
		assertNotNull(h.newDelegate());
		assertNotNull(h.getRepresentationDescription(def));
		assertNotNull(h.getRepresentationDescription(full));
		assertNotNull(h.getRepresentationDescription(ref));
		assertNotNull(h.getCreatableProperties());
		assertNotNull(h.getUpdatableProperties());
		assertNotNull(h.getTypeName());
		assertNotNull(h.getGETModel(def));
		assertNotNull(h.getGETModel(full));
		assertNotNull(h.getGETModel(ref));
		assertNotNull(h.getCREATEModel(def));
		assertNotNull(h.getUPDATEModel(def));

		IdentifierPool pool = new IdentifierPool();
		pool.setName("Pool");
		pool.setIdentifierType(new PatientIdentifierType());
		assertTrue(h.getDisplayString(pool).contains("Pool"));
	}

	@Test
	public void remoteHandler_shouldExposeMetadataDescriptionsAndModels() {
		RemoteIdentifierSourceResourceHandler h = new RemoteIdentifierSourceResourceHandler();
		assertEquals("2.2", h.getResourceVersion());
		assertNotNull(h.newDelegate());
		assertNotNull(h.getRepresentationDescription(def));
		assertNotNull(h.getRepresentationDescription(full));
		assertNotNull(h.getRepresentationDescription(ref));
		assertNotNull(h.getCreatableProperties());
		assertNotNull(h.getUpdatableProperties());
		assertNotNull(h.getTypeName());
		assertNotNull(h.getGETModel(def));
		assertNotNull(h.getGETModel(full));
		assertNotNull(h.getCREATEModel(def));

		RemoteIdentifierSource remote = new RemoteIdentifierSource();
		remote.setName("Remote");
		remote.setIdentifierType(new PatientIdentifierType());
		assertTrue(h.getDisplayString(remote).contains("Remote"));
	}
}
