public class ParameterParser {
    public Map<String, String> parse(final String str, char[] separators) { // definition of a
        if (separators == null || separators.length == 0) {
            return new HashMap<String, String>();
        }
        char separator = separators[0];
        if (str != null) {
            int idx = str.length();
            for (char separator2 : separators) {
                int tmp = str.indexOf(separator2);
                if (tmp != -1 && tmp < idx) {
                    idx = tmp;
                    separator = separator2;
                }
            }
        }
        return parse(str, separator); // call to b
    }

    public Map<String, String> parse(final String str, char separator) { // definition of b
        if (str == null) {
            return new HashMap<String, String>();
        }
        return parse(str.toCharArray(), separator);
    }
}

public class FileUploadBase {

    @Deprecated
    public List<FileItem> parseRequest(HttpServletRequest req)
    throws FileUploadException { // called from test
        return parseRequest(new ServletRequestContext(req));
    }

    public List<FileItem> parseRequest(RequestContext ctx)
        throws FileUploadException {
        List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            FileItemIterator iter = getItemIterator(ctx); // calls a and b
            ...
        }
        ...
     }

    public FileItemIterator getItemIterator(RequestContext ctx)
    throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(ctx); // calls b
        } catch (FileUploadIOException e) {
            // unwrap encapsulated SizeException
            throw (FileUploadException) e.getCause();
        }
    }

    private class FileItemIteratorImpl implements FileItemIterator {
        FileItemIteratorImpl(RequestContext ctx)
            throws FileUploadException, IOException {
            ...
            boundary = getBoundary(contentType); // calls a
            if (boundary == null) {
                IOUtils.closeQuietly(input); // avoid possible resource leak
                throw new FileUploadException("the request was rejected because no multipart boundary was found");
            }
            ...
        }

    }

    protected byte[] getBoundary(String contentType) { // called from FileItemIteratorImpl()
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<String, String> params = parser.parse(contentType, new char[] {';', ','}); // call to a
        String boundaryStr = params.get("boundary"); // NullPointerException here

        ...
        return boundary;
    }

}

public class DiskFileUploadTest {
    @Test
    public void testWithInvalidRequest() {
        HttpServletRequest req = HttpServletRequestFactory.createInvalidHttpServletRequest();

        try {
            upload.parseRequest(req); // calls a and b
            fail("testWithInvalidRequest: expected exception was not thrown");
        } catch (FileUploadException expected) {
            // this exception is expected
        }
    }
}
