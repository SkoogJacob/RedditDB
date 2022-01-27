package db.accessors;

import org.jetbrains.annotations.NotNull;

public record SQLAccessParams(@NotNull String url, @NotNull String username, @NotNull String password) {
}
