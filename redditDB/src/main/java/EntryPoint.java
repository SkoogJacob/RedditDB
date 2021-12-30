import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import encoders.Base36;
import secrets.CONNINFO;

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
        System.out.println(Base36.toBase36("12344564564646546544502934"));
        File smallFile = new File(args[0]);
        System.out.println(smallFile.isFile());
        try (Connection conn = DriverManager.getConnection(CONNINFO.URL.toString(), CONNINFO.USERNAME.toString(), CONNINFO.PASSWORD.toString())) {
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
                //System.out.println((String) gson.fromJson(reader, String.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
