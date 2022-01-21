package db_accessors;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Type {
    @NotNull
    private final Types type;

    public Type(String type) {
        type = type.replaceAll("_", "");
        switch (type.toLowerCase(Locale.ROOT)) {
            case "t1" -> this.type = Types.t1;
            case "t2" -> this.type = Types.t2;
            case "t3" -> this.type = Types.t3;
            case "t4" -> this.type = Types.t4;
            case "t5" -> this.type = Types.t5;
            case "t6" -> this.type = Types.t6;
            default -> throw new IllegalArgumentException("This type is not recognized!");
        }
    }
    public String type() {
        String typeString = "Unknown";
        switch (this.type) {
            case t1 -> typeString = "t1";
            case t2 -> typeString = "t2";
            case t3 -> typeString = "t3";
            case t4 -> typeString = "t4";
            case t5 -> typeString = "t5";
            case t6 -> typeString = "t6";
        }
        return typeString;
    }
    public String getCommentType() {
        String commentType = "Unknown";
        switch (this.type) {
            case t1 -> commentType = "Comment";
            case t2 -> commentType = "Account";
            case t3 -> commentType = "Link";
            case t4 -> commentType = "Message";
            case t5 -> commentType = "Subreddit";
            case t6 -> commentType = "Award";
        }
        return commentType;
    }

    @Override
    public String toString() {
        return getCommentType();
    }

    /**
     * Enum for the different reddit comment types.
     * See https://www.reddit.com/dev/api
     */
    private enum Types {
        t1, t2, t3, t4, t5, t6
    }
}
