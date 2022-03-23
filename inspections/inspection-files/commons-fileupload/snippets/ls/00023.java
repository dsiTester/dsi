public class DiskFileUploadTest {
    @Test
    public void testMoveFile() throws Exception {
        DiskFileUpload myUpload = new DiskFileUpload();
        myUpload.setSizeThreshold(0); // call to a
    	final String content = ...;
    	final byte[] contentBytes = content.getBytes("US-ASCII");
        final HttpServletRequest request = new MockHttpServletRequest(contentBytes, Constants.CONTENT_TYPE);
        final List<FileItem> items = myUpload.parseRequest(request); // calls b
        assertNotNull(items);
        ...
    }
}

public class DiskFileUpload {
    @Deprecated
    public void setSizeThreshold(int sizeThreshold) { // definition of a
        fileItemFactory.setSizeThreshold(sizeThreshold); // calls DiskFileItemFactory.setSizeThreshold()
    }
}

public class FileUploadBase {
    public List<FileItem> parseRequest(RequestContext ctx)
            throws FileUploadException {
        try {
            FileItemIterator iter = getItemIterator(ctx); // calls b
            ...
        }
        ...
    }

    public FileItemIterator getItemIterator(RequestContext ctx)
    throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(ctx); // calls b
        } ...
    }

    FileItemIteratorImpl(RequestContext ctx)
                throws FileUploadException, IOException {
        ...
        findNextItem(); // calls b
    }

    private boolean findNextItem() throws IOException {
        ...
        if (currentFieldName == null) {
            ...
            String fileName = getFileName(headers); // call to b
            ...
        }
        ...
    }

    protected String getFileName(FileItemHeaders headers) { // definiton of b
        return getFileName(headers.getHeader(CONTENT_DISPOSITION));
    }

}
