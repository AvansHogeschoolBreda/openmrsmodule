/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.validation.ValidationException;

/**
 * Vertaalt REST-payloads (SimpleObject) naar/vanuit {@link IdentifierSource}-domeinobjecten.
 *
 * Deze klasse is via "Extract Class" uit {@link IdentifierSourceResource} gehaald: de resource was een
 * God Class waarin de create/update-parsing samen met het CRUD-contract zat. Het herhaalde
 * null/blank/parse-patroon per veld is samengebracht in een paar veld-helpers, waardoor de eerdere
 * Brain Methods (createSequentialGenerator, createIdentifierPool) onder de complexiteitsdrempel komen
 * en de viervoudig gedupliceerde foutmelding (S1192) verdwijnt. Het gedrag, de validatieregels en de
 * REST-representaties zijn ongewijzigd.
 *
 * Let op: bewust geen lambdas/method-references. De OpenMRS-classpathscanner leest elke class in dit
 * pakket met een ASM-versie die invokedynamic (van lambdas) niet aankan; de helpers geven daarom een
 * waarde terug in plaats van een Consumer te accepteren.
 */
class IdentifierSourcePayloadMapper {

	// Payload-sleutels
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String IDENTIFIER_TYPE = "identifierType";
	private static final String SOURCE_TYPE = "sourceType";
	private static final String FIRST_IDENTIFIER_BASE = "firstIdentifierBase";
	private static final String BASE_CHARACTER_SET = "baseCharacterSet";
	private static final String PREFIX = "prefix";
	private static final String SUFFIX = "suffix";
	private static final String MIN_LENGTH = "minLength";
	private static final String MAX_LENGTH = "maxLength";
	private static final String URL = "url";
	private static final String USERNAME = "username";
	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String BATCH_SIZE = "batchSize";
	private static final String MIN_POOL_SIZE = "minPoolSize";
	private static final String SOURCE_UUID = "sourceUuid";
	private static final String SEQUENTIAL = "sequential";
	private static final String REFILL_WITH_SCHEDULED_TASK = "refillWithScheduledTask";
	private static final String COMMENT = "comment";
	private static final String NUMBER_TO_GENERATE = "numberToGenerate";
	private static final String GENERATE_IDENTIFIERS = "generateIdentifiers";
	private static final String OPERATION = "operation";
	private static final String IDENTIFIERS = "identifiers";
	private static final String RESERVED_IDENTIFIERS = "reservedIdentifiers";

	private static final String INVALID_INPUTS = "The values of the following inputs are missing or invalid: ";

	private IdentifierSourceService service() {
		return Context.getService(IdentifierSourceService.class);
	}

	// ***** CREATE *****

	/**
	 * Als de payload om directe identifier-generatie vraagt, voer die uit en geef het resultaat terug.
	 * Geeft null als er niet om generatie is gevraagd of als de bron niet gevonden wordt (dan volgt
	 * normale create).
	 */
	Object tryGenerateIdentifiers(SimpleObject postBody) {
		if (postBody.get(GENERATE_IDENTIFIERS) == null) {
			return null;
		}
		Object comment = postBody.get(COMMENT);
		Object numberToGenerate = postBody.get(NUMBER_TO_GENERATE);
		Object sourceUuid = postBody.get(SOURCE_UUID);
		if (comment == null) {
			comment = "Batch Export of " + numberToGenerate + " to file";
		}
		if (numberToGenerate == null) {
			numberToGenerate = "0";
		}
		int count;
		try {
			count = Integer.parseInt(numberToGenerate.toString());
		} catch (NumberFormatException e) {
			throw new ValidationException("numberToGenerate must be a valid integer");
		}
		if (sourceUuid != null) {
			IdentifierSource source = service().getIdentifierSourceByUuid(sourceUuid.toString());
			if (source != null) {
				List<String> identifiers = service().generateIdentifiers(source, count, comment.toString());
				SimpleObject identifiersToExport = new SimpleObject();
				identifiersToExport.add("identifiers", identifiers);
				return identifiersToExport;
			}
		}
		return null;
	}

