package org.shtelo.sch.vending_project.vending_machine;

/**
 * 상품에 대한 정보
 */
class Product {
    private Kind kind;
    private int amount;

    public Kind getKind() {
        return this.kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }
}
