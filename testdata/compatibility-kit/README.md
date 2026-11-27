# Acceptance test data

The Gherkin formatter uses the examples from the [Cucumber Compatibility Kit](https://github.com/cucumber/compatibility-kit)
for acceptance testing. These examples consist of `.feature` files created by
the CCK's reference implementation.

* The `.feature` files are copied in by running `npm install`.

We ensure the `.feature` files stay up to date by running `npm install` in CI
and verifying nothing changed. 

Should there be changes, please reformat the features in the CCK.
 
```shell
cd java
UPDATE_EXPECTED_FILES=true mvn test
```
