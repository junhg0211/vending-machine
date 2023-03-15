package org.shtelo.sch.vending_project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Util {
    /**
     * res 폴더가 없다면 폴더를 만듭니다.
     */
    public static void assumeResFolder() {
        try {
            Files.createDirectories(Path.of("res"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
