package org.shtelo.sch.vending_project.vending_machine.data_type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.shtelo.sch.vending_project.util.Util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * 자판기에 있는 상품들의 목록을 저장하는 클래스
 */
public class Inventory {
    private static final String INVENTORY_PATH = "res/inventory.json";
    private LinkedList<Product> juices;

    /**
     * 파일에 저장되어있는 인벤토리를 불러옵니다.
     * 만약 파일이 존재하지 않는다면 코드에 설정된 기본값으로 파일을 생성하고 불러옵니다.
     */
    public static Inventory getInventory() {
        Inventory inventory;

        Util.ensureResFolder();

        try {
            FileReader reader = new FileReader(INVENTORY_PATH, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            inventory = gson.fromJson(reader, Inventory.class);
            reader.close();
        } catch (FileNotFoundException e) {
            // 파일이 없으면 기본값으로 만든다
            inventory = Inventory.getDefault();
            inventory.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return inventory;
    }

    /**
     * <code>Inventory</code>의 기본값을 생성합니다.
     * @return Inventory 기본값
     */
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

    /**
     * 기본 주소에 객체의 상태값을 저장합니다.
     */
    public void save() {
        try {
            FileWriter writer = new FileWriter(INVENTORY_PATH);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public java.util.List<Product> getJuices() {
        return this.juices;
    }

    public void setJuices(LinkedList<Product> juices) {
        this.juices = juices;
    }
}
