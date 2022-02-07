package db.accessors;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains static methods that take a connection object
 * and target schema name and then creates
 * various tables used in this assignment.
 */
public final class SQLTableManager {
    private SQLTableManager() { }

    /**
     * Creates all the tables for reddit comments with various constraints to ensure sane values.
     *
     * @param params Access parameters to create a SQL connection from.
     * @param targetSchema The SQL schema to create the tables in.
     * @return true if all operations went without error, false otherwise.
     */
    public static boolean createConstrainedTables(@NotNull final SQLAccessParams params, @NotNull final String targetSchema) {
        try (Connection conn = params.getConnection()) {
            createConstrainedTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            if (!e.getMessage().contains("exists")) e.printStackTrace();
            return false;
        }
    }
    /**
     * Internally called by createStaticTables.
     *
     * @param conn Connection used to create Statement
     * @param targetSchema The schema to store the created tables in
     * @throws SQLException Throws any SQL exceptions that occur
     */
    private static void createConstrainedTablesPrivate(@NotNull final Connection conn, @NotNull final String targetSchema) throws SQLException {
        final String redditUsers = "redditors_constrained";
        final String redditUsersShort = "rc";
        final String subreddits = "subreddits_constrained";
        final String subredditsShort = "sc";
        final String redditComments = "comments_constrained";
        final String redditCommentsShort = "cc";

        conn.setCatalog(targetSchema);
        Statement statement = conn.createStatement();
        // Creating user table and inserting a default username '[deleted]'
        statement.addBatch("""
            CREATE TABLE %1$s
            (
                username    VARCHAR(20) NOT NULL,
                CONSTRAINT %2$s_pk PRIMARY KEY (username)
            );
            """.formatted(redditUsers, redditUsersShort).trim());
        statement.addBatch("""
            CREATE UNIQUE INDEX IF NOT EXISTS %2$s_username_uindex ON %1$s (username);
            """.formatted(redditUsers, redditUsersShort).trim());
        statement.addBatch("""
            INSERT INTO %1$s (username) VALUES ('[deleted]');
            """.formatted(redditUsers));

        // Creating subreddits table, no default value as deletion cascades
        statement.addBatch("""
            CREATE TABLE %1$s
            (
                subreddit_id    VARCHAR(10) NOT NULL,
                subreddit_name  VARCHAR(24) NOT NULL,
                CONSTRAINT %2$s_pk
                    PRIMARY KEY (subreddit_id)
            );
            """.formatted(subreddits, subredditsShort).trim());
        statement.addBatch("""
                CREATE UNIQUE INDEX %2$s_subreddit_id_uindex ON %1$s (subreddit_id);
                """.formatted(subreddits, subredditsShort).trim());
        statement.addBatch("""
            CREATE INDEX subreddit_name_index ON %1$s (subreddit_name);
            """.formatted(subreddits).trim());
        // Creating comments table
        statement.addBatch("""
                CREATE TABLE %1$s
                (
                    id              VARCHAR(10)                                 NOT NULL,
                    parent_id       VARCHAR(10)                                 NOT NULL,
                    link_id         VARCHAR(10)                                 NOT NULL,
                    type            ENUM ('t1', 't2', 't3', 't4', 't5', 't6')   NOT NULL,
                    author          VARCHAR(20) default '[deleted]'             NOT NULL,
                    body            TEXT        default '[deleted]'             NOT NULL,
                    subreddit_id    VARCHAR(10)                                 NOT NULL,
                    score           INT                                         NOT NULL,
                    created_utc     INT                                         NOT NULL,
                    CONSTRAINT %2$s_pk PRIMARY KEY (id),
                    CONSTRAINT %2$s___fk_author_exists
                        FOREIGN KEY (author) REFERENCES %3$s (username)
                        ON UPDATE CASCADE ON DELETE SET DEFAULT,
                    CONSTRAINT %3$s___fk_subreddit_exists
                        FOREIGN KEY (subreddit_id) REFERENCES %4$s (subreddit_id)
                        ON UPDATE CASCADE ON DELETE NO ACTION
                );
                """.formatted(redditComments, redditCommentsShort, redditUsers, subreddits).trim());
        addCommentIndices(statement);
        statement.executeBatch();
    }

    /**
     * Creates a table to contain all the reddit data, but with absolutely 0 rules. Not even primary keys.
     *
     * @param params SQL parameters to create a SQL Connection.
     * @param targetSchema The schema to store the created tables in.
     * @return True if no errors were thrown by the requests, false otherwise.
     */
    public static boolean createUnconstrainedTables(@NotNull final SQLAccessParams params, @NotNull final String targetSchema) {
        try (Connection conn = params.getConnection()){
            createUnconstrainedTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            if (!e.getMessage().contains("exists")) e.printStackTrace();
            return false;
        }
    }
    private static void createUnconstrainedTablesPrivate(@NotNull final Connection conn, @NotNull final String targetSchema) throws SQLException {
        Statement statement = conn.createStatement();
        final String redditUsers = "redditors_unconstrained";
        final String subreddits = "subreddits_unconstrained";
        final String redditComments = "comments_unconstrained";

        // Creating user table and inserting a default username '[deleted]'
        statement.addBatch("""
            CREATE TABLE %1$s.%2$s
            (
                username    VARCHAR(20) NULL
            );
            """.formatted(targetSchema, redditUsers).trim());

        // Creating subreddits table, no default value as deletion cascades
        statement.addBatch("""
            CREATE TABLE %1$s.%2$s
            (
                subreddit_id    VARCHAR(10) NULL,
                subreddit_name  VARCHAR(24) NULL
            );
            """.formatted(targetSchema, subreddits).trim());
        // Creating comments table
        statement.addBatch("""
                CREATE TABLE %1$s.%2$s
                (
                    id              VARCHAR(10)                                 NULL,
                    parent_id       VARCHAR(10)                                 NULL,
                    link_id         VARCHAR(10)                                 NULL,
                    type            ENUM ('t1', 't2', 't3', 't4', 't5', 't6')   NULL,
                    author          VARCHAR(20)                                 NULL,
                    body            TEXT                                        NULL,
                    subreddit_id    VARCHAR(10)                                 NULL,
                    score           INT                                         NULL,
                    created_utc     INT                                         NULL
                );
                """.formatted(targetSchema, redditComments).trim());
        statement.executeBatch();
    }

