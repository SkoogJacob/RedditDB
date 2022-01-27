import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db_accessors.*;
import file_readers.RedditJSONExtractor;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class EntryPoint {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Dotenv env = Dotenv.load();
        try {
            // Creating extractor to extract JSON objects from the file
            RedditJSONExtractor extractor = new RedditJSONExtractor(args[0]);
            // Creating a base connection to the SQL DB
            final SQLAccessParams params = new SQLAccessParams(env.get("SQL_URL"), env.get("SQL_UNAME"), env.get("SQL_PWORD"));
            final Connection conn = DriverManager.getConnection(params.url(), params.username(), params.password());
            // Creating the schema and required tables if they don't exist
            final String schemaName = "test_db";
            SchemaOperations.createSchema(conn, schemaName);
            SQLTableManager.createUnconstrainedTables(conn, schemaName);
            boolean constrained = false;
            // Creating moshi adapter to read in JSON to FullComment objects
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);
            LinkedList<FullComment> list = new LinkedList<>();
            // Reading JSON from the file. If 1000 objects have been read in,
            int batchSize = constrained ? 10000 : 1000; // Constrained can have larger batches as it is only processing one batch at a time
            while (extractor.hasNext()) {
                String json = extractor.extractJSONObject();
                list.add(adapter.fromJson(json));
                if (list.size() == 1000 || !extractor.hasNext()) {
                    FullComment[] comments = new FullComment[list.size()];
                    list.toArray(comments);
                    list.clear();
                    Runnable loader = constrained ? new DBLoaderConstrained(params, schemaName, comments) : new DBLoaderUnconstrained(params, schemaName, comments);
                    Thread t = new Thread(loader);
                    t.start();
                    if (constrained) {
                        try {
                            t.join(); // Weird collisions happen with multithreading, putting this join here (making multithreading entirely pointless) until I know why
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
