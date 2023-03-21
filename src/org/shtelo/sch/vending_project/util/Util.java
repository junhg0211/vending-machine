package org.shtelo.sch.vending_project.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    /**
     * res 폴더가 없다면 폴더를 만듭니다.
     */
    public static void ensureResFolder() {
        try {
            Files.createDirectories(Path.of("res"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 문자열에 숫자가 포함되어있는지 여부를 확인합니다.
     * @param string 포함 여부를 확인할 문자열
     * @return 숫자 포함 여부
     */
    public static boolean hasNumber(String string) {
        for (int i = 0; i < string.length(); i++) {
            if ("0123456789".indexOf(string.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 문자열에 키보드에서 입력할 수 있는 특수문자가 포함되어있는지 여부를 확인합니다.
     * @param string 포함 여부를 확인할 문자열
     * @return 특수문자 포함 여부
     */
    public static boolean hasSpecialCharacter(String string) {
        for (int i = 0; i < string.length(); i++) {
            if ("!@#$%^&*()_+|-=\\,.<>/?'\"[]{};:".indexOf(string.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 문자열을 암호화하여 출력합니다.
     * 비밀번호 검증과 파일 저장에 활용합니다.
     * @param password 암호화할 비밀번호
     * @return 암호화된 비밀번호
     */
    public static String encrypt(String password) {
        password = "순천향대학교 소프트웨어융합대학 컴퓨터소프트웨어공학과 " + password + " 20223519 전한결 2023학년도 JAVA프로그래밍 실습 과제";

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] byteString = password.getBytes();
        for (int i = 0; i < 5000; i++) {
            byteString = digest.digest(byteString);
        }

        return byteToString(byteString);
    }

    /**
     * 바이트 문자열을 문자열로 변환합니다.
     * @param byteString 변환할 바이트 문자열
     * @return 변환된 문자열
     */
    private static String byteToString(byte[] byteString) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : byteString) {
            builder.append(String.format("%02x", aByte));
        }
        return builder.toString();
    }
}
