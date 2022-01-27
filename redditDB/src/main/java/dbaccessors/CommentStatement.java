package dbaccessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommentStatement {
    PreparedStatement statement;
    public CommentStatement(Connection conn, String targetSchema, boolean constrained) throws SQLException {
        String affix = constrained ? "constrained" : "unconstrained";
        statement = conn.prepareStatement(
                "INSERT INTO %1$s.reddit_comments_%2$s (id, parent_id, link_id, type, author, body, subreddit_id, score, created_utc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);".formatted(targetSchema, affix)
        );
    }

    public void setString(int parameterIndex, String value) throws SQLException {
        statement.setString(parameterIndex, value);
    }

    public void setInt(int parameterIndex, int value) throws SQLException {
        statement.setInt(parameterIndex, value);
    }

    public void addBatch() throws SQLException {
        statement.addBatch();
    }

    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }

    public void close() throws SQLException {
        statement.close();
    }
}
