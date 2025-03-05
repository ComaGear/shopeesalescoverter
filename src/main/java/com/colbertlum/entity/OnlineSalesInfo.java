package com.colbertlum.entity;

public class OnlineSalesInfo extends ListingStock{

    private String parentSku;
    private double price;
    private String productId;
    private String variationId;
    private int foundRow;
    private String productName;
    private String variationName;
    

    @Override
    public String getName() {
        return getProductName() + "-" + getVariationName();
    }

    @Override
    public void setName(String productName) {
        String[] split = productName.split("\\-");
        setProductName(split[0]);
        setVariationName(split[1]);
    }

    

    @Override
    public String getSku() {
        if(super.getSku() == null) {
            if(parentSku != null) super.setSku(getParentSku());
            if(getParentSku() != null) return getParentSku();
            return "";
        }
        return super.getSku();
    }

    @Override
    public void setSku(String sku) {
        if(sku == null && getParentSku() != null) {
            setSku(getParentSku());
        }
        super.setSku(sku);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public int getFoundRow() {
        return foundRow;
    }

    public void setFoundRow(int foundRow) {
        this.foundRow = foundRow;
    }

    public String getParentSku() {
        return parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public int getQuantity() {
        return super.getStock();
    }

    public void setQuantity(int quantity) {
        super.setStock(quantity);
    }

}
