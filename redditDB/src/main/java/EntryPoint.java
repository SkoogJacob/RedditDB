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
import java.util.LinkedList;

public class EntryPoint {
    public static void runTest(String dataSrcPath, String reportFilePath, SQLAccessParams params, String schema) throws IOException, SQLException, InterruptedException {
        File report = new File(reportFilePath);
        Tester tester = new Tester(params, dataSrcPath, schema);
        MarkDownWriter writer = new MarkDownWriter(report);

        for (Test.TestType type : Test.TestType.values()) {
            for (int testNr = 1; testNr <= 4; testNr++) {
                String testString = PresentTest.getTestString(tester.run(type), testNr);
                SQLTableManager.clearTables(params, schema);
                writer.writeToFile(testString);
            }
        }
    }
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
        assert args.length > 1;
        String srcFile = args[0];
        assert srcFile != null && !srcFile.equals("");
        String resultFile = args[1];
        assert resultFile != null && !resultFile.equals("");

//        runTest(srcFile, resultFile, params, schema); // Runs the loading tests
        Tester tester = new Tester(params, srcFile, schema);
        tester.stagingTest(); // This test will load the database using unconstrained tables as staging tables
    }
}
