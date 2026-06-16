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
package org.openmrs.module.idgen.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import static org.junit.Assert.assertTrue;

/**
 * Context-sensitieve tests voor de scheduled-task laag. De {@code RunnableTask} van
 * {@link RefillIdentifierPoolsTask} wordt direct uitgevoerd (zonder daemon), wat de refill-logica
 * en {@code checkAndRefillIdentifierPool} dekt. Daarnaast de enabled-vlag en de run-tak zonder
 * daemon-token.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class IdgenTaskContextTest extends IdgenBaseTest {

	private IdentifierSourceService service;

	@Before
	public void beforeEachTest() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		service = Context.getService(IdentifierSourceService.class);
	}

	@Test
	public void runnableTask_shouldRefillPoolsConfiguredForScheduledRefill() {
		// Pool 4 is geconfigureerd met refill_with_scheduled_task = true en min pool size 1
		IdentifierPool pool = (IdentifierPool) service.getIdentifierSource(4);
		assertTrue(service.getQuantityInPool(pool, true, false) == 0);

		new RefillIdentifierPoolsTask().getRunnableTask().run();

		IdentifierPool refilled = (IdentifierPool) service.getIdentifierSource(4);
		assertTrue(service.getQuantityInPool(refilled, true, false) >= 1);
	}

	@Test
	public void run_shouldSkipWhenNoDaemonTokenAndLogWarning() {
		IdgenTask.setEnabled(true);
		assertTrue(IdgenTask.isEnabled());
		// Zonder daemon-token valt run() in de waarschuwingstak en gooit geen fout
		new RefillIdentifierPoolsTask().run();
		IdgenTask.setEnabled(false);
		assertTrue(!IdgenTask.isEnabled());
	}
}
