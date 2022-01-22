package db_accessors.data_types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Enum implements DataType {
    private final String possibleValues;

    public Enum(String[] possibleValues) {
        StringBuilder values = new StringBuilder();
        for (String value : possibleValues) {
            values.append("'").append(value).append("', ");
        }
        this.possibleValues = values.substring(0, values.length() - 2);
    }

    @NotNull
    @Override
    public String getName() {
        return "enum";
    }

    @NotNull
    @Override
    public Optional<String> getTypeParams() {
        return Optional.of(possibleValues);
    }
}
