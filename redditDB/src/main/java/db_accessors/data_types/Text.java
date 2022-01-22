package db_accessors.data_types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Text implements DataType {
    @NotNull
    @Override
    public String getName() {
        return "text";
    }

    @NotNull
    @Override
    public Optional<String> getTypeParams() {
        return Optional.empty();
    }
}
