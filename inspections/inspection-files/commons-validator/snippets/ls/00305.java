public class ValidatorAction implements Serializable {

    /**
     * @return true if the javascript for this action has already been loaded.
     */
    private boolean javascriptAlreadyLoaded() { // definition of a
        return (this.javascript != null);
    }

    /**
     * Read a javascript function from a file.
     * @param javascriptFileName The file containing the javascript.
     * @return The javascript function or null if it could not be loaded.
     */
    private String readJavascriptFile(String javascriptFileName) { // definition of b
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        InputStream is = classLoader.getResourceAsStream(javascriptFileName);
        if (is == null) {
            is = this.getClass().getResourceAsStream(javascriptFileName);
        }

        if (is == null) {
            getLog().debug("  Unable to read javascript name "+javascriptFileName);
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is)); // TODO encoding
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

        } catch(IOException e) {
            getLog().error("Error reading javascript file.", e);

        } finally {
            try {
                reader.close();
            } catch(IOException e) {
                getLog().error("Error closing stream to javascript file.", e);
            }
        }

        String function = buffer.toString();
        return function.equals("") ? null : function;
    }

    protected synchronized void loadJavascriptFunction() { // indirectly called from commons-digester/test (ValidatorResources.addValidatorAction())

        if (this.javascriptAlreadyLoaded()) { // call to a
            return;
        }

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading function begun");
        }

        if (this.jsFunction == null) {
            this.jsFunction = this.generateJsFunction();
        }

        String javascriptFileName = this.formatJavascriptFileName();

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading js function '" + javascriptFileName + "'");
        }

        this.javascript = this.readJavascriptFile(javascriptFileName); // call to b

        if (getLog().isTraceEnabled()) {
            getLog().trace("  Loading javascript function completed");
        }

    }

}

public class ValidatorTest extends TestCase {
   public void testOnlyValidateField() throws ValidatorException {
        //     property name of the method we are validating
        String property = "date";
        // name of ValidatorAction
        String action = "date";
        validatorresources resources = setupDateResources(property, action); // calls a and b

        TestBean bean = new TestBean();
        bean.setDate("2/3/1999");

        Validator validator = new Validator(resources, "testForm", property);
        validator.setParameter(Validator.BEAN_PARAM, bean);

        ValidatorResults results = validator.validate();

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
        resources.addValidatorAction(va); // calls a and b
        resources.addFormSet(fs);
        resources.process();

        return resources;
    }

}
