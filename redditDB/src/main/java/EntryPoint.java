import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import file_readers.RedditJSONExtractor;
import io.github.cdimascio.dotenv.Dotenv;
import comments.FullComment;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class EntryPoint {
    public static void main(String[] args) throws SQLException, IOException {
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

        Dotenv dotenv = Dotenv.load();
        File smallFile = new File(args[0]);
        try (Connection conn =
                     DriverManager.getConnection(dotenv.get("SQL_URL"),
                             dotenv.get("SQL_UNAME"),
                             dotenv.get("SQL_PWORD"))
        ) {
            conn.setCatalog("reddit_db");
//            try (Statement stmt = conn.createStatement()) {
//                try (ResultSet rs = stmt.executeQuery("SELECT ID FROM classes")) {
//                    rs.first();
//                    System.out.println(rs.getString(1));
//                    while (rs.next()) {
//                        System.out.println(rs.getString(1));
//                    }
//                }
//            }
        }
    }
}
