package hanu.a2_1901040128.models;

public class Products {
    private String thumbnail ;
    private String name;
    private int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private int unitPrice;
    private int quantity;
    private int sumPrice;

    public Products(int id, String thumbnail, String name, int unitPrice, int quantity, int sumPrice) {
        this.ID = id;
        this.thumbnail = thumbnail;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.sumPrice = sumPrice;
    }

    public Products(int id, String thumbnail, String name, int unitPrice) {
        this.ID = id;
        this.thumbnail = thumbnail;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(int sumPrice) {
        this.sumPrice = sumPrice;
    }
}

