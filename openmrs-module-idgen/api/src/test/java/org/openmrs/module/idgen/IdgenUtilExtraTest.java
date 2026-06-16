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
package org.openmrs.module.idgen;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Aanvullende unit tests voor {@link IdgenUtil} die de bestaande {@link IdgenUtilTest} niet raakt:
 * padding, de foutpaden en de stream- en log-sanitisatiehelpers. Pure unit test.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdgenUtilExtraTest {

	@Test
	public void convertToBase_shouldPadToRequestedLength() {
		// 5 in basis 2 is "101", gepad tot lengte 8
		assertEquals("00000101", IdgenUtil.convertToBase(5, "01".toCharArray(), 8));
	}

	@Test
	public void convertToBase_shouldReturnEmptyForZeroWithoutPadding() {
		assertEquals("", IdgenUtil.convertToBase(0, "01".toCharArray(), 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertFromBase_shouldRejectCharacterOutsideBase() {
		IdgenUtil.convertFromBase("Z", "01".toCharArray());
	}

	@Test
	public void getIdsFromStream_shouldReadAndTrimEachLine() {
		InputStream in = new ByteArrayInputStream("a\n b \nc".getBytes(StandardCharsets.UTF_8));
		List<String> ids = IdgenUtil.getIdsFromStream(in);
		assertEquals(3, ids.size());
		assertEquals("a", ids.get(0));
		assertEquals("b", ids.get(1));
		assertEquals("c", ids.get(2));
	}

	@Test
	public void sanitizeForLogging_shouldReplaceCrlfAndHandleNull() {
		assertEquals("", IdgenUtil.sanitizeForLogging(null));
		assertEquals("a_b_c", IdgenUtil.sanitizeForLogging("a\nb\rc"));
	}
}
