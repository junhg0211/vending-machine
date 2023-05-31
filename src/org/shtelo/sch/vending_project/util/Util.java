package org.shtelo.sch.vending_project.util;

import org.shtelo.sch.vending_project.vending_machine.subwindow.AdminPrompt;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    public static final String PASSWORD_PATH = "res/password.txt";

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
     *
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
     *
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
     *
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
     *
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

    public static String passwordCondition(String password, String rePassword) {
        if (password.equals("") && rePassword.equals("")) {
            return "-";
        }

        if (password.length() < 8) {
            return "비밀번호는 8자리 이상으로 설정해야 합니다.";
        }

        if (!Util.hasNumber(password)) {
            return "하나 이상의 숫자를 포함해야 합니다.";
        }

        if (!Util.hasSpecialCharacter(password)) {
            return "하나 이상의 특수문자를 포함해야 합니다.";
        }

        if (!rePassword.equals(password)) {
            return "재확인 비밀번호가 같지 않습니다.";
        }

        return "";
    }

    /**
     * 비밀번호 조건을 확인하고 버튼의 enabled 여부를 결정합니다.
     */
    public static void checkPasswordCondition(JPasswordField passwordField, JPasswordField rePasswordField, JLabel conditionLabel, JButton confirmButton) {
        confirmButton.setEnabled(false);

        String password = String.valueOf(passwordField.getPassword());
        String rePassword = String.valueOf(rePasswordField.getPassword());

        String message = Util.passwordCondition(password, rePassword);

        conditionLabel.setText(message);
        confirmButton.setEnabled(message.isEmpty());
    }

    /**
     * 입력한 비밀번호로 로그인을 진행합니다.
     */
    public static String confirmLogin(JPasswordField passwordField) {
        String password;
        password = String.valueOf(passwordField.getPassword());

        return confirmLogin(password);
    }

    public static String confirmLogin(String password) {
        password = Util.encrypt(password);

        File file = new File(PASSWORD_PATH);
        String correct;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            correct = reader.readLine();

            reader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!password.equals(correct)) {
            Log.writeLog(Log.ADMIN_FAIL, "관리자 로그인 시도 중 비밀번호가 틀렸습니다.");
            return "비밀번호가 틀립니다.";
        }

        Log.writeLog(Log.ADMIN_LOGIN, "비밀번호를 사용하여 관리자 콘솔에 로그인하였습니다.");
        return "";
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param password 변경할 비밀번호
     */
    public static void setPassword(String password) {
        // 비밀번호를 암호화
        String encrypted = Util.encrypt(password);

        // 파일에 저장
        File file = new File(AdminPrompt.PASSWORD_PATH);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(encrypted);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
