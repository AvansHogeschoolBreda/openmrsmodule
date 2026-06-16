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
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Pure unit tests voor {@link IdentifierResource}. De sub-resource ondersteunt vrijwel geen CRUD
 * (alles gooit {@link ResourceDoesNotSupportOperationException}); deze test legt dat contract vast
 * en dekt de context-onafhankelijke model- en property-methoden.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdentifierResourceUnitTest {

	private final IdentifierResource resource = new IdentifierResource();
	private final DefaultRepresentation rep = new DefaultRepresentation();

	@Test
	public void create_shouldReturnNullForNullPost() {
		assertNull(resource.create("parent-uuid", null, null));
	}

	@Test
	public void models_shouldExposeOnlyCreateModel() {
		assertNull(resource.getGETModel(rep));
		assertNull(resource.getUPDATEModel(rep));
		assertNotNull(resource.getCREATEModel(rep));
	}

	@Test
	public void getCreatableProperties_shouldContainComment() {
		DelegatingResourceDescription description = resource.getCreatableProperties();
		assertNotNull(description.getProperties().get("comment"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getUri_shouldNotBeSupported() {
		resource.getUri(null);
	}

	@Test
	public void crudOperations_shouldAllBeUnsupported() throws Exception {
		assertUnsupported(new Call() { public void run() throws Exception { resource.newDelegate(); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.save(null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.getByUniqueId("x"); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.getParent(null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.setParent(null, null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.doGetAll(null, null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.getRepresentationDescription(rep); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.getAll("p", null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.put("p", null, null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.retrieve("p", "u", null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.update("p", "u", null, null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.delete("p", "u", "reason", null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.purge("p", "u", null); } });
	}

	private interface Call {
		void run() throws Exception;
	}

	private void assertUnsupported(Call call) throws Exception {
		try {
			call.run();
			fail("expected ResourceDoesNotSupportOperationException");
		}
		catch (ResourceDoesNotSupportOperationException expected) {
			// contract: operation is not supported
		}
	}
}
