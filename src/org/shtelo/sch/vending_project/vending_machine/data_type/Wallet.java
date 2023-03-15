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

    Wallet() {
        this.tens = 5;
        this.fifties = 5;
        this.hundreds = 5;
        this.fiveHundreds = 5;
        this.thousands = 5;
    }

    /**
     * 파일에 저장되어있는 지갑을 불러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    public static Wallet getWallet() {
        Wallet wallet;
        String WALLET_PATH = "res/wallet.json";

        Util.assumeResFolder();

        try {
            FileReader reader = new FileReader(WALLET_PATH);
            Gson gson = new Gson();
            wallet = gson.fromJson(reader, Wallet.class);
            reader.close();
        } catch (FileNotFoundException e) {
            // 파일이 없으면 기본값으로 만든다
            wallet = Wallet.getDefault();
            try {
                FileWriter writer = new FileWriter(WALLET_PATH);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(wallet, writer);
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return wallet;
    }

    private static Wallet getDefault() {
        return new Wallet();
    }

    public void insertCash(int tens, int fifties, int hundreds, int fiveHundreds, int thousands) {
        this.tens += tens;
        this.fifties += fifties;
        this.hundreds += hundreds;
        this.fiveHundreds += fiveHundreds;
        this.thousands += thousands;

        System.out.println(this);
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

        System.out.println(this);

        return changes;
    }
}
