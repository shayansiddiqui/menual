package de.fbl.menual.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.fbl.menual.utils.Evaluator;

public class FoodItem implements Serializable {
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

    public static FoodItem getMockFoodItem() {
        Map<String, String> comments = new HashMap<>();
        comments.put("comment1", "Boo, this meal contains too much sugar");
        comments.put("comment2", "Bad news, this meal contains too much sugar");
        double[] statisticsValue = {0.0, 88.0, 100.0, 100.0, 60.0, 84.0, 5.0, 0.0, 13.0, 6.0, 0.0, 0.0, 2.35, 2.9677, 1.2444, 0.0474, 0.002644444444444444, 0.187, 0.009023076923076924, 0.0, 0.19054545454545455, 0.13807692307692307, 2.9254615384615383, 0.17338461538461536, 0.0, 0.07055, 0.085, 0.0, 0.027540000000000002, -1.0, 0.0439875, 0.0076500000000000005, 0.17631428571428573, 0.03452307692307693, 0.03413076923076923, -1.0, 0.154275, 0.20713846153846155};
        String[] statisticText = Evaluator.getStatistics("Pork Knuckle", new double[]{1, 1}, new int[]{1, 1}, 1);
        String img = "https://d2eawub7utcl6.cloudfront.net/images/nix-apple-grey.png";
        return new FoodItem("Pork Knuckle", "red", comments, img, null, statisticText, statisticsValue);
    }
}
