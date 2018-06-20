package de.fbl.menual.utils;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;

import de.fbl.menual.ScanHistory;
import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.api.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NutritionUtils {

    public ArrayList<String> list = new ArrayList<String>();
    private ApiInterface apiInterface;

    public void initiateApi(final String foodName){
        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        getNutrition(foodName);
    }

    private void getNutrition(final String foodName) {

        JsonObject httpQuery = new JsonParser().parse(
                "{" +
                        "\"query\":" + "\"" + foodName + "\"" +
                        ", " +
                        "\"timezone\":\"US/Eastern\"" +
                        "}").getAsJsonObject();

        Callback<JsonObject> callbackGetNutrition = new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //JsonObject lresponse = response.body();
                //System.out.println(response.body().toString());
                String sApiValues = response.body().toString();
                double[] apiValues = new double[32];
                String a = "";
                String[] splitApiValues = sApiValues.split("full_nutrients", -1);
                String[] subHaupt = splitApiValues[0].split(",", -1);
                String[] inhalt = {"nf_protein", "nf_total_fat", "nf_total_carbohydrate", "nf_sugars", "nf_dietary_fiber", "nf_saturated_fat"};
                for (int i = 0; i < subHaupt.length; i++) {
                    for (int j = 0; j < inhalt.length; j++) {
                        if (subHaupt[i].contains(inhalt[j])) {
                            if (subHaupt[i].substring(inhalt[j].length() + 3).equals("null"))
                                apiValues[j] = -1;
                            else
                                apiValues[j] = Double.parseDouble(subHaupt[i].substring(inhalt[j].length() + 3));
                        }
                    }
                }
                String[] subExtra = splitApiValues[1].split("\\}", -1);
                String[] inhaltExtra = {"attr_id\":645,", "attr_id\":646,"}; //645 monosaturated, 646 polysaturated
                for (int i = 0; i < subExtra.length; i++) {
                    for (int j = 0; j < inhaltExtra.length; j++) {
                        if (subExtra[i].contains(inhaltExtra[j])) {
                            if (subExtra[i].substring(inhaltExtra[j].length() + 11).equals("null"))
                                apiValues[j + 6] = -1;
                            else
                                apiValues[j + 6] = Double.parseDouble(subExtra[i].substring(inhaltExtra[j].length() + 11));
                        }
                    }
                }

                //Test Code
                String s = "";
                System.out.println("Food result for: " + foodName);
                System.out.println("The dish contains the following nutrients");
                for (double i : apiValues)
                    s += Double.toString(i) + "\n";
                for (int i = 8; i < apiValues.length; i++)
                    apiValues[i] = 0;
                System.out.println(s);
                Evaluator e = new Evaluator();
                int[] preferences = {1, 1, 1, 1, 1};
                System.out.println();
                System.out.println("The dish receives the following scores");
                int scores[] = e.evaluateDish(1, preferences, apiValues);
                for (int i = 0; i < scores.length; i++)
                    System.out.println(scores[i]);
                System.out.println();
                System.out.println("The dish receives the following colour");
                // feed information to Scan History Screen



                if (scores[0] > 100){
                    setList(foodName, 1);
                    System.out.println("green");

                }else {
                    if (scores[0] > 90) {
                        setList(foodName, 2);
                        System.out.println("yellow");
                    }else {
                        setList(foodName, 3);
                        System.out.println("red");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        };


        Call<JsonObject> callGetNutrition = apiInterface.getNutrition(httpQuery.toString());
        callGetNutrition.enqueue(callbackGetNutrition);

    }

    public void getComment(int[] scores, Evaluator e)
    {
        int[] sortedScores = scores.clone();
        Arrays.sort(sortedScores);

        String[] ingredients = {"", "proteins", "sugar", "fiber", "healthy fats", "vitamins"};
        String[] ingredientsUnhealthy = {"", "proteins", "sugar", "fiber", "saturated fats", "vitamins"};


        String comment1 = "";
        String comment2 ="";
        if (scores[0] > 100) {
            System.out.println("green");
            int max = sortedScores[sortedScores.length - 1];
            int max2 = sortedScores[sortedScores.length - 2];

            if (max > 110) {
                int result1st = e.indexOf(scores,max);
                String kriterium1 = ingredients[result1st];
                if (result1st != 0)
                    comment1 =("Awesome, this meal contains a lot of " + kriterium1 + "!");
            }
            if (max2 > 100) {
                int result2nd = e.indexOf(scores,max2);
                if(result2nd == 0)
                {
                    max2 = sortedScores[sortedScores.length - 3];
                    if(max2 > 100)
                        result2nd = e.indexOf(scores,max2);
                }
                String kriterium2 = ingredients[result2nd];
                if (result2nd != 0)
                    comment2 = ("Great, this meal contains a lot of " + kriterium2 + "!");
            }

        } else {
            if (scores[0] > 90) {
                System.out.println("yellow");
            } else {
                System.out.println("red");
            }
            int min = sortedScores[0];
            int min2 = sortedScores[1];
            if (min < 87) {
                //int result1st = Arrays.asList(scores).indexOf(min);
                int result1st = e.indexOf(scores, min);
                String kriterium1 = ingredientsUnhealthy[result1st];
                if (result1st != 0) {
                    if (result1st == 2)
                        comment1 = ("Boo, this meal contains too much " + kriterium1 + "!");
                    if (result1st == 4)
                        comment1 = ("Oh no, this meal contains too many " + kriterium1 + "!");
                    if (result1st == 3 || result1st == 5)
                        comment1 = ("This meal contains not enough " + kriterium1 + "!");
                    if (result1st == 1) {
                        if (e.getDetails()[0] == 1) {
                            comment1 = ("This meal has too many carbohydrates!");
                        }
                        if (e.getDetails()[1] == 1) {
                            comment1 = ("This meal has too many fats!");
                        }
                        int[][] mahlzeit = e.getMahlzeit();
                        int mealtime = 1; //Has to be replaced with actual mealtime
                        String[] mealtype = {"breakfast", "lunch", "dinner", "snack"};
                        comment1 = ("The optimal " + mealtype[mealtime] + " should only consist of a maximum proportion of " + mahlzeit[mealtime][3] + "% fat and at most " + mahlzeit[mealtime][4] + "% carbohydrates!");
                    }
                }
            }


            if (min2 < 87) {
                int result2nd = e.indexOf(scores, min2);

                if(result2nd == 0) {
                    min2 = sortedScores[2] ;
                    if(min2<87)
                        result2nd = e.indexOf(scores,min2);
                }
                String kriterium2 = ingredientsUnhealthy[result2nd];

                if (result2nd != 0) {

                    if (result2nd == 2)
                        comment2 = ("Bad news, this meal contains too much " + kriterium2 + "!");
                    if (result2nd == 4)
                        comment2 =("Bad news, this meal contains too many " + kriterium2 + "!");
                    if (result2nd == 3 || result2nd == 5)
                        comment2 =("Unfortunately, this meal contains not enough " + kriterium2 + "!");
                }

                if (result2nd == 1) {
                    if (e.getDetails()[0] == 1) {
                        comment2 =("This meal has too many carbohydrates!");
                    }
                    if (e.getDetails()[1] == 1) {
                        comment2 =("This meal has too many fats!");
                    }
                    int[][] mahlzeit = e.getMahlzeit();
                    int mealtime = 1; //Has to be replaced with actual mealtime
                    String[] mealtype = {"breakfast", "lunch", "dinner", "snack"};
                    comment2 =("The optimal " + mealtype[mealtime] + " should only consist of a maximum proportion of " + mahlzeit[mealtime][2] + "% fat and at most " + mahlzeit[mealtime][3] + "% carbohydrates!");
                }


            }
        }
        if(!comment1.isEmpty())
            System.out.println(comment1); //you need to fetch only these 2 comments and display them on the screen
        if(!comment2.isEmpty())
            System.out.println(comment2);



    }

    public void setList(String foodName, int category) {
        // category description: 1 = green ; 2 = yellow ; 3 = red
        ArrayList<String> localList = new ArrayList<String>();
        localList.add(foodName);
        list = localList;
    }

    public ArrayList<String> getList() {
        System.out.println(list.toString());
        return list;
    }
}
