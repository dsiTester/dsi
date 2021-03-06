public class PureJavaCrc32C {

  @Override
  public void update(final byte[] b, int off, int len) { // definition of a?
    int localCrc = crc;

    while(len > 7) {
      final int c0 =(b[off+0] ^ localCrc) & 0xff;
      final int c1 =(b[off+1] ^ (localCrc >>>= 8)) & 0xff;
      final int c2 =(b[off+2] ^ (localCrc >>>= 8)) & 0xff;
      final int c3 =(b[off+3] ^ (localCrc >>>= 8)) & 0xff;
      localCrc = (T[T8_7_start + c0] ^ T[T8_6_start + c1]) ^
                 (T[T8_5_start + c2] ^ T[T8_4_start + c3]);

      final int c4 = b[off+4] & 0xff;
      final int c5 = b[off+5] & 0xff;
      final int c6 = b[off+6] & 0xff;
      final int c7 = b[off+7] & 0xff;

      localCrc ^= (T[T8_3_start + c4] ^ T[T8_2_start + c5]) ^
                  (T[T8_1_start + c6] ^ T[T8_0_start + c7]);

      off += 8;
      len -= 8;
    }

    /* loop unroll - duff's device style */
    switch(len) {
      case 7: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 6: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 5: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 4: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 3: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 2: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      case 1: localCrc = (localCrc >>> 8) ^ T[T8_0_start + ((localCrc ^ b[off++]) & 0xff)];
      default:
        break; // satisfy Findbugs
    }

    // Publish crc out to object
    crc = localCrc;
  }

  @Override
  public long getValue() {      // definition of b?
    final long ret = crc;
    return (~ret) & 0xffffffffL;
  }
}

public class PureJavaCrc32CTest {
    @Test
    public void testOnes() {
        Arrays.fill(data, (byte) 0xFF);
        check(0x62a8ab43); // 43 ab a8 62
    }

    // Using int because only want 32 bits
    private void check(final int expected) {
        crc.reset();
        crc.update(data, 0, data.length); // call to a?
        final int actual = (int) crc.getValue(); // call to b?
        Assert.assertEquals(Integer.toHexString(expected), Integer.toHexString(actual));
    }
}
