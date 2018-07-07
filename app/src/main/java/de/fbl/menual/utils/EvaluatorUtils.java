package de.fbl.menual.utils;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.fbl.menual.models.FoodItem;
import retrofit2.Response;

public class EvaluatorUtils {
    public static FoodItem evaluateResponse(Response<JsonObject> response, String foodName){
        String temp = response.body().toString();
        if(!temp.contains("match any of your food")) //In this would be true, the API didn't find the food
        {
            if(temp.contains("food_name")) {
                JsonObject lresponse = response.body().getAsJsonArray("foods").get(0).getAsJsonObject();
                String foodQuery = lresponse.get("food_name").toString();
                String foodQueryLow = foodQuery.trim().toLowerCase();
                String foodNameLow = foodName.trim().toLowerCase();
                if (foodQueryLow.contains(foodNameLow)) //only outputs food that is matches/is contained in the query
                {
                    System.out.println(response.body().toString());
                    String sApiValues = response.body().toString();

                    //if(sApiValues.contains(foodName)) {
                    double[] apiValues = new double[34];
                    for(int i = 0;i<apiValues.length;i++)
                    {
                        apiValues[i] = -1;
                    }
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
                    String[] inhaltExtra = {"attr_id\":645,", "attr_id\":646,","attr_id\":318,","attr_id\":324,","attr_id\":323,","attr_id\":430,","attr_id\":404,","attr_id\":405,","attr_id\":406,","attr_id\":415,","attr_id\":417,","attr_id\":410,","attr_id\":???,","attr_id\":418,","attr_id\":401,","attr_id\":307,","attr_id\":???,","attr_id\":306,","attr_id\":301,","attr_id\":305,","attr_id\":304,","attr_id\":303,","attr_id\":313,","attr_id\":309,","attr_id\":317,","attr_id\":605,","attr_id\":212,"};
                    //645 monosaturated, 646 polysaturated, next: Vitamin A(IU),D(IU),E(mg),K(µg),B1(mg),B2(mg),Niacin(mg),B6(mg),Folat(µg),Pantothenic acid(mg), Biotin(currently not in nutritionX),B12(µg),C(mg)
                    //Minerals Natrium, Chlorid, Kalium, Calcium, Phosphor,Magnesium,Eisen, Fluorid(Microgramm), Zink, Selen(Microgramm)
                    //Transfats, Fructose
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
                    apiValues = Evaluator.nutritionXgetCorrectUnits(apiValues);
                    double[] apiValuesForStatistic = apiValues.clone();
                    foodQuery = Evaluator.capitalize(foodQuery);


                    //Test Code
                    //String s = "";
                    // System.out.println("Food result for: " + foodQuery);
                    // System.out.println("The dish contains the following nutrients");
                    // for (double i : apiValues)
                    //    s += Double.toString(i) + "\n";
                    //for (int i = 8; i < apiValues.length; i++) //This call ignores minerals and vitamines
                    //    apiValues[i] = 0;
                    //System.out.println(s);
                    Evaluator e = new Evaluator();
                    int[] preferences = {1, 1, 1, 1, 1};
                    //System.out.println();
                    //System.out.println("The dish receives the following scores");
                    int scores[] = e.evaluateDish(1, preferences, apiValues);
                    int[] scoresForStatistic = scores.clone();
                    int mealtype = 1; //mockup, replace with real value later
                    double[] staticsValues = Evaluator.getStatisticsValues(apiValuesForStatistic,scoresForStatistic,mealtype); //relevant for statistic
                    String[] statisticText = Evaluator.getStatistics(foodQuery, apiValuesForStatistic,scoresForStatistic, mealtype); //relevant for statistic
                    for(int i = 0; i<statisticText.length;i++)
                    {
                        if(i != 0) {
                            if (staticsValues[i] != -1) {
                                System.out.println("["+i+"] " + statisticText[i] + "  " + staticsValues[i]);
                            }
                        }
                        else
                        {
                            System.out.println("["+i+"] " + statisticText[i]);
                        }

                    }
                    System.out.println();
                    // for (int i = 0; i < scores.length; i++)
                    //     System.out.println(scores[i]);
                    System.out.println();
                    System.out.println("The dish receives the following colour");

                    Map<String, String> comments = getComment(scores, e);
                    String result = null;
                    if (scores[0] > 100) {
                        result = "green";
                        System.out.println("green");
                    } else {
                        if (scores[0] > 90) {
                            result = "yellow";
                            System.out.println("yellow");
                        } else {
                            result = "red";
                            System.out.println("red");
                        }

                    }
                    return new FoodItem(foodQuery,result, comments);
                }
            }
        }
        return null;
    }

