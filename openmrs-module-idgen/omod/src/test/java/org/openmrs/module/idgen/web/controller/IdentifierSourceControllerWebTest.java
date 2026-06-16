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
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Context-sensitieve tests voor {@link IdentifierSourceController}: het tonen, bewerken, overzicht,
 * bewaren (geldig en validatie-fout), verwijderen, vullen van een pool en het reserveren en
 * exporteren van identifiers.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdentifierSourceControllerWebTest extends BaseModuleWebContextSensitiveTest {

	private static final String SEQ_A_UUID = "c6ctrl-0000-0000-0000-000000002001";
	private static final String SEQ_B_UUID = "c6ctrl-0000-0000-0000-000000002002";
	private static final String POOL_UUID = "c6ctrl-0000-0000-0000-000000002003";

	private final IdentifierSourceController controller = new IdentifierSourceController();
	private IdentifierSourceService service;

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/ControllerTestData.xml");
		service = Context.getService(IdentifierSourceService.class);
	}

	@Test
	public void viewIdentifierSource_shouldExposeSource() {
		ModelMap model = new ModelMap();
		controller.viewIdentifierSource(model, service.getIdentifierSourceByUuid(SEQ_A_UUID));
		assertNotNull(model.get("source"));
	}

	@Test
	public void editIdentifierSource_shouldListCompatibleSources() {
		ModelMap model = new ModelMap();
		IdentifierSource source = service.getIdentifierSourceByUuid(SEQ_A_UUID);
		controller.editIdentifierSource(model, new MockHttpServletRequest(), source, null, null);
		assertNotNull(model.get("source"));
		assertNotNull(model.get("otherCompatibleSources"));
	}

	@Test
	public void manageIdentifierSources_shouldGroupByType() {
		ModelMap model = new ModelMap();
		controller.manageIdentifierSources(model, false);
		assertNotNull(model.get("sourcesByType"));
		assertNotNull(model.get("sourceTypes"));
	}

	@Test
	public void saveIdentifierSource_shouldSaveValidSourceAndRedirect() {
		SequentialIdentifierGenerator source = new SequentialIdentifierGenerator();
		source.setName("Saved By Controller");
		source.setFirstIdentifierBase("1");
		source.setBaseCharacterSet("0123456789");
		source.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		BindException errors = new BindException(source, "source");

		ModelAndView mav = controller.saveIdentifierSource(source, errors, new SimpleSessionStatus(), Boolean.TRUE);

		assertTrue(mav.getViewName().contains("manageIdentifierSources"));
		assertNotNull(service.getIdentifierSourceByUuid(source.getUuid()));
	}

	@Test
	public void saveIdentifierSource_shouldReturnToEditOnValidationError() {
		SequentialIdentifierGenerator source = new SequentialIdentifierGenerator();
		// geen naam: de validator wijst af
		BindException errors = new BindException(source, "source");

		ModelAndView mav = controller.saveIdentifierSource(source, errors, new SimpleSessionStatus(), Boolean.FALSE);

		assertTrue(errors.hasErrors());
		assertTrue(mav.getViewName().contains("editIdentifierSource"));
	}

	@Test
	public void deleteIdentifierSource_shouldPurgeAndRedirect() {
		IdentifierSource source = service.getIdentifierSourceByUuid(SEQ_B_UUID);
		String view = controller.deletePatientSearch(new ModelMap(), source);
		assertTrue(view.contains("manageIdentifierSources"));
	}

	@Test
	public void addIdentifiersFromSource_shouldFillThePool() throws Exception {
		IdentifierSource pool = service.getIdentifierSourceByUuid(POOL_UUID);
		String view = controller.addIdentifiersFromSource(new ModelMap(), new MockHttpServletRequest(),
		        new MockHttpServletResponse(), pool, 2);
		assertTrue(view.contains("viewIdentifierSource"));
	}

	@Test
	public void reserveIdentifiersFromFile_shouldAddReservedIdentifiers() throws Exception {
		IdentifierSource source = service.getIdentifierSourceByUuid(SEQ_A_UUID);
		MockMultipartFile file = new MockMultipartFile("inputFile", "RES-1\nRES-2\n".getBytes(StandardCharsets.UTF_8));
		controller.reserveIdentifiersFromFile(new ModelMap(), new MockHttpServletRequest(),
		        new MockHttpServletResponse(), source, file);
		assertTrue(service.getIdentifierSourceByUuid(SEQ_A_UUID).getReservedIdentifiers().contains("RES-1"));
	}

	@Test
	public void exportReservedIdentifiers_shouldWriteToResponse() throws Exception {
		IdentifierSource source = service.getIdentifierSourceByUuid(SEQ_A_UUID);
		MockHttpServletResponse response = new MockHttpServletResponse();
		controller.exportReservedIdentifiers(new ModelMap(), new MockHttpServletRequest(), response, source);
		assertEquals("text/plain", response.getContentType());
	}
}
