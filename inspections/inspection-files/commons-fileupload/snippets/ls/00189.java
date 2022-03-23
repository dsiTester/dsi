public class ServletFileUpload extends FileUpload {
    /**
     * Constructs an uninitialised instance of this class. A factory must be
     * configured, using <code>setFileItemFactory()</code>, before attempting
     * to parse requests.
     *
     * @see FileUpload#FileUpload(FileItemFactory)
     */
    public ServletFileUpload() {
        super();
    }
}

public class FileUpload extends FileUploadBase {
    @Override
    public void setFileItemFactory(FileItemFactory factory) { // called definition of a
        this.fileItemFactory = factory;
    }
}

public class FileUploadBase {

    protected String getFieldName(FileItemHeaders headers) { // definition of b
        return getFieldName(headers.getHeader(CONTENT_DISPOSITION));
    }

    private String getFieldName(String pContentDisposition) { // called from b
        String fieldName = null;
        if (pContentDisposition != null
                && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
            ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            // Parameter parser can handle null input
            Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }

    public List<FileItem> parseRequest(RequestContext ctx)
        throws FileUploadException { // called from StreamingTest
        List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            FileItemIterator iter = getItemIterator(ctx); // calls b
            FileItemFactory fac = getFileItemFactory(); // retrieves value set by a
            final byte[] buffer = new byte[Streams.DEFAULT_BUFFER_SIZE];
            if (fac == null) {
                throw new NullPointerException("No FileItemFactory has been set."); // if state was not restored, this exception would have been thrown
            }
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

}

private class FileItemIteratorImpl {
    FileItemIteratorImpl(RequestContext ctx)
                throws FileUploadException, IOException {
        ...
        findNextItem(); // calls b
    }

    private boolean findNextItem() throws IOException { // calls b
        ...
        for (;;) {
            ...
            FileItemHeaders headers = getParsedHeaders(multi.readHeaders());
            if (currentFieldName == null) {
                // We're parsing the outer multipart
                String fieldName = getFieldName(headers); // call to b
                if (fieldName != null) {
                        ...
                }
            }
        }
    }
}

public class StreamingTest {
    public void testFILEUPLOAD135()
            throws IOException, FileUploadException {
        byte[] request = newShortRequest();
        final ByteArrayInputStream bais = new ByteArrayInputStream(request);
        List<FileItem> fileItems = parseUpload(new InputStream() {
            @Override
            public int read()
            throws IOException
            {
                return bais.read();
            }
            @Override
            public int read(byte b[], int off, int len) throws IOException
            {
                return bais.read(b, off, Math.min(len, 3));
            }

        }, request.length); // calls a and b
        Iterator<FileItem> fileIter = fileItems.iterator();
        assertTrue(fileIter.hasNext());
        FileItem item = fileIter.next();
        assertEquals("field", item.getFieldName());
        byte[] bytes = item.get();
        assertEquals(3, bytes.length);
        assertEquals((byte)'1', bytes[0]);
        assertEquals((byte)'2', bytes[1]);
        assertEquals((byte)'3', bytes[2]);
        assertTrue(!fileIter.hasNext());
    }

    private List<FileItem> parseUpload(InputStream pStream, int pLength)
            throws FileUploadException {
        String contentType = "multipart/form-data; boundary=---1234";

        FileUploadBase upload = new ServletFileUpload();
        upload.setFileItemFactory(new DiskFileItemFactory()); // call to a
        HttpServletRequest request = new MockHttpServletRequest(pStream,
                pLength, contentType);

        List<FileItem> fileItems = upload.parseRequest(new ServletRequestContext(request)); // calls b
        return fileItems;
    }
}
