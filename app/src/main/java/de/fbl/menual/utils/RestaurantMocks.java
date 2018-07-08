package de.fbl.menual.utils;

public class RestaurantMocks {
    Restaurant[] restaurants;

    public RestaurantMocks() {
        restaurants[0]= new Restaurant("Onkel Luu's Asia Imbiss","100m","","Chinese");
        restaurants[1]= new Restaurant("Cantineria","500m","","Cafe");
        restaurants[2]= new Restaurant("Ristorante Passione Italiana","900m","","Italian");
        restaurants[3]= new Restaurant("Poseidon","1000m","","Greek");
        restaurants[4]= new Restaurant("Gasthof Neuwirt","1200","","German");
        restaurants[5]= new Restaurant("Gasthof zur Mühle","1500m","", "German");
    }

    public Restaurant[] getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Restaurant[] restaurants) {
        this.restaurants = restaurants;
    }
}
