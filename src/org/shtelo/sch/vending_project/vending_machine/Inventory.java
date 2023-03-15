package org.shtelo.sch.vending_project.vending_machine;

import java.util.LinkedList;

/**
 * 하나의 상품 대한 인벤토리 슬롯
 */
public class Inventory {
    private LinkedList<Product> juices;

    public static Inventory getDefault() {
        Inventory inventory = new Inventory();

        LinkedList<Product> juices = new LinkedList<>();

        Product product;
        Kind kind;

        product = new Product();
        product.setAmount(5);
        kind = new Kind();
        kind.setName("물");
        kind.setPrice(450);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(5);
        kind = new Kind();
        kind.setName("커피");
        kind.setPrice(500);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(5);
        kind = new Kind();
        kind.setName("이온음료");
        kind.setPrice(550);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(5);
        kind = new Kind();
        kind.setName("고급커피");
        kind.setPrice(700);
        product.setKind(kind);
        juices.add(product);

        product = new Product();
        product.setAmount(5);
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
