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
                          @Json(name = "created_utc") int createdUTC) {
    public FullComment(
            String id, String name,
            String author, String parentID,
            String linkID, String body,
            String subredditID, String subreddit,
            int score, int createdUTC
    ) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.parentID = parentID;
        this.linkID = linkID;
        this.body = body;
        this.subredditID = subredditID;
        this.subreddit = subreddit;
        this.score = score;
        this.createdUTC = createdUTC;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getParentID() {
        return parentID;
    }

    public String getLinkID() {
        return linkID;
    }

    public String getBody() {
        return body;
    }

    public String getSubredditID() {
        return subredditID;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public int getScore() {
        return score;
    }

    public int getCreatedUTC() {
        return createdUTC;
    }

    @Override
    public String toString() {
        return "FullComment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", parentID='" + parentID + '\'' +
                ", linkID='" + linkID + '\'' +
                ", body='" + body + '\'' +
                ", subredditID='" + subredditID + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", score=" + score +
                ", createdUTC=" + createdUTC +
                '}';
    }
}
