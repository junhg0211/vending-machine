package org.shtelo.sch.vending_project.vending_machine.data_type;

/**
 * 상품 종류
 */
public class Kind {
    private String name;
    private int price;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }
}
