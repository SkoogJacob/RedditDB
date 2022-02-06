package db.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class handles adding stored operations, functions, and views to the reddit schema.
 */
public class AddOperations {
    /**
     * Adds all the stored operations, functions, and views to target schema.
     *
     * @param params The access params to open SQL connection with.
     * @param targetSchema The schema to store the views, procedures, and functions in.
     * @throws SQLException If bad sql stuff happens.
     */
    public static void addOperations(SQLAccessParams params, String targetSchema) throws SQLException {
        Connection conn = params.getConnection();
        conn.setCatalog(targetSchema);
        Statement statement = conn.createStatement();
        // Adding function to get total number of posts of user.
        addGetTotalPostsFunc(statement);

    }

    private static void addGetTotalPostsFunc(Statement statement) throws SQLException {
        statement.addBatch(
            """
            CREATE FUNCTION
                user_posts(p_username VARCHAR(20))
                RETURNS INT
                READS SQL DATA
                BEGIN
                    RETURN
                        (SELECT COUNT(*)
                            FROM comments_constrained
                            WHERE author=p_username;
                        )
                END;
            """.trim()
        );
    }
}
