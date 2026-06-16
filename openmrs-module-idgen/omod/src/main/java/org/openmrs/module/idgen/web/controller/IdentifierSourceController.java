package org.openmrs.module.idgen.web.controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifiersMessage;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.propertyeditor.IdentifierSourceEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.validator.IdentifierSourceValidator;
import org.openmrs.module.idgen.validator.RemoteIdentifierSourceValidator;
import org.openmrs.module.idgen.validator.SequentialIdentifierGeneratorValidator;
import org.openmrs.propertyeditor.PatientIdentifierTypeEditor;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("source")
public class IdentifierSourceController {

	protected static Log log = LogFactory.getLog(IdentifierSourceController.class);

	private static final String AUDIT_USER_PREFIX = "[AUDIT] UserID: ";
	private static final String SYSTEM_USER = "SYSTEM";
	private static final String REDIRECT_VIEW_SOURCE = "redirect:/module/idgen/viewIdentifierSource.form?source=";
	private static final String REDIRECT_MANAGE_SOURCES = "redirect:/module/idgen/manageIdentifierSources.form";

	@Autowired
	private IdentifierSourceService iss;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public IdentifierSourceController() {
		// Required by Spring MVC for @Controller instantiation
	}
	
	//***** INSTANCE METHODS *****
	
	@InitBinder
	public void initBinder(@SuppressWarnings("unused") HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(PatientIdentifierType.class, new PatientIdentifierTypeEditor());
		binder.registerCustomEditor(IdentifierSource.class, new IdentifierSourceEditor());
	}
	
