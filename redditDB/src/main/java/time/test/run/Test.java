package time.test.run;

/**
 * <p>Records a test.</p>
 *
 * <p>
 *     <b>'testType'</b> denotes if the test was using the Unconstrained tables, the constrained tables,
 *     or if it staged to the unconstrained tables first and then moved them to the constrained tables.
 * </p>
 * <p>
 *     <b>'timeTakenMicroseconds' records how many microseconds the test took.</b>
 * </p>
 * <p>
 *     <b>'numberOfRecords'</b> shows how many records were added during the test.
 * </p>
 *
 */
public record Test(TestType testType, int timeTakenMicroseconds, int numberOfRecords) {
    public enum TestType {
        UNCONSTRAINED, CONSTRAINED, PRESTAGED
    }
}
