package org.shtelo.sch.vending_project.vending_machine.data_type;

/**
 * 상품에 대한 정보
 */
public class Product {
    private Kind kind;
    private int amount;

    public Kind getKind() {
        return this.kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
