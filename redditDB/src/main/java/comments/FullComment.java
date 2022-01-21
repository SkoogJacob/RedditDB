package comments;

import java.util.Base64;
import java.util.Date;
import com.google.gson.annotations.JsonAdapter;

public class FullComment {
    private String id; // COMMENT ID, not user ID
    private String name; // Equals prefix + '_' + id;
    private String author; // Username associated with id

    private String parentID;
    private String linkID;

    private String body; // The comment body

    private String subredditID;
    private String subreddit; // Name associate with subredditID

    private int score;
    private Date createdUTC; // Time created UTC
}
