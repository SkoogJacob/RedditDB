import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import comments.FullComment;
import db_accessors.DBLoaderConstrained;
import db_accessors.SQLAccessParams;
import db_accessors.SQLTableManager;
import db_accessors.SchemaOperations;
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
            SQLTableManager.createConstrainedTables(conn, schemaName);
            boolean constrained = true;
            // Creating moshi adapter to read in JSON to FullComment objects
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FullComment> adapter = moshi.adapter(FullComment.class);
            LinkedList<FullComment> list = new LinkedList<>();
            // Reading JSON from the file. If 1000 objects have been read in,
            while (extractor.hasNext()) {
                String json = extractor.extractJSONObject();
                list.add(adapter.fromJson(json));
                /*
                {
                "author":"jamiemccarthy",
                "score":3,
                "link_id":
                "t3_5yba3",
                TODO This comment contained {} chars, which broke my scanner! Fix tomorrow!
                "body":"RSS feeds are broken right now, showing up in my reader with URLs
                    like\n\nhttp : // {{ thing.urlprefix }}
                    /goto?rss=true&amp;id=foobar\n\n(spaces added to prevent any attempts at auto-munging)\n\n
                    ...so my only options in RSS are the [link] and [more] buttons.
                    If comment counts are down today, it's probably that, not the new comment code...",
                "score_hidden":false,
                "author_flair_text":null,
                "gilded":0,
                "subreddit":"reddit.com",
                "edited":false,
                "author_flair_css_class":null,
                "name":"t1_c0299mj",
                "retrieved_on":1427426405,
                "parent_id":"t1_c0299bv",
                "created_utc":"1192454017",
                "controversiality":0,
                "ups":3,
                "distinguished":null,
                "id":"c0299mj",
                "subreddit_id":"t5_6",
                "downs":0,
                "archived":true
                }
                 */
                if (list.size() == 1000 || !extractor.hasNext()) {
                    for (FullComment comment : list) System.out.println(comment);

                    FullComment[] comments = new FullComment[list.size()];
                    list.toArray(comments);
                    list.clear();
                    Runnable loader = constrained ? new DBLoaderConstrained(params, schemaName, comments) : null;
                    new Thread(loader).start();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
