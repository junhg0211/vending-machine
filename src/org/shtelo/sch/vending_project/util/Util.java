package org.shtelo.sch.vending_project.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static boolean hasNumber(String string) {
        for (int i = 0; i < string.length(); i++) {
            if ("0123456789".indexOf(string.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSpecialCharacter(String string) {
        for (int i = 0; i < string.length(); i++) {
            if ("!@#$%^&*()_+|-=\\,.<>/?'\"[]{};:".indexOf(string.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    public static String encrypt(String password) {
        // TODO: 암호화 구현하기
        return password;
    }
}
