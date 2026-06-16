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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertNotNull;

/**
 * Context-sensitieve test voor {@link SequenceIdentifierResource#doSearch}: de source-parameter is
 * verplicht en moet een geldig getal zijn; bij een geldige bron wordt een identifier gegenereerd.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class SequenceIdentifierResourceWebTest extends BaseModuleWebContextSensitiveTest {

	private final SequenceIdentifierResource resource = new SequenceIdentifierResource();

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/ControllerTestData.xml");
	}

	private RequestContext requestWith(String sourceParam) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		if (sourceParam != null) {
			request.addParameter("source", sourceParam);
		}
		RequestContext context = new RequestContext();
		context.setRequest(request);
		return context;
	}

	@Test(expected = IllegalArgumentException.class)
	public void doSearch_shouldRejectMissingSource() {
		resource.doSearch(requestWith(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void doSearch_shouldRejectNonNumericSource() {
		resource.doSearch(requestWith("not-a-number"));
	}

	@Test
	public void doSearch_shouldGenerateIdentifierForValidSource() {
		// Bron 2001 (sequential) uit ControllerTestData
		PageableResult result = resource.doSearch(requestWith("2001"));
		assertNotNull(result);
	}
}
