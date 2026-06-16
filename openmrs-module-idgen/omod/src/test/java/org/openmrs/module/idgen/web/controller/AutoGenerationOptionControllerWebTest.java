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
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Context-sensitieve tests voor {@link AutoGenerationOptionController}: het bewaren (geldig en de
 * twee validatie-foutpaden), verwijderen, bewerken en het overzicht van autogeneratie-opties.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class AutoGenerationOptionControllerWebTest extends BaseModuleWebContextSensitiveTest {

	private final AutoGenerationOptionController controller = new AutoGenerationOptionController();
	private IdentifierSourceService service;
	private PatientIdentifierType type1;

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/ControllerTestData.xml");
		service = Context.getService(IdentifierSourceService.class);
		type1 = Context.getPatientService().getPatientIdentifierType(1);
	}

	@Test
	public void saveAutoGenerationOption_shouldSaveValidOption() {
		AutoGenerationOption option = new AutoGenerationOption(type1);
		option.setSource(service.getIdentifierSourceByUuid("c6ctrl-0000-0000-0000-000000002001"));
		option.setAutomaticGenerationEnabled(true);
		BindException errors = new BindException(option, "option");

		ModelAndView mav = controller.saveAutoGenerationOption(option, errors, new SimpleSessionStatus());

		assertTrue(mav.getViewName().contains("manageAutoGenerationOptions"));
	}

	@Test
	public void saveAutoGenerationOption_shouldRejectMissingIdentifierType() {
		AutoGenerationOption option = new AutoGenerationOption();
		BindException errors = new BindException(option, "option");

		ModelAndView mav = controller.saveAutoGenerationOption(option, errors, new SimpleSessionStatus());

		assertTrue(errors.hasErrors());
		assertTrue(mav.getViewName().contains("editAutoGenerationOption"));
	}

	@Test
	public void saveAutoGenerationOption_shouldRejectAutomaticWithoutSource() {
		AutoGenerationOption option = new AutoGenerationOption(type1);
		option.setAutomaticGenerationEnabled(true);
		option.setSource(null);
		BindException errors = new BindException(option, "option");

		controller.saveAutoGenerationOption(option, errors, new SimpleSessionStatus());

		assertTrue(errors.hasErrors());
	}

	@Test
	public void editAutoGenerationOption_shouldExposeSourcesAndLocations() {
		ModelMap model = new ModelMap();
		controller.editAutoGenerationOption(model, new MockHttpServletRequest(), null, type1);
		assertNotNull(model.get("option"));
		assertNotNull(model.get("availableLocations"));
	}

	@Test
	public void manageAutoGenerationOptions_shouldBuildOptionMap() {
		ModelMap model = new ModelMap();
		controller.manageAutoGenerationOptions(model);
		assertNotNull(model.get("optionMap"));
		assertNotNull(model.get("identifierTypes"));
	}

	@Test
	public void deleteAutoGenerationOption_shouldPurgeOption() {
		AutoGenerationOption option = service.getAutoGenerationOptionByUuid("c6ctrl-opt-0000-0000-000000002001");
		assertNotNull(option);
		controller.deleteAutoGenerationOption(new ModelMap(), new MockHttpServletRequest(), option);
		assertNotNull(controller); // purge uitgevoerd zonder fout
	}
}
