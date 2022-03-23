public abstract class BaseNCodec {
    /**
     * Tests a given byte array to see if it contains any characters within the alphabet or PAD.
     *
     * Intended for use in checking line-ending arrays
     *
     * @param arrayOctet
     *            byte array to test
     * @return {@code true} if any byte is a valid character in the alphabet or PAD; {@code false} otherwise
     */
    protected boolean containsAlphabetOrPad(final byte[] arrayOctet) { // definition of a; not defined in Base64
        if (arrayOctet == null) {
            return false;
        }
        for (final byte element : arrayOctet) {
            if (pad == element || isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters in the alphabet.
     *
     * @param pArray
     *            a byte array containing binary data
     * @return A byte array containing only the base N alphabetic character data
     */
    @Override
    public byte[] encode(final byte[] pArray) { // definition of b; not defined in Base64
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        return encode(pArray, 0, pArray.length);
    }

}

public class Base64 extends BaseNCodec {

    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked,
                                      final boolean urlSafe, final int maxResultSize) { // called from wrapper methods that are called by test
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        // Create this so can use the super-class method
        // Also ensures that the same roundings are performed by the ctor and the code
        final Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe); // calls a
        final long len = b64.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" +
                len +
                ") than the specified maximum size of " +
                maxResultSize);
        }

        return b64.encode(binaryData); // call to b
    }

    public Base64(final int lineLength, final byte[] lineSeparator, final boolean urlSafe,
                  final CodecPolicy decodingPolicy) {
        ...
        // @see test case Base64Test.testConstructors()
        if (lineSeparator != null) {
            if (containsAlphabetOrPad(lineSeparator)) { // call to a
                final String sep = StringUtils.newStringUtf8(lineSeparator);
                throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
            }
            ...
        } ...
    }

}

public class Base64Codec13Test {
    @Test
    public void testStaticEncode() throws EncoderException {
        for (int i = 0; i < STRINGS.length; i++) {
            if (STRINGS[i] != null) {
                final byte[] base64 = utf8(STRINGS[i]);
                final byte[] binary = BYTES[i];
                final boolean b = Arrays.equals(base64, Base64.encodeBase64(binary)); // calls a and b
                assertTrue("static Base64.encodeBase64() test-" + i, b);
            }
        }
    }
}
