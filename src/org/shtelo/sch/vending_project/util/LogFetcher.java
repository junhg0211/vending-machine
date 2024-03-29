package org.shtelo.sch.vending_project.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 로그를 순서대로 불러오기 위한 클래스입니다.
 */
public class LogFetcher {
    private String lastDate;
    private String lastTimestamp;
    private int lastIndex;
    private boolean end; // 불러올 과거의 기록이 없는지에 대한 여부
    private boolean firstIndex;
    private boolean firstTimestamp;

    public LogFetcher() {
        this.lastDate = null;
        this.lastTimestamp = null;
        this.lastIndex = -1;
        this.end = false;
        this.firstTimestamp = true;
        this.firstIndex = true;
    }

    /**
     * 현재 읽고 있는 로그 이전의 로그를 읽도록 설정합니다.
     */
    public void updateLast() {
        if (end)
            return;

        if (this.firstTimestamp && this.firstIndex)
            this.updateLastDate();

        if (this.firstIndex)
            this.updateLastTimestamp();

        this.updateLastIndex();
    }

    /**
     * 이전 실행일 로그 확인하도록 변경하기
     */
    private void updateLastDate() {
        File file = new File("res/log");
        String[] filenames = file.list();
        assert filenames != null;
        Arrays.sort(filenames);
        this.firstTimestamp = false;

        if (this.lastDate == null) {
            this.lastDate = filenames[filenames.length - 1];
            return;
        }

        int index = -1;
        for (int i = 0; i < filenames.length; i++) {
            if (filenames[i].equals(this.lastDate)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            updateLast();
            return;
        }

        if (index == 0) {
            this.end = true;
            return;
        }

        this.end = index == 1;
        this.lastDate = filenames[index - 1];
        this.lastTimestamp = null;
        this.lastIndex = -1;
    }

    /**
     * 이전 실행 로그 확인하도록 변경하기
     */
    private void updateLastTimestamp() {
        File file = new File(String.format("res/log/%s", lastDate));
        String[] filenames = file.list();
        assert filenames != null;
        Arrays.sort(filenames);
        this.firstTimestamp = false;

        if (this.lastTimestamp == null) {
            this.lastTimestamp = filenames[filenames.length - 1];
            return;
        }

        int index = -1;
        for (int i = 0; i < filenames.length; i++) {
            if (filenames[i].equals(this.lastTimestamp)) {
                index = i;
                break;
            }
        }

        if (index <= 0) {
            updateLastDate();
            return;
        }

        this.firstTimestamp = index == 1;
        this.lastTimestamp = filenames[index - 1];
        this.lastIndex = -1;
    }

    /**
     * 이전 로그 확인해도록 변경하기
     */
    private void updateLastIndex() {
        if (lastTimestamp == null)
            return;

        File file = new File(String.format("res/log/%s/%s", lastDate, lastTimestamp));

        this.firstIndex = false;

        if (this.lastIndex > 0) {
            this.firstIndex = --this.lastIndex == 0;
            return;
        }

        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));

            while (reader.readLine() != null)
                count++;
            this.firstIndex = count == 1;
            this.lastIndex = count - 1;

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 현재 읽는 것으로 설정되어있는 로그를 반환합니다.
     * @return 현재 읽는 것으로 설정되어있는 로그
     */
    public String getCurrent() {
        if (this.lastDate == null || this.lastTimestamp == null || this.lastIndex == -1) {
            updateLast();
        }

        String result;

        File file = new File(String.format("res/log/%s/%s", this.lastDate, this.lastTimestamp));
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            for (int i = 0; i < this.lastIndex; i++)
                reader.readLine();
            result = reader.readLine();

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 로그가 더 이상 존재하지 않는지 확인합니다.
     * @return 로그가 더 이상 존재하지 않는지에 대한 여부
     */
    public boolean isStill() {
        return !this.end;
    }

    /**
     * 테스트용 메인 메소드입니다.
     */
    public static void main(String[] args) {
        LogFetcher fetcher = new LogFetcher();

        while (!fetcher.end) {
            fetcher.updateLast();
            System.out.printf("%s %s %s%n", fetcher.lastDate, fetcher.lastTimestamp, fetcher.lastIndex);
        }
    }
}
