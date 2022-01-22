package db_accessors.data_types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Int implements DataType {

    @NotNull
    @Override
    public String getName() {
        return "int";
    }

    @NotNull
    @Override
    public Optional<String> getTypeParams() {
        return Optional.empty();
    }
}
