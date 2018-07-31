package de.fbl.menual.utils;

/**
 * Restaurant Class. A restaurant contains a name, location, kitchentype and Menu
 */
public class Restaurant {

    private String name;
    private String location;
    private String menu;
    private String kitchenType;
    public Restaurant(String pname, String plocation, String pmenu, String pkitchentype) //Menu is a previous user scanned menu and represents the String after response after text detection
    {
        name = pname;
        location=plocation;
        menu=pmenu;
        kitchenType = pkitchentype;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getMenu() {
        return menu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKitchenType() {
        return kitchenType;
    }

    public void setKitchenType(String kitchenType) {
        this.kitchenType = kitchenType;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
