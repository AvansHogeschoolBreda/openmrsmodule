/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.api.context.Context;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.AutoGenerationOption;
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
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidationException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE
        + "/autogenerationoption", supportedClass = AutoGenerationOption.class, supportedOpenmrsVersions = {"1.9.* - 9.9.*"})
public class AutoGenerationOptionResource extends MetadataDelegatingCrudResource<AutoGenerationOption> {

	private static final String IDENTIFIER_TYPE = "identifierType";
	private static final String LOCATION = "location";
	private static final String SOURCE = "source";
	private static final String MANUAL_ENTRY_ENABLED = "manualEntryEnabled";
	private static final String AUTOMATIC_GENERATION_ENABLED = "automaticGenerationEnabled";
	private static final String LOCATION_GET_DEFINITION = "#/definitions/LocationGet";
	private static final String IDGEN_IDENTIFIER_SOURCE_GET_DEFINITION = "#/definitions/IdgenIdentifiersourceGet";
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
		} else if (rep instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty(IDENTIFIER_TYPE);
			description.addProperty(LOCATION);
			description.addProperty(SOURCE);
			description.addProperty(MANUAL_ENTRY_ENABLED);
			description.addProperty(AUTOMATIC_GENERATION_ENABLED);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		} else if (rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty(IDENTIFIER_TYPE);
			description.addProperty(LOCATION);
			description.addProperty(SOURCE);
			description.addProperty(MANUAL_ENTRY_ENABLED);
			description.addProperty(AUTOMATIC_GENERATION_ENABLED);
			description.addSelfLink();
		}
		return description;
	}
	
	@PropertyGetter("display")
	public String getDisplayString(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getIdentifierType() + " - " + autoGenerationOption.getSource() + " - "
		        + autoGenerationOption.getLocation();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty(IDENTIFIER_TYPE);
		description.addProperty(LOCATION);
		description.addRequiredProperty(SOURCE);
		description.addProperty(MANUAL_ENTRY_ENABLED);
		description.addProperty(AUTOMATIC_GENERATION_ENABLED);
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = getCreatableProperties();
		description.removeProperty(IDENTIFIER_TYPE);
		return description;
	}
	
	@Override
	public SimpleObject create(SimpleObject postBody, RequestContext context) throws ResponseException {
		Object manualEntryEnabled = postBody.get(MANUAL_ENTRY_ENABLED);
		Object automaticallyEnabled = postBody.get(AUTOMATIC_GENERATION_ENABLED);
		Object sourceUuid = postBody.get(SOURCE);
		Object locationUuid = postBody.get(LOCATION);
		Object identifierTypeUuid = postBody.get(IDENTIFIER_TYPE);
		ArrayList<String> errors = new ArrayList<>();
		IdentifierSource identifierSource = null;
		PatientIdentifierType patientIdentifierType = null;
		if (sourceUuid == null || StringUtils.isBlank(sourceUuid.toString())) {
			errors.add(SOURCE);
		} else {
			identifierSource = Context.getService(IdentifierSourceService.class)
			        .getIdentifierSourceByUuid(sourceUuid.toString());
			if (identifierSource == null) {
				errors.add(SOURCE);
			}
		}
		if (identifierTypeUuid == null || StringUtils.isBlank(identifierTypeUuid.toString())) {
			errors.add(IDENTIFIER_TYPE);
		} else {
			patientIdentifierType = Context.getPatientService()
			        .getPatientIdentifierTypeByUuid(identifierTypeUuid.toString());
			if (patientIdentifierType == null) {
				errors.add(IDENTIFIER_TYPE);
			}
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(
			        "The values of the following inputs are missing or invalid : " + errors.toString());
		}
		Location location = null;
		if (locationUuid != null && StringUtils.isNotBlank(locationUuid.toString())) {
			location = Context.getLocationService().getLocationByUuid(locationUuid.toString());
		}
		AutoGenerationOption autoGenerationOption = new AutoGenerationOption();
		autoGenerationOption.setSource(identifierSource);
		autoGenerationOption.setIdentifierType(patientIdentifierType);
		autoGenerationOption.setManualEntryEnabled(this.parseBoolean(manualEntryEnabled));
		autoGenerationOption.setAutomaticGenerationEnabled(this.parseBoolean(automaticallyEnabled));
		autoGenerationOption.setLocation(location);
		return (SimpleObject) ConversionUtil.convertToRepresentation(save(autoGenerationOption), Representation.FULL);
	}
	
	@Override
	public SimpleObject update(String uuid, SimpleObject postBody, RequestContext context) throws ResponseException {
		AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class)
		        .getAutoGenerationOptionByUuid(uuid);
		if (autoGenerationOption == null) {
			throw new ObjectNotFoundException();
		}
		Object manualEntryEnabled = postBody.get(MANUAL_ENTRY_ENABLED);
		Object automaticGenerationEnabled = postBody.get(AUTOMATIC_GENERATION_ENABLED);
		Object sourceUuid = postBody.get(SOURCE);
		Object locationUuid = postBody.get(LOCATION);
		IdentifierSource identifierSource = null;
		if (sourceUuid != null && StringUtils.isNotBlank(sourceUuid.toString())) {
			identifierSource = Context.getService(IdentifierSourceService.class)
			        .getIdentifierSourceByUuid(sourceUuid.toString());
		}
		Location location = null;
		if (locationUuid != null && StringUtils.isNotBlank(locationUuid.toString())) {
			location = Context.getLocationService().getLocationByUuid(locationUuid.toString());
		}
		if (location == null && identifierSource == null && automaticGenerationEnabled == null
		        && manualEntryEnabled == null) {
			throw new ResourceDoesNotSupportOperationException(
			        "You must provide at least a location, or a source, or an automaticGenerationEnabled, or a manualEntryEnabled parameter  to update an autoGenerationOption");
		}
		if (manualEntryEnabled != null) {
			autoGenerationOption.setManualEntryEnabled(this.parseBoolean(manualEntryEnabled));
		}
		if (automaticGenerationEnabled != null) {
			autoGenerationOption.setAutomaticGenerationEnabled(this.parseBoolean(automaticGenerationEnabled));
		}
		if (identifierSource != null) {
			autoGenerationOption.setSource(identifierSource);
		}
		if (location != null) {
			autoGenerationOption.setLocation(location);
		}
		return (SimpleObject) ConversionUtil.convertToRepresentation(save(autoGenerationOption), Representation.REF);
	}
	
	@Override
	public AutoGenerationOption newDelegate() {
		return new AutoGenerationOption();
	}
	
	@Override
	public AutoGenerationOption save(AutoGenerationOption autoGenerationOption) {
		return Context.getService(IdentifierSourceService.class).saveAutoGenerationOption(autoGenerationOption);
	}
	
	@Override
	public AutoGenerationOption getByUniqueId(String uniqueId) {
		return Context.getService(IdentifierSourceService.class).getAutoGenerationOptionByUuid(uniqueId);
	}
	
	@Override
	public void purge(AutoGenerationOption delegate, RequestContext context) throws ResponseException {
		Context.getService(IdentifierSourceService.class).purgeAutoGenerationOption(delegate);
	}
	
	@Override
	public void delete(AutoGenerationOption delegate, String id, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Deleting of AutoGenerationOption is not supported");
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<AutoGenerationOption> autoGenerationOptionList = new ArrayList<>();
		for (PatientIdentifierType patientIdentifierType : Context.getPatientService().getAllPatientIdentifierTypes()) {
			List<AutoGenerationOption> autoGenerationOptions = Context.getService(IdentifierSourceService.class)
			        .getAutoGenerationOptions(patientIdentifierType);
			if (autoGenerationOptions != null && !autoGenerationOptions.isEmpty()) {
				autoGenerationOptionList.addAll(autoGenerationOptions);
			}
		}
		return new NeedsPaging<>(autoGenerationOptionList, context);
	}
	
	private Boolean parseBoolean(Object value) {
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
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = ((ModelImpl) super.getGETModel(rep));
		
		if (rep instanceof RefRepresentation) {
			model   
					.property("uuid", new StringProperty())
					.property("display", new StringProperty());
		}	
		if (!(rep instanceof RefRepresentation)) {
			model
					.property("uuid", new StringProperty())
			        .property(IDENTIFIER_TYPE, new RefProperty("#/definitions/PatientidentifiertypeGet"))
			        .property(LOCATION, new RefProperty(LOCATION_GET_DEFINITION))
			        .property(SOURCE, new RefProperty(IDGEN_IDENTIFIER_SOURCE_GET_DEFINITION))
			        .property(MANUAL_ENTRY_ENABLED, new BooleanProperty())
			        .property(AUTOMATIC_GENERATION_ENABLED, new BooleanProperty());
		}
		if (rep instanceof DefaultRepresentation) {
			model   
					.property("full", new StringProperty());
		}
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		return new ModelImpl()
				.property(IDENTIFIER_TYPE, new RefProperty("#/definitions/PatientidentifiertypeGet"))
				.property(LOCATION, new RefProperty(LOCATION_GET_DEFINITION))
		        .property(SOURCE, new RefProperty(IDGEN_IDENTIFIER_SOURCE_GET_DEFINITION))
		        .property(MANUAL_ENTRY_ENABLED, new BooleanProperty())
		        .property(AUTOMATIC_GENERATION_ENABLED, new BooleanProperty());
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		return new ModelImpl()
				.property(LOCATION, new RefProperty(LOCATION_GET_DEFINITION))
		        .property(SOURCE, new RefProperty(IDGEN_IDENTIFIER_SOURCE_GET_DEFINITION))
		        .property(MANUAL_ENTRY_ENABLED, new BooleanProperty())
		        .property(AUTOMATIC_GENERATION_ENABLED, new BooleanProperty());
	}
}
