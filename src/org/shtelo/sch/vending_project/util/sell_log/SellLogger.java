package org.shtelo.sch.vending_project.util.sell_log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 판매 실적을 기록하는 로거
 */
public class SellLogger {
    private static final String SELL_LOG_PATH = "res/sell_log.json";
    private final ArrayList<DailyLog> dailyLogs;

    SellLogger() {
        this.dailyLogs = new ArrayList<>();
    }

    public static SellLogger getLogger() {
        SellLogger result;

        try {
            FileReader reader = new FileReader(SELL_LOG_PATH);
            Gson gson = new Gson();
            result = gson.fromJson(reader, SellLogger.class);
            reader.close();
        } catch (FileNotFoundException e) {
            result = new SellLogger();
            result.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 로거의 내용을 파일로 기록합니다.
     */
    public void save() {
        try {
            FileWriter writer = new FileWriter(SELL_LOG_PATH);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * <code>date</code> 날짜의 로그를 불러옵니다.
     * @param date 로그의 날짜
     * @return <code>date</code> 날짜의 로그
     */
    public DailyLog getLog(String date) {
        for (DailyLog log : this.dailyLogs) {
            if (log.getDate().equals(date))
                return log;
        }

        DailyLog log = new DailyLog(date);
        this.dailyLogs.add(log);
        return log;
    }

    /**
     * 로거의 작동을 테스트 · 디버그하기 위한 <code>main</code> 함수
     */
    public static void main(String[] args) {
        SellLogger logger = SellLogger.getLogger();

        DailyLog log = logger.getLog("20230524");
        log.setSells("물", 200);
        log.setSales(450 * 200);

        logger.save();
    }
}
