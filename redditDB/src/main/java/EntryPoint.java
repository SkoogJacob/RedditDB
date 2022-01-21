import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import io.github.cdimascio.dotenv.Dotenv;
import encoders.Base36;
import comments.FullComment;

import javax.naming.Context;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.stream.Stream;

public class EntryPoint {
    public static void main(String[] args) throws SQLException {
        String ogB10 = "8238641";
        System.out.println("Number of b10 digits: " + ogB10.length());
        String b36 = Base36.toBase36(ogB10);
        String b10 = Base36.fromBase36(b36);
        System.out.println("The original string: " + ogB10 + "\nIn base36: " + b36 +
         "\nAnd back to b10: " + b10);

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
