package db.accessors.loaders;

import comments.FullComment;
import db.accessors.AddCommentBatch;
import db.accessors.CommentStatement;
import db.accessors.SQLAccessParams;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.TreeSet;

public class DBLoaderConstrained implements Runnable {
    @NotNull private final Connection conn;
    @NotNull private final String targetSchema;
    @NotNull private final FullComment[] data;

    /**
     * Creates a Runnable to add a set of data to the target schema in the database that the loader connects to using
     * the passed url, username, and password details.
     *
     * @param accessParams Contains access params for sql db
     * @param targetSchema The schema in the database to add all the data to
     * @param data The data to add
     * @throws SQLException If some SQL stuff happens when creating the Connection.
     */
    public DBLoaderConstrained(@NotNull SQLAccessParams accessParams, @NotNull String targetSchema, @NotNull FullComment[] data) throws SQLException {
        this.conn = DriverManager.getConnection(accessParams.url(), accessParams.username(), accessParams.password());
        this.targetSchema = targetSchema;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            // Adding subreddit data
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT IGNORE INTO %1$s.subreddits_constrained (subreddit_id, subreddit_name) VALUES (?, ?);".formatted(targetSchema)
            );
            for (FullComment comment : data) {
                statement.setString(1, comment.subredditID());
                statement.setString(2, comment.subreddit());
                statement.addBatch();
            }
            try {
                statement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            statement.close();

            // Switching to inserting reddit usernames table
            statement = conn.prepareStatement(
                    "INSERT IGNORE INTO %1$s.redditors_constrained (username) VALUES (?);".formatted(targetSchema)
            );
            for (FullComment comment : data) {
                statement.setString(1, comment.author());
                statement.addBatch();
            }
            try {
                statement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            statement.close();

            // Finally, inserting comment data
            CommentStatement commentStatement = new CommentStatement(conn, targetSchema, true);
            for (FullComment comment : data) {
                AddCommentBatch.addBatch(commentStatement, comment);
            }
            try {
                commentStatement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            commentStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
