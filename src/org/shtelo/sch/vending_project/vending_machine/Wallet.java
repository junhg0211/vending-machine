package org.shtelo.sch.vending_project.vending_machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.shtelo.sch.vending_project.Util;

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
    private int fiveThousands;

    Wallet() {
        this.tens = 5;
        this.fifties = 5;
        this.hundreds = 5;
        this.fiveHundreds = 5;
        this.thousands = 5;
        this.fiveThousands = 5;
    }

    /**
     * 파일에 저장되어있는 지갑을 불러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    static Wallet getWallet() {
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
}
