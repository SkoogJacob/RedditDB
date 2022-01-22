package db_accessors;

import java.util.Optional;

public class DataType {
    public final org.mariadb.jdbc.client.DataType type;
    private final String typeOptions;

    public DataType(org.mariadb.jdbc.client.DataType type) {
        this.type = type;
        typeOptions = null;
    }

    public DataType(org.mariadb.jdbc.client.DataType type, String[] typeOptions) {
        this.type = type;
        StringBuilder optionString = new StringBuilder();
        for (int i = 0; i < typeOptions.length; i++) {
            optionString.append("'").append(typeOptions[i])
                    .append("'");
            if (i != typeOptions.length - 1) optionString.append(", ");
        }
        this.typeOptions = optionString.toString();
    }

    public Optional<String> getTypeOptions() {
        return typeOptions == null ? Optional.empty() : Optional.of(typeOptions);
    }
}
