package com.reitplace.tvalue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordV2 {

    private String user = null;
    LocationV2 location = new LocationV2();
    Product product = new Product();

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public RecordV2() {

    }

    public RecordV2(String user, LocationV2 loc, Product prod) {
        this.setUser(user);
        this.location = loc;
        this.product = prod;
    }

    public LocationV2 getLocation() {
        return this.location;
    }

    public void setLocation(LocationV2 loc) {
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

    public boolean isUserSet() {
        return (this.user != null) ? true : false;
    }

    public boolean isLocationSet() {
        return (this.location != null && this.location.getPrimary() != null && this.location.getPrimaryValue() != null)? true : false;
    }

    public boolean isUPCSet() {
        return (this.product != null && this.product.getUpc() != null)?true:false;
    }

    public boolean isQtySet() {
        return (this.product != null && this.product.getQty() > -1)?true:false;
    }

    public boolean isRecordComplete() {
        if (isUserSet() && isLocationSet() && isUPCSet() && isQtySet()) return true;
        else return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("User => ").append(this.user).append(",");
        buf.append("Location => ").append(this.location).append(",");
        buf.append("Product => ").append(this.product).append(".");

        return buf.toString();
    }

    public String toDisplay(Config config) {
        StringBuffer buf = new StringBuffer();
        buf.append(this.location.toSearchOutput(config)).append(", ");
        buf.append(this.product.toDisplay()).append(".");

        return buf.toString();
    }

    public String toFormatForFile(Config config) {
        StringBuffer buf = new StringBuffer();
        String timeStamp = sdf.format(new Date());
        // LOC_PRI_LBL, LOC_PRI_VAL, LOC_SEC_LBL, LOC_SEC_VAL, UPC, QTY, CREATED_BY, CREATED_AT, IS_ACTIVE
        if (location != null && product != null) {

            buf.append(config.locationCodeMap.get(location.getPrimary())).append(",");
            buf.append(location.getPrimaryValue()).append(",");
            buf.append(config.locationCodeMap.get(location.getSecondary())).append(",");
            buf.append(location.getSecondaryValue()).append(",");
            buf.append(product.getUpc()).append(",");
            buf.append(product.getQty()).append(",");
            buf.append(user).append(",");
            buf.append(timeStamp).append(",").append("true");
            buf.append("\n");
        }
        return buf.toString();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
