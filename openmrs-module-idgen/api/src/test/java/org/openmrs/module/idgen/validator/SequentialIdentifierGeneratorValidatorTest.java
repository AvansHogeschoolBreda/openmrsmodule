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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor {@link SequentialIdentifierGeneratorValidator}.
 *
 * Pure unit test (geen Spring-context of database). De configuratie gebruikt een
 * PatientIdentifierType zonder validator-class, zodat de Context-afhankelijke tak
 * validateIdentifierType() bewust wordt overgeslagen. De verplichte-velden- en
 * lengtebranches zijn pure logica en daarmee zonder context testbaar.
 */
public class SequentialIdentifierGeneratorValidatorTest {

	private SequentialIdentifierGeneratorValidator validator;

	@Before
	public void setUp() {
		validator = new SequentialIdentifierGeneratorValidator();
	}

	/** Bouwt een geldige generator zonder validator-class (geen Context nodig). */
	private SequentialIdentifierGenerator validGenerator() {
		SequentialIdentifierGenerator gen = new SequentialIdentifierGenerator();
		gen.setName("Test Source");
		gen.setFirstIdentifierBase("1000");
		gen.setIdentifierType(new PatientIdentifierType());
		return gen;
	}

	private Errors errorsFor(Object target) {
		return new BindException(target, "sequentialIdentifierGenerator");
	}

	@Test
	public void supports_shouldReturnTrueForSequentialIdentifierGenerator() {
		assertTrue(validator.supports(SequentialIdentifierGenerator.class));
	}

	@Test
	public void supports_shouldReturnFalseForUnrelatedClass() {
		assertFalse(validator.supports(Object.class));
	}

	@Test
	public void validate_shouldRejectWhenNameIsMissing() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setName(null);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldRejectWhenFirstIdentifierBaseIsMissing() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setFirstIdentifierBase(null);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldRejectAndStopWhenIdentifierTypeIsMissing() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setIdentifierType(null);
		// Onmogelijke lengte-eis: zou een tweede fout opleveren als de validatie zou doorlopen
		gen.setMinLength(999);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		// Exact één fout: de methode keert terug na de identifierType-controle en
		// evalueert de lengte-eis dus niet meer.
		assertEquals(1, errors.getErrorCount());
	}

	@Test
	public void validate_shouldAcceptValidConfiguration() {
		SequentialIdentifierGenerator gen = validGenerator();
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_shouldRejectWhenIdentifierIsShorterThanMinLength() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setFirstIdentifierBase("12");   // lengte 2
		gen.setMinLength(5);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldRejectWhenIdentifierIsLongerThanMaxLength() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setFirstIdentifierBase("123456");   // lengte 6
		gen.setMaxLength(4);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldIncludePrefixAndSuffixWhenComputingLength() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setFirstIdentifierBase("1");   // base lengte 1
		gen.setPrefix("AB");                // +2
		gen.setSuffix("CD");                // +2 => firstId = "AB1CD", lengte 5
		gen.setMaxLength(4);               // 5 > 4 => fout
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldAcceptWhenLengthIsWithinBounds() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setFirstIdentifierBase("1000");   // lengte 4
		gen.setMinLength(3);
		gen.setMaxLength(6);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		assertFalse(errors.hasErrors());
	}

	@Test
	public void validate_shouldIgnoreZeroLengthConstraints() {
		SequentialIdentifierGenerator gen = validGenerator();
		gen.setMinLength(0);
		gen.setMaxLength(0);
		Errors errors = errorsFor(gen);

		validator.validate(gen, errors);

		// minLength/maxLength van 0 zijn geen actieve grens (bewaakt door de > 0 controle)
		assertFalse(errors.hasErrors());
	}
}
