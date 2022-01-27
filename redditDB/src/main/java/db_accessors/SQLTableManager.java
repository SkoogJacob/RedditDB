package db_accessors;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains static methods that take a connection object and target schema name and then creates
 * various tables used in this assignment.
 */
public class SQLTableManager {

    /**
     * Creates all the tables for reddit comments with various constraints to ensure sane values.
     *
     * @param conn The SQL connection to use in creating Statements
     * @param targetSchema The SQL schema to create the tables in.
     * @return true if all operations went without error, false otherwise.
     */
    public static boolean createConstrainedTables(@NotNull Connection conn, @NotNull String targetSchema) {
        try {
            createConstrainedTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
    private static void createConstrainedTablesPrivate(@NotNull Connection conn, @NotNull String targetSchema) throws SQLException {
        final String redditUsers = "reddit_users_constrained";
        final String redditUsersShort = "ruc";
        final String subreddits = "subreddits_constrained";
        final String subredditsShort = "sc";
        final String redditComments = "reddit_comments_constrained";
        final String redditCommentsShort = "rcc";

        Statement statement = conn.createStatement();
        // Creating user table and inserting a default username '[deleted]'
        statement.addBatch("""
            CREATE TABLE %1$s.%2$s
            (
                username    VARCHAR(20) NOT NULL,
                CONSTRAINT %3$s_pk PRIMARY KEY (username)
            );
            """.formatted(targetSchema, redditUsers, redditUsersShort).trim());
        statement.addBatch("""
            CREATE UNIQUE INDEX IF NOT EXISTS %3$s_username_uindex ON %1$s.%2$s (username);
            """.formatted(targetSchema, redditUsers, redditUsersShort).trim());
        statement.addBatch("""
            INSERT INTO %1$s.%2$s (username) VALUES ('[deleted]');
            """.formatted(targetSchema, redditUsers));

        // Creating subreddits table, no default value as deletion cascades
        statement.addBatch("""
            CREATE TABLE %1$s.%2$s
            (
                subreddit_id    VARCHAR(10) NOT NULL,
                subreddit_name  VARCHAR(20) NOT NULL,
                CONSTRAINT %3$s_pk
                    PRIMARY KEY (subreddit_id)
            );
            """.formatted(targetSchema, subreddits, subredditsShort).trim());
        statement.addBatch("""
                CREATE UNIQUE INDEX %3$s_subreddit_id_uindex ON %1$s.%2$s (subreddit_id);
                """.formatted(targetSchema, subreddits, subredditsShort).trim());
        statement.addBatch("""
                CREATE UNIQUE INDEX %3$s_subreddit_name_uindex ON %1$s.%2$s (subreddit_name);
                """.formatted(targetSchema, subreddits, subredditsShort).trim());
        // Creating comments table
        statement.addBatch("""
                CREATE TABLE %1$s.%2$s
                (
                    id              VARCHAR(10)                                 NOT NULL,
                    parent_id       VARCHAR(10)                                 NOT NULL,
                    link_id         VARCHAR(10)                                 NOT NULL,
                    type            ENUM ('t1', 't2', 't3', 't4', 't5', 't6')   NOT NULL,
                    author          VARCHAR(20) default '[deleted]'             NOT NULL,
                    body            TEXT        default '[deleted]'             NOT NULL,
                    subreddit_id    VARCHAR(10) default 'deleted'               NOT NULL,
                    score           INT                                         NOT NULL,
                    created_utc     INT                                         NOT NULL,
                    CONSTRAINT %3$s_pk PRIMARY KEY (id),
                    CONSTRAINT %3$s___fk_author_exists
                        FOREIGN KEY (author) REFERENCES %1$s.%4$s (username)
                        ON UPDATE CASCADE ON DELETE SET DEFAULT,
                    CONSTRAINT %3$s___fk_subreddit_exists
                        FOREIGN KEY (subreddit_id) REFERENCES %1$s.%5$s (subreddit_id)
                        ON UPDATE CASCADE ON DELETE SET DEFAULT
                );
                """.formatted(targetSchema, redditComments, redditCommentsShort, redditUsers, subreddits).trim());
        statement.addBatch("""
            CREATE UNIQUE INDEX IF NOT EXISTS %3$s_id_uindex ON %1$s.%2$s (id);
            """.formatted(targetSchema, redditComments, redditCommentsShort).trim());
        statement.executeBatch();
    }

    public static boolean createUnconstrainedTables(@NotNull Connection conn, @NotNull String targetSchema) {
        try {
            createUnconstrainedTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void createUnconstrainedTablesPrivate(@NotNull Connection conn, @NotNull String targetSchema) throws SQLException {
        Statement statement = conn.createStatement();
        final String redditUsers = "reddit_users_unconstrained";
        final String subreddits = "subreddits_unconstrained";
        final String redditComments = "reddit_comments_unconstrained";

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
                subreddit_name  VARCHAR(20) NULL
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
                    author          VARCHAR(20) default '[deleted]'             NULL,
                    body            TEXT        default '[deleted]'             NULL,
                    subreddit_id    VARCHAR(10)                                 NULL,
                    score           INT                                         NULL,
                    created_utc     INT                                         NULL
                );
                """.formatted(targetSchema, redditComments).trim());
        statement.executeBatch();
    }

    public static boolean clearTables(Connection conn, String targetSchema) {
        try {
            clearTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void clearTablesPrivate (Connection conn, String targetSchema) throws SQLException {
        Statement statement = conn.createStatement();
        statement.addBatch("DELETE FROM %1$s.subreddits_constrained;".formatted(targetSchema));
        statement.addBatch("DELETE FROM %1$s.reddit_comments_constrained WHERE (subreddit_id) NOT IN ('deleted');".formatted(targetSchema));
        statement.addBatch("DELETE FROM %1$s.reddit_users_constrained WHERE username NOT IN ('[deleted]');".formatted(targetSchema));
        try {
            statement.executeBatch();
        } catch (SQLException ignore) {}
        statement.addBatch("TRUNCATE TABLE %1$s.subreddits_unconstrained;".formatted(targetSchema));
        statement.addBatch("TRUNCATE TABLE %1$s.reddit_comments_unconstrained;".formatted(targetSchema));
        statement.addBatch("TRUNCATE TABLE %1$s.reddit_users_unconstrained;".formatted(targetSchema));
    }
    public static boolean dropTables(Connection conn, String targetSchema) {
        try {
            dropTablesPrivate(conn, targetSchema);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static void dropTablesPrivate(Connection conn, String targetSchema) throws SQLException {
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
    }
}
