package db.accessors;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record SQLAccessParams(@NotNull String url, @NotNull String username, @NotNull String password) {
    /**
     * Calls DriverManager.getConnection using the parameters stored in this object.
     *
     * @return A Connection to an SQL server
     * @throws SQLException If the connection failed for some reason (likely bad URL or bad user credentials)
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
