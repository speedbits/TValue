package com.reitplace.tvalue;

public class Product {
    private String upc = null;
    private int qty = -1;

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void addQty(int qty) {
        if (qty == -1) {
            this.qty = qty;
        } else {
            this.qty = this.qty + qty;
        }
    }

    public boolean isComplete() {
        if (upc != null && !"".equals(upc) && qty > -1) {
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        this.upc = null;
        this.qty = -1;
    }

    public String toString () {
        StringBuffer buf = new StringBuffer();
        buf.append("UPC => ").append(this.upc).append(", ");
        buf.append("Qty => ").append(this.qty);
        return buf.toString();
    }

    public String toDisplay() {
        StringBuffer buf = new StringBuffer();
        buf.append("UPC (").append(this.upc).append(")");
        buf.append(", Qty (").append(this.qty).append(")");
        return buf.toString();
    }
    public String toCompactString () {
        StringBuffer buf = new StringBuffer();
        buf.append("UPC");
        if (this.upc == null) buf.append("(None)"); else buf.append("(").append(this.upc).append(")");
        buf.append("\n");
        buf.append("Qty => ");
        if (this.qty == -1) buf.append("None"); else buf.append(this.qty);
        return buf.toString();
    }
}
