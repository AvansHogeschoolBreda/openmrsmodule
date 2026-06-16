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
package org.openmrs.module.idgen.prefixprovider;

import org.junit.Test;
import org.openmrs.module.idgen.suffixprovider.ConstantSuffixProvider;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests voor {@link ConstantPrefixProvider} en {@link ConstantSuffixProvider}.
 * Pure unit test: een blanco of null waarde valt terug op een lege string.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class ConstantProviderTest {

	@Test
	public void prefix_shouldReturnConfiguredValue() {
		assertEquals("PRE-", new ConstantPrefixProvider("PRE-").getValue());
	}

	@Test
	public void prefix_shouldFallBackToEmptyForBlankOrNull() {
		assertEquals("", new ConstantPrefixProvider("").getValue());
		assertEquals("", new ConstantPrefixProvider(null).getValue());
	}

	@Test
	public void suffix_shouldReturnConfiguredValue() {
		assertEquals("-SUF", new ConstantSuffixProvider("-SUF").getValue());
	}

	@Test
	public void suffix_shouldFallBackToEmptyForBlankOrNull() {
		assertEquals("", new ConstantSuffixProvider("").getValue());
		assertEquals("", new ConstantSuffixProvider(null).getValue());
	}
}
