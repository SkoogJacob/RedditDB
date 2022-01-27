package dbaccessors;

import comments.FullComment;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBLoaderUnconstrained implements Runnable {
    @NotNull private final Connection conn;
    @NotNull private final String targetSchema;
    @NotNull private final FullComment[] data;

    /**
     * Creates a Runnable to add a set of data to the target schema in the database that the loader connects to using
     * the passed url, username, and password details.
     *
     * @param accessParams Contains the parameters to establish connection with db
     * @param targetSchema The schema in the database to add all the data to
     * @param data The data to add
     * @throws SQLException If some SQL stuff happens when creating the Connection.
     */
    public DBLoaderUnconstrained(@NotNull SQLAccessParams accessParams, @NotNull String targetSchema, @NotNull FullComment[] data) throws SQLException {
        this.conn = DriverManager.getConnection(accessParams.url(), accessParams.username(), accessParams.password());
        this.targetSchema = targetSchema;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            // Adding subreddit data
            PreparedStatement subredditStatement = conn.prepareStatement(
                    "INSERT INTO %1$s.subreddits_unconstrained (subreddit_id, subreddit_name) VALUES (?, ?);".formatted(targetSchema)
            );
            PreparedStatement userStatement = conn.prepareStatement(
                    "INSERT INTO %1$s.reddit_users_unconstrained (username) VALUES (?);".formatted(targetSchema)
            );
            CommentStatement commentStatement = new CommentStatement(conn, targetSchema, false);
            for (FullComment comment : data) {
                subredditStatement.setString(1, comment.subredditID());
                subredditStatement.setString(2, comment.subreddit());
                subredditStatement.addBatch();

                userStatement.setString(1, comment.author());
                userStatement.addBatch();

                AddCommentBatch.addBatch(commentStatement, comment);
            }

            try {
                subredditStatement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                userStatement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            try {
                commentStatement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
