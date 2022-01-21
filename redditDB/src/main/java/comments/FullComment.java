package comments;

import com.google.gson.annotations.SerializedName;

public class FullComment {
    private String id; // COMMENT ID, not user ID
    private String name; // Equals prefix + '_' + id;
    private String author; // Username associated with id

    @SerializedName("parent_id")
    private String parentID;
    @SerializedName("link_id")
    private String linkID;

    private String body; // The comment body

    @SerializedName("subreddit_id")
    private String subredditID;
    private String subreddit; // Name associate with subredditID

    private int score;
    @SerializedName("created_utc")
    private int createdUTC; // Time created UTC

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
