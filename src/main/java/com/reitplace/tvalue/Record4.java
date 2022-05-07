package com.reitplace.tvalue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Record4 {

    Location4 location = null;
    Product product = null;
    String upcCode = null;
    int qty = -1;

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Record4() {

    }

    public Record4(Location4 loc, Product prod) {
        this.location = loc;
        this.product = prod;
    }

    public Location4 getLocation() {
        return this.location;
    }

    public void setLocation(Location4 loc) {
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
        // Loc.Tag, Loc.TagNo.,	Loc.Subtag,	Loc.SubNo.,	Loc.Section,UPCCode,Quantity,TimeStamp
        if (location != null && product != null) {

            String t1 = location.getLocT1() != null ? Config.locationCodeMap.get(location.getLocT1())+"-"+location.getLocT1Value() : "";
            String t2 = location.getLocT2() != null ? Config.locationCodeMap.get(location.getLocT2())+"-"+location.getLocT2Value() : "";
            String t3 = location.getLocT3() != null ? Config.locationCodeMap.get(location.getLocT3())+"-"+location.getLocT3Value() : "";

            buf.append(t1).append(",");
            buf.append(t2).append(",");
            buf.append(t3).append(",");
            buf.append(product.getUpc()).append(",");
            buf.append(product.getQty()).append(",");
            buf.append(timeStamp).append("\n");
        }
        return buf.toString();
    }

}
