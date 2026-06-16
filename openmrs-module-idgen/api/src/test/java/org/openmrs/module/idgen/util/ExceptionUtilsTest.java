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
package org.openmrs.module.idgen.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor {@link ExceptionUtils#isThrownFrom}. Pure unit test.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class ExceptionUtilsTest {

	private static final String THIS_CLASS = "org.openmrs.module.idgen.util.ExceptionUtilsTest";

	@Test
	public void isThrownFrom_shouldMatchClassAndMethod() {
		Exception e = new Exception("boom");
		assertTrue(ExceptionUtils.isThrownFrom(e, THIS_CLASS, "isThrownFrom_shouldMatchClassAndMethod"));
	}

	@Test
	public void isThrownFrom_shouldMatchClassWhenMethodIsNull() {
		Exception e = new Exception("boom");
		assertTrue(ExceptionUtils.isThrownFrom(e, THIS_CLASS, null));
	}

	@Test
	public void isThrownFrom_shouldReturnFalseForUnknownOrigin() {
		Exception e = new Exception("boom");
		assertFalse(ExceptionUtils.isThrownFrom(e, "com.example.Nope", "missing"));
	}

	@Test
	public void isThrownFrom_shouldReturnFalseForEmptyStackTrace() {
		Exception e = new Exception("boom");
		e.setStackTrace(new StackTraceElement[0]);
		assertFalse(ExceptionUtils.isThrownFrom(e, THIS_CLASS, null));
	}
}
