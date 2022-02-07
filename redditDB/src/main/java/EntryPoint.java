import db.accessors.AddOperations;
import db.accessors.SQLAccessParams;
import db.accessors.SQLTableManager;
import db.accessors.SchemaOperations;
import files.writers.MarkDownWriter;
import io.github.cdimascio.dotenv.Dotenv;
import time.test.present.PresentTest;
import time.test.run.Test;
import time.test.run.Tester;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class EntryPoint {
    /**
     * <p>This function will either load the database tables once for use in the assignment
     * or it will load it several times with different SQL methods to test performance.</p>
     *
     * <p>
     *     <b>DO PASS --testOutput FLAG WITH BIG FILES</b>
     *     <p>The constrained tables loader is very bad and gets exponentially worse at big datasets.</p>
     * </p>
     *
     * <p>
     *     If only one argument is used on the function it will just load the table once for use.
     *     If two arguments are passed it will run the tests and output them to a markdown file for
     *     review.
     * </p>
     * @param args Each argument should be a file path. The first should be the path to
     *             the file containing the data for the database. The second, if desired,
     *             should have the path to the desired output file for the test results.
     * @throws SQLException If some sql stuff went wrong.
     * @throws IOException If something went wrong in reading from the source file or writing to the report.
     * @throws InterruptedException If something went wrong in the multithreading.
     */
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Dotenv env = Dotenv.load();
        SQLAccessParams params = new SQLAccessParams(
                env.get("SQL_URL"),
                env.get("SQL_UNAME"),
                env.get("SQL_PWORD")
        );


        // Start by setting the first source file
        assert args.length > 0;
        FileSet fileSet = parseArgs(args);
        List<String> srcFiles = fileSet.srcFilePaths();
        String resultFile = fileSet.testFilePath();
        assert srcFiles.size() > 0;
        assert fileSet.schemaName() != null && !fileSet.schemaName().equals("");

        final String schema = fileSet.schemaName();
        SchemaOperations.createSchema(params, schema);
        SQLTableManager.createUnconstrainedTables(params, schema);
        SQLTableManager.createConstrainedTables(params, schema);

        if (resultFile != null) {
            assert !resultFile.equals("");
            runTest(srcFiles, resultFile, params, schema); // Runs the loading tests
        } else {
            Tester tester = new Tester(params, srcFiles, schema);
            tester.stagingTest(); // This test will load the database using unconstrained tables as staging tables
            AddOperations.addOperations(params, schema);
        }
    }


    public static void runTest(List<String> dataSrcPaths, String reportFilePath, SQLAccessParams params, String schema) throws IOException, SQLException, InterruptedException {
        File report = new File(reportFilePath);
        Tester tester = dataSrcPaths.size() == 1 ?
                new Tester(params, dataSrcPaths.get(0), schema) : new Tester(params, dataSrcPaths, schema);
        MarkDownWriter writer = new MarkDownWriter(report);

        for (Test.TestType type : Test.TestType.values()) {
            for (int testNr = 1; testNr <= 4; testNr++) {
                String testString = PresentTest.getTestString(tester.run(type), testNr);
                SQLTableManager.clearTables(params, schema);
                writer.writeToFile(testString);
            }
        }
    }


    private static FileSet parseArgs(String[] args) {
        int argsLeft = args.length;
        boolean testFlag = false;
        boolean schemaFlag = false;
        String testFilePath = null;
        String schemaName = null;
        List<String> srcFilePaths = new LinkedList<>();

        for (String currArg : args) {
            boolean consumed = false;
            argsLeft--; // Argsleft denotes how many arguments are left after this loop.
            if (testFlag && testFilePath == null) {
                testFilePath = currArg;
                consumed = true;
            } else if (schemaFlag && schemaName == null) {
                schemaName = currArg;
                consumed = true;
            }
            testFlag = currArg.equals("--testOutput");
            schemaFlag = currArg.equals("--schema");

            // Throwing errors if --testOutput has been passed twice or if it was passed as the final arg
            if ((testFlag && testFilePath != null) || (schemaFlag && schemaName != null)) {
                throw new IllegalArgumentException("--testOutput may only be specified once!");
            } else if ((testFlag || schemaFlag) && argsLeft == 0) {
                throw new IllegalArgumentException("--testOutput and --schema requires an accompanying filepath");
            }

            if (!testFlag && !schemaFlag && !consumed) srcFilePaths.add(currArg);
        }
        return new FileSet(schemaName, testFilePath, srcFilePaths);
    }

    private record FileSet(String schemaName, String testFilePath, List<String> srcFilePaths) {
    }
}
