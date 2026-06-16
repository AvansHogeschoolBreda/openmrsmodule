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
import org.openmrs.module.idgen.prefixprovider.ConstantPrefixProvider;
import org.openmrs.module.idgen.prefixprovider.PrefixProvider;
import org.openmrs.module.idgen.suffixprovider.ConstantSuffixProvider;
import org.openmrs.module.idgen.suffixprovider.SuffixProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Pure unit tests voor {@link SequentialIdentifierGenerator}: identifier-generatie voor een seed,
 * de lengtegrenzen en de prefix/suffix-provider-resolutie voor statische en blanco waarden.
 *
 * De configureerbare provider-tak ("provider:bean") en de check-digit-validator vereisen de
 * OpenMRS Context en worden hier bewust niet geraakt. Naam wijkt af van de bestaande (uitgesloten)
 * SequentialIdentifierGeneratorTest om naamconflicten te voorkomen.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class SequentialIdentifierGeneratorUnitTest {

	private SequentialIdentifierGenerator generator(String base) {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.setName("Test Generator");
		gen.setBaseCharacterSet("0123456789");
		gen.setFirstIdentifierBase(base);
		return gen;
	}

	@Test
	public void getIdentifierForSeed_shouldConvertSeedAndPadToBaseLength() {
		assertEquals("005", generator("100").getIdentifierForSeed(5));
	}

	@Test
	public void getIdentifierForSeed_shouldApplyStaticPrefixAndSuffix() {
		SequentialIdentifierGenerator gen = generator("100");
		gen.setPrefix("A");
		gen.setSuffix("Z");
		assertEquals("A005Z", gen.getIdentifierForSeed(5));
	}

	@Test(expected = IllegalStateException.class)
	public void getIdentifierForSeed_shouldRejectWhenShorterThanMinLength() {
		SequentialIdentifierGenerator gen = generator("100");
		gen.setMinLength(10);
		gen.getIdentifierForSeed(5);
	}

	@Test(expected = IllegalStateException.class)
	public void getIdentifierForSeed_shouldRejectWhenLongerThanMaxLength() {
		SequentialIdentifierGenerator gen = generator("100");
		gen.setMaxLength(2);
		gen.getIdentifierForSeed(5);
	}

	@Test
	public void getPrefixProvider_shouldReturnConstantForBlankAndStatic() {
		SequentialIdentifierGenerator gen = generator("100");
		PrefixProvider blank = gen.getPrefixProvider(null);
		assertTrue(blank instanceof ConstantPrefixProvider);
		assertEquals("", blank.getValue());

		PrefixProvider staticPrefix = gen.getPrefixProvider("LOC-");
		assertEquals("LOC-", staticPrefix.getValue());
	}

	@Test
	public void getSuffixProvider_shouldReturnConstantForBlankAndStatic() {
		SequentialIdentifierGenerator gen = generator("100");
		SuffixProvider blank = gen.getSuffixProvider("");
		assertTrue(blank instanceof ConstantSuffixProvider);
		assertEquals("", blank.getValue());

		SuffixProvider staticSuffix = gen.getSuffixProvider("-X");
		assertEquals("-X", staticSuffix.getValue());
	}

	@Test
	public void getNextSequenceValue_shouldDefaultToMinusOne() {
		assertEquals(Long.valueOf(-1L), generator("100").getNextSequenceValue());
	}

	@Test
	public void setNextSequenceValue_shouldBeReturned() {
		SequentialIdentifierGenerator gen = generator("100");
		gen.setNextSequenceValue(42L);
		assertEquals(Long.valueOf(42L), gen.getNextSequenceValue());
	}
}
