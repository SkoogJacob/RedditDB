package db_accessors;

import comments.FullComment;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * This class presents the type of a reddit comment in a user readable way.
 */
public class Type {
    @NotNull
    private final Types type;

    public Type(String type) {
        type = type.replaceAll("_", "");
        this.type = translateToEnum(type);
    }
    public Type(FullComment comment) {
        String typeString = comment.id().split("_")[0];
        this.type = translateToEnum(typeString);
    }

    /**
     * Allows to create a Type object from any ID string.
     * @param name The ID (with type) of the comment
     * @return
     */
    public static Type fromName(String name) {
        String typeString = name.split("_")[0];
        return new Type(typeString);
    }
    private Types translateToEnum(String typeString) {
        Types t;
        switch (typeString.toLowerCase(Locale.ROOT)) {
            case "t1" -> t = Types.t1;
            case "t2" -> t = Types.t2;
            case "t3" -> t = Types.t3;
            case "t4" -> t = Types.t4;
            case "t5" -> t = Types.t5;
            case "t6" -> t = Types.t6;
            default -> throw new IllegalArgumentException("This type is not recognized!");
        }
        return t;
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