	/** Bouwt (zonder op te slaan) een nieuwe IdentifierSource uit de create-payload. */
	IdentifierSource buildNewSource(SimpleObject postBody) {
		List<String> errors = new ArrayList<>();
		PatientIdentifierType type = validateAndGetIdentifierType(postBody, errors);
		Object name = postBody.get(NAME);
		Object sourceType = postBody.get(SOURCE_TYPE);
		String type0 = sourceType != null ? sourceType.toString() : null;
		if ("SequentialIdentifierGenerator".equals(type0)) {
			return buildSequentialGenerator(postBody, type, name, errors);
		} else if ("RemoteIdentifierSource".equals(type0)) {
			return buildRemoteSource(postBody, type, name, errors);
		} else if ("IdentifierPool".equals(type0)) {
			return buildIdentifierPool(postBody, type, name, errors);
		}
		return null;
	}

	private PatientIdentifierType validateAndGetIdentifierType(SimpleObject postBody, List<String> errors) {
		if (str(postBody, SOURCE_TYPE) == null) {
			errors.add("source type");
		}
		if (str(postBody, NAME) == null) {
			errors.add("name");
		}
		String identifierTypeUuid = str(postBody, IDENTIFIER_TYPE);
		PatientIdentifierType patientIdentifierType = null;
		if (identifierTypeUuid == null) {
			errors.add("patient identifier type");
		} else {
			patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid);
			if (patientIdentifierType == null) {
				errors.add("patient identifier type");
			}
		}
		failIfErrors(errors);
		return patientIdentifierType;
	}

	private SequentialIdentifierGenerator buildSequentialGenerator(SimpleObject body, PatientIdentifierType type,
			Object name, List<String> errors) {
		SequentialIdentifierGenerator source = new SequentialIdentifierGenerator();
		if (str(body, FIRST_IDENTIFIER_BASE) == null) {
			errors.add("first identifier base");
		}
		if (str(body, BASE_CHARACTER_SET) == null) {
			errors.add("base character set");
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		String prefix = str(body, PREFIX);
		if (prefix != null) {
			source.setPrefix(prefix);
		}
		String suffix = str(body, SUFFIX);
		if (suffix != null) {
			source.setSuffix(suffix);
		}
		Integer minLength = intOrError(body, MIN_LENGTH, errors, "minLength must be a valid integer");
		if (minLength != null) {
			source.setMinLength(minLength);
		}
		Integer maxLength = intOrError(body, MAX_LENGTH, errors, "maxLength must be a valid integer");
		if (maxLength != null) {
			source.setMaxLength(maxLength);
		}
		failIfErrors(errors);
		source.setIdentifierType(type);
		source.setName(name.toString());
		source.setBaseCharacterSet(str(body, BASE_CHARACTER_SET));
		source.setFirstIdentifierBase(str(body, FIRST_IDENTIFIER_BASE));
		return source;
	}

	private RemoteIdentifierSource buildRemoteSource(SimpleObject body, PatientIdentifierType type,
			Object name, List<String> errors) {
		RemoteIdentifierSource source = new RemoteIdentifierSource();
		String username = str(body, USERNAME);
		String password = str(body, PASSWORD);
		if (str(body, URL) == null) {
			errors.add("url");
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		if (username != null) {
			source.setUser(username);
		}
		if (password != null) {
			if (username == null) {
				errors.add("username");
			} else {
				source.setPassword(password);
			}
		}
		failIfErrors(errors);
		source.setIdentifierType(type);
		source.setName(name.toString());
		source.setUrl(str(body, URL));
		return source;
	}

	private IdentifierPool buildIdentifierPool(SimpleObject body, PatientIdentifierType type,
			Object name, List<String> errors) {
		IdentifierPool source = new IdentifierPool();
		String sourceUuid = str(body, SOURCE_UUID);
		if (sourceUuid != null) {
			IdentifierSource poolIdentifierSource = service().getIdentifierSourceByUuid(sourceUuid);
			if (poolIdentifierSource != null) {
				source.setSource(poolIdentifierSource);
			} else {
				errors.add("identifier souce");
			}
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		Integer batchSize = intOrError(body, BATCH_SIZE, errors, "batchSize must be a valid integer");
		if (batchSize != null) {
			source.setBatchSize(batchSize);
		}
		Integer minPoolSize = intOrError(body, MIN_POOL_SIZE, errors, "minPoolSize must be a valid integer");
		if (minPoolSize != null) {
			source.setMinPoolSize(minPoolSize);
		}
		Boolean sequential = bool(body, SEQUENTIAL);
		if (sequential != null) {
			source.setSequential(sequential);
		}
		Boolean refill = bool(body, REFILL_WITH_SCHEDULED_TASK);
		if (refill != null) {
			source.setRefillWithScheduledTask(refill);
		}
		failIfErrors(errors);
		source.setIdentifierType(type);
		source.setName(name.toString());
		return source;
	}

	// ***** UPDATE *****

	/** Past de update-payload toe op de bestaande bron, slaat op en geeft de bijgewerkte bron terug. */
	IdentifierSource applyUpdate(String uuid, SimpleObject updateBody) {
		IdentifierSource source = service().getIdentifierSourceByUuid(uuid);
		Object operation = updateBody.get(OPERATION);
		handleReservedIdentifiers(updateBody, source);
		handleUploadFromSource(updateBody, operation, source);
		handleUploadFromFile(updateBody, operation, source);
		if (source instanceof SequentialIdentifierGenerator) {
			return updateSequentialGenerator(updateBody, (SequentialIdentifierGenerator) source);
		} else if (source instanceof IdentifierPool) {
			return updateIdentifierPool(updateBody, (IdentifierPool) source);
		} else if (source instanceof RemoteIdentifierSource) {
			return updateRemoteSource(updateBody, (RemoteIdentifierSource) source);
		}
		return null;
	}

	private void handleReservedIdentifiers(SimpleObject updateBody, IdentifierSource source) {
		Object reserved = updateBody.get(RESERVED_IDENTIFIERS);
		if (reserved != null && source != null) {
			for (String id : reserved.toString().split(",")) {
				if (StringUtils.isNotBlank(id)) {
					source.addReservedIdentifier(id);
				}
			}
			service().saveIdentifierSource(source);
		}
	}

	private void handleUploadFromSource(SimpleObject updateBody, Object operation, IdentifierSource source) {
		if ("uploadFromSource".equals(operationToString(operation)) && source instanceof IdentifierPool) {
			Object batchSize = updateBody.get(BATCH_SIZE);
			if (batchSize != null && StringUtils.isNumeric(batchSize.toString())) {
				service().addIdentifiersToPool((IdentifierPool) source, Integer.parseInt(batchSize.toString()));
			}
		}
	}

	private void handleUploadFromFile(SimpleObject updateBody, Object operation, IdentifierSource source) {
		if ("uploadFromFile".equals(operationToString(operation)) && source instanceof IdentifierPool) {
			Object identifiers = updateBody.get(IDENTIFIERS);
			if (identifiers != null) {
				List<String> ids = new ArrayList<>(Arrays.asList(identifiers.toString().split(",")));
				service().addIdentifiersToPool((IdentifierPool) source, ids);
			}
		}
	}

	private String operationToString(Object operation) {
		return operation != null ? operation.toString() : null;
	}

	private IdentifierSource updateSequentialGenerator(SimpleObject body, SequentialIdentifierGenerator source) {
		String firstIdentifierBase = str(body, FIRST_IDENTIFIER_BASE);
		if (firstIdentifierBase != null) {
			source.setFirstIdentifierBase(firstIdentifierBase);
		}
		String baseCharacterSet = str(body, BASE_CHARACTER_SET);
		if (baseCharacterSet != null) {
			source.setBaseCharacterSet(baseCharacterSet);
		}
		String name = str(body, NAME);
		if (name != null) {
			source.setName(name);
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		String prefix = str(body, PREFIX);
		if (prefix != null) {
			source.setPrefix(prefix);
		}
		String suffix = str(body, SUFFIX);
		if (suffix != null) {
			source.setSuffix(suffix);
		}
		Integer minLength = intOrThrow(body, MIN_LENGTH);
		if (minLength != null) {
			source.setMinLength(minLength);
		}
		Integer maxLength = intOrThrow(body, MAX_LENGTH);
		if (maxLength != null) {
			source.setMaxLength(maxLength);
		}
		return service().saveIdentifierSource(source);
	}

	private IdentifierSource updateIdentifierPool(SimpleObject body, IdentifierPool source) {
		String sourceUuid = str(body, SOURCE_UUID);
		if (sourceUuid != null) {
			source.setSource(service().getIdentifierSourceByUuid(sourceUuid));
		} else {
			source.setSource(null);
		}
		String name = str(body, NAME);
		if (name != null) {
			source.setName(name);
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		Integer batchSize = intOrThrow(body, BATCH_SIZE);
		if (batchSize != null) {
			source.setBatchSize(batchSize);
		}
		Integer minPoolSize = intOrThrow(body, MIN_POOL_SIZE);
		if (minPoolSize != null) {
			source.setMinPoolSize(minPoolSize);
		}
		Boolean sequential = bool(body, SEQUENTIAL);
		if (sequential != null) {
			source.setSequential(sequential);
		}
		Boolean refill = bool(body, REFILL_WITH_SCHEDULED_TASK);
		if (refill != null) {
			source.setRefillWithScheduledTask(refill);
		}
		return service().saveIdentifierSource(source);
	}

	private IdentifierSource updateRemoteSource(SimpleObject body, RemoteIdentifierSource source) {
		String url = str(body, URL);
		if (url != null) {
			source.setUrl(url);
		}
		String name = str(body, NAME);
		if (name != null) {
			source.setName(name);
		}
		String description = str(body, DESCRIPTION);
		if (description != null) {
			source.setDescription(description);
		}
		String user = str(body, USER);
		if (user != null) {
			source.setUser(user);
		}
		String password = str(body, PASSWORD);
		if (password != null) {
			source.setPassword(password);
		}
		return service().saveIdentifierSource(source);
	}

	// ***** VELD-HELPERS (geen lambdas: OpenMRS-scanner leest lambdas niet) *****

	/** Getrimde waarde of null bij ontbrekend/leeg veld. */
	private static String str(SimpleObject body, String key) {
		Object val = body.get(key);
		return (val != null && StringUtils.isNotBlank(val.toString())) ? val.toString() : null;
	}

	/** Parse als integer; bij fout een melding toevoegen en null teruggeven (create-pad). */
	private static Integer intOrError(SimpleObject body, String key, List<String> errors, String label) {
		String val = str(body, key);
		if (val == null) {
			return null;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			errors.add(label);
			return null;
		}
	}

	/** Parse als integer; bij fout direct een ValidationException gooien (update-pad). */
	private static Integer intOrThrow(SimpleObject body, String key) {
		String val = str(body, key);
		if (val == null) {
			return null;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			throw new ValidationException(key + " must be a valid integer");
		}
	}

	private static Boolean bool(SimpleObject body, String key) {
		String val = str(body, key);
		return val != null ? parseBoolean(val) : null;
	}

	private static void failIfErrors(List<String> errors) {
		if (!errors.isEmpty()) {
			throw new ValidationException(INVALID_INPUTS + errors);
		}
	}

	private static Boolean parseBoolean(Object value) {
		List<String> trueValues = Arrays.asList("true", "1", "on", "yes");
		List<String> falseValues = Arrays.asList("false", "0", "off", "no");
		String val = value.toString().trim().toLowerCase();
		if (trueValues.contains(val)) {
			return Boolean.TRUE;
		} else if (falseValues.contains(val)) {
			return Boolean.FALSE;
		}
		return null;
	}
}
