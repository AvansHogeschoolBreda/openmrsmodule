package org.openmrs.module.idgen.service;

import java.io.StringWriter;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.springframework.beans.factory.annotation.Autowired;

public class LoggingAuditTest extends IdgenBaseTest {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    private StringWriter logOutput;
    private WriterAppender appender;
    private Logger serviceLogger;

    @Before
    public void setUp() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");

        // Redirect log4j output for the service logger
        logOutput = new StringWriter();
        appender = new WriterAppender(new PatternLayout("%p - %m"), logOutput);
        serviceLogger = Logger.getLogger("org.openmrs.module.idgen");
        serviceLogger.setLevel(org.apache.log4j.Level.INFO);
        serviceLogger.addAppender(appender);
    }

    @After
    public void tearDown() {
        if (serviceLogger != null && appender != null) {
            serviceLogger.removeAppender(appender);
        }
    }

    @Test
    public void testSuccessfulAccessLogged() throws Exception {
        IdentifierSource source = identifierSourceService.getIdentifierSource(1);
        Assert.assertNotNull(source);

        // Perform action that triggers audit log (generation / read of identifier)
        List<String> ids = identifierSourceService.generateIdentifiers(source, 2, "Test generation logging");
        Assert.assertEquals(2, ids.size());

        String logs = logOutput.toString();
        
        // Assertions for Eis 1 & Eis 2: Audit log exists and contains correct metadata
        Assert.assertTrue("Log output should contain [AUDIT] prefix", logs.contains("[AUDIT]"));
        Assert.assertTrue("Log should register READ_PATIENT_IDENTIFIER event", logs.contains("Event: READ_PATIENT_IDENTIFIER"));
        Assert.assertTrue("Log should register SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
        Assert.assertTrue("Log should contain the authenticated UserID", logs.contains("UserID: admin"));
        Assert.assertTrue("Log should contain the resource UUID", logs.contains("ResourceUUID: " + source.getUuid()));
    }

    @Test
    public void testNoBsnOrSensitiveDataInLogs() throws Exception {
        IdentifierSource source = identifierSourceService.getIdentifierSource(1);
        List<String> ids = identifierSourceService.generateIdentifiers(source, 1, "Test logging leakage");
        
        String generatedId = ids.get(0);
        String logs = logOutput.toString();

        // Eis 3: Verify the actual generated identifier (patient data) is NOT leaked in standard logs
        Assert.assertFalse("Logs should not contain the actual identifier value: " + generatedId, logs.contains(generatedId));
        Assert.assertFalse("Logs should not contain medical info or BSN", logs.contains("BSN"));
    }

    @Test
    public void testSaveIdentifierSourceLogged() throws Exception {
        SequentialIdentifierGenerator sig = new SequentialIdentifierGenerator();
        sig.setName("Test Logging Source");
        sig.setBaseCharacterSet("0123456789");
        sig.setFirstIdentifierBase("1");
        sig.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));

        identifierSourceService.saveIdentifierSource(sig);

        String logs = logOutput.toString();
        
        Assert.assertTrue("Log should register SAVE_IDENTIFIER_SOURCE event", logs.contains("Event: SAVE_IDENTIFIER_SOURCE"));
        Assert.assertTrue("Log should contain ResourceUUID of the new source", logs.contains("ResourceUUID: " + sig.getUuid()));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }

    @Test
    public void testRetireIdentifierSourceLogged() throws Exception {
        IdentifierSource source = identifierSourceService.getIdentifierSource(1);
        Assert.assertFalse(source.isRetired());

        identifierSourceService.retireIdentifierSource(source, "Retired for test purpose");

        String logs = logOutput.toString();

        Assert.assertTrue("Log should register RETIRE_IDENTIFIER_SOURCE event", logs.contains("Event: RETIRE_IDENTIFIER_SOURCE"));
        Assert.assertTrue("Log should contain UUID of retired source", logs.contains("ResourceUUID: " + source.getUuid()));
        Assert.assertTrue("Log should contain retire reason", logs.contains("Reason: Retired for test purpose"));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }

    @Test
    public void testFailedAccessLogged() throws Exception {
        SequentialIdentifierGenerator sig = new SequentialIdentifierGenerator();
        sig.setName(null); // Triggers validation failure
        sig.setBaseCharacterSet("0123456789");
        sig.setFirstIdentifierBase("1");
        sig.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));

        try {
            identifierSourceService.saveIdentifierSource(sig);
            Assert.fail("Should have thrown APIException due to missing name");
        } catch (org.openmrs.api.APIException e) {
            // expected
        }

        String logs = logOutput.toString();
        Assert.assertTrue("Log should register SAVE_IDENTIFIER_SOURCE event for failure", logs.contains("Event: SAVE_IDENTIFIER_SOURCE"));
        Assert.assertTrue("Log should register FAILURE outcome", logs.contains("Outcome: FAILURE"));
        Assert.assertTrue("Log should contain details about missing name", logs.contains("Name is required"));
    }

    @Test
    public void testAddIdentifiersToPoolLogged() throws Exception {
        org.openmrs.module.idgen.IdentifierPool pool = (org.openmrs.module.idgen.IdentifierPool) identifierSourceService.getIdentifierSource(3);
        java.util.List<String> identifiers = new java.util.ArrayList<String>();
        identifiers.add("TESTPOOL1");
        identifiers.add("TESTPOOL2");

        identifierSourceService.addIdentifiersToPool(pool, identifiers);

        String logs = logOutput.toString();
        Assert.assertTrue("Log should register ADD_IDENTIFIERS_TO_POOL event", logs.contains("Event: ADD_IDENTIFIERS_TO_POOL"));
        Assert.assertTrue("Log should contain pool ResourceUUID", logs.contains("ResourceUUID: " + pool.getUuid()));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }

    @Test
    public void testSaveAutoGenerationOptionLogged() throws Exception {
        AutoGenerationOption option = identifierSourceService.getAutoGenerationOption(1);
        Assert.assertNotNull(option);

        option.setManualEntryEnabled(false);
        identifierSourceService.saveAutoGenerationOption(option);

        String logs = logOutput.toString();
        Assert.assertTrue("Log should register SAVE_AUTOGENERATION_OPTION event", logs.contains("Event: SAVE_AUTOGENERATION_OPTION"));
        Assert.assertTrue("Log should contain option ResourceUUID", logs.contains("ResourceUUID: " + option.getUuid()));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }

    @Test
    public void testPurgeAutoGenerationOptionLogged() throws Exception {
        AutoGenerationOption option = identifierSourceService.getAutoGenerationOption(1);
        Assert.assertNotNull(option);

        identifierSourceService.purgeAutoGenerationOption(option);

        String logs = logOutput.toString();
        Assert.assertTrue("Log should register PURGE_AUTOGENERATION_OPTION event", logs.contains("Event: PURGE_AUTOGENERATION_OPTION"));
        Assert.assertTrue("Log should contain option ResourceUUID", logs.contains("ResourceUUID: " + option.getUuid()));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }

    @Test
    public void testPurgeIdentifierSourceLogged() throws Exception {
        IdentifierSource source = identifierSourceService.getIdentifierSource(1);
        Assert.assertNotNull(source);

        identifierSourceService.purgeIdentifierSource(source);

        String logs = logOutput.toString();
        Assert.assertTrue("Log should register PURGE_IDENTIFIER_SOURCE event", logs.contains("Event: PURGE_IDENTIFIER_SOURCE"));
        Assert.assertTrue("Log should contain source ResourceUUID", logs.contains("ResourceUUID: " + source.getUuid()));
        Assert.assertTrue("Log should contain SUCCESS outcome", logs.contains("Outcome: SUCCESS"));
    }
}
