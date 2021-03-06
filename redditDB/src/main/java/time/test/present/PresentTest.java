package time.test.present;

import time.test.run.Test;

public final class PresentTest {
    /**
     * Prints the results of a test in markdown format.
     * @param test The test to print
     * @param testNumber The number of the test
     * @return A markdown string presenting test results.
     */
    public static String getTestString(Test test, int testNumber) {
        String typeString;
        float seconds = test.timeTakenNanoseconds() / 1000000000f;
        switch (test.testType()) {
            case UNCONSTRAINED -> typeString = "Test with unconstrained tables";
            case CONSTRAINED -> typeString = "Test with constrained tables";
            case PRESTAGED -> typeString = "Test using staging to unconstrained tables";
            default -> typeString = "Impossible!";
        }
        typeString += " " + testNumber;
        return
"""
## %1$s
                
%2$d records were inserted in %3$d nanoseconds (%4$f seconds).
                
""".formatted(typeString, test.numberOfRecords(), test.timeTakenNanoseconds(), seconds);
    }
}
