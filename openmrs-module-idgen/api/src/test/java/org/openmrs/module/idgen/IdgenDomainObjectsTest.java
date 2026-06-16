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
import org.openmrs.PatientIdentifierType;

import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor de domeinobjecten van de module: identiteitsgedrag (equals/hashCode op id en
 * uuid) en de bedrijfslogica in {@link IdentifierPool} en {@link PooledIdentifier}.
 * Pure unit test: geen Spring-context of database.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdgenDomainObjectsTest {

	// ---------- AutoGenerationOption ----------

	@Test
	public void autoGenerationOption_shouldExposeConstructorDefaultsAndProperties() {
		PatientIdentifierType type = new PatientIdentifierType();
		AutoGenerationOption option = new AutoGenerationOption(type);
		assertEquals(type, option.getIdentifierType());
		// Documented defaults
		assertTrue(option.isManualEntryEnabled());
		assertFalse(option.isAutomaticGenerationEnabled());

		SequentialIdentifierGenerator source = new SequentialIdentifierGenerator();
		AutoGenerationOption full = new AutoGenerationOption(type, source, false, true);
		assertEquals(source, full.getSource());
		assertFalse(full.isManualEntryEnabled());
		assertTrue(full.isAutomaticGenerationEnabled());
	}

	@Test
	public void autoGenerationOption_shouldUseIdForEquality() {
		AutoGenerationOption a = new AutoGenerationOption();
		AutoGenerationOption b = new AutoGenerationOption();
		a.setId(7);
		b.setId(7);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		b.setId(8);
		assertNotEquals(a, b);
	}

	// ---------- LogEntry ----------

	@Test
	public void logEntry_shouldExposePropertiesAndUseIdentifierAsUuid() {
		Date now = new Date();
		LogEntry entry = new LogEntry(null, "ID-123", now, null, "generated");
		assertEquals("ID-123", entry.getIdentifier());
		assertEquals(now, entry.getDateGenerated());
		assertEquals("generated", entry.getComment());
		// getUuid is overridden to return the identifier value
		assertEquals("ID-123", entry.getUuid());
	}

	@Test
	public void logEntry_shouldUseIdForEquality() {
		LogEntry a = new LogEntry();
		LogEntry b = new LogEntry();
		a.setId(1);
		b.setId(1);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	// ---------- IdentifierPool ----------

	@Test
	public void identifierPool_shouldSeparateAvailableAndUsedIdentifiers() {
		IdentifierPool pool = new IdentifierPool();
		pool.setName("Pool");
		pool.addIdentifierToPool("A1");
		pool.addIdentifierToPool("A2");

		assertEquals(2, pool.getAvailableIdentifiers().size());
		assertTrue(pool.getUsedIdentifiers().isEmpty());

		String issued = pool.nextIdentifier();
		assertNotNull(issued);
		assertEquals(1, pool.getAvailableIdentifiers().size());
		assertEquals(1, pool.getUsedIdentifiers().size());
	}

	@Test(expected = EmptyIdentifierPoolException.class)
	public void identifierPool_shouldThrowWhenEmpty() {
		new IdentifierPool().nextIdentifier();
	}

	@Test
	public void identifierPool_shouldExposeConfigurationProperties() {
		IdentifierPool pool = new IdentifierPool();
		pool.setBatchSize(250);
		pool.setMinPoolSize(50);
		pool.setSequential(Boolean.TRUE);
		pool.setRefillWithScheduledTask(Boolean.FALSE);
		assertEquals(Integer.valueOf(250), pool.getBatchSize());
		assertEquals(Integer.valueOf(50), pool.getMinPoolSize());
		assertTrue(pool.getSequential());
		assertFalse(pool.isRefillWithScheduledTask());
	}

	// ---------- BaseIdentifierSource (via SequentialIdentifierGenerator) ----------

	@Test
	public void baseIdentifierSource_shouldExposeMetadataAndToString() {
		SequentialIdentifierGenerator source = new SequentialIdentifierGenerator();
		source.setName("Sequential A");
		source.setDescription("desc");
		assertEquals("Sequential A", source.toString());
		assertEquals("desc", source.getDescription());
		assertEquals(Boolean.FALSE, source.isRetired());

		source.addReservedIdentifier("R1");
		Set<String> reserved = source.getReservedIdentifiers();
		assertTrue(reserved.contains("R1"));
	}

	@Test
	public void baseIdentifierSource_shouldFallBackToUuidForEqualityWhenIdIsNull() {
		SequentialIdentifierGenerator a = new SequentialIdentifierGenerator();
		SequentialIdentifierGenerator b = new SequentialIdentifierGenerator();
		a.setUuid("same-uuid");
		b.setUuid("same-uuid");
		assertEquals(a, b);

		b.setUuid("other-uuid");
		assertNotEquals(a, b);
	}

	// ---------- PooledIdentifier ----------

	@Test
	public void pooledIdentifier_shouldTrackAvailabilityAndRenderToString() {
		IdentifierPool pool = new IdentifierPool();
		pool.setName("Pool");
		PooledIdentifier pooled = new PooledIdentifier(pool, "ID-9");
		assertTrue(pooled.isAvailable());
		assertNotNull(pooled.getUuid());
		assertEquals("Pool: ID-9", pooled.toString());

		pooled.setDateUsed(new Date());
		assertFalse(pooled.isAvailable());
	}

	// ---------- Identifier ----------

	@Test
	public void identifier_shouldExposeValue() {
		Identifier identifier = new Identifier();
		identifier.setIdentifierValue("XYZ");
		assertEquals("XYZ", identifier.getIdentifierValue());
	}
}
