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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Context-sensitieve tests voor {@link LogEntryController#viewLogEntries}: zonder action-parameter
 * worden geen logregels opgehaald, met action wel.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class LogEntryControllerWebTest extends BaseModuleWebContextSensitiveTest {

	private final LogEntryController controller = new LogEntryController();

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void viewLogEntries_shouldQueryWhenActionIsSet() {
		ModelMap model = new ModelMap();
		controller.viewLogEntries(model, new MockHttpServletRequest(), null, null, null, null, null, null, "search");
		List<LogEntry> entries = (List<LogEntry>) model.get("logEntries");
		assertNotNull(entries);
		assertTrue(entries.size() >= 1);
		assertNotNull(model.get("identifierSources"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void viewLogEntries_shouldReturnEmptyWithoutAction() {
		ModelMap model = new ModelMap();
		controller.viewLogEntries(model, new MockHttpServletRequest(), null, null, null, null, null, null, null);
		List<LogEntry> entries = (List<LogEntry>) model.get("logEntries");
		assertNotNull(entries);
		assertTrue(entries.isEmpty());
	}
}
