package comments;

import com.squareup.moshi.Json;

/**
 * Represents the parts of a reddit comment from the source data that we are interested in.
 * <p>
 * Takes up some 80 bytes in pointers, 40*
 */
public record FullComment(String id, String name, String author,
                          @Json(name = "parent_id") String parentID,
                          @Json(name = "link_id") String linkID,
                          String body,
                          @Json(name = "subreddit_id") String subredditID,
                          String subreddit, int score,
                          @Json(name = "created_utc") int createdUTC) {}
