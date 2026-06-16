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
package org.openmrs.module.idgen.propertyeditor;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.IdgenBaseTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Context-sensitieve tests voor de property editors. setAsText resolveert via de
 * IdentifierSourceService, wat de OpenMRS Context vereist; de lege-tekst- en getAsText-paden
 * worden ook geraakt.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdentifierSourceEditorContextTest extends IdgenBaseTest {

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}

	@Test
	public void identifierSourceEditor_shouldResolveByIdAndRenderBack() {
		IdentifierSourceEditor editor = new IdentifierSourceEditor();
		editor.setAsText("1");
		assertNotNull(editor.getValue());
		assertEquals("1", editor.getAsText());
	}

	@Test
	public void identifierSourceEditor_shouldYieldNullForBlankText() {
		IdentifierSourceEditor editor = new IdentifierSourceEditor();
		editor.setAsText("");
		assertNull(editor.getValue());
		assertNull(editor.getAsText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void identifierSourceEditor_shouldRejectNonNumericText() {
		// Integer.valueOf gooit NumberFormatException, die de editor doorvertaalt naar IllegalArgumentException
		new IdentifierSourceEditor().setAsText("not-a-number");
	}

	@Test
	public void autoGenerationOptionEditor_shouldResolveByIdAndRenderBack() {
		AutoGenerationOptionEditor editor = new AutoGenerationOptionEditor();
		editor.setAsText("1");
		assertNotNull(editor.getValue());
		assertEquals("1", editor.getAsText());
	}

	@Test
	public void autoGenerationOptionEditor_shouldYieldNullForBlankText() {
		AutoGenerationOptionEditor editor = new AutoGenerationOptionEditor();
		editor.setAsText("");
		assertNull(editor.getValue());
		assertNull(editor.getAsText());
	}
}
