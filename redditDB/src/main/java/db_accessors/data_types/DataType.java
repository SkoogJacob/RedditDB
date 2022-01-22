package db_accessors.data_types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * This interface is the base of an SQL data type, which has a name,
 * and sometimes some parameters, that are used when creating a column
 * using them.
 */
public interface DataType {
    /**
     * Gets the name of the data type.
     *
     * @return The name of the data type (i.e. int, varchar etc.)
     */
    @NotNull
    String getName();

    /**
     * Gets the type params for the DataType, if such exist.
     *
     * Example of data types using this is, for example 'varchar' and
     * 'enum'
     *
     * @return An optional containing Type parameters if such exist.
     */
    @NotNull
    Optional<String> getTypeParams();
}
