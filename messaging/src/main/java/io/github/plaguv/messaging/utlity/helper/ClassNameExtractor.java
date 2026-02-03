package io.github.plaguv.messaging.utlity.helper;

import jakarta.annotation.Nonnull;

public final class ClassNameExtractor {

    private static final String REGEX = "(?<!^)([A-Z])";

    private ClassNameExtractor() {}

    /**
     * Extracts an upper-lower case differentiating class name and adds a separator
     * @param clazz the class from which the name will be extracted from
     * @return the class name to lower-case with a default '_' separator in between
     */
    public static @Nonnull String extractUpperLower(@Nonnull Class<?> clazz) {
        return clazz.getSimpleName().replaceAll(REGEX, "_$1").toLowerCase();
    }

    /**
     * Extracts an upper-lower case differentiating class name and adds a separator
     * @param clazz the class from which the name will be extracted from
     * @param separator the character that will be used to separate the differentiating words
     * @return the class name to lower-case with the {@code separator} in between
     */
    public static @Nonnull String extractUpperLower(@Nonnull Class<?> clazz, char separator) {
        return clazz.getSimpleName().replaceAll(REGEX, String.valueOf(separator).concat("$1")).toLowerCase();
    }
}