package db_accessors.data_types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a varchar data type.
 */
public class Varchar implements DataType {
    private final String size;

    /**
     * Constructs a varchar datatype with the passed size.
     * @param size The size of the varchar.
     */
    public Varchar(int size) {
        assert size > 0 && size <= 10_000;
        this.size = Integer.toString(size);
    }

    @NotNull
    @Override
    public String getName() {
        return "varchar";
    }

    @NotNull
    @Override
    public Optional<String> getTypeParams() {
        return Optional.of(size);
    }
}
