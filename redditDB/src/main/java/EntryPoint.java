import secrets.CONNINFO;

import java.sql.*;

public class EntryPoint {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNINFO.URL.toString(), CONNINFO.USERNAME.toString(), CONNINFO.PASSWORD.toString())) {
            conn.setCatalog("test");
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT ID FROM classes")) {
                    rs.first();
                    System.out.println(rs.getString(1));
                    while (rs.next()) {
                        System.out.println(rs.getString(1));
                    }
                }
            }
        }


    }
}
