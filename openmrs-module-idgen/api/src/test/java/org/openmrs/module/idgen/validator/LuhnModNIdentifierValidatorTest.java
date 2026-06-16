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
package org.openmrs.module.idgen.validator;

import org.junit.Test;
import org.openmrs.patient.UnallowedIdentifierException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor de abstracte {@link LuhnModNIdentifierValidator}, gedraaid via de drie
 * concrete subklassen (Mod-10, Mod-25, Mod-30). Pure unit test: geen Spring-context of database.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen). Het check-digit
 * algoritme is veiligheidskritisch (patient-ID validatie) en was vrijwel ongedekt.
 */
public class LuhnModNIdentifierValidatorTest {

	@Test
	public void getValidIdentifier_shouldAppendCheckDigitMod10() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertEquals("24687", v.getValidIdentifier("2468"));
	}

	@Test
	public void computeCheckDigit_shouldComputeExpectedDigitMod10() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertEquals('7', v.computeCheckDigit("2468"));
	}

	@Test
	public void isValid_shouldReturnTrueForCorrectCheckDigit() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertTrue(v.isValid("24687"));
	}

	@Test
	public void isValid_shouldReturnFalseForWrongCheckDigit() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertFalse(v.isValid("24688"));
	}

	@Test
	public void validateCheckDigit_shouldReturnTrueForValidIdentifier() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertTrue(v.validateCheckDigit("24687"));
	}

	@Test
	public void validateCheckDigit_shouldReturnFalseForInvalidIdentifier() {
		LuhnMod10IdentifierValidator v = new LuhnMod10IdentifierValidator();
		assertFalse(v.validateCheckDigit("24688"));
	}

	@Test(expected = UnallowedIdentifierException.class)
	public void computeCheckDigit_shouldRejectCharacterOutsideBase() {
		// 'X' zit niet in de Mod-10 basis "0123456789"
		new LuhnMod10IdentifierValidator().computeCheckDigit("12X");
	}

	@Test(expected = UnallowedIdentifierException.class)
	public void isValid_shouldThrowOnNullIdentifier() {
		new LuhnMod10IdentifierValidator().isValid(null);
	}

	@Test
	public void standardizeValidIdentifier_shouldUppercaseAndTrim() {
		LuhnMod30IdentifierValidator v = new LuhnMod30IdentifierValidator();
		assertEquals("AB12", v.standardizeValidIdentifier("  ab12 "));
	}

	@Test
	public void standardizeValidIdentifier_shouldReturnNullForNull() {
		assertEquals(null, new LuhnMod10IdentifierValidator().standardizeValidIdentifier(null));
	}

	@Test
	public void getName_shouldContainBaseLength() {
		assertEquals("Luhn Mod-10 Check-Digit Validator", new LuhnMod10IdentifierValidator().getName());
		assertEquals("Luhn Mod-25 Check-Digit Validator", new LuhnMod25IdentifierValidator().getName());
		assertEquals("Luhn Mod-30 Check-Digit Validator", new LuhnMod30IdentifierValidator().getName());
	}

	@Test
	public void getAllowedCharacters_shouldContainBothCases() {
		// Voor cijfers zijn lower- en upper-case gelijk: elke basekarakter komt twee keer terug
		assertEquals("00112233445566778899", new LuhnMod10IdentifierValidator().getAllowedCharacters());
	}

	@Test
	public void roundTrip_shouldValidateForMod25AndMod30() {
		LuhnMod25IdentifierValidator m25 = new LuhnMod25IdentifierValidator();
		assertTrue(m25.isValid(m25.getValidIdentifier("3467")));

		LuhnMod30IdentifierValidator m30 = new LuhnMod30IdentifierValidator();
		assertTrue(m30.isValid(m30.getValidIdentifier("1234")));
	}
}
