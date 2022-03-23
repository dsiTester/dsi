public class BaseNCodecOutputStream extends FilterOutputStream {
    // a is defined in FilterOutputStream, a third party class

    /**
     * Writes EOF.
     *
     * @throws IOException
     *             if an I/O error occurs.
     * @since 1.11
     */
    public void eof() throws IOException { // definition of b; not defined in Base32OutputStream
        // Notify encoder of EOF (-1).
        if (doEncode) {
            baseNCodec.encode(singleByte, 0, EOF, context);
        } else {
            baseNCodec.decode(singleByte, 0, EOF, context);
        }
    }

    @Override
    public void close() throws IOException { // called from test
        eof(); // call to b
        flush();
        out.close();
    }

}

public class Base32InputStreamTest {
    /**
     * Tests the problem reported in CODEC-130. Missing / wrong implementation of skip.
     */
    @Test
    public void testCodec130() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final Base32OutputStream base32os = new Base32OutputStream(bos)) {
            base32os.write(StringUtils.getBytesUtf8(STRING_FIXTURE)); // call to a
        } // implicitly calls b via try-with-resources

        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        final Base32InputStream ins = new Base32InputStream(bis);

        // we skip the first character read from the reader
        ins.skip(1);
        final byte[] decodedBytes = BaseNTestData.streamToBytes(ins, new byte[64]);
        final String str = StringUtils.newStringUtf8(decodedBytes);

        assertEquals(STRING_FIXTURE.substring(1), str); // assertion fails here
    }

}
