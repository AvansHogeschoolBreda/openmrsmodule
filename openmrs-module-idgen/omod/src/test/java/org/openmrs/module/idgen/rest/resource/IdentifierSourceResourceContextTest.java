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
package org.openmrs.module.idgen.rest.resource;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.junit.Assert.assertNotNull;

/**
 * Context-sensitieve tests voor de create/update-paden van de God Class {@link IdentifierSourceResource}.
 * Deze paden lopen via de private helpers createSequentialGenerator/createRemoteSource/createIdentifierPool
 * en updateSequentialGenerator/updateIdentifierPool/updateRemoteSource, die veel veld-validatietakken
 * bevatten. Ze vereisen de Context (type-lookup + save), dus draaien ze in een web-context-test in plaats
 * van de pure unit-test {@link IdentifierSourceResourceUnitTest}.
 *
 * Doel: de conditie-dekking op nieuwe code over de 80%-quality-gate-drempel tillen en een regressie-vangnet
 * bieden voor de latere klassesplitsing (Refactoring-Onderbouwing 3.2).
 *
 * Toegevoegd door groep 6.
 */
public class IdentifierSourceResourceContextTest extends BaseModuleWebContextSensitiveTest {

	private final IdentifierSourceResource resource = new IdentifierSourceResource();
	private String typeUuid;

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		typeUuid = Context.getPatientService().getPatientIdentifierType(1).getUuid();
	}

	private SimpleObject base(String sourceType, String name) {
		SimpleObject o = new SimpleObject();
		o.add("sourceType", sourceType);
		o.add("name", name);
		o.add("identifierType", typeUuid);
		return o;
	}

	// ---------- create: SequentialIdentifierGenerator ----------

	@Test
	public void create_sequential_shouldBuildAndSaveWithAllOptionalFields() {
		SimpleObject body = base("SequentialIdentifierGenerator", "Seq Full");
		body.add("firstIdentifierBase", "100");
		body.add("baseCharacterSet", "0123456789");
		body.add("prefix", "PRE");
		body.add("suffix", "SUF");
		body.add("minLength", "3");
		body.add("maxLength", "12");
		body.add("description", "een sequentiele bron");
		assertNotNull(resource.create(body, null));
	}

	@Test(expected = ValidationException.class)
	public void create_sequential_shouldRejectMissingBaseFields() {
		// geen firstIdentifierBase en geen baseCharacterSet
		resource.create(base("SequentialIdentifierGenerator", "Seq Incompleet"), null);
	}

	@Test(expected = ValidationException.class)
	public void create_sequential_shouldRejectNonNumericMinLength() {
		SimpleObject body = base("SequentialIdentifierGenerator", "Seq Bad MinLen");
		body.add("firstIdentifierBase", "100");
		body.add("baseCharacterSet", "0123456789");
		body.add("minLength", "abc");
		resource.create(body, null);
	}

	// ---------- create: RemoteIdentifierSource ----------

	@Test
	public void create_remote_shouldBuildAndSaveWithCredentials() {
		SimpleObject body = base("RemoteIdentifierSource", "Remote Full");
		body.add("url", "http://example.org/idgen");
		body.add("username", "svc");
		body.add("password", "secret");
		body.add("description", "een remote bron");
		assertNotNull(resource.create(body, null));
	}

	@Test(expected = ValidationException.class)
	public void create_remote_shouldRejectMissingUrl() {
		resource.create(base("RemoteIdentifierSource", "Remote Geen Url"), null);
	}

	@Test(expected = ValidationException.class)
	public void create_remote_shouldRejectPasswordWithoutUsername() {
		SimpleObject body = base("RemoteIdentifierSource", "Remote Geen User");
		body.add("url", "http://example.org/idgen");
		body.add("password", "secret");
		resource.create(body, null);
	}

	// ---------- create: IdentifierPool ----------

	@Test
	public void create_pool_shouldBuildAndSaveWithSourceAndFlags() {
		SimpleObject body = base("IdentifierPool", "Pool Full");
		body.add("sourceUuid", "0d47284f-9e9b-4a81-a88b-8bb42bc0a901"); // bron 1 (sequentieel)
		body.add("batchSize", "50");
		body.add("minPoolSize", "10");
		body.add("sequential", "true");
		body.add("refillWithScheduledTask", "no");
		assertNotNull(resource.create(body, null));
	}

	@Test(expected = ValidationException.class)
	public void create_pool_shouldRejectNonNumericBatchSize() {
		SimpleObject body = base("IdentifierPool", "Pool Bad Batch");
		body.add("batchSize", "xx");
		resource.create(body, null);
	}

	// ---------- create: generate identifiers shortcut ----------

	@Test
	public void create_shouldGenerateIdentifiersWhenRequested() {
		SimpleObject body = new SimpleObject();
		body.add("generateIdentifiers", "true");
		body.add("sourceUuid", "0d47284f-9e9b-4a81-a88b-8bb42bc0a901"); // bron 1 (sequentieel)
		body.add("numberToGenerate", "3");
		body.add("comment", "batch");
		assertNotNull(resource.create(body, null));
	}

	// ---------- update ----------

	@Test
	public void update_sequential_shouldApplyChangedFields() {
		SimpleObject body = new SimpleObject();
		body.add("name", "Seq Hernoemd");
		body.add("description", "nieuwe omschrijving");
		body.add("prefix", "X");
		body.add("minLength", "2");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a901", body, null));
	}

	@Test
	public void update_pool_shouldApplySourceAndFlags() {
		SimpleObject body = new SimpleObject();
		body.add("name", "Pool Hernoemd");
		body.add("batchSize", "20");
		body.add("minPoolSize", "5");
		body.add("sequential", "false");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a903", body, null));
	}

	@Test
	public void update_remote_shouldApplyUrlAndCredentials() {
		SimpleObject body = new SimpleObject();
		body.add("url", "http://example.org/new");
		body.add("name", "Remote Hernoemd");
		body.add("user", "u2");
		body.add("password", "p2");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a902", body, null));
	}

	// ---------- update operations on a pool ----------

	@Test
	public void update_shouldReserveIdentifiers() {
		SimpleObject body = new SimpleObject();
		body.add("reservedIdentifiers", "R-1,R-2, ,R-3");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a901", body, null));
	}

	@Test
	public void update_shouldUploadFromSourceIntoPool() {
		SimpleObject body = new SimpleObject();
		body.add("operation", "uploadFromSource");
		body.add("batchSize", "5");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a903", body, null)); // pool 3 (bron 1 sequentieel)
	}

	@Test
	public void update_shouldUploadFromFileIntoPool() {
		SimpleObject body = new SimpleObject();
		body.add("operation", "uploadFromFile");
		body.add("identifiers", "F-1,F-2,F-3");
		assertNotNull(resource.update("0d47284f-9e9b-4a81-a88b-8bb42bc0a903", body, null)); // pool 3
	}

	// ---------- read-side delegations ----------

	@Test
	public void getByUniqueId_shouldResolveAnExistingSource() {
		assertNotNull(resource.getByUniqueId("0d47284f-9e9b-4a81-a88b-8bb42bc0a901"));
	}
}
