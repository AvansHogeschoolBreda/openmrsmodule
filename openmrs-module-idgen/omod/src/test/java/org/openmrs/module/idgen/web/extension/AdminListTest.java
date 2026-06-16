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
package org.openmrs.module.idgen.web.extension;

import org.junit.Test;
import org.openmrs.module.Extension;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor {@link AdminList}, de admin-paginalinks van de module. Pure unit test:
 * de extensie levert een vaste set links en titel zonder OpenMRS Context.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class AdminListTest {

	@Test
	public void getMediaType_shouldBeHtml() {
		assertEquals(Extension.MEDIA_TYPE.html, new AdminList().getMediaType());
	}

	@Test
	public void getTitle_shouldReturnMessageCode() {
		assertEquals("idgen.title", new AdminList().getTitle());
	}

	@Test
	public void getLinks_shouldContainTheThreeAdminPages() {
		Map<String, String> links = new AdminList().getLinks();
		assertEquals(3, links.size());
		assertTrue(links.containsKey("module/idgen/manageIdentifierSources.form"));
		assertTrue(links.containsKey("module/idgen/manageAutoGenerationOptions.form"));
		assertTrue(links.containsKey("module/idgen/viewLogEntries.form"));
	}
}
