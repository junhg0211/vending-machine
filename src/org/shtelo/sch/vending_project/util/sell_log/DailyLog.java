package org.shtelo.sch.vending_project.util.sell_log;

import java.util.HashMap;

/**
 * 하루치 판매 기록을 저장하는 클래스
 */
public class DailyLog {
    private final String date;
    private final HashMap<String, Integer> sells;
    private int sales;

    public DailyLog(String date) {
        this.date = date;
        this.sells = new HashMap<>();
        this.sales = 0;
    }

    public String getDate() {
        return date;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    /**
     * 특정 상품의 판매량을 반환한다.
     * @param product 상품 이름
     * @return 판매량
     */
    public int getSells(String product) {
        if (!sells.containsKey(product))
            return 0;

        return sells.get(product);
    }

    public void setSells(String product, int amount) {
        sells.put(product, amount);
    }

    public HashMap<String, Integer> getSells() {
        return sells;
    }
}