package com.reitplace.tvalue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {

    Location location = null;
    Product product = null;
    String upcCode = null;
    int qty = -1;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Record() {

    }

    public Record (Location loc, Product prod) {
        this.location = loc;
        this.product = prod;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location loc) {
        this.location = loc;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product prod) {
        this.product = prod;
    }

    public void resetProduct() {
        if (this.product != null) {
            this.product.reset();
        }
    }

    public void resetLocation() {
        if (this.location != null) {
            this.location.reset();
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("Location => ").append(this.location).append(",");
        buf.append("Product => ").append(this.product).append(".");

        return buf.toString();
    }

    public String toFormatForFile(Config config) {
        StringBuffer buf = new StringBuffer();
        String timeStamp = sdf.format(new Date());
        // Section, SectionNo,	Aisle,	AisleNo, UPCCode, Quantity, TimeStamp
        if (location != null && product != null) {

            String section = location.getSection() != null ? config.locationCodeMap.get(location.getSection())+"-"+location.getSectionValue() : "";
            String aisle = location.getAisle() != null ? config.locationCodeMap.get(location.getAisle())+"-"+location.getAisleValue() : "";

            buf.append(section).append(",");
            buf.append(aisle).append(",");
            buf.append(product.getUpc()).append(",");
            buf.append(product.getQty()).append(",");
            buf.append(timeStamp).append(",").append("ACTIVE");
            buf.append("\n");
        }
        return buf.toString();
    }

}
