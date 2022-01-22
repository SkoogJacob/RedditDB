package db_accessors;

import java.util.ArrayList;

/**
 * Represents a Schema in SQL
 */
public class Schema {
    String collation;
    ArrayList<Table> tables;

    public Schema() {
        collation = "utf8mb4_unicode_ci";
        tables = new ArrayList<>();
    }
}
