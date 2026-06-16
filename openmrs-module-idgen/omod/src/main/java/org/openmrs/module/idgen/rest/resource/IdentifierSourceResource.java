/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.validation.ValidationException;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

import org.openmrs.module.webservices.rest.SimpleObject;
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
	private static final String PROP_SOURCE_UUID = "sourceUuid";
	private static final String PROP_BATCH_SIZE = "batchSize";
	private static final String PROP_MIN_POOL_SIZE = "minPoolSize";
	private static final String PROP_SEQUENTIAL = "sequential";
	private static final String PROP_REFILL_WITH_SCHEDULED_TASK = "refillWithScheduledTask";
	private static final String PROP_FIRST_IDENTIFIER_BASE = "firstIdentifierBase";
	private static final String PROP_BASE_CHARACTER_SET = "baseCharacterSet";
	private static final String PROP_PREFIX = "prefix";
	private static final String PROP_SUFFIX = "suffix";
	private static final String PROP_MIN_LENGTH = "minLength";
	private static final String PROP_MAX_LENGTH = "maxLength";
	private static final String PROP_URL = "url";
	private static final String PROP_USER = "user";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_COMMENT = "comment";
	private static final String PROP_NUMBER_TO_GENERATE = "numberToGenerate";
	// Swagger model definition constants
	private static final String SWAGGER_PATIENT_IDENTIFIER_TYPE_GET = "#/definitions/PatientidentifiertypeGet";

  
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = null;
        if (rep instanceof RefRepresentation) {
        	description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addSelfLink();
            return description;
        }
        else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
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
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getIdentifiers().toString();
        }
        return  null;
    }
    
    @PropertyGetter("usedIdentifiers")
    public Integer getUsedIdentifiers(IdentifierSource identifierSource) {
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getUsedIdentifiers().size();
        }
        return  null;
    }
    
    @PropertyGetter("availableIdentifiers")
    public Integer getAvailableIdentifiers(IdentifierSource identifierSource) {
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getAvailableIdentifiers().size();
        }
        return  null;
    }

    @Override
    public IdentifierSource newDelegate() {
        return new SequentialIdentifierGenerator();
    }
	
    @Override
    public Object create(SimpleObject postBody, RequestContext context) throws ResponseException {
        Object generateIdentifiers = postBody.get("generateIdentifiers");
        if (generateIdentifiers != null) {
            Object res = handleGenerateIdentifiers(postBody);
            if (res != null) {
                return res;
            }
        }

        ArrayList<String> errors = new ArrayList<>();
        PatientIdentifierType patientIdentifierType = validateAndGetIdentifierType(postBody, errors);
        
        Object name = postBody.get("name");
        Object description = postBody.get("description");
        Object sourceType = postBody.get("sourceType");

        IdentifierSource source = createIdentifierSourceFromType(
            sourceType != null ? sourceType.toString() : null,
            postBody,
            patientIdentifierType,
            name,
            description,
            errors
        );

        Object savedIdentifierSource = save(source);
        return ConversionUtil.convertToRepresentation(savedIdentifierSource, Representation.DEFAULT);         
    }

    private PatientIdentifierType validateAndGetIdentifierType(SimpleObject postBody, ArrayList<String> errors) {
        Object identifierSourceType = postBody.get("sourceType");
        Object name = postBody.get("name");
        Object identifierTypeUuid = postBody.get("identifierType");

        if (identifierSourceType == null || StringUtils.isBlank(identifierSourceType.toString())) {
            errors.add("source type"); 
        }
        if (name == null || StringUtils.isBlank(name.toString())) {
            errors.add("name"); 
        }

        PatientIdentifierType patientIdentifierType = null;
        if (identifierTypeUuid == null || StringUtils.isBlank(identifierTypeUuid.toString())) {
            errors.add("patient identifier type"); 
        } else {
            patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid.toString());
            if (patientIdentifierType == null) {
                errors.add("patient identifier type");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("The values of the following inputs are missing or invalid: " + errors);
        }
        return patientIdentifierType;
    }

    private IdentifierSource createIdentifierSourceFromType(String sourceType, SimpleObject postBody, 
            PatientIdentifierType patientIdentifierType, Object name, Object description, ArrayList<String> errors) {
        if ("SequentialIdentifierGenerator".equals(sourceType)) {
            return createSequentialGenerator(postBody, patientIdentifierType, name, description, errors);
        } else if ("RemoteIdentifierSource".equals(sourceType)) {
            return createRemoteSource(postBody, patientIdentifierType, name, description, errors);
        } else if ("IdentifierPool".equals(sourceType)) {
            return createIdentifierPool(postBody, patientIdentifierType, name, description, errors);
        }
        return null;
    }

    private Object handleGenerateIdentifiers(SimpleObject postBody) {
        Object comment = postBody.get(PROP_COMMENT);
        Object numberToGenerate = postBody.get(PROP_NUMBER_TO_GENERATE);
        Object sourceUuid = postBody.get(PROP_SOURCE_UUID);
        SimpleObject identifiersToExport = new SimpleObject();
        if (comment == null) {
            comment = "Batch Export of " + numberToGenerate + " to file";
        }
        if (numberToGenerate == null) {
            numberToGenerate = "0";
        }
        int count = 0;
        try {
            count = Integer.parseInt(numberToGenerate.toString());
        } catch (NumberFormatException e) {
            throw new ValidationException("numberToGenerate must be a valid integer");
        }
        if (sourceUuid != null) {
            IdentifierSource identifierSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
            if (identifierSource != null) {
                List<String> identifiers = Context.getService(IdentifierSourceService.class).
                    generateIdentifiers(identifierSource, count, comment.toString());
                identifiersToExport.add("identifiers", identifiers);
                return identifiersToExport;
            }
        }
        return null;
    }

    private SequentialIdentifierGenerator createSequentialGenerator(SimpleObject postBody, PatientIdentifierType patientIdentifierType, Object name, Object description, ArrayList<String> errors) {
        SequentialIdentifierGenerator identifierSource = new SequentialIdentifierGenerator();
        Object firstIdentifierBase = postBody.get("firstIdentifierBase");
        Object baseCharacterSet = postBody.get("baseCharacterSet");
        Object prefix = postBody.get("prefix");
        Object suffix = postBody.get("suffix");
        Object minLength = postBody.get("minLength");
        Object maxLength = postBody.get("maxLength");

        if (firstIdentifierBase == null || StringUtils.isBlank(firstIdentifierBase.toString())) { 
            errors.add("first identifier base"); 
        }
        if (baseCharacterSet == null || StringUtils.isBlank(baseCharacterSet.toString())) { 
            errors.add("base character set"); 
        }
        if (description != null && StringUtils.isNotBlank(description.toString())) {
            identifierSource.setDescription(description.toString());
        }
        if (prefix != null && StringUtils.isNotBlank(prefix.toString())) { 
            identifierSource.setPrefix(prefix.toString()); 
        }
        if (suffix != null && StringUtils.isNotBlank(suffix.toString())) { 
            identifierSource.setSuffix(suffix.toString()); 
        }
        if (minLength != null && StringUtils.isNotBlank(minLength.toString())) {
            try {
                identifierSource.setMinLength(Integer.parseInt(minLength.toString()));
            } catch (NumberFormatException e) {
                errors.add("minLength must be a valid integer");
            }
        }
        if (maxLength != null && StringUtils.isNotBlank(maxLength.toString())) {
            try {
                identifierSource.setMaxLength(Integer.parseInt(maxLength.toString()));
            } catch (NumberFormatException e) {
                errors.add("maxLength must be a valid integer");
            }
        }
        if(errors.size() > 0) {
            throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
        }

        identifierSource.setIdentifierType(patientIdentifierType);
        identifierSource.setName(name.toString());
        if (baseCharacterSet != null) {
            identifierSource.setBaseCharacterSet(baseCharacterSet.toString());
        }
        if (firstIdentifierBase != null) {
            identifierSource.setFirstIdentifierBase(firstIdentifierBase.toString());
        }
        return identifierSource;
    }

    private RemoteIdentifierSource createRemoteSource(SimpleObject postBody, PatientIdentifierType patientIdentifierType, Object name, Object description, ArrayList<String> errors) {
        RemoteIdentifierSource identifierSource = new RemoteIdentifierSource();
        Object username = postBody.get("username");
        Object password = postBody.get("password");
        Object url = postBody.get("url");

        if (url == null || StringUtils.isBlank(url.toString())) { 
                errors.add("url"); 
        }
        if (description != null && StringUtils.isNotBlank(description.toString())) {
            identifierSource.setDescription(description.toString());
        }
        if (username!= null && StringUtils.isNotBlank(username.toString())) { 
                identifierSource.setUser(username.toString()); 
        }
        if (password != null && StringUtils.isNotBlank(password.toString())) {
            if(username == null || StringUtils.isBlank(username.toString())) {
                errors.add("username");
            }
            else {
                identifierSource.setPassword(password.toString());
            }
        }
        if(errors.size() > 0) {
            throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
        }

        identifierSource.setIdentifierType(patientIdentifierType);
        identifierSource.setName(name.toString());
        if (url != null) {
            identifierSource.setUrl(url.toString());
        }
        return identifierSource;
    }

    private IdentifierPool createIdentifierPool(SimpleObject postBody, PatientIdentifierType patientIdentifierType, Object name, Object description, ArrayList<String> errors) {
        IdentifierPool identifierSource = new IdentifierPool();
        Object batchSize = postBody.get("batchSize");
        Object minPoolSize = postBody.get("minPoolSize");
        Object sourceUuid = postBody.get("sourceUuid");
        Object sequential = postBody.get("sequential");
        Object refillWithScheduledTask = postBody.get("refillWithScheduledTask");

        if (sourceUuid != null && StringUtils.isNotBlank(sourceUuid.toString())) {
            IdentifierSource poolIdentifierSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
            if (poolIdentifierSource != null) {
                identifierSource.setSource(poolIdentifierSource);
            } else {
                errors.add("identifier souce");
            }
        }
        if (description != null && StringUtils.isNotBlank(description.toString())) {
            identifierSource.setDescription(description.toString());
        }
        if (batchSize != null && StringUtils.isNotBlank(batchSize.toString())) {
            try {
                identifierSource.setBatchSize(Integer.parseInt(batchSize.toString()));
            } catch (NumberFormatException e) {
                errors.add("batchSize must be a valid integer");
            }
        }
        if (minPoolSize != null && StringUtils.isNotBlank(minPoolSize.toString())) {
            try {
                identifierSource.setMinPoolSize(Integer.parseInt(minPoolSize.toString()));
            } catch (NumberFormatException e) {
                errors.add("minPoolSize must be a valid integer");
            }
        }
        if (sequential != null && StringUtils.isNotBlank(sequential.toString())) {
            identifierSource.setSequential(parseBoolean(sequential));
        }
        if (refillWithScheduledTask != null && StringUtils.isNotBlank(refillWithScheduledTask.toString())) {
            identifierSource.setRefillWithScheduledTask(parseBoolean(refillWithScheduledTask));
        }
        if(errors.size() > 0) {
            throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
        }

        identifierSource.setIdentifierType(patientIdentifierType);
        identifierSource.setName(name.toString());
        return identifierSource;
    }
    
    @Override
    public Object update(String uuid, SimpleObject updateBody, RequestContext context) throws ResponseException {
        IdentifierSource identifierSourceToUpdate = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(uuid);
        Object operation = updateBody.get("operation");

        handleReservedIdentifiers(updateBody, identifierSourceToUpdate);
        handleUploadFromSource(updateBody, operation, identifierSourceToUpdate);
        handleUploadFromFile(updateBody, operation, identifierSourceToUpdate);

        Object updatedIdentifierSource = null;
        if (identifierSourceToUpdate instanceof SequentialIdentifierGenerator) {
            updatedIdentifierSource = updateSequentialGenerator(updateBody, (SequentialIdentifierGenerator) identifierSourceToUpdate);
        } else if (identifierSourceToUpdate instanceof IdentifierPool) {
            updatedIdentifierSource = updateIdentifierPool(updateBody, (IdentifierPool) identifierSourceToUpdate);
        } else if (identifierSourceToUpdate instanceof RemoteIdentifierSource) {
            updatedIdentifierSource = updateRemoteSource(updateBody, (RemoteIdentifierSource) identifierSourceToUpdate);
        }
        return ConversionUtil.convertToRepresentation(updatedIdentifierSource, Representation.DEFAULT);
    }

    private void handleReservedIdentifiers(SimpleObject updateBody, IdentifierSource source) {
        Object reservedIdentifiersToUpload = updateBody.get("reservedIdentifiers");
        if (reservedIdentifiersToUpload != null && source != null) {
            List<String> reserved = new ArrayList<>(Arrays.asList(reservedIdentifiersToUpload.toString().split(",")));
            for (String id : reserved) {
                if (StringUtils.isNotBlank(id)) {
                    source.addReservedIdentifier(id);
                }
            }
            Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
        }
    }

    private void handleUploadFromSource(SimpleObject updateBody, Object operation, IdentifierSource source) {
        if ("uploadFromSource".equals(operationToString(operation))) {
            Object batchSize = updateBody.get(PROP_BATCH_SIZE);
            if (source instanceof IdentifierPool && batchSize != null && StringUtils.isNumeric(batchSize.toString())) {
                try {
                    Context.getService(IdentifierSourceService.class).addIdentifiersToPool((IdentifierPool) source, Integer.parseInt(batchSize.toString()));
                } catch (NumberFormatException e) {
                    throw new ValidationException("batchSize must be a valid integer");
                }
            }
        }
    }

    private void handleUploadFromFile(SimpleObject updateBody, Object operation, IdentifierSource source) {
        if ("uploadFromFile".equals(operationToString(operation))) {
            Object identifiers = updateBody.get("identifiers");
            if (source instanceof IdentifierPool && identifiers != null) {
                List<String> ids = new ArrayList<>(Arrays.asList(identifiers.toString().split(",")));
                Context.getService(IdentifierSourceService.class).addIdentifiersToPool((IdentifierPool) source, ids);
            }
        }
    }

    private String operationToString(Object operation) {
        return operation != null ? operation.toString() : null;
    }

    private Object updateSequentialGenerator(SimpleObject updateBody, SequentialIdentifierGenerator identifierSource) {
        String firstIdentifierBase = getStringField(updateBody, PROP_FIRST_IDENTIFIER_BASE);
        if (firstIdentifierBase != null) {
            identifierSource.setFirstIdentifierBase(firstIdentifierBase);
        }
        String baseCharacterSet = getStringField(updateBody, PROP_BASE_CHARACTER_SET);
        if (baseCharacterSet != null) {
            identifierSource.setBaseCharacterSet(baseCharacterSet);
        }
        String name = getStringField(updateBody, PROP_NAME);
        if (name != null) {
            identifierSource.setName(name);
        }
        String description = getStringField(updateBody, PROP_DESCRIPTION);
        if (description != null) {
            identifierSource.setDescription(description);
        }
        String prefix = getStringField(updateBody, PROP_PREFIX);
        if (prefix != null) {
            identifierSource.setPrefix(prefix);
        }
        String suffix = getStringField(updateBody, PROP_SUFFIX);
        if (suffix != null) {
            identifierSource.setSuffix(suffix);
        }
        Integer minLength = getIntField(updateBody, PROP_MIN_LENGTH);
        if (minLength != null) {
            identifierSource.setMinLength(minLength);
        }
        Integer maxLength = getIntField(updateBody, PROP_MAX_LENGTH);
        if (maxLength != null) {
            identifierSource.setMaxLength(maxLength);
        }
        return save(identifierSource);
    }

    private Object updateIdentifierPool(SimpleObject updateBody, IdentifierPool identifierSource) {
        Object sourceUuid = updateBody.get(PROP_SOURCE_UUID);
        if (sourceUuid != null && StringUtils.isNotBlank(sourceUuid.toString())) {
            IdentifierSource poolSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
            identifierSource.setSource(poolSource);
        } else {
            identifierSource.setSource(null);
        }
        String name = getStringField(updateBody, PROP_NAME);
        if (name != null) {
            identifierSource.setName(name);
        }
        String description = getStringField(updateBody, PROP_DESCRIPTION);
        if (description != null) {
            identifierSource.setDescription(description);
        }
        Integer batchSize = getIntField(updateBody, PROP_BATCH_SIZE);
        if (batchSize != null) {
            identifierSource.setBatchSize(batchSize);
        }
        Integer minPoolSize = getIntField(updateBody, PROP_MIN_POOL_SIZE);
        if (minPoolSize != null) {
            identifierSource.setMinPoolSize(minPoolSize);
        }
        Boolean sequential = getBooleanField(updateBody, PROP_SEQUENTIAL);
        if (sequential != null) {
            identifierSource.setSequential(sequential);
        }
        Boolean refill = getBooleanField(updateBody, PROP_REFILL_WITH_SCHEDULED_TASK);
        if (refill != null) {
            identifierSource.setRefillWithScheduledTask(refill);
        }
        return save(identifierSource);
    }

    private Object updateRemoteSource(SimpleObject updateBody, RemoteIdentifierSource identifierSource) {
        String url = getStringField(updateBody, PROP_URL);
        if (url != null) {
            identifierSource.setUrl(url);
        }
        String name = getStringField(updateBody, PROP_NAME);
        if (name != null) {
            identifierSource.setName(name);
        }
        String description = getStringField(updateBody, PROP_DESCRIPTION);
        if (description != null) {
            identifierSource.setDescription(description);
        }
        String user = getStringField(updateBody, PROP_USER);
        if (user != null) {
            identifierSource.setUser(user);
        }
        String password = getStringField(updateBody, PROP_PASSWORD);
        if (password != null) {
            identifierSource.setPassword(password);
        }
        return save(identifierSource);
    }

    private String getStringField(SimpleObject updateBody, String key) {
        Object val = updateBody.get(key);
        return (val != null && StringUtils.isNotBlank(val.toString())) ? val.toString() : null;
    }

    private Integer getIntField(SimpleObject updateBody, String key) {
        Object val = updateBody.get(key);
        if (val != null && StringUtils.isNotBlank(val.toString())) {
            try {
                return Integer.parseInt(val.toString());
            } catch (NumberFormatException e) {
                throw new ValidationException(key + " must be a valid integer");
            }
        }
        return null;
    }

    private Boolean getBooleanField(SimpleObject updateBody, String key) {
        Object val = updateBody.get(key);
        return (val != null && StringUtils.isNotBlank(val.toString())) ? parseBoolean(val) : null;
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
        if(identifierType != null){
            PatientIdentifierType requestedPatientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierType);
            if(requestedPatientIdentifierType != null){  
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

    private Boolean parseBoolean(Object value) {
        List<String> trueValues = Arrays.asList("true", "1", "on", "yes");
        List<String> falseValues = Arrays.asList("false", "0", "off", "no"); 
        String val = value.toString().trim().toLowerCase();
        
        if (trueValues.contains(val)) {
            return Boolean.TRUE;
        }
        else if (falseValues.contains(val)) {
            return Boolean.FALSE;
        }
        return null;
    }
    
}
