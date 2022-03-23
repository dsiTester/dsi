public class MatchRatingApproachEncoder {
    /**
     * Encodes a String using the Match Rating Approach (MRA) algorithm.
     *
     * @param name
     *            String object to encode
     * @return The MRA code corresponding to the String supplied
     */
    @Override
    public final String encode(String name) { // definition of a
        // Bulletproof for trivial input - NINO
        if (name == null || EMPTY.equalsIgnoreCase(name) || SPACE.equalsIgnoreCase(name) || name.length() == 1) {
            return EMPTY;
        }

        // Preprocessing
        name = cleanName(name);

        // BEGIN: Actual encoding part of the algorithm...
        // 1. Delete all vowels unless the vowel begins the word
        name = removeVowels(name); // call to b

        // 2. Remove second consonant from any double consonant
        name = removeDoubleConsonants(name);

        // 3. Reduce codex to 6 letters by joining the first 3 and last 3 letters
        name = getFirst3Last3(name);

        return name;
    }

    /**
     * Deletes all vowels unless the vowel begins the word.
     *
     * <h2>API Usage</h2>
     * <p>
     * Consider this method private, it is package protected for unit testing only.
     * </p>
     *
     * @param name
     *            The name to have vowels removed
     * @return De-voweled word
     */
    String removeVowels(String name) { // definition of b
        // Extract first letter
        final String firstLetter = name.substring(0, 1);

        name = name.replace("A", EMPTY);
        name = name.replace("E", EMPTY);
        name = name.replace("I", EMPTY);
        name = name.replace("O", EMPTY);
        name = name.replace("U", EMPTY);

        name = name.replaceAll("\\s{2,}\\b", SPACE);

        // return isVowel(firstLetter) ? (firstLetter + name) : name;
        if (isVowel(firstLetter)) {
            return firstLetter + name;
        }
        return name;
    }

}

public class MatchRatingApproachEncoderTest {

    @Test
    public final void testGetEncoding_HARPER_HRPR() { // invalidated test
        assertEquals("HRPR", this.getStringEncoder().encode("HARPER")); // call to a
    }

}
