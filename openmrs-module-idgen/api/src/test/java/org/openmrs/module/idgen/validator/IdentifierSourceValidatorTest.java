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
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor {@link IdentifierSourceValidator} en {@link RemoteIdentifierSourceValidator}.
 * Pure unit test: de validate-logica is enkel een naam- respectievelijk url-controle.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdentifierSourceValidatorTest {

	private Errors errorsFor(Object target) {
		return new BindException(target, "source");
	}

	@Test
	public void supports_shouldAcceptIdentifierSourceSubclasses() {
		assertTrue(new IdentifierSourceValidator().supports(IdentifierPool.class));
		assertTrue(new RemoteIdentifierSourceValidator().supports(RemoteIdentifierSource.class));
	}

	@Test
	public void supports_shouldRejectUnrelatedClass() {
		assertFalse(new IdentifierSourceValidator().supports(Object.class));
	}

	@Test
	public void validate_shouldRejectWhenNameIsMissing() {
		IdentifierPool pool = new IdentifierPool();
		Errors errors = errorsFor(pool);
		new IdentifierSourceValidator().validate(pool, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void validate_shouldAcceptWhenNameIsPresent() {
		IdentifierPool pool = new IdentifierPool();
		pool.setName("Pool A");
		Errors errors = errorsFor(pool);
		new IdentifierSourceValidator().validate(pool, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void remoteValidate_shouldRejectWhenUrlIsMissing() {
		RemoteIdentifierSource source = new RemoteIdentifierSource();
		Errors errors = errorsFor(source);
		new RemoteIdentifierSourceValidator().validate(source, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void remoteValidate_shouldAcceptWhenUrlIsPresent() {
		RemoteIdentifierSource source = new RemoteIdentifierSource();
		source.setUrl("http://example.org/ids");
		Errors errors = errorsFor(source);
		new RemoteIdentifierSourceValidator().validate(source, errors);
		assertFalse(errors.hasErrors());
	}
}
