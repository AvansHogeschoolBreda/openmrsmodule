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

import org.junit.After;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.module.idgen.suffixprovider.LocationBasedSuffixProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests voor de context-onafhankelijke delen van {@link LocationBasedPrefixProvider} en
 * {@link LocationBasedSuffixProvider}: het herkennen van de global property en het bijwerken van
 * de gecachete attribuuttypenaam via de listener-callbacks.
 *
 * De hierarchie-walk (getLocationPrefix/getLocationSuffix) en getValue() vereisen een UserContext
 * en horen daarom in de context-sensitieve integratietests, niet hier. Na elke test wordt de
 * statische cache via globalPropertyDeleted teruggezet, zodat geen toestand naar andere tests lekt.
 *
 * Toegevoegd door groep C6 voor de validatie van Deel 6 (testdekking ophogen).
 */
public class LocationBasedProvidersUnitTest {

	private final LocationBasedPrefixProvider prefixProvider = new LocationBasedPrefixProvider();
	private final LocationBasedSuffixProvider suffixProvider = new LocationBasedSuffixProvider();

	@After
	public void resetStaticCache() {
		// Zet de gecachete attribuuttypenaam terug op null zodat een volgende test (of context-test)
		// de waarde opnieuw uit de AdministrationService leest in plaats van uit deze cache.
		prefixProvider.globalPropertyDeleted(LocationBasedPrefixProvider.PREFIX_LOCATION_ATTRIBUTE_TYPE_GP);
		suffixProvider.globalPropertyDeleted(LocationBasedSuffixProvider.SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP);
	}

	@Test
	public void prefixProvider_shouldRecognizeOwnGlobalProperty() {
		assertTrue(prefixProvider.supportsPropertyName(LocationBasedPrefixProvider.PREFIX_LOCATION_ATTRIBUTE_TYPE_GP));
		assertFalse(prefixProvider.supportsPropertyName("some.other.property"));
	}

	@Test
	public void prefixProvider_shouldCacheValueFromGlobalPropertyChange() {
		prefixProvider.globalPropertyChanged(
		        new GlobalProperty(LocationBasedPrefixProvider.PREFIX_LOCATION_ATTRIBUTE_TYPE_GP, "Prefix Attribute"));
		assertEquals("Prefix Attribute", LocationBasedPrefixProvider.getPrefixLocationAttributeType());
	}

	@Test
	public void suffixProvider_shouldRecognizeOwnGlobalProperty() {
		assertTrue(suffixProvider.supportsPropertyName(LocationBasedSuffixProvider.SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP));
		assertFalse(suffixProvider.supportsPropertyName("some.other.property"));
	}

	@Test
	public void suffixProvider_shouldCacheValueFromGlobalPropertyChange() {
		suffixProvider.globalPropertyChanged(
		        new GlobalProperty(LocationBasedSuffixProvider.SUFFIX_LOCATION_ATTRIBUTE_TYPE_GP, "Suffix Attribute"));
		assertEquals("Suffix Attribute", LocationBasedSuffixProvider.getSuffixLocationAttributeType());
	}
}
