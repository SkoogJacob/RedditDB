import secrets.CONNINFO;

import java.sql.*;

public class EntryPoint {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNINFO.URL.toString(), CONNINFO.URL.toString(), CONNINFO.PASSWORD.toString())) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("USE `test` SELECT `ID`")) {
                    rs.first();
                    System.out.println(rs.getString(1));
                }
            }
        }
    }
}
