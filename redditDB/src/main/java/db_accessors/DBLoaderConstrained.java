package db_accessors;

import comments.FullComment;
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
            TreeSet<String> id = new TreeSet<>(); // Stores the ID's that have already been added

            // Adding subreddit data
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO %1$s.subreddits_constrained (subreddit_id, subreddit_name) VALUES (?, ?);".formatted(targetSchema)
            );
            for (FullComment comment : data) {
                boolean contains = id.contains(comment.subredditID());
                if (
                        !contains && conn.createStatement().executeQuery(
                                "SELECT * FROM %1$s.subreddits_constrained WHERE subreddit_id = '%2$s';".formatted(targetSchema, comment.subredditID())
                        ).first()
                ) {
                    id.add(comment.subredditID());
                    continue;
                }
                else if (id.contains(comment.subredditID())) {
                    continue;
                }
                statement.setString(1, comment.subredditID());
                statement.setString(2, comment.subreddit());
                statement.addBatch();
                id.add(comment.subredditID());
            }
            try {
                statement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            id.clear();
            statement.close();

            // Switching to inserting reddit usernames table
            statement = conn.prepareStatement(
                    "INSERT INTO %1$s.reddit_users_constrained (username) VALUES (?);".formatted(targetSchema)
            );
            for (FullComment comment : data) {
                boolean contains = id.contains(comment.author());
                if (
                        !contains && conn.createStatement().executeQuery(
                        "SELECT * FROM %1$s.reddit_users_constrained WHERE username = '%2$s';".formatted(targetSchema, comment.author())
                        ).first()
                ) {
                    id.add(comment.author());
                    continue;
                } else if (id.contains(comment.author())) {
                    continue;
                }
                statement.setString(1, comment.author());
                statement.addBatch();
                id.add(comment.author());
            }
            try {
                statement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            id.clear();
            statement.close();

            // Finally, inserting comment data
            CommentStatement commentStatement = new CommentStatement(conn, targetSchema, true);
            for (FullComment comment : data) {
                boolean contains = id.contains(comment.id());
                if (
                        !contains && conn.createStatement().executeQuery(
                                "SELECT * FROM %1$s.reddit_comments_constrained WHERE id = '%2$s';".formatted(targetSchema, comment.id())
                        ).first()
                ) {
                    id.add(comment.id());
                    continue;
                } else if (id.contains(comment.id())) {
                    continue;
                }
                AddCommentBatch.addBatch(commentStatement, comment);
                id.add(comment.id());
            }
            try {
                commentStatement.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
            id.clear();
            commentStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
