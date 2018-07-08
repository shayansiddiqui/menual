package de.fbl.menual.models;

import java.util.HashMap;
import java.util.Map;

public class FoodItem {
    private String foodName;
    private String result;
    private Map<String, String> comments;
    private String lowResPhoto;
    private String highResPhoto;
    String[] statisticText;
    double[] staticsValues;



    public FoodItem(String foodName, String result, Map<String, String> comments) {
        this.foodName = foodName;
        this.result = result;
        this.comments = comments;
    }

    public FoodItem(String foodName, String result, Map<String, String> comments, String lowResPhoto, String highResPhoto, String[] statisticText, double[] staticsValues) {
        this.foodName = foodName;
        this.result = result;
        this.comments = comments;
        this.lowResPhoto = lowResPhoto;
        this.highResPhoto = highResPhoto;
        this.statisticText = statisticText;
        this.staticsValues = staticsValues;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public String getLowResPhoto() {
        return lowResPhoto;
    }

    public void setLowResPhoto(String lowResPhoto) {
        this.lowResPhoto = lowResPhoto;
    }

    public String getHighResPhoto() {
        return highResPhoto;
    }

    public void setHighResPhoto(String highResPhoto) {
        this.highResPhoto = highResPhoto;
    }

    public String[] getStatisticText() {
        return statisticText;
    }

    public void setStatisticText(String[] statisticText) {
        this.statisticText = statisticText;
    }

    public double[] getStaticsValues() {
        return staticsValues;
    }

    public void setStaticsValues(double[] staticsValues) {
        this.staticsValues = staticsValues;
    }

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }

    public static FoodItem getMockFoodItem(){
        Map<String, String> comments=new HashMap<>();
        comments.put("comment1","Boo, this meal contains too much sugar");
        comments.put("comment2","Bad news, this meal contains too much sugar");
        return new FoodItem("Spaghetti", "red",comments);
    }
}