# Acceptance test data

The Gherkin formatter these uses features for acceptance testing. Please add new
samples **only** to this set, not any of the others.

Should there be changes, the Java tests can be used to update the  expected
data.

```shell
cd java
UPDATE_EXPECTED_FILES=true mvn test
```
