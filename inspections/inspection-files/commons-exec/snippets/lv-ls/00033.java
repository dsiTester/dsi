public class CommandLine {
    /**
     * Cleans the executable string. The argument is trimmed and '/' and '\\' are
     * replaced with the platform specific file separator char
     *
     * @param dirtyExecutable the executable
     * @return the platform-specific executable string
     */
    private String toCleanExecutable(final String dirtyExecutable) { // definition of a
        if (dirtyExecutable == null) {
            throw new IllegalArgumentException("Executable can not be null");
        }
        if (dirtyExecutable.trim().isEmpty()) {
            throw new IllegalArgumentException("Executable can not be empty");
        }
        return StringUtils.fixFileSeparatorChar(dirtyExecutable);
    }

    /**
     * Add a single argument.
     *
     * @param argument The argument to add
     * @param handleQuoting Add the argument with/without handling quoting
     * @return The command line itself
     */
    public CommandLine addArgument(final String argument, final boolean handleQuoting) { // definition of b

       if (argument == null)
       {
           return this;
       }

       // check if we can really quote the argument - if not throw an
       // IllegalArgumentException
       if (handleQuoting)
       {
           StringUtils.quoteArgument(argument);
       }

       arguments.add(new Argument(argument, handleQuoting));
       return this;
   }

    public CommandLine(final String executable) { // called from test
        this.isFile=false;
        this.executable=toCleanExecutable(executable); // call to a
    }

    public CommandLine addArgument(final String argument) { // called from test
        return this.addArgument(argument, true);
    }
}

public class CommandLineTest {
    @Test
    public void testAddArgumentWithBothQuotes() { // validating test
        final CommandLine cmdl = new CommandLine("test"); // calls a

        try {
            cmdl.addArgument("b\"a'r"); // calls b; throws expected exception
            fail("IllegalArgumentException should be thrown");
        } catch (final IllegalArgumentException e) {
            // OK, expected
        }
    }

    @Test
    public void testAddNullArgument() {
        final CommandLine cmdl = new CommandLine("test"); // calls a

        cmdl.addArgument(null); // calls b
        assertEquals("[test]", cmdl.toString());
        assertArrayEquals(new String[]{"test"}, cmdl.toStrings());
    }

}
