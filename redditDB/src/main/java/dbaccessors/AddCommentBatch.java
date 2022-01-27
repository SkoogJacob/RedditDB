package dbaccessors;

import comments.FullComment;

import java.sql.SQLException;

public class AddCommentBatch {
    protected static void addBatch(CommentStatement statement, FullComment comment) throws SQLException {
        statement.setString(1, comment.id());
        statement.setString(2, comment.parentID());
        statement.setString(3, comment.linkID());
        statement.setString(4, comment.name().split("_")[0]);
        statement.setString(5, comment.author());
        statement.setString(6, comment.body());
        statement.setString(7, comment.subredditID());
        statement.setInt(8, comment.score());
        statement.setInt(9, comment.createdUTC());
        statement.addBatch();
    }
}
