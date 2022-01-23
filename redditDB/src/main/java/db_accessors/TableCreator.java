package db_accessors;

/**
 * This class will contain static methods that return SQL query strings
 * that create the different tables I have designed for this assignment.
 *
 * This is hard coded to make this specific assignment faster, but obviously
 * taking the time to implement infrastructure to specify database and table structure
 * through config files and the like would be necessary for this code
 * to be usable outside this specific assignment.
 */
public class TableCreator {
    public static String createSubredditsConstrained() {

    }
    public static String createSubredditsUnconstrained() {

    }
    public static String createRedditCommentsConstrained() {
        return "create table reddit_comments_constrained\n" +
                "(\n" +
                "    id           VARCHAR(10)                               NOT NULL,\n" +
                "    parent_id    VARCHAR(10)                               NULL,\n" +
                "    link_id      VARCHAR(10)                               NOT NULL,\n" +
                "    type         ENUM ('t1', 't2', 't3', 't4', 't5', 't6') NOT NULL,\n" +
                "    author       VARCHAR(20) default '[deleted]'           NULL,\n" +
                "    body         TEXT        default '[deleted]'           NULL,\n" +
                "    subreddit_id VARCHAR(10)                               NOT NULL,\n" +
                "    score        INT                                       NULL,\n" +
                "    created_utc  DATETIME                                  NOT NULL,\n" +
                "    CONSTRAINT reddit_comments_constrained_pk\n" +
                "        PRIMARY KEY (id),\n" +
                "    CONSTRAINT rcc___fk_author_exists\n" +
                "        FOREIGN KEY (author) REFERENCES reddit_users_constrained (username)\n" +
                "            ON UPDATE CASCADE ON DELETE SET DEFAULT,\n" +
                "    CONSTRAINT rcc___fk_subreddit_exists\n" +
                "        FOREIGN KEY (subreddit_id) REFERENCES subreddits_constrained (subreddit_id)\n" +
                "            ON UPDATE CASCADE ON DELETE CASCADE\n" +
                ");";
    }
    public static String createRedditCommentsUnconstrained() {

    }
}
