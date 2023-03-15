package org.shtelo.sch.vending_project.vending_machine.data_type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.shtelo.sch.vending_project.Util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * 하나의 상품 대한 인벤토리 슬롯
 */
public class Inventory {
    private LinkedList<Product> juices;

    /**
     * 파일에 저장되어있는 인벤토리를 불러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    public static Inventory getInventory() {
        Inventory inventory;
        String INVENTORY_PATH = "res/inventory.json";

        Util.assumeResFolder();

        try {
            FileReader reader = new FileReader(INVENTORY_PATH);
            Gson gson = new Gson();
            inventory = gson.fromJson(reader, Inventory.class);
            reader.close();
        } catch (FileNotFoundException e) {
            // 파일이 없으면 기본값으로 만든다
            inventory = Inventory.getDefault();
            try {
                FileWriter writer = new FileWriter(INVENTORY_PATH);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(inventory, writer);
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inventory;
    }

    public static Inventory getDefault() {
        Inventory inventory = new Inventory();

        LinkedList<Product> juices = new LinkedList<>();

        Product product;
        Kind kind;

        product = new Product();
        product.setAmount(3);
        kind = new Kind();
        kind.setName("물");
        kind.setPrice(450);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(3);
        kind = new Kind();
        kind.setName("커피");
        kind.setPrice(500);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(3);
        kind = new Kind();
        kind.setName("이온음료");
        kind.setPrice(550);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(3);
        kind = new Kind();
        kind.setName("고급커피");
        kind.setPrice(700);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(3);
        kind = new Kind();
        kind.setName("탄산음료");
        kind.setPrice(750);
        product.setKind(kind);
        juices.add(product);

        inventory.setJuices(juices);
        return inventory;
    }

    public java.util.List<Product> getJuices() {
        return this.juices;
    }

    public void setJuices(LinkedList<Product> juices) {
        this.juices = juices;
    }
}