    /**
     * Clears all entries from the reddit tables in the target schema.
     *
     * @param params The params to establish sql connection.
     * @param targetSchema The schema containing the reddit data tables.
     * @return true if no exceptions were thrown, false otherwise.
     */
    public static boolean clearTables(@NotNull final SQLAccessParams params, @NotNull final String targetSchema) {
        try (Connection conn = params.getConnection()) {
            clearTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void clearTablesPrivate(@NotNull final Connection conn, @NotNull final String targetSchema) throws SQLException {
        dropCommentIndexes(conn, targetSchema);
        conn.setCatalog(targetSchema);
        Statement statement = conn.createStatement();

        statement.addBatch("DELETE FROM comments_constrained;");
        statement.addBatch("DELETE FROM subreddits_constrained;");
        statement.addBatch("DELETE FROM redditors_constrained;");
        statement.addBatch("INSERT INTO redditors_constrained (username) VALUES ('[deleted]')");
        addCommentIndices(statement); // Adds indices queries to batch
        try {
            statement.executeBatch();
        } catch (SQLException ignore) { }
        statement.addBatch("TRUNCATE TABLE subreddits_unconstrained;");
        statement.addBatch("TRUNCATE TABLE comments_unconstrained;");
        statement.addBatch("TRUNCATE TABLE redditors_unconstrained;");
        try {
            statement.executeBatch();
        } catch (SQLException ignore) { }
        statement.close();
    }

    /**
     * Drops all the reddit tables that are in the target schema.
     * @param params SQL access credentials
     * @param targetSchema The schema to drop tables from.
     * @return true if no exceptions, false otherwise
     */
    public static boolean dropTables(@NotNull final SQLAccessParams params, @NotNull final String targetSchema) {
        try (Connection conn = params.getConnection()){
            dropTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void dropTablesPrivate(@NotNull final Connection conn, @NotNull final String targetSchema) throws SQLException {
        dropCommentIndexes(conn, targetSchema);
        conn.setCatalog(targetSchema);
        Statement statement = conn.createStatement();
        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.reddit_comments_constrained;
            """.formatted(targetSchema));
        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.reddit_users_constrained;
            """.formatted(targetSchema));
        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.subreddits_constrained;
            """.formatted(targetSchema));

        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.reddit_comments_unconstrained;
            """.formatted(targetSchema));
        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.reddit_users_unconstrained;
            """.formatted(targetSchema));
        statement.addBatch("""
            DROP TABLE IF EXISTS %1$s.subreddits_unconstrained;
            """.formatted(targetSchema));
        statement.executeBatch();
        statement.close();
    }

    public static void dropCommentIndexes(@NotNull Connection conn, @NotNull String targetSchema) throws SQLException {
        conn.setCatalog(targetSchema);
        Statement statement = conn.createStatement();
        statement.addBatch("""
            DROP INDEX IF EXISTS author_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS created_utc_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS link_id_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS parent_id_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS score_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS subreddit_id_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS author_index ON comments_constrained;
            """);
        statement.addBatch("""
            DROP INDEX IF EXISTS author_index ON comments_constrained;
            """);
        statement.executeBatch();
        statement.close();
    }

    /**
     * Adds batch queries to the passed statement to add indices to comments_constrained
     *
     * @param statement The statement to add batches to. The statement should be set to the correct schema.
     * @throws SQLException If bad sql stuff
     */
    public static void addCommentIndices(Statement statement) throws SQLException {
        final String redditComments = "comments_constrained";

        statement.addBatch("""
            CREATE UNIQUE INDEX IF NOT EXISTS cc_id_uindex ON comments_constrained (id);
            """.trim());
        statement.addBatch("""
            CREATE INDEX author_index ON comments_constrained (author);
            """);
        statement.addBatch("""
            CREATE INDEX created_utc_index ON comments_constrained (created_utc DESC);
            """);
        statement.addBatch("""
            CREATE INDEX link_id_index ON comments_constrained (link_id);
            """);
        statement.addBatch("""
            CREATE INDEX parent_id_index ON comments_constrained (parent_id);
            """);
        statement.addBatch("""
            CREATE INDEX score_index ON comments_constrained (score DESC);
            """);
        statement.addBatch("""
            CREATE INDEX subreddit_id_index ON comments_constrained (subreddit_id)
            """);
    }
}
