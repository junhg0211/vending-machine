package org.shtelo.sch.vending_project.vending_machine;

/**
 * 상품 종류
 */
class Kind {
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
