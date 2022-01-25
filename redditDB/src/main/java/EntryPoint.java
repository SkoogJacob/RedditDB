import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db_accessors.SQLTableManager;
import file_readers.RedditJSONExtractor;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class EntryPoint {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);
            RedditJSONExtractor extractor = new RedditJSONExtractor("./src/main/resources/badjson");
            LinkedList<FullComment> list = new LinkedList<>();
            while (extractor.hasNext()) {
                String json = extractor.extractJSONObject();
                list.add(adapter.fromJson(json));
            }
            for (FullComment comment : list) {
                System.out.println(comment);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Dotenv env = Dotenv.load();
        try (Connection conn = DriverManager.getConnection(
                env.get("SQL_URL"),
                env.get("SQL_UNAME"),
                env.get("SQL_PWORD")
        )) {
            final String schemaName = "test_db";
            SQLTableManager.createConstrainedTables(conn, schemaName);
            SQLTableManager.createUnconstrainedTables(conn, schemaName);
            SQLTableManager.dropTables(conn, schemaName);
        }

    }
}
