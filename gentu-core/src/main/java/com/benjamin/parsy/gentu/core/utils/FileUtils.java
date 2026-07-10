package com.benjamin.parsy.gentu.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility class providing helper methods for file system operations.
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("FileUtils is a utility class and cannot be instantiated");
    }

    /**
     * Deletes all entries (files and subdirectories) inside the given directory without removing the directory itself.
     *
     * @param path the directory whose content must be deleted
     * @throws IOException if {@code path} is not a directory, or if a deletion fails
     */
    public static void deleteContent(Path path) throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IOException("%s is not a directory".formatted(path.toString()));
        }

        deleteRecursively(path);
    }

    /**
     * Recursively deletes all entries (files and subdirectories) inside the given directory without removing the directory itself.
     *
     * @param path the directory to recurse into
     * @throws IOException if listing or deleting an entry fails
     */
    public static void deleteRecursively(Path path) throws IOException {

        try (Stream<Path> stream = Files.list(path)) {
            for (Path child : stream.toList()) {
                if (Files.isDirectory(child)) {
                    deleteRecursively(child);
                }
                Files.deleteIfExists(child);
            }
        }
    }

    /**
     * Ensures the given directory exists and is empty.
     * If the directory already exists, its content is deleted; otherwise the directory is created.
     *
     * @param directory the directory to create or clear
     * @throws IOException if clearing or creating the directory fails
     */
    public static void forceCreateDirectory(Path directory) throws IOException {

        if (Files.exists(directory)) {
            FileUtils.deleteContent(directory);
        } else {
            Files.createDirectories(directory);
        }
    }

}
