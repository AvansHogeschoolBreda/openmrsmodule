# Overzicht Code Quality Issues (SAST)

In de onderstaande tabel staan de gevonden code quality issues uit de statische broncode-analyse.

| Issue (Sonar Regel) | Bestand                                        | Regel | Severity           | Gemitigeerd | Beschrijving                                                                         |
| :------------------ | :--------------------------------------------- | :---- | :----------------- | :---------- | :----------------------------------------------------------------------------------- |
| `java:S1192`      | `BaseIdentifierSourceService.java`           | 119   | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "SYSTEM" 8 times.                   |
| `java:S1192`      | `BaseIdentifierSourceService.java`           | 120   | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "[AUDIT] UserID: " 8 times.         |
| `java:S1192`      | `IdentifierSourceController.java`            | 206   | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "[AUDIT] UserID: " 4 times.         |
| `java:S2093`      | `IdgenUtil.java`                             | 80    | **CRITICAL** | Ja          | Change this "try" to a try-with-resources statement.                                 |
| `java:S2093`      | `IdentifierSourceController.java`            | 290   | **CRITICAL** | Ja          | Change this "try" to a try-with-resources statement.                                 |
| `java:S2696`      | `LocationBasedPrefixProvider.java`           | 73    | **CRITICAL** | Ja          | Make the enclosing method "static" or remove this set.                               |
| `java:S2696`      | `LocationBasedPrefixProvider.java`           | 78    | **CRITICAL** | Ja          | Make the enclosing method "static" or remove this set.                               |
| `java:S1186`      | `AutoGenerationOptionEditor.java`            | 20    | **CRITICAL** | Ja          | Add comment explaining why method is empty, or throw UnsupportedOperationException.  |
| `java:S1186`      | `IdentifierSourceEditor.java`                | 35    | **CRITICAL** | Ja          | Add comment explaining why method is empty, or throw UnsupportedOperationException.  |
| `java:S2696`      | `BaseIdentifierSourceService.java`           | 411   | **CRITICAL** | Ja          | Make the enclosing method "static" or remove this set.                               |
| `java:S1192`      | `HibernateIdentifierSourceDAO.java`          | 169   | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "identifierType" 4 times.           |
| `java:S1192`      | `HibernateIdentifierSourceDAO.java`          | 228   | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "dateGenerated" 4 times.            |
| `java:S2696`      | `LocationBasedSuffixProvider.java`           | 73    | **CRITICAL** | Ja          | Make the enclosing method "static" or remove this set.                               |
| `java:S2696`      | `LocationBasedSuffixProvider.java`           | 78    | **CRITICAL** | Ja          | Make the enclosing method "static" or remove this set.                               |
| `java:S3776`      | `SequentialIdentifierGenValidator.java`      | 41    | **CRITICAL** | Ja          | Refactor method to reduce Cognitive Complexity from 27 to the 15 allowed.            |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 64    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "identifierType" 9 times.           |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 65    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "location" 8 times.                 |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 66    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "source" 10 times.                  |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 67    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "manualEntryEnabled" 8 times.       |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 68    | **CRITICAL** | Ja          | Define constant instead of duplicating literal "automaticGenerationEnabled".         |
| `java:S1192`      | `AutoGenerationOptionResource.java`          | 256   | **CRITICAL** | Ja          | Define constant instead of duplicating literal "#/definitions/LocationGet" 3 times.  |
| `java:S1192`      | `LogEntryResource.java`                      | 64    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "identifier" 5 times.               |
| `java:S1192`      | `LogEntryResource.java`                      | 65    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "comment" 3 times.                  |
| `java:S1192`      | `IdentifierPoolResourceHandler.java`         | 74    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "identifierType" 5 times.           |
| `java:S1192`      | `IdentifierPoolResourceHandler.java`         | 76    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "display" 5 times.                  |
| `java:S1192`      | `RemoteIdSourceResourceHandler.java`         | 61    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "display" 4 times.                  |
| `java:S1192`      | `RemoteIdSourceResourceHandler.java`         | 71    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "password" 5 times.                 |
| `java:S1192`      | `SequentialIdGenResourceHandler.java`        | 59    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "baseCharacterSet" 8 times.         |
| `java:S1192`      | `SequentialIdGenResourceHandler.java`        | 64    | **CRITICAL** | Ja          | Define a constant instead of duplicating literal "maxLength" 6 times.                |
| `java:S3776`      | `IdentifierSourceResource.java`              | 142   | **CRITICAL** | Ja          | Refactor method to reduce Cognitive Complexity from 101 to the 15 allowed.           |
| `java:S1186`      | `IdgenEditPatientIdentifiersController.java` | 38    | **CRITICAL** | Ja          | Add comment explaining why method is empty, or throw UnsupportedOperationException.  |
| `java:S1186`      | `LogEntryController.java`                    | 36    | **CRITICAL** | Ja          | Add comment explaining why method is empty, or throw UnsupportedOperationException.  |
| `java:S1068`      | `IdgenModuleActivator.java`                  | 48    | MAJOR              | Ja          | Remove this unused "REGISTRY_URL" private field.                                     |
| `java:S1068`      | `IdgenModuleActivator.java`                  | 49    | MAJOR              | Ja          | Remove this unused "REGISTRY_API_USER" private field.                                |
| `java:S1068`      | `IdgenModuleActivator.java`                  | 50    | MAJOR              | Ja          | Remove this unused "REGISTRY_API_PASSWORD" private field.                            |
| `java:S1068`      | `RemoteIdentifierSourceProcessor.java`       | 43    | MAJOR              | Ja          | Remove this unused "log" private field.                                              |
| `java:S1068`      | `IdgenTask.java`                             | 21    | MAJOR              | Ja          | Remove this unused "taskClass" private field.                                        |
| `java:S1068`      | `RemoteIdSourceProcessorStub.java`           | 31    | MAJOR              | Ja          | Remove this unused "batchSize" private field.                                        |
| `java:S1854`      | `IdentifierSourceServiceTest.java`           | 334   | MAJOR              | Ja          | Remove useless assignment to local variable "sig".                                   |
| `java:S1854`      | `IdentifierSourceServiceTest.java`           | 351   | MAJOR              | Ja          | Remove useless assignment to local variable "sig".                                   |
| `java:S1854`      | `IdentifierSourceResource.java`              | 322   | MAJOR              | Ja          | Remove useless assignment to local variable "generateIdentifiers".                   |
| `java:S3740`      | `IdentifierSourceDAO.java`                   | 192   | MAJOR              | Ja          | Provide the parametrized type for this generic.                                      |
| `java:S5993`      | `BaseIdentifierSource.java`                  | 48    | MAJOR              | Ja          | Change the visibility of this constructor to "protected".                            |
| `java:S4144`      | `BaseIdentifierSource.java`                  | 207   | MAJOR              | Ja          | Update method so implementation is not identical to "getRetired".                    |
| `java:S112`       | `IdentifierPool.java`                        | 78    | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S4144`      | `IdentifierPool.java`                        | 177   | MAJOR              | Ja          | Update method so implementation is not identical to "getRefillWithScheduledTask".    |
| `java:S1118`      | `IdgenConstants.java`                        | 19    | MAJOR              | Ja          | Add a private constructor to hide the implicit public one.                           |
| `java:S2068`      | `IdgenModuleActivator.java`                  | 50    | MAJOR              | Ja          | 'PASSWORD' detected in this expression, review this potentially hard-coded password. |
| `java:S1118`      | `IdgenUtil.java`                             | 28    | MAJOR              | Ja          | Add a private constructor to hide the implicit public one.                           |
| `java:S112`       | `IdgenUtil.java`                             | 66    | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S1117`      | `SequentialIdentifierGenerator.java`         | 69    | MAJOR              | Ja          | Rename "nextSequenceValue" which hides the field declared at line 32.                |
| `java:S112`       | `SequentialIdentifierGenerator.java`         | 108   | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S1066`      | `SequentialIdentifierGenerator.java`         | 113   | MAJOR              | Ja          | Merge this if statement with the enclosing one.                                      |
| `java:S112`       | `LocationBasedPrefixProvider.java`           | 30    | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S112`       | `RemoteIdentifierSourceProcessor.java`       | 55    | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S127`       | `SequentialIdGenProcessor.java`              | 75    | MAJOR              | Ja          | Refactor code to not assign loop counter from within the loop body.                  |
| `java:S1161`      | `AutoGenerationOptionEditor.java`            | 25    | MAJOR              | Ja          | Add the "@Override" annotation above this method signature.                          |
| `java:S1161`      | `IdentifierSourceEditor.java`                | 40    | MAJOR              | Ja          | Add the "@Override" annotation above this method signature.                          |
| `java:S112`       | `BaseIdentifierSourceService.java`           | 274   | MAJOR              | Ja          | Replace generic exception with specific library exception or custom exception.       |
| `java:S1118`      | `ExceptionUtils.java`                        | 6     | MAJOR              | Ja          | Add a private constructor to hide the implicit public one.                           |
| `java:S1161`      | `RemoteIdentifierSourceValidator.java`       | 27    | MAJOR              | Ja          | Add the "@Override" annotation above this method signature.                          |
| `java:S1161`      | `SequentialIdGenValidator.java`              | 34    | MAJOR              | Ja          | Add the "@Override" annotation above this method signature.                          |
| `java:S108`       | `DuplicateIdentifiersPoolTest.java`          | 75    | MAJOR              | Ja          | Remove block of code, fill it in, or add comment explaining why it is empty.         |
| `java:S2925`      | `DuplicateIdentifiersPoolTest.java`          | 85    | MAJOR              | Ja          | Remove this use of "Thread.sleep()".                                                 |
| `java:S2925`      | `IdentifierPoolSchedulerIT.java`             | 58    | MAJOR              | Ja          | Remove this use of "Thread.sleep()".                                                 |
| `java:S1066`      | `IdentifierSourceResource.java`              | 220   | MAJOR              | Ja          | Merge this if statement with the enclosing one.                                      |
| `java:S1134`      | `IdentifierSourceResource.java`              | 535   | MAJOR              | Ja          | Take the required action to fix the issue indicated by the TODO comment.             |
| `java:S2293`      | `BaseIdentifierSource.java`                  | 257   | MINOR              | Ja          | Replace type specification with diamond operator (`<>`).                           |
| `java:S2293`      | `IdentifierPool.java`                        | 46    | MINOR              | Ja          | Replace type specification with diamond operator (`<>`).                           |
| `java:S2293`      | `IdgenUtil.java`                             | 78    | MINOR              | Ja          | Replace type specification with diamond operator (`<>`).                           |
| `java:S1874`      | `SequentialIdentifierGenerator.java`         | 104   | MINOR              | Ja          | Remove this use of "newInstance"; it is deprecated.                                  |
| `java:S2293`      | `AutoGenerationOptionResource.java`          | 115   | MINOR              | Ja          | Replace type specification with diamond operator (`<>`).                           |
| `java:S2293`      | `IdentifierSourceResource.java`              | 148   | MINOR              | Ja          | Replace type specification with diamond operator (`<>`).                           |
| `java:S4201`      | `AutoGenerationOption.java`                  | 64    | MINOR              | Ja          | Remove unnecessary null check; instanceof returns false for nulls.                   |
| `java:S1905`      | `IdgenUtil.java`                             | 38    | MINOR              | Ja          | Remove this unnecessary cast to "long".                                              |
| `java:S2160`      | `SequentialIdentifierGenerator.java`         | 29    | MINOR              | Ja          | Override the "equals" method in this class.                                          |
| `java:S1124`      | `LocationBasedPrefixProvider.java`           | 21    | MINOR              | Ja          | Reorder the modifiers to comply with the Java Language Specification.                |
| `java:S5411`      | `IdentifierPoolProcessor.java`               | 37    | MINOR              | Ja          | Use a primitive boolean expression here.                                             |
| `java:S1481`      | `IdentifierSourceServiceTest.java`           | 334   | MINOR              | Ja          | Remove this unused local variable.                                                   |
| `java:S1155`      | `AutoGenerationOptionResource.java`          | 136   | MINOR              | Ja          | Use isEmpty() to check whether the collection is empty or not.                       |
| `java:S3008`      | `LogEntrySearchHandlerTest.java`             | 26    | MINOR              | Ja          | Rename field to match regex ^[a-z][a-zA-Z0-9]*$.                                     |
| `java:S2143`      | `BaseIdentifierSource.java`                  | -     | INFO               | Ja          | Use the "java.time" API for date and time.                                           |
| `java:S2143`      | `IdentifierPool.java`                        | -     | INFO               | Ja          | Use the "java.time" API for date and time.                                           |
| `java:S2143`      | `LogEntry.java`                              | -     | INFO               | Ja          | Use the "java.time" API for date and time.                                           |
| `java:S2143`      | `PooledIdentifier.java`                      | -     | INFO               | Ja          | Use the "java.time" API for date and time.                                           |
| `java:S8692`      | `IdentifierSourceServiceTest.java`           | 340   | INFO               | Ja          | Do not use the system clock in tests.                                                |
| `java:S6541`      | `IdentifierSourceResource.java`              | 142   | INFO               | Ja          | A "Brain Method" was detected. Refactor to reduce metrics.                           |
| `java:S1135`      | `AutoGenerationOptionController.java`        | 110   | INFO               | Ja          | Complete the task associated to this TODO comment.                                   |



# Tweede Iteratie

## 🆕 Nieuwe Issues

Deze problemen zijn nieuw opgedoken in de laatste scan (aanmaakdatum 15 juni 2026).

| Issue (Sonar Regel) | Bestand | Regel | Severity | Gemitigeerd | Beschrijving |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `java:S1452` | `IdentifierSourceDAO.java` | 192 | **CRITICAL** | Nee | Remove usage of generic wildcard type. |
| `java:S1192` | `AutoGenerationOptionResource.java` | 264 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "#/definitions/IdgenIdentifiersourceGet" 3 times. |
| `java:S3776` | `IdentifierSourceResource.java` | 308 | **CRITICAL** | Nee | Refactor this method to reduce its Cognitive Complexity from 16 to the 15 allowed. |
| `java:S2886` | `LocationBasedPrefixProvider.java` | 58 | MAJOR | Nee | Synchronize this method to match the synchronization on "setPrefixLocationAttributeType". |
| `java:S2886` | `LocationBasedSuffixProvider.java` | 58 | MAJOR | Nee | Synchronize this method to match the synchronization on "setSuffixLocationAttributeType". |
| `java:S2293` | `BaseIdentifierSourceService.java` | 60 | MINOR | Nee | Replace type specification with diamond operator (`<>`). |

## ⚠️ Bestaande Issues (Selectie van belangrijkste)

Deze problemen waren al aanwezig in eerdere scans. Let vooral op de CRITICAL string-duplicaties en de extreem lange (Brain) methodes voor je refactoring-Proof of Concept.

| Issue (Sonar Regel) | Bestand | Regel | Severity | Gemitigeerd | Beschrijving |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `java:S1192` | `IdentifierResource.java` | 47 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "comment" 4 times. |
| `java:S1192` | `IdentifierSourceResource.java` | 75 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "description" 9 times. |
| `java:S1192` | `IdentifierSourceResource.java` | 76 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "identifierType" 6 times. |
| `java:S1192` | `IdentifierSourceResource.java` | 214 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "sourceUuid" 3 times. |
| `java:S1192` | `IdentifierSourceResource.java` | 310 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "batchSize" 3 times. |
| `java:S3776` | `IdentifierSourceResource.java` | 349 | **CRITICAL** | Nee | Refactor this method to reduce its Cognitive Complexity from 94 to the 15 allowed. |
| `java:S1192` | `LogEntryResource.java` | 69 | **CRITICAL** | Nee | Define constant instead of duplicating literal "generatedBy" 3 times. |
| `java:S1192` | `IdentifierPoolResourceHandler.java` | 118 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "sequential" 3 times. |
| `java:S1192` | `IdentifierPoolResourceHandler.java` | 119 | **CRITICAL** | Nee | Define constant instead of duplicating literal "refillWithScheduledTask" 3 times. |
| `java:S1192` | `IdentifierPoolResourceHandler.java` | 120 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "source" 3 times. |
| `java:S1192` | `IdentifierPoolResourceHandler.java` | 121 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "batchSize" 3 times. |
| `java:S1192` | `IdentifierPoolResourceHandler.java` | 122 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "minPoolSize" 3 times. |
| `java:S1192` | `RemoteIdentifierSourceResourceHandler.java` | 64 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "identifierType" 3 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 63 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "prefix" 6 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 64 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "suffix" 6 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 65 | **CRITICAL** | Nee | Define constant instead of duplicating literal "firstIdentifierBase" 6 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 66 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "minLength" 6 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 68 | **CRITICAL** | Nee | Define a constant instead of duplicating literal "identifierType" 3 times. |
| `java:S1192` | `SequentialIdentifierGeneratorResourceHandler.java` | 76 | **CRITICAL** | Nee | Define constant instead of duplicating literal "nextSequenceValue" 5 times. |
| `java:S1186` | `AutoGenerationOptionController.java` | 42 | **CRITICAL** | Nee | Add comment explaining why method is empty, or throw UnsupportedOperationException. |
| `java:S1186` | `IdentifierSourceController.java` | 67 | **CRITICAL** | Nee | Add comment explaining why method is empty, or throw UnsupportedOperationException. |
| `java:S1192` | `IdentifierSourceController.java` | 267 | **CRITICAL** | Nee | Define constant instead of duplicating literal "redirect:/module/idgen..." 3 times. |
| `java:S3776` | `IdgenEditPatientIdentifiersController.java` | 46 | **CRITICAL** | Nee | Refactor method to reduce Cognitive Complexity from 16 to the 15 allowed. |
| `java:S1192` | `IdentifierTableHeaderExtension.java` | 68 | **CRITICAL** | Nee | Define a constant instead of duplicating literal `<script src=\"` 3 times. |
| `java:S1066` | `SequentialIdentifierGeneratorValidator.java` | 87 | MAJOR | Nee | Merge this if statement with the enclosing one. |
| `java:S1066` | `SequentialIdentifierGeneratorValidator.java` | 92 | MAJOR | Nee | Merge this if statement with the enclosing one. |
| `java:S2925` | `IdgenTaskIT.java` | 50 | MAJOR | Nee | Remove this use of "Thread.sleep()". |
| `java:S1134` | `IdentifierPoolResourceHandler.java` | 195 | MAJOR | Nee | Take the required action to fix the issue indicated by this TODO comment. |
| `java:S112`  | `IdentifierSourceController.java` | 97 | MAJOR | Nee | Replace generic exception with specific library exception or custom exception. |
| `java:S1117` | `IdentifierSourceController.java` | 119 | MAJOR | Nee | Rename "iss" which hides the field declared at line 59. |
| `java:S1141` | `IdentifierSourceController.java` | 245 | MAJOR | Nee | Extract this nested try block into a separate method. |
| `java:S112`  | `IdentifierSourceController.java` | 255 | MAJOR | Nee | Replace generic exception with specific library exception or custom exception. |
| `java:S112`  | `IdentifierSourceController.java` | 260 | MAJOR | Nee | Replace generic exception with specific library exception or custom exception. |
| `java:S1066` | `IdentifierSourceResource.java` | 366 | MAJOR | Nee | Merge this if statement with the enclosing one. |
| `java:S2293` | `BaseIdentifierSourceService.java` | 65 | MINOR | Nee | Replace type specification with diamond operator (`<>`). |
| `java:S1155` | `AutoGenerationOptionResource.java` | 143 | MINOR | Nee | Use isEmpty() to check whether the collection is empty or not. |
| `java:S3008` | `LogEntryControllerTest.java` | 23 | MINOR | Nee | Rename field to match regex `^[a-z][a-zA-Z0-9]*$`. |
| `java:S2143` | `BaseIdentifierSource.java` | - | INFO | Nee | Use the "java.time" API for date and time. |
| `java:S6541` | `IdentifierSourceResource.java` | 349 | INFO | Nee | A "Brain Method" was detected. Refactor to reduce metrics. |