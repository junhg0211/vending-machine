package org.shtelo.sch.vending_project.vending_machine.data_type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.shtelo.sch.vending_project.util.Util;

import java.io.*;

/**
 * 화폐 보관함 클래스
 */
public class Wallet {
    private int tens;
    private int fifties;
    private int hundreds;
    private int fiveHundreds;
    private int thousands;
    private int cash;
    private static final String WALLET_PATH = "res/wallet.json";

    Wallet() {
        this.tens = 5;
        this.fifties = 5;
        this.hundreds = 5;
        this.fiveHundreds = 5;
        this.thousands = 5;
        this.cash = 0;
    }

    /**
     * 파일에 저장되어있는 지갑을 불러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    public static Wallet getWallet() {
        Wallet wallet;
        Util.ensureResFolder();

        try {
            FileReader reader = new FileReader(WALLET_PATH);
            Gson gson = new Gson();
            wallet = gson.fromJson(reader, Wallet.class);
            reader.close();
        } catch (FileNotFoundException e) {
            // 파일이 없으면 기본값으로 만든다
            wallet = Wallet.getDefault();
            wallet.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return wallet;
    }

    private static Wallet getDefault() {
        return new Wallet();
    }

    /**
     * 지갑에 파라미터에 해당하는 만큼의 화폐를 투입한 것으로 처리합니다.
     * 화폐를 투입한 후에는 기본 주소에 있는 파일에 상태값을 저장합니다.
     * @param tens 10원짜리 화폐의 개수
     * @param fifties 50원짜리 화폐의 개수
     * @param hundreds 100원짜리 화폐의 개수
     * @param fiveHundreds 500원짜리 화폐의 개수
     * @param thousands 1000원짜리 화폐의 개수
     */
    public void insertCash(int tens, int fifties, int hundreds, int fiveHundreds, int thousands) {
        this.tens += tens;
        this.fifties += fifties;
        this.hundreds += hundreds;
        this.fiveHundreds += fiveHundreds;
        this.thousands += thousands;

        save();
    }

    public String toString() {
        return String.format(
            "<Wallet %d, %d, %d, %d, %d>",
            tens, fifties, hundreds, fiveHundreds, thousands);
    }

    /**
     * `amount` 만큼의 잔돈을 `wallet` 에서 거슬러줍니다.
     * 출력되는 배열에는 10, 50, 100, ..., 1000의 순서로 5가지 화폐권의 개수가 표시됩니다.
     * 만약 모종의 이유로 잔돈을 거를 수 없다면 출력되는 배열의 5번 인덱스에 처리하지 못한 잔돈의 액수를 기록합니다.
     * 잔돈을 거스른 후에는 상태를 파일에 저장합니다.
     */
    public int[] change(int amount) {
        int[] changes = new int[6];

        while (amount >= 1000 && thousands > 0) {
            changes[4]++;
            thousands--;
            amount -= 1000;
        }
        while (amount >= 500 && fiveHundreds > 0) {
            changes[3]++;
            fiveHundreds--;
            amount -= 500;
        }
        while (amount >= 100 && hundreds > 0) {
            changes[2]++;
            hundreds--;
            amount -= 100;
        }
        while (amount >= 50 && fifties > 0) {
            changes[1]++;
            fifties--;
            amount -= 50;
        }
        while (amount >= 10 && tens > 0) {
            changes[0]++;
            tens--;
            amount -= 10;
        }
        changes[5] = amount;

        save();

        return changes;
    }

    /**
     * 기본 주소에 객체의 상태값을 저장합니다.
     */
    public void save() {
        try {
            FileWriter writer = new FileWriter(WALLET_PATH);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int getTens() {
        return tens;
    }

    public int getFifties() {
        return fifties;
    }

    public int getHundreds() {
        return hundreds;
    }

    public int getFiveHundreds() {
        return fiveHundreds;
    }

    public int getThousands() {
        return thousands;
    }

    public void setTens(int tens) {
        this.tens = tens;
    }

    public void setFifties(int fifties) {
        this.fifties = fifties;
    }

    public void setHundreds(int hundreds) {
        this.hundreds = hundreds;
    }

    public void setFiveHundreds(int fiveHundreds) {
        this.fiveHundreds = fiveHundreds;
    }

    public void setThousands(int thousands) {
        this.thousands = thousands;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
