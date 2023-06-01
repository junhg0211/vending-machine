package org.shtelo.sch.vending_project.vending_machine.data_type;

/**
 * 상품 종류 dataclass.
 * 상품의 이름과 가격을 저장합니다.
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

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
