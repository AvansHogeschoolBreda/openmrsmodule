/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

/**
 * REST-resource voor {@link IdentifierSource}. Dit is een dunne CRUD-facade: het REST-contract
 * (representaties, Swagger-modellen, create/update/delete/search) leeft hier, terwijl het parsen en
 * opbouwen van de payloads is gedelegeerd aan {@link IdentifierSourcePayloadMapper} (Extract Class).
 */
@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE + "/identifiersource", supportedClass = IdentifierSource.class, supportedOpenmrsVersions = {"1.9.* - 9.9.*"})
public class IdentifierSourceResource extends MetadataDelegatingCrudResource<IdentifierSource> {

	/*
	 * Names for Types
	 * */
	public static final String IDENTIFIER_POOL = "identifierpool";
	public static final String SEQUENTIAL_IDENTIFIER_GENERATOR = "sequentialidentifiergenerator";
	public static final String REMOTE_IDENTIFIER_SOURCE = "remoteidentifiersource";

	// Property name constants (S1192)
	private static final String PROP_NAME = "name";
	private static final String PROP_DESCRIPTION = "description";
	private static final String PROP_IDENTIFIER_TYPE = "identifierType";
	// Swagger model definition constants
	private static final String SWAGGER_PATIENT_IDENTIFIER_TYPE_GET = "#/definitions/PatientidentifiertypeGet";

	private final IdentifierSourcePayloadMapper mapper = new IdentifierSourcePayloadMapper();

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty(PROP_NAME);
			description.addProperty(PROP_DESCRIPTION);
			description.addProperty(PROP_IDENTIFIER_TYPE);
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			}
		}
		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty(PROP_NAME);
		description.addProperty(PROP_DESCRIPTION);
		description.addProperty(PROP_IDENTIFIER_TYPE);
		return description;
	}

	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty(PROP_NAME);
		description.addProperty(PROP_DESCRIPTION);
		return description;
	}

	@PropertyGetter("display")
	@Override
	public String getDisplayString(IdentifierSource identifierSource) {
		return identifierSource.getIdentifierType() + " - "
				+ identifierSource.getName() + " - "
				+ identifierSource.getClass().getName();
	}

	@PropertyGetter("identifiers")
	public String getIdentifiers(IdentifierSource identifierSource) {
		if (identifierSource instanceof IdentifierPool) {
			IdentifierPool pool = (IdentifierPool) identifierSource;
			return pool.getIdentifiers().toString();
		}
		return null;
	}

	@PropertyGetter("usedIdentifiers")
	public Integer getUsedIdentifiers(IdentifierSource identifierSource) {
		if (identifierSource instanceof IdentifierPool) {
			IdentifierPool pool = (IdentifierPool) identifierSource;
			return pool.getUsedIdentifiers().size();
		}
		return null;
	}

	@PropertyGetter("availableIdentifiers")
	public Integer getAvailableIdentifiers(IdentifierSource identifierSource) {
		if (identifierSource instanceof IdentifierPool) {
			IdentifierPool pool = (IdentifierPool) identifierSource;
			return pool.getAvailableIdentifiers().size();
		}
		return null;
	}

	@Override
	public IdentifierSource newDelegate() {
		return new SequentialIdentifierGenerator();
	}

	@Override
	public Object create(SimpleObject postBody, RequestContext context) throws ResponseException {
		Object generated = mapper.tryGenerateIdentifiers(postBody);
		if (generated != null) {
			return generated;
		}
		IdentifierSource source = mapper.buildNewSource(postBody);
		Object savedIdentifierSource = save(source);
		return ConversionUtil.convertToRepresentation(savedIdentifierSource, Representation.DEFAULT);
	}

	@Override
	public Object update(String uuid, SimpleObject updateBody, RequestContext context) throws ResponseException {
		IdentifierSource updatedIdentifierSource = mapper.applyUpdate(uuid, updateBody);
		return ConversionUtil.convertToRepresentation(updatedIdentifierSource, Representation.DEFAULT);
	}

	@Override
	public IdentifierSource save(IdentifierSource identifierSource) {
		return Context.getService(IdentifierSourceService.class).saveIdentifierSource(identifierSource);
	}

	@Override
	public IdentifierSource getByUniqueId(String uniqueId) {
		return Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(uniqueId);
	}

	@Override
	public void delete(IdentifierSource identifierSource, String reason, RequestContext context) throws ResponseException {
		Context.getService(IdentifierSourceService.class).retireIdentifierSource(identifierSource, reason);
	}

	@Override
	public void purge(IdentifierSource identifierSource, RequestContext context) throws ResponseException {
		Context.getService(IdentifierSourceService.class).purgeIdentifierSource(identifierSource);
	}

	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<>(Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false), context);
	}

	@Override
	protected PageableResult doSearch(RequestContext context) throws ResponseException {
		String identifierType = context.getRequest().getParameter("identifierType");
		if (identifierType != null) {
			PatientIdentifierType requestedPatientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierType);
			if (requestedPatientIdentifierType != null) {
				List<IdentifierSource> requestedIdentifierSources = Context.getService(IdentifierSourceService.class)
						.getIdentifierSourcesByType(requestedPatientIdentifierType);
				return new NeedsPaging<>(requestedIdentifierSources, context);
			}
		}
		return new EmptySearchResult();
	}

	@Override
	public boolean hasTypesDefined() {
		return true;
	}

	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty());
			model.property(PROP_NAME, new StringProperty());
			model.property(PROP_DESCRIPTION, new StringProperty());
			model.property(PROP_IDENTIFIER_TYPE, new RefProperty(SWAGGER_PATIENT_IDENTIFIER_TYPE_GET));
		} else {
			model.property("uuid", new StringProperty());
			model.property("display", new StringProperty());
		}
		return model;
	}

	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
				.property(PROP_NAME, new StringProperty())
				.property(PROP_DESCRIPTION, new StringProperty())
				.property(PROP_IDENTIFIER_TYPE, new RefProperty(SWAGGER_PATIENT_IDENTIFIER_TYPE_GET));
	}

	@Override
	public Model getUPDATEModel(Representation rep) {
		// The update model is identical to the create model for this resource
		return getCREATEModel(rep);
	}
}
