package org.lisasp.basics.jre.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public interface FileFacade {
    void put(Path file, byte... b) throws IOException;

    void append(Path file, byte... b) throws IOException;

    byte[] get(Path file) throws IOException;

    boolean exists(Path path);

    void createDirectories(Path path) throws IOException;

    Stream<Path> find(Path basePath, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher) throws IOException;
}
