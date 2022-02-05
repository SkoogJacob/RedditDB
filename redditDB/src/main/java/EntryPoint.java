import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db.accessors.*;
import files.readers.RedditJSONExtractor;
import files.writers.MarkDownWriter;
import io.github.cdimascio.dotenv.Dotenv;
import time.test.present.PresentTest;
import time.test.run.Test;
import time.test.run.Tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EntryPoint {
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

    /**
     * <p>This function will either load the database tables once for use in the assignment
     * or it will load it several times with different SQL methods to test performance.</p>
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
        final String schema = "test_db";
        SchemaOperations.createSchema(params, schema);
        SQLTableManager.createUnconstrainedTables(params, schema);
        SQLTableManager.createConstrainedTables(params, schema);

        // Start by setting the first source file
        assert args.length > 0;
        List<String> srcFiles = new LinkedList<>();
        srcFiles.add(args[0]);
        assert srcFiles.get(0) != null && !srcFiles.get(0).equals("");

        // If there are more than 1 argument, add all but the last argument as source files
        if (args.length > 1) {
            srcFiles.addAll(Arrays.asList(args).subList(1, args.length - 1));
            String resultFile = args[args.length - 1];
            assert resultFile != null && !resultFile.equals("");
            runTest(srcFiles, resultFile, params, schema); // Runs the loading tests
        } else {
            Tester tester = new Tester(params, srcFiles.get(0), schema);
            tester.stagingTest(); // This test will load the database using unconstrained tables as staging tables
        }
    }
}
