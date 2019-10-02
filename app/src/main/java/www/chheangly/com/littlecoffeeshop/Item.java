package www.chheangly.com.littlecoffeeshop;

public class Item {

    String title;
    String price;
    String imgURL;
    String type;
    int qty;
    Boolean fav = false;

    public Item(String title, String price, String imgURL, String type, Boolean fav) {
        this.title = title;
        this.price = price;
        this.imgURL = imgURL;
        this.type = type;
        this.fav = fav;
    }

    public Item(String title, String price, String imgURL, String type) {
        this.title = title;
        this.price = price;
        this.imgURL = imgURL;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public Boolean getFav() {
        return fav;
    }

    public void setFav(Boolean fav) {
        this.fav = fav;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
