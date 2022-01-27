package comments;

import com.squareup.moshi.Json;
import db.accessors.CommentType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * <p>
 * Represents the parts of a reddit comment from the source data that we are interested in.
 * </p>
 * <p>
 * Takes up some 80 bytes in pointers, 280 bytes for the ID string fields,
 * up to 20 000 bytes for the body string field, 8 bytes for score and created UTC together.
 * </p>
 * <b>Total memory is up towards 20 368 bytes, or about 20kB</b>
 */
public record FullComment(String id, String name, String author,
                          @Json(name = "parent_id") String parentID,
                          @Json(name = "link_id") String linkID,
                          String body,
                          @Json(name = "subreddit_id") String subredditID,
                          String subreddit, int score,
                          @Json(name = "created_utc") int createdUTC) {
    public LocalDate getDateCreated() {
        return Instant.ofEpochSecond(createdUTC).atZone(ZoneId.systemDefault()).toLocalDate();
    }
    public CommentType getCommentType() {
        return CommentType.fromCommentName(this.name);
    }
    public CommentType getParentType() {
        return CommentType.fromCommentName(this.parentID);
    }
}
