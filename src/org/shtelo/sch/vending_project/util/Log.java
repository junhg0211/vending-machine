package org.shtelo.sch.vending_project.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HHmmssSSS");
    private static final DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String SOLD = "상품판매";
    public static final String INSERT_CASH = "현금투입";

    /**
     * 로그 디렉토리가 없다면 생성합니다.
     */
    private static String ensureLogDirectory(LocalDateTime date) {
        Util.ensureResFolder();

        try {
            Files.createDirectories(Path.of("res/log"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String directory = String.format("res/log/%s", dateFormat.format(date));

        try {
            Files.createDirectories(Path.of(directory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return directory;
    }

    /**
     * 로그를 작성합니다. 모든 로그에 대한 일반적인 로깅 솔루션을 제공합니다.
     * @param message 로그로 남겨질 메시지
     */
    public static void writeLog(String type, String message) {
        LocalDateTime date = LocalDateTime.now();
        String directory = ensureLogDirectory(date);

        String logString = String.format("%s [%s] %s", datetimeFormat.format(date), type, message);

        String path = String.format("%s/%s.log", directory, timeFormat.format(date));
        File file = new File(path);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(logString);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(logString);
    }
}