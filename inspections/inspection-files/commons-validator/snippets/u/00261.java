public class ValidatorAction implements Serializable {

    /**
     * Gets the name of the validator action.
     * @return Validator Action name.
     */
    public String getName() {   // definition of a
        return name;
    }

    /**
     * Returns the ClassLoader set in the Validator contained in the parameter
     * Map.
     */
    private ClassLoader getClassLoader(Map<String, Object> params) { // definition of b
        Validator v = (Validator) params.get(Validator.VALIDATOR_PARAM);
        return v.getClassLoader();
    }

    boolean executeValidationMethod(
        Field field,
        // TODO What is this the correct value type?
        // both ValidatorAction and Validator are added as parameters
        Map<String, Object> params,
        ValidatorResults results,
        int pos)
        throws ValidatorException { // called from Field.validateForRule()

        params.put(Validator.VALIDATOR_ACTION_PARAM, this);

        try {
            if (this.validationMethod == null) {
                synchronized(this) {
                    ClassLoader loader = this.getClassLoader(params); // call to b
                    this.loadValidationClass(loader);
                    this.loadParameterClasses(loader);
                    this.loadValidationMethod();
                }
            }
            ...
        } ...

        return true;
    }

}

public class ValidatorResources implements Serializable {
    public void addValidatorAction(ValidatorAction va) { // called from test
        va.init();

        getActions().put(va.getName(), va); // call to a

        if (getLog().isDebugEnabled()) {
            getLog().debug("Add ValidatorAction: " + va.getName() + "," + va.getClassname());
        }
    }

}

public class Field implements Cloneable, Serializable {

    public ValidatorResults validate(Map<String, Object> params, Map<String, ValidatorAction> actions)
        throws ValidatorException { // indirectly called from Validator.validate()
        ...
        for (int fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; fieldNumber++) {

            ValidatorResults results = new ValidatorResults();
            synchronized(dependencyList) {
                Iterator<String> dependencies = this.dependencyList.iterator();
                while (dependencies.hasNext()) {
                    String depend = dependencies.next();

                    ValidatorAction action = actions.get(depend); // this will return null in the DSI experiment because the key was set to null, instead of the String value depend.
                    if (action == null) {
                        this.handleMissingAction(depend); // throws exception
                    }

                    boolean good =
                        validateForRule(action, results, actions, params, fieldNumber); // calls b
                    ...
                }
                ...
            }
            ...
        }
    }

    private void handleMissingAction(String name) throws ValidatorException {
        throw new ValidatorException("No ValidatorAction named " + name // this exception thrown
                + " found for field " + this.getProperty());
    }

    private boolean validateForRule(
        ValidatorAction va,
        ValidatorResults results,
        Map<String, ValidatorAction> actions,
        Map<String, Object> params,
        int pos)
        throws ValidatorException { // called from Field.validate()

        ValidatorResult result = results.getValidatorResult(this.getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }

        if (!this.runDependentValidators(va, results, actions, params, pos)) {
            return false;
        }

        return va.executeValidationMethod(this, params, results, pos); // calls b
    }

}

public class ValidatorTest extends TestCase {
   public void testOnlyValidateField() throws ValidatorException {
        //     property name of the method we are validating
        String property = "date";
        // name of ValidatorAction
        String action = "date";
        validatorresources resources = setupDateResources(property, action); // calls a

        TestBean bean = new TestBean();
        bean.setDate("2/3/1999");

        Validator validator = new Validator(resources, "testForm", property);
        validator.setParameter(Validator.BEAN_PARAM, bean);

        ValidatorResults results = validator.validate(); // calls b

        assertNotNull(results);

        // Field passed and should be in results
        assertTrue(results.getPropertyNames().contains(property));
   }

    private ValidatorResources setupDateResources(String property, String action) {

        ValidatorResources resources = new ValidatorResources();

        ValidatorAction va = new ValidatorAction();
        va.setName(action);
        va.setClassname("org.apache.commons.validator.ValidatorTest");
        va.setMethod("formatDate");
        va.setMethodParams("java.lang.Object,org.apache.commons.validator.Field");

        ...
        resources.addValidatorAction(va); // calls a
        resources.addFormSet(fs);
        resources.process();

        return resources;
    }

}
