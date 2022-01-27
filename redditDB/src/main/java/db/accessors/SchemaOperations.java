package db.accessors;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SchemaOperations {
    /**
     * Returns the SQL query to create a schema with the passed schema name
     * and utf8mb4_unicode_ci collation.
     *
     * @param schemaName The desired name of the schema.
     * @return An SQL string to create a schema with the desired name.
     */
    public static boolean createSchema(@NotNull Connection conn, @NotNull String schemaName) {
        assertValidSchemaName(schemaName);
        try {
            conn.createStatement().execute("CREATE SCHEMA %1$s COLLATE utf8mb4_unicode_ci;".formatted(schemaName));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns an SQL query string to drop (delete) a schema with the passed schema name.
     *
     * @param schemaName The name of the schema to delete.
     * @return A sql query string to delete the desired schema.
     */
    public static boolean dropSchema(@NotNull Connection conn, @NotNull String schemaName) {
        assertValidSchemaName(schemaName);
        try {
            conn.createStatement().execute("DROP SCHEMA %1$s;".formatted(schemaName));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Asserts that the passed schema name is a valid name for a schema (i.e. is not empty
     * string and contains no whitespace)
     *
     * @param schemaName The proposed schema name
     */
    private static void assertValidSchemaName(@NotNull String schemaName) {
        assert !schemaName.equals("") && !Pattern.matches("\\s", schemaName); // assert that string is not empty and that it has no whitespaces
    }
}
