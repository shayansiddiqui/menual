package de.fbl.menual.utils;

import java.util.ArrayList;

/**
 * Current mockups of the restaurant database
 */

public class RestaurantMocks {

    private static ArrayList<Restaurant> restaurants=new ArrayList<>();

    public static ArrayList<Restaurant> getRestaurants(){
        if(restaurants.isEmpty()){
            restaurants.add(new Restaurant("Cantineria","500m","","Cafe"));
            restaurants.add(new Restaurant("Ristorante Passione Italiana","900m","","Italian"));
            restaurants.add(new Restaurant("Poseidon","1000m","","Greek"));
            restaurants.add(new Restaurant("Gasthof Neuwirt","1200m","","German"));
            restaurants.add(new Restaurant("Gasthof zur Mühle","1500m","", "German"));
        }
        return restaurants;
    }

}
