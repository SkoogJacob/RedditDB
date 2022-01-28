package db.accessors;

import comments.FullComment;

import java.sql.SQLException;

public final class AddCommentBatch {
    private AddCommentBatch() { } // No constructor
    public static void addBatch(CommentStatement statement, FullComment comment) throws SQLException {
        statement.setID(comment.id());
        statement.setParentID(comment.parentID());
        statement.setLinkID(comment.linkID());
        statement.setType(comment.name().split("_")[0]);
        statement.setAuthor(comment.author());
        statement.setBody(comment.body());
        statement.setSubredditID(comment.subredditID());
        statement.setScore(comment.score());
        statement.setCreatedUTC(comment.createdUTC());
        statement.addBatch();
    }
}
