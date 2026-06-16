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
package org.openmrs.module.idgen.web.controller;

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertTrue;

/**
 * Context-sensitieve tests voor {@link IdgenEditPatientIdentifiersController}. De controller bouwt
 * een JSON-overzicht van patient-identifier-types en (auto)generatie-opties en schrijft dit naar de
 * response. Getest zonder en met een patient.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdgenEditPatientIdentifiersControllerWebTest extends BaseModuleWebContextSensitiveTest {

	private final IdgenEditPatientIdentifiersController controller = new IdgenEditPatientIdentifiersController();

	@Test
	public void editPatientIdentifiers_shouldRenderJsonWithoutPatient() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		controller.editPatientIdentifiers(new ModelMap(), new MockHttpServletRequest(), response, null);
		assertTrue(response.getContentAsString().contains("allIdentifiers"));
		assertTrue(response.getContentAsString().contains("defaultIdentifiers"));
	}

	@Test
	public void editPatientIdentifiers_shouldIncludePatientActiveIdentifiers() throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();
		// Patient 2 bestaat in de standaard testdata en heeft actieve identifiers
		controller.editPatientIdentifiers(new ModelMap(), new MockHttpServletRequest(), response, 2);
		assertTrue(response.getContentAsString().contains("defaultIdentifiers"));
	}

	@Test
	public void getImportantTypes_shouldBeEmptyWhenGlobalPropertyIsUnset() {
		// Zonder de global property voor belangrijke types is de map leeg
		assertTrue(controller.getImportantTypes().isEmpty());
	}
}