    /**
     * Edit a new or existing IdentifierSource
     */
    @RequestMapping("/module/idgen/editIdentifierSource.form")
    public void editIdentifierSource(ModelMap model, @SuppressWarnings("unused") HttpServletRequest request,
    							     @RequestParam(required=false, value="source") IdentifierSource source,
    							     @RequestParam(required=false, value="identifierType") PatientIdentifierType identifierType,
    							     @RequestParam(required=false, value="sourceType") String sourceType) {
    	
		if (Context.isAuthenticated()) {
			
			Thread.currentThread().setContextClassLoader(OpenmrsClassLoader.getInstance());
			if (source == null) {
				try {
					Class<?> idSourceType = Context.loadClass(sourceType);
					model.addAttribute("sourceType", sourceType);
					source = (IdentifierSource)idSourceType.getDeclaredConstructor().newInstance();
					source.setIdentifierType(identifierType);
				}
				catch (Exception e) {
					throw new RuntimeException("Unable to instantiate class " + sourceType, e);
				}
			}
			model.addAttribute("source", source);
			
			List<IdentifierSource> otherCompatibleSources = new ArrayList<>();
			for (IdentifierSource s : Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false)) {
				if (!s.equals(source) && s.getIdentifierType().equals(source.getIdentifierType())) {
					otherCompatibleSources.add(s);			
				}
			}
			model.addAttribute("otherCompatibleSources", otherCompatibleSources);
		}
    }
    
    /**
     * Retrieves all IdentifierSources
     */
    @RequestMapping("/module/idgen/manageIdentifierSources.form")
    public void manageIdentifierSources(ModelMap model, 
    									@RequestParam(required=false, value="includeRetired") Boolean includeRetired) {
		if (Context.isAuthenticated()) {
			IdentifierSourceService sourceService = Context.getService(IdentifierSourceService.class);
			boolean ret = includeRetired == Boolean.TRUE;
			
			Map<PatientIdentifierType, List<IdentifierSource>> sourcesByType = sourceService.getIdentifierSourcesByType(ret);
			
			List<PatientIdentifierType> identifierTypes = new ArrayList<>();
			for (Iterator<PatientIdentifierType> i = sourcesByType.keySet().iterator(); i.hasNext();) {
				PatientIdentifierType pit = i.next();
				if (sourcesByType.get(pit).isEmpty()) {
					i.remove();
				}
				identifierTypes.add(pit);
			}
			model.addAttribute("sourcesByType", sourcesByType);
			model.addAttribute("identifierTypes", identifierTypes);
			model.addAttribute("sourceTypes", sourceService.getIdentifierSourceTypes());
		}
    }
    
    /**
     * Deletes an IdentifierSource
     */
    @RequestMapping("/module/idgen/deleteIdentifierSource.form")
    public String deletePatientSearch(@SuppressWarnings("unused") ModelMap model, @RequestParam(required=true, value="source") IdentifierSource source) {
    	Context.getService(IdentifierSourceService.class).purgeIdentifierSource(source);
    	return REDIRECT_MANAGE_SOURCES;
    }
    
    /**
     * Saves an IdentifierSource
     */
    @RequestMapping("/module/idgen/saveIdentifierSource.form")
    public ModelAndView saveIdentifierSource(@ModelAttribute("source") IdentifierSource source,
		    BindingResult result, SessionStatus status,
		    @RequestParam(value="skipValidation", required = false) Boolean skipValidation) {
		
    	// Validate input
	    if (BooleanUtils.isNotTrue(skipValidation)) {
		    Validator v = new IdentifierSourceValidator();
		    if (source instanceof SequentialIdentifierGenerator) {
			    v = new SequentialIdentifierGeneratorValidator();
		    } else if (source instanceof RemoteIdentifierSource) {
			    v = new RemoteIdentifierSourceValidator();
		    }
		    v.validate(source, result);
	    }
    	
		if (result.hasErrors()) {
			return new ModelAndView("/module/idgen/editIdentifierSource");
		}
		
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		status.setComplete();
		return new ModelAndView(REDIRECT_MANAGE_SOURCES);
	}
    
    /**
     * Generate Identifiers Page
     */
    @RequestMapping("/module/idgen/viewIdentifierSource.form")
    public void viewIdentifierSource(ModelMap model, @RequestParam(required=true, value="source") IdentifierSource source) {
    	model.addAttribute("source", source);
    }
    
    /**
     * Generate and Output a Single new Identifier
     */
    @RequestMapping("/module/idgen/generateIdentifier.form")
    public void generateIdentifier(ModelMap model, HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=false, value="comment") String comment,
                                   @RequestParam(required=false, value="username") String username,
                                   @RequestParam(required=false, value="password") String password) throws Exception {
    	exportIdentifiers(model, request, response, source, 1, comment, username, password);
    }
    
    /**
     * Export Identifiers To File
     */
    @RequestMapping("/module/idgen/exportIdentifiers.form")
    public void exportIdentifiers(@SuppressWarnings("unused") ModelMap model, @SuppressWarnings("unused") HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="numberToGenerate") Integer numberToGenerate,
    							   @RequestParam(required=false, value="comment") String comment,
                                   @RequestParam(required=false, value="username") String username,
                                   @RequestParam(required=false, value="password") String password) throws Exception {

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            try {
                Context.authenticate(username, password);
                log.info(AUDIT_USER_PREFIX + IdgenUtil.sanitizeForLogging(username) + " | Event: LOGIN | ResourceUUID: N/A | Outcome: SUCCESS | Details: User authenticated via request parameters for export");
            } catch (Exception ex) {
                log.warn(AUDIT_USER_PREFIX + IdgenUtil.sanitizeForLogging(username) + " | Event: LOGIN | ResourceUUID: N/A | Outcome: FAILURE | Details: Authentication failed: " + IdgenUtil.sanitizeForLogging(ex.getMessage()));
                throw ex;
            }
        }
    	

        if (StringUtils.isEmpty(comment)) {
            comment = "Batch Export of " + numberToGenerate + " to file";
        }
        List<String> batch = iss.generateIdentifiers(source, numberToGenerate, comment);

        response.setHeader("Content-Disposition", "attachment; filename=identifiers.txt");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        
        new ObjectMapper().writeValue(out, new RemoteIdentifiersMessage(batch));
    }
    
    /**
     * Upload Identifiers From File
     */
    @RequestMapping("/module/idgen/addIdentifiersFromFile.form")
    public String addIdentifiersFromFile(@SuppressWarnings("unused") ModelMap model, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response,
                                         @RequestParam(required=true, value="source") IdentifierSource source,
                                         @RequestParam(required=true, value="inputFile") MultipartFile inputFile) throws Exception {

        IdentifierPool pool = (IdentifierPool) source;
        if (inputFile != null) {
            readAndAddIdentifiersFromFile(pool, inputFile);
            request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully uploaded.");
        }
        return REDIRECT_VIEW_SOURCE + source.getId();
    }

    /**
     * Reads identifiers from a JSON MultipartFile and adds them to the given pool.
     */
    private void readAndAddIdentifiersFromFile(IdentifierPool pool, MultipartFile inputFile) throws IOException {
        try (InputStream streamReader = inputFile.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            RemoteIdentifiersMessage remoteIdentifiersMessage = mapper.readValue(streamReader, RemoteIdentifiersMessage.class);
            if (remoteIdentifiersMessage != null) {
                iss.addIdentifiersToPool(pool, remoteIdentifiersMessage.getIdentifiers());
            }
        }
    }
    
    /**
     * Upload Identifiers to Pool From Source
     */
    @RequestMapping("/module/idgen/addIdentifiersFromSource.form")
    public String addIdentifiersFromSource(@SuppressWarnings("unused") ModelMap model, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="batchSize") Integer batchSize) throws Exception {
    	
    	IdentifierPool pool = (IdentifierPool)source;
		Context.getService(IdentifierSourceService.class).addIdentifiersToPool(pool, batchSize);
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully imported.");
		return REDIRECT_VIEW_SOURCE + source.getId();
    }
    
    /**
     * Reserve Identifiers From File
     */
    @RequestMapping("/module/idgen/reserveIdentifiersFromFile.form")
    public String reserveIdentifiersFromFile(@SuppressWarnings("unused") ModelMap model, HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source,
    							   @RequestParam(required=true, value="inputFile") MultipartFile inputFile) throws Exception {
    	
    	try (BufferedReader r = new BufferedReader(new InputStreamReader(inputFile.getInputStream()))) {
    		for (String s = r.readLine(); s != null; s = r.readLine()) {
    			if (StringUtils.isNotBlank(s)) {
    				source.addReservedIdentifier(s);
    			}
    		}
    	}
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		
		// NEN-7510 audit log
		org.openmrs.User u = Context.getAuthenticatedUser();
		String userId = (u != null) ? u.getUsername() : SYSTEM_USER;
		log.info(AUDIT_USER_PREFIX + IdgenUtil.sanitizeForLogging(userId) + " | Event: RESERVE_IDENTIFIERS | ResourceUUID: " + IdgenUtil.sanitizeForLogging(source.getUuid()) + " | Outcome: SUCCESS | Details: Uploaded and reserved identifiers from file for source '" + IdgenUtil.sanitizeForLogging(source.getName()) + "'");
		
		request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Success: Identifiers successfully uploaded.");
		return REDIRECT_VIEW_SOURCE + source.getId();
    }
    
    /**
     * Export Identifiers To File
     */
    @RequestMapping("/module/idgen/exportReservedIdentifiers.form")
    public void exportReservedIdentifiers(@SuppressWarnings("unused") ModelMap model, @SuppressWarnings("unused") HttpServletRequest request, HttpServletResponse response,
    							   @RequestParam(required=true, value="source") IdentifierSource source) throws Exception {

		response.setHeader("Content-Disposition", "attachment; filename=reservedIdentifiers.txt");
		response.setHeader("Pragma", "no-cache");
    	response.setContentType("text/plain");
    	ServletOutputStream out = response.getOutputStream();
    	String separator = System.getProperty("line.separator");
    	
    	for (Iterator<String> i = source.getReservedIdentifiers().iterator(); i.hasNext();) {
    		String identifier = i.next();
    		out.print(identifier + (i.hasNext() ? separator : ""));
    	}
		
		// NEN-7510 audit log
		org.openmrs.User u = Context.getAuthenticatedUser();
		String userId = (u != null) ? u.getUsername() : SYSTEM_USER;
		log.info(AUDIT_USER_PREFIX + IdgenUtil.sanitizeForLogging(userId) + " | Event: EXPORT_RESERVED_IDENTIFIERS | ResourceUUID: " + IdgenUtil.sanitizeForLogging(source.getUuid()) + " | Outcome: SUCCESS | Details: Exported reserved identifiers for source '" + IdgenUtil.sanitizeForLogging(source.getName()) + "'");
    }
}