    public static Map<String, String> getComment(int[] scores, Evaluator e) {
        int[] sortedScores = scores.clone();
        Arrays.sort(sortedScores);

        String[] ingredients = {"", "proteins", "sugar", "fiber", "healthy fats", "vitamins","minerals"};
        String[] ingredientsUnhealthy = {"", "proteins", "sugar", "fiber", "unhealthy fats", "vitamins","minerals"};

        String colour = ""; //relevant for statistic
        String comment1 = ""; //relevant for statistic
        String comment2 = ""; //relevant for statistic
        if (scores[0] > 100) {
            System.out.println("green");
            colour = "green";
            int max = sortedScores[sortedScores.length - 1];
            int max2 = sortedScores[sortedScores.length - 2];

            if (max > 110) {
                int result1st = e.indexOf(scores, max);
                String kriterium1 = ingredients[result1st];
                if (result1st != 0)
                    comment1 = ("Awesome, this meal contains a lot of " + kriterium1 + "!");
            }
            if (max2 > 100) {
                int result2nd = e.indexOf(scores, max2);
                if (result2nd == 0) {
                    max2 = sortedScores[sortedScores.length - 3];
                    if (max2 > 100)
                        result2nd = e.indexOf(scores, max2);
                }
                String kriterium2 = ingredients[result2nd];
                if (result2nd != 0)
                    comment2 = ("Great, this meal contains a lot of " + kriterium2 + "!");
            }

        } else {
            if (scores[0] > 90) {
                System.out.println("yellow");
                colour = "yellow";
            } else {
                System.out.println("red");
                colour = "red";
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

                if (result2nd == 0) {
                    min2 = sortedScores[2];
                    if (min2 < 87)
                        result2nd = e.indexOf(scores, min2);
                }
                String kriterium2 = ingredientsUnhealthy[result2nd];

                if (result2nd != 0) {

                    if (result2nd == 2)
                        comment2 = ("Bad news, this meal contains too much " + kriterium2 + "!");
                    if (result2nd == 4)
                        comment2 = ("Bad news, this meal contains too many " + kriterium2 + "!");
                    if (result2nd == 3 || result2nd == 5)
                        comment2 = ("Unfortunately, this meal contains not enough " + kriterium2 + "!");
                }

                if (result2nd == 1) {
                    if (e.getDetails()[0] == 1) {
                        comment2 = ("This meal has too many carbohydrates!");
                    }
                    if (e.getDetails()[1] == 1) {
                        comment2 = ("This meal has too many fats!");
                    }
                    int[][] mahlzeit = e.getMahlzeit();
                    int mealtime = 1; //Has to be replaced with actual mealtime
                    String[] mealtype = {"breakfast", "lunch", "dinner", "snack"};
                    comment2 = ("The optimal " + mealtype[mealtime] + " should only consist of a maximum proportion of " + mahlzeit[mealtime][2] + "% fat and at most " + mahlzeit[mealtime][3] + "% carbohydrates!");
                }
            }
        }
        Map<String, String> comments = new HashMap<>();
        comments.put("comment1", comment1);
        comments.put("comment2", comment2);

        return comments;

    }

}
