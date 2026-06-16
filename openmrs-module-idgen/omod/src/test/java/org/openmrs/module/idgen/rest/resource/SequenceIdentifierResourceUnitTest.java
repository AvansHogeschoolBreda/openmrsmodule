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
import org.openmrs.module.idgen.Identifier;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Pure unit tests voor {@link SequenceIdentifierResource}: het newDelegate-contract, de
 * niet-ondersteunde CRUD-operaties en de representatiebeschrijving per representatietype.
 * De doSearch-methode vereist een RequestContext en de OpenMRS Context en valt buiten deze test.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class SequenceIdentifierResourceUnitTest {

	private final SequenceIdentifierResource resource = new SequenceIdentifierResource();

	@Test
	public void newDelegate_shouldReturnEmptyIdentifier() {
		assertNotNull(resource.newDelegate());
	}

	@Test
	public void getRepresentationDescription_shouldDescribeIdentifierValue() {
		for (Object rep : new Object[] { new RefRepresentation(), new DefaultRepresentation(), new FullRepresentation() }) {
			DelegatingResourceDescription description =
			        resource.getRepresentationDescription((org.openmrs.module.webservices.rest.web.representation.Representation) rep);
			assertNotNull(description);
			assertNotNull(description.getProperties().get("identifierValue"));
		}
	}

	@Test
	public void crudOperations_shouldAllBeUnsupported() throws Exception {
		assertUnsupported(new Call() { public void run() throws Exception { resource.save(new Identifier()); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.getByUniqueId("x"); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.delete(new Identifier(), "reason", null); } });
		assertUnsupported(new Call() { public void run() throws Exception { resource.purge(new Identifier(), null); } });
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
