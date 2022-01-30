package db.accessors;

import comments.FullComment;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadAfterStaging {
    private final SQLAccessParams params;
    private final String targetSchema;

    public LoadAfterStaging(SQLAccessParams accessParams, String targetSchema) throws SQLException {
        this.params = accessParams;
        this.targetSchema = targetSchema;
    }

    /**
     * Loads data into the constrained tables from the unconstrained tables
     */
    public void load() {
        try {
            Connection conn = params.getConnection();
            Statement statement = conn.createStatement();
            statement.addBatch("""
                INSERT INTO %1$s.redditors_constrained (username)
                SELECT DISTINCT * FROM %1$s.redditors_unconstrained user
                WHERE user.username NOT IN (SELECT username FROM %1$s.redditors_constrained);
                """.formatted(targetSchema).trim());
            statement.addBatch("""
                INSERT INTO %1$s.subreddits_constrained (subreddit_id, subreddit_name)
                SELECT DISTINCT * FROM %1$s.subreddits_unconstrained sub
                WHERE sub.subreddit_id NOT IN (SELECT subreddit_id FROM %1$s.subreddits_constrained);
                """.formatted(targetSchema).trim());
            statement.addBatch("""
                INSERT INTO %1$s.comments_constrained (id, parent_id, link_id, type, author, body, subreddit_id, score, created_utc)
                SELECT DISTINCT * FROM %1$s.comments_unconstrained comment
                WHERE comment.id NOT IN (SELECT id FROM %1$s.comments_constrained);
                """.formatted(targetSchema).trim());
            statement.addBatch("TRUNCATE %1$s.subreddits_unconstrained;".formatted(targetSchema));
            statement.addBatch("TRUNCATE  %1$s.redditors_unconstrained;".formatted(targetSchema));
            statement.addBatch("TRUNCATE %1$s.comments_unconstrained;".formatted(targetSchema));
            statement.executeBatch();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
