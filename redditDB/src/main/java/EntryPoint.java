import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db.accessors.*;
import files.readers.RedditJSONExtractor;
import io.github.cdimascio.dotenv.Dotenv;
import time.test.run.Test;
import time.test.run.Tester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class EntryPoint {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Dotenv env = Dotenv.load();
        SQLAccessParams params = new SQLAccessParams(
                env.get("SQL_URL"),
                env.get("SQL_UNAME"),
                env.get("SQL_PWORD")
        );
        assert args.length > 1;
        String srcFile = args[0];
        assert srcFile != null && !srcFile.equals("");
        String resultFile = args[1];
        assert resultFile != null && !resultFile.equals("");
        Tester tester = new Tester(params, srcFile, "test_db");

        for (Test.TestType type : Test.TestType.values()) {
            tester.run(type);
        }

    }
}
