package comments;

import com.squareup.moshi.Json;

/**
 * <p>
 * Represents the parts of a reddit comment from the source data that we are interested in.
 * </p>
 * <p>
 * Takes up some 80 bytes in pointers, 280 bytes for the ID fields,
 * up to 20 000 bytes for the body field, 8 bytes for score and created total.
 * </p>
 * <b>Total memory is up towards 20 368 bytes, or about 20kB</b>
 */
public record FullComment(String id, String name, String author,
                          @Json(name = "parent_id") String parentID,
                          @Json(name = "link_id") String linkID,
                          String body,
                          @Json(name = "subreddit_id") String subredditID,
                          String subreddit, int score,
                          @Json(name = "created_utc") int createdUTC) {}
