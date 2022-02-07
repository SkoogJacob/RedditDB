package db.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommentStatement {
    PreparedStatement statement;
    public CommentStatement(Connection conn, String targetSchema, boolean constrained) throws SQLException {
        String affix = constrained ? "constrained" : "unconstrained";
        statement = conn.prepareStatement(
                "INSERT IGNORE INTO %1$s.comments_%2$s (id, parent_id, link_id, type, author, body, subreddit_id, score, created_utc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);".formatted(targetSchema, affix)
        );
    }

    public void setID(String value) throws SQLException {
        statement.setString(1, value);
    }

    public void setParentID(String value) throws SQLException {
        statement.setString(2, value);
    }

    public void setLinkID(String value) throws SQLException {
        statement.setString(3, value);
    }

    public void setType(String value) throws SQLException {
        statement.setString(4, value);
    }

    public void setAuthor(String value) throws SQLException {
        statement.setString(5, value);
    }

    public void setBody(String value) throws SQLException {
        statement.setString(6, value);
    }

    public void setSubredditID(String value) throws SQLException {
        statement.setString(7, value);
    }

    public void setScore(int value) throws SQLException {
        statement.setInt(8, value);
    }

    public void setCreatedUTC(int value) throws SQLException {
        statement.setInt(9, value);
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
