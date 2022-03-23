public class DomainValidator implements Serializable {
    public boolean isValid(String domain) { // called from test
        if (domain == null) {
            return false;
        }
        domain = unicodeToASCII(domain);
        // hosts must be equally reachable via punycode and Unicode
        // Unicode is never shorter than punycode, so check punycode
        // if domain did not convert, then it will be caught by ASCII
        // checks in the regexes below
        if (domain.length() > MAX_DOMAIN_LENGTH) {
            return false;
        }
        String[] groups = domainRegex.match(domain);
        if (groups != null && groups.length > 0) {
            return isValidTld(groups[0]); // calls a and b
        }
        return allowLocal && hostnameRegex.isValid(domain);
    }

    public boolean isValidTld(String tld) { // called from EmailValidator.isValidDomain() in invalidated test; called from a in unknown test
        if(allowLocal && isValidLocalTld(tld)) {
            return true;
        }
        return isValidInfrastructureTld(tld) // call to a
                || isValidGenericTld(tld)    // call to b
                || isValidCountryCodeTld(tld);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined infrastructure top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param iTld the parameter to check for infrastructure TLD status, not null
     * @return true if the parameter is an infrastructure TLD
     */
    public boolean isValidInfrastructureTld(String iTld) { // definition of a
        final String key = chompLeadingDot(unicodeToASCII(iTld).toLowerCase(Locale.ENGLISH));
        return arrayContains(INFRASTRUCTURE_TLDS, key);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined generic top-level domain. Leading dots are ignored
     * if present. The search is case-insensitive.
     * @param gTld the parameter to check for generic TLD status, not null
     * @return true if the parameter is a generic TLD
     */
    public boolean isValidGenericTld(String gTld) { // definition of b
        final String key = chompLeadingDot(unicodeToASCII(gTld).toLowerCase(Locale.ENGLISH));
        return (arrayContains(GENERIC_TLDS, key) || arrayContains(mygenericTLDsPlus, key))
                && !arrayContains(mygenericTLDsMinus, key);
    }
}

public class DomainValidatorTest extends TestCase {
    public void testIDN() {     // unknown test
       assertTrue("b\u00fccher.ch in IDN should validate", validator.isValid("www.xn--bcher-kva.ch")); // calls a and b
    }
}
