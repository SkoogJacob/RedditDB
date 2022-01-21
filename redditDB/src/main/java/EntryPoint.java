import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import file_readers.RedditJSONExtractor;
import io.github.cdimascio.dotenv.Dotenv;
import comments.FullComment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class EntryPoint {
    public static void main(String[] args) throws SQLException {
        try {
            Gson gson = new Gson();
            RedditJSONExtractor extractor = new RedditJSONExtractor(args[0]);
//            JSONExtractor extractor = new JSONExtractor("/home/agryphos/unicourses/2dv513/a2/redditDB/src/main/resources/badjson");
            while (extractor.hasNext()) {
                String json = extractor.extractJSONObject();
                System.out.println("Before");
                System.out.println(json);
                System.out.println("After");
                System.out.println(gson.fromJson(json, FullComment.class).toString());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Dotenv dotenv = Dotenv.load();
        File smallFile = new File(args[0]);
        System.out.println(smallFile.isFile());
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
        try {
            LinkedList<JsonObject> list = new LinkedList<>();
            Gson gson = new Gson();
            InputStream is = Files.newInputStream(Path.of(args[0]));
            JsonReader reader = new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            reader.beginObject();
            int i = 0;
            while (reader.hasNext() && ++i < 50) {
                System.out.println(i);
                System.out.println((String) gson.fromJson(reader, FullComment.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
