public class FileUploadBase {
    public List<FileItem> parseRequest(RequestContext ctx)
            throws FileUploadException { // called from ServletFileUpload.parseRequest()
        List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            FileItemIterator iter = getItemIterator(ctx); // calls a and b
            ...
        }
        ...
    }

    public FileItemIterator getItemIterator(RequestContext ctx)
    throws FileUploadException, IOException { // called from above
        try {
            return new FileItemIteratorImpl(ctx); // calls a and b
        } catch (FileUploadIOException e) {
            // unwrap encapsulated SizeException
            throw (FileUploadException) e.getCause();
        }
    }

    private class FileItemIteratorImpl implements FileItemIterator {
        FileItemIteratorImpl(RequestContext ctx)
            throws FileUploadException, IOException {
            ...
            findNextItem(); // calls a and b
        }

        private boolean findNextItem() throws IOException { // calls a and b
            for (;;) {
                ...
                FileItemHeaders headers = getParsedHeaders(multi.readHeaders()); // call to a; calls b
                if (currentFieldName == null) {
                    ...
                }
                ...
            }

    }

    protected FileItemHeaders getParsedHeaders(String headerPart) { // definition of a
        final int len = headerPart.length();
        FileItemHeadersImpl headers = newFileItemHeaders();
        int start = 0;
        for (;;) {
            int end = parseEndOfLine(headerPart, start);
            if (start == end) {
                break;
            }
            StringBuilder header = new StringBuilder(headerPart.substring(start, end));
            start = end + 2;
            while (start < len) {
                int nonWs = start;
                while (nonWs < len) {
                    char c = headerPart.charAt(nonWs);
                    if (c != ' '  &&  c != '\t') {
                        break;
                    }
                    ++nonWs;
                }
                if (nonWs == start) {
                    break;
                }
                // Continuation line found
                end = parseEndOfLine(headerPart, nonWs);
                header.append(" ").append(headerPart.substring(nonWs, end));
                start = end + 2;
            }
            parseHeaderLine(headers, header.toString()); // call to b
        }
        return headers;
    }

    private void parseHeaderLine(FileItemHeadersImpl headers, String header) { // definition of b
        final int colonOffset = header.indexOf(':');
        if (colonOffset == -1) {
            // This header line is malformed, skip it.
            return;
        }
        String headerName = header.substring(0, colonOffset).trim();
        String headerValue =
            header.substring(header.indexOf(':') + 1).trim();
        headers.addHeader(headerName, headerValue);
    }

}

public class ServletFileUpload {
    @Override
    public List<FileItem> parseRequest(HttpServletRequest request) // called from test
    throws FileUploadException {
        return parseRequest(new ServletRequestContext(request)); // calls a and b
    }
}

public class SizesTest {
    @Test
    public void testFileSizeLimit()
            throws IOException, FileUploadException {
        final String request =
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"foo.tab\"\r\n" +
            "Content-Type: text/whatever\r\n" +
            "\r\n" +
            "This is the content of the file\n" +
            "\r\n" +
            "-----1234--\r\n";

        ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
        ...
        HttpServletRequest req = new MockHttpServletRequest(
                request.getBytes("US-ASCII"), Constants.CONTENT_TYPE);
        List<FileItem> fileItems = upload.parseRequest(req); // calls a and b
        ...

        upload = new ServletFileUpload(new DiskFileItemFactory());
        ...
        req = new MockHttpServletRequest(request.getBytes("US-ASCII"), Constants.CONTENT_TYPE);
        fileItems = upload.parseRequest(req);
        ...

        upload = new ServletFileUpload(new DiskFileItemFactory());
        ...
        req = new MockHttpServletRequest(request.getBytes("US-ASCII"), Constants.CONTENT_TYPE);
        try {
            upload.parseRequest(req); // calls a and b
            fail("Expected exception.");
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            assertEquals(30, e.getPermittedSize());
        }
    }
}
