public class UrlValidator implements Serializable {
    public boolean isValid(String value) { // definition of a
        if (value == null) {
            return false;
        }

        URI uri; // ensure value is a valid URI
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            return false;
        }
        // OK, perfom additional validation

        String scheme = uri.getScheme();
        if (!isValidScheme(scheme)) {
            return false;
        }

        String authority = uri.getRawAuthority();
        if ("file".equals(scheme) && (authority == null || "".equals(authority))) {// Special case - file: allows an empty authority
            return true; // this is a local file - nothing more to do here
        }
        if ("file".equals(scheme) && authority != null && authority.contains(":")) {
            return false;
        }
        // Validate the authority
        if (!isValidAuthority(authority)) {
            return false;
        }

        if (!isValidPath(uri.getRawPath())) {
            return false;
        }

        if (!isValidQuery(uri.getRawQuery())) { // call to b
            return false;
        }

        if (!isValidFragment(uri.getRawFragment())) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the query is null or it's a properly formatted query string.
     * @param query Query value to validate.
     * @return true if query is valid.
     */
    protected boolean isValidQuery(String query) { // definition of b
        if (query == null) {
            return true;
        }

        return QUERY_PATTERN.matcher(query).matches();
    }
}

public class UrlValidatorTest {
   @Test
   public void testValidator202() {
       String[] schemes = {"http","https"};
       UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.NO_FRAGMENTS);
       assertTrue(urlValidator.isValid("http://l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.l.org")); // calls a and b
   }

}
