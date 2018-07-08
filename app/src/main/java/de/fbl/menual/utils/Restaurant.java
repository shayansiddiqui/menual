package de.fbl.menual.utils;

public class Restaurant {

    private String name;
    private String location;
    private String menu;
    private String kitchenType;
    public Restaurant(String pname, String plocation, String pmenu, String pkitchentype) //Menu has to be replaced with an actual menu
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
