package db.accessors;

import java.sql.Connection;
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
        // Add functions to translate from subreddit ID to subreddit name and vice versa
        addSubredditIdAndNameTranslatorFunctions(statement);
        // Add function to translate epoch to date
        addEpochToDateFunction(statement);
        // Add a view of the comments_constrained scheme to show only comment id, subreddit name and posted date
        addCommentDateView(statement);
        // Add a procedure to get average posts per day for a subreddit
        addAveragePostsPerDayInSubreddit(statement);
        // Add a procedure for finding number of posts containing phrase
        addPostsContainingFunction(statement);
        // Adds a procedure to select what subreddits commenters of a particular thread have particiapted in
        addParticipatedInFromPost(statement);
        // Adds procedures to get lowest and highest score users in the dataset.
        addHighLowScoreProcedures(statement);
        // Adds procedures to find the subreddits with the highest and lowest scores
        addHighLowSubredditScoreProcedures(statement);
        // Add procedure to find all users that one user might have potentially interacted with
        addPotentialInteractionsProcedure(statement);
        // Add procedure to find all users who have particiapted in at most n subreddits
        addPostedToMaxNSubreddits(statement);

        // Finally, execute batch
        statement.executeBatch();
        // Close resources
        statement.close();
        conn.close();
    }

    private static void addPostedToMaxNSubreddits(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE PROCEDURE only_n_subreddits(IN p_n INT)
                READS SQL DATA
                BEGIN
                    SELECT *
                    FROM (SELECT author, COUNT(*) AS 'subreddit_count' FROM
                        (
                            SELECT DISTINCT author, subreddit_id
                            FROM comments_constrained
                            WHERE NOT author='[deleted]'
                        ) AS author_reddits
                    GROUP BY author) AS only_n_reddits
                    WHERE subreddit_count<=p_n;
                END;
            """);
    }

    private static void addPotentialInteractionsProcedure(Statement statement) throws SQLException {
        statement.addBatch("""
                CREATE PROCEDURE get_interactions(
                    IN p_redditor VARCHAR(20)
                )
                    READS SQL DATA
                    BEGIN
                        SELECT DISTINCT author
                        FROM test_db.comments_constrained
                        WHERE link_id IN
                        (
                            SELECT link_id
                            FROM test_db.comments_constrained
                            WHERE author=p_redditor
                        )
                        AND author NOT IN ('[deleted]', p_redditor);
                    END;
            """);
    }

    private static void addHighLowSubredditScoreProcedures(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE PROCEDURE subreddit_highscores ()
                READS SQL DATA
                BEGIN
                    SELECT
                        get_subreddit_name(subreddit_id) AS subreddit,
                        SUM(score) AS total_score
                    FROM comments_constrained
                    GROUP BY subreddit
                    ORDER BY total_score DESC;
                END;
            """);
        statement.addBatch("""
            CREATE PROCEDURE subreddit_lowscores ()
                READS SQL DATA
                BEGIN
                    SELECT
                        get_subreddit_name(subreddit_id) AS subreddit,
                        SUM(score) AS total_score
                    FROM comments_constrained
                    GROUP BY subreddit
                    ORDER BY total_score;
                END;
            """);
    }


    private static void addHighLowScoreProcedures(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE PROCEDURE user_highscores ()
                READS SQL DATA
                BEGIN
                    SELECT author, SUM(score) AS total_score
                    FROM comments_constrained
                    WHERE NOT author='[deleted]'
                    GROUP BY author
                    ORDER BY total_score DESC;
                END;
            """);
        statement.addBatch("""
            CREATE PROCEDURE user_lowscores ()
                READS SQL DATA
                BEGIN
                    SELECT author, SUM(score) AS total_score
                    FROM comments_constrained
                    WHERE NOT author='[deleted]'
                    GROUP BY author
                    ORDER BY total_score;
                END;
            """);
    }

    private static void addParticipatedInFromPost(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE PROCEDURE subreddits_from_link_id (
                IN p_link_id VARCHAR(10)
            )
                READS SQL DATA
                    BEGIN
                    SELECT DISTINCT get_subreddit_name(subreddit_id) AS subreddit
                        FROM comments_constrained
                        WHERE author IN
                            (SELECT DISTINCT author
                            FROM comments_constrained
                            WHERE link_id = p_link_id
                            AND NOT author='[deleted]'
                            );
                    END;
            """);
    }

    private static void addPostsContainingFunction(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE FUNCTION posts_containing(
                p_search_term VARCHAR(50)
            )
                RETURNS INT
                READS SQL DATA
                BEGIN
                    RETURN  (
                        SELECT COUNT(*) FROM
                            (SELECT * FROM comments_constrained
                            WHERE body LIKE CONCAT('%', p_search_term, '%'))
                        AS contains_term
                    );
                END;
            """);
    }

    private static void addAveragePostsPerDayInSubreddit(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE FUNCTION posts_per_day(
                p_subreddit_name VARCHAR(24)
            )
                RETURNS FLOAT(10, 3)
                READS SQL DATA
                BEGIN
                    RETURN
                        (SELECT AVG(posts_in_day) AS avg_per_day FROM (
                            SELECT COUNT(posted_date) AS posts_in_day
                            FROM comment_date_view
                            WHERE subreddit_name = p_subreddit_name
                            GROUP BY posted_date)
                        AS posts_per_day);
                END;
            """);
    }

    private static void addCommentDateView(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE VIEW comment_date_view AS
                SELECT
                    id,
                    get_subreddit_name(comments_constrained.subreddit_id)
                        AS subreddit_name,
                    epoch_to_date(comments_constrained.created_utc)
                        AS posted_date
                FROM comments_constrained;
            """);
    }

    /**
     * <p>Adds a CREATE FUNCTION statement to the statement batch</p>
     *
     * <p>
     *     The added function is called `epoch_to_date` and takes a epoch time as a parameter (INT).
     *     It then converts it into a date and returns the date. Note that the date returned is based on the database's
     *     host's time zone.
     * </p>
     *
     * @param statement The statement to add the batch operation to.
     * @throws SQLException If bad SQL stuff
     */
    private static void addEpochToDateFunction(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE FUNCTION
                epoch_to_date(p_epoch INT)
                RETURNS DATE
                READS SQL DATA
                BEGIN
                    RETURN (DATE(FROM_UNIXTIME(p_epoch)));
                END;
            """);
    }

    /**
     * <p>Adds a CREATE FUNCTION statement to the statement batch</p>
     *
     * <p>
     *     The added function is called `user_posts` and takes a username as a parameter.
     *     It then returns the total number of posts the user has made in the comments known to the database.
     * </p>
     *
     * @param statement The statement to add the batch operation to.
     * @throws SQLException If bad SQL stuff
     */
    private static void addGetTotalPostsFunc(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE FUNCTION
                user_posts(p_username VARCHAR(20))
                RETURNS INT
                READS SQL DATA
                BEGIN
                    RETURN
                        (SELECT COUNT(*)
                            FROM comments_constrained
                            WHERE author=p_username
                        );
                END;
            """.trim()
        );
    }

    /**
     * <p>Adds two CREATE FUNCTION statement to the statement batch</p>
     *
     * <p>
     *     The added functions are called `get_subreddit_name` and `get_subreddit_id`. The functions take a subreddit ID
     *     (VARCHAR(10)) and subreddit name (VARCHAR(20)), respectively.
     *     It then returns the name or ID matching the passed name or ID.
     * </p>
     *
     * @param statement The statement to add the batch operation to.
     * @throws SQLException If bad SQL stuff
     */
    private static void addSubredditIdAndNameTranslatorFunctions(Statement statement) throws SQLException {
        statement.addBatch("""
            CREATE FUNCTION
                get_subreddit_name(p_subreddit_id VARCHAR(10))
                RETURNS VARCHAR(24)
                READS SQL DATA
                BEGIN
                    RETURN
                        (
                        SELECT subreddit_name
                        FROM subreddits_constrained
                        WHERE subreddit_id = p_subreddit_id
                        );
                END;
            """);

        statement.addBatch("""
            CREATE FUNCTION
                get_subreddit_id(p_subreddit_name VARCHAR(24))
                RETURNS VARCHAR(10)
                READS SQL DATA
                BEGIN
                    RETURN (
                        SELECT subreddit_id
                        FROM subreddits_constrained
                        WHERE subreddit_name = p_subreddit_name
                    );
                END;
            """);
    }
}
