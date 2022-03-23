public class QCodec extends RFC1522Codec implements StringEncoder, StringDecoder {
    @Override
    public Object encode(final Object obj) throws EncoderException { // called from test
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return encode((String) obj); // calls a
        }
        throw new EncoderException("Objects of type " +
              obj.getClass().getName() +
              " cannot be encoded using Q codec");
    }

    @Override
    public String encode(final String sourceStr) throws EncoderException {
        if (sourceStr == null) {
            return null;
        }
        return encode(sourceStr, getCharset()); // call to a
    }

    /**
     * Encodes a string into its quoted-printable form using the specified Charset. Unsafe characters are escaped.
     *
     * @param sourceStr
     *            string to convert to quoted-printable form
     * @param sourceCharset
     *            the Charset for sourceStr
     * @return quoted-printable string
     * @throws EncoderException
     *             thrown if a failure condition is encountered during the encoding process.
     * @since 1.7
     */
    public String encode(final String sourceStr, final Charset sourceCharset) throws EncoderException { // definition of a
        if (sourceStr == null) {
            return null;
        }
        return encodeText(sourceStr, sourceCharset); // calls b
    }

    @Override
    protected String getEncoding() { // used implementation of b
        return "Q";
    }

}

abstract class RFC1522Codec {

    /**
     * Returns the codec name (referred to as encoding in the RFC 1522).
     *
     * @return name of the codec
     */
    protected abstract String getEncoding(); // b

    protected String encodeText(final String text, final Charset charset) throws EncoderException {
        if (text == null) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(PREFIX);
        buffer.append(charset);
        buffer.append(SEP);
        buffer.append(this.getEncoding()); // call to b
        buffer.append(SEP);
        buffer.append(StringUtils.newStringUsAscii(this.doEncoding(text.getBytes(charset))));
        buffer.append(POSTFIX);
        return buffer.toString();
    }
}

public class QCodecTest {
    @Test
    public void testEncodeObjects() throws Exception {
        final QCodec qcodec = new QCodec();
        final String plain = "1+1 = 2";
        final String encoded = (String) qcodec.encode((Object) plain); // calls a and b
        assertEquals("Basic Q encoding test",
            "=?UTF-8?Q?1+1 =3D 2?=", encoded); // assertion fails here

        final Object result = qcodec.encode((Object) null);
        assertEquals( "Encoding a null Object should return null", null, result);

        try {
            final Object dObj = Double.valueOf(3.0d);
            qcodec.encode( dObj );
            fail( "Trying to url encode a Double object should cause an exception.");
        } catch (final EncoderException ee) {
            // Exception expected, test segment passes.
        }
    }
}
