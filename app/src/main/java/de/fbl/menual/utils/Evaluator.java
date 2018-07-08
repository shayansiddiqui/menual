package de.fbl.menual.utils;
import java.util.Arrays;

/**
 * @author Christopher Harth-Kitzerow
 *
 */
public class Evaluator {
    private int[] makroverteilung = {200, 300, 500}; //proteine, Fett Kohlenhydrate Referenzmenge (DGE)
    private double[] gMakros = {200 / 4, 300 / 9, 500 / 4};
    private int gballaststoffe = 13;
    private double gfettO6 = (1000 * 0.025) / 9;
    private double gfett03 = (1000 * 0.005) / 9;
    private double[] mgVitamine = {0.9, 20, 13, 65, 1.1, 1.3, 1.3, 1.3, 300, 6, 45, 3, 100}; //A,D(Microgramm),E,K(Microgramm),B1,B2,Niacin ,B6, Folat(Microgramm), Pantothensäure, Biotin, B12(Microgramm), C
    private double[] mgMineralStoffe = {1500, 2300, 4000, 1000, 700, 325, 13, 3.5, 8, 65}; //Natrium, Chlorid, Kalium, Calcium, Phosphor,Magnesium,Eisen, Fluorid, Zink, Selen(Microgramm)
    private int[][] mahlzeit = {{25, 20, 25, 30}, {30, 35, 30, 30}, {25, 30, 25, 25}, {10, 10, 10, 10}}; //erster Identifier: Frühstück, Mitagessen, Abendessen, Snack. Zweiter Identifier: Energie,Proteine,Fett,KH
//laut der DGE sollte eine Mahlzeit 10% Zucker am Kalorienanteil nicht überschreiten

    private int[] details = new int[25]; //[0]: too many fats [1]: too many carbohydrates [2-14]: good amount of vitamine [15-24]: good amount of mineral

    public int[] getDetails() {
        return details;
    }

    public int[][] getMahlzeit() {
        return mahlzeit;
    }

    public int indexOf(int[] a, int value) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == value)
                return i;
        }
        return -1;
    }

    public static double[] nutritionXgetCorrectUnits(double[] apiValues) {
        apiValues[8] = apiValues[8] / 3 / 1000; //Calculating from IU to µg to mg
        apiValues[9] = apiValues[9] / 3; //Calculating from IU to µg
        //Vitamine E,B1,B2,Niacin, B6, Pantothenic acid, B12, C is already in mg, Vitamin K, Folat is already in µg
        apiValues[28] = apiValues[28] / 1000; //Fluoride from µg to mg
        for (int i = 8; i < 30; i++) {
            if (apiValues[i] < 0) {
                apiValues[i] = -1; //Reset unjustified calculations
            }
        }
        return apiValues;
    }

    /**
     * Evaluates a Health Score for a Dish. Standard score is 100. >100 = green, >90 = yellow, <90 = red, -1 = not enough information available
     *
     * @param userMahlzeit [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     * @param preferences  Importance Multiplier (standard: 1) for [0]: High amount of Proteins [1]: Low amount of fats
     *                     [2]: Low amount of Carbs [3]: Low amount of sugar [4] High amount of Ballaststoffe [5-...]: certain eating habits
     * @param aPiValues    Information on the dish provided by the API (-1 for no Information) [0]: Grams of Proteins [1]: Grams of Fats [2]: Grams of Carbohydrates
     *                     [3]: Grams of Sugar [4]: Grams of Ballaststoffe [5]: Grams of saturated Fats [6]: Grams of mono-unsaturated Fats [7]: Grams of poly-unsaturated Fat
     *                     [8-20]: Grams of each Vitamines in the same order as above [21-30]: Grams of Mineralstoffe in the same order as above  [31]: Trans fats(added with later version),
     *                     [32]:Fructose (added with later version) [33]: Food Category //currently not integrated
     * @return [0]: Total score [1]: Makroscore [2]: sugarscore [3]:ballaststoffscore [4]: Fatscore [5]: Vitaminscore [6]: mineralscore
     */
    public int[] evaluateDish(int userMahlzeit, int[] preferences, double[] aPiValues) {

        double score = 0;
        int counter[] = new int[4];

        //The following lines evaluate the macronutrients-ratio score (*2)
        double[] aPIgMakros = {aPiValues[0], aPiValues[1], aPiValues[2]};
        int[] makropreferences = {preferences[0], preferences[1], preferences[2]};
        int makroscore = eveluateMakros(userMahlzeit, makropreferences, aPIgMakros);
        for (int i = 0; i < 3; i++) {
            if (aPiValues[i] == -1) {
                makroscore = 0;
                counter[0] = 1;
            }
        }


        if (aPiValues[32] < 0)
            aPiValues[32] = 0; //Adjust invalid Fructose value
        //The following lines evaluate the sugar score (*3)
        int sugarscore = evaluateSugar(preferences[3], aPIgMakros, aPiValues[3], aPiValues[32]);
        int highSugar = 0;
        for (int i = 0; i < 4; i++) {
            if (aPiValues[i] == -1) {
                sugarscore = 0;
                counter[1] = 1;
            }
        }
        if (sugarscore < 75 && counter[1] == 0)
            highSugar++;

        //The following lines evaluate the fiber score
        int ballaststoffscore = evaluateBallaststoffe(userMahlzeit, preferences[4], aPiValues[4]);
        if (aPiValues[4] == -1) {
            ballaststoffscore = 0;
            counter[2] = 1;
        }

        if (aPiValues[31] < 0) //Adjust invalid Transfat value
        {
            aPiValues[31] = 0;
        }
        //Evaluates the healthyness of fats contained in the meal (*2)
        double[] aPIFatValues = {aPiValues[5], aPiValues[6], aPiValues[7], aPiValues[31]};
        int fatscore = evaluateFats(aPIFatValues);
        counter[3] = 3;
        for (int i = 5; i < 8; i++) {
            if (aPiValues[i] == -1) {
                aPiValues[i] = 0;
                if (counter[3] > 0)
                    counter[3]--;
            }

        }

        //Evaluates all Vitamins contained in the dish
        double[] aPIVitaminValues = Arrays.copyOfRange(aPiValues, 8, 21);
        for (int i = 8; i < 22; i++) {
            if (aPiValues[i] < 0)
                aPiValues[i] = 0;
        }
        int vitaminscore = evaluateVitamines(userMahlzeit, aPIVitaminValues);

        double[] aPIMineralValues = Arrays.copyOfRange(aPiValues, 21, 31);
        for (int i = 21; i < 30; i++) {
            if (aPiValues[i] < 0)
                aPiValues[i] = 0;
        }
        int mineralScore = evaluateMinerales(userMahlzeit, aPIMineralValues);

        //The followoing evaluations will be added here: Mineralstoffe, eating preferences/disorders


        score = makroscore * 2 * (1 - counter[0]) + sugarscore * (1 + (3 * highSugar)) * (1 - counter[1]) + ballaststoffscore * 1 * (1 - counter[2]) + fatscore * counter[3] + (vitaminscore - 100) + (mineralScore - 100);
        if (counter[0] * 2 - counter[1] * (2 + (2 * highSugar)) - counter[2] - (3 - counter[3]) == -10) {
            System.out.println("Not enough information on dish to evaluate");
            int a[] = {-1};
            return a;

        }
        score = score / (10 - (3 * (1 - highSugar)) - counter[0] * 2 - counter[1] * 1 - counter[2] - (3 - counter[3]));
        int[] scores = {(int) score, makroscore, sugarscore, ballaststoffscore, fatscore, vitaminscore, mineralScore};
       // System.out.println("total score, makros, sugar, ballast, fat, vitamins, minerals");
        return scores;
        //return (int) score;
    }

/**
 public int[][] evaluate multipleDishes(int userMahlzeit, int[] preferences, double[] aPiValues, String[] dishes)
 {
 int[][] foodresults = new int[dishes.length][6];
 for()
 }
 */


    /**
     * Returns a score for the Meal macronutrients composition based of recommended amounts and user preferences
     *
     * @param userMahlzeit [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     * @param preferences  Importance Multiplier (standard: 1) for: [0]:P [1]: F [2]: C
     * @param aPIgMakros   returned Makros for the meals from API call
     * @return
     */
    public int eveluateMakros(int userMahlzeit, int[] preferences, double[] aPIgMakros) {
        double score = 100;
        double APIgMakros[] = aPIgMakros.clone();
        double[] optimalgMakroverteilung = {gMakros[0] * mahlzeit[userMahlzeit][1] / 100, gMakros[1] * mahlzeit[userMahlzeit][2] / 100, gMakros[2] * mahlzeit[userMahlzeit][3] / 100};
        double[] optimalpMakroverteilung = optimalgMakroverteilung;
        double sum = 0;
        for (int i = 0; i < optimalgMakroverteilung.length; i++)
            sum += optimalgMakroverteilung[i];
        for (int i = 0; i < optimalgMakroverteilung.length; i++)
            optimalpMakroverteilung[i] = optimalgMakroverteilung[i] / sum; // optimales prozentuales Makroverhältnis

        double[] actualMakroverteilung = APIgMakros;
        sum = 0;
        for (int i = 0; i < APIgMakros.length; i++)
            sum += APIgMakros[i];
        for (int i = 0; i < APIgMakros.length; i++)
            actualMakroverteilung[i] = APIgMakros[i] / sum; // tatsächliches prozentuales Makroverhältnis

        double punishFats = (actualMakroverteilung[1] - optimalpMakroverteilung[1]) * 100 * preferences[0];
        double punishCarbs = (actualMakroverteilung[2] - optimalpMakroverteilung[2]) * 100 * preferences[0];

        if (actualMakroverteilung[0] > optimalpMakroverteilung[0])
            score = score + 0.5 * (actualMakroverteilung[0] - optimalpMakroverteilung[0]) * 100 * preferences[0]; //Pluspunkte für proteinreiche Mahlzeit
        if (actualMakroverteilung[1] > optimalpMakroverteilung[1])
            score = score - punishFats; //Strafpunkte für zu viele Fette
        if (actualMakroverteilung[2] > optimalpMakroverteilung[2])
            score = score - punishCarbs; //Strafpunkte für zu viele Kohlenhydrate

        if (punishFats > 20)
            details[0] = 1;
        if (punishCarbs > 20)
            details[1] = 1;

        return (int) score;
    }

    /**
     * @param preferences preferences for sugar, Standard is 1
     * @param aPIgMakros  grams of each macronutrient in the dish
     * @param aPIgSugar   grams of sugar in the dish
     * @return
     */
    public int evaluateSugar(int preferences, double[] aPIgMakros, double aPIgSugar, double apigFructose) {
        double score = 100;
        double sum = 0;
        for (int i = 0; i < aPIgMakros.length; i++) {
            sum += aPIgMakros[i];
        }
        double sugarRatio = ((aPIgSugar - apigFructose) / sum) - 0.1; //Up to 10% is ok. Everything bigger than 10 percent of sugar is punished. Fructose is not punished
        if (sugarRatio <= 0)
            return 100;
        score = 100 - (100 * sugarRatio) * 3 * preferences;
        if (score < 0)
            return 0;

        return (int) score;
    }

    public int evaluateBallaststoffe(int userMahlzeit, int preferences, double APIValue) {
        int score = 100;
        double recommendedB = gballaststoffe * mahlzeit[userMahlzeit][1] / 100; //recommended amount in grams of B for specific meal
        score = (int) (score - (recommendedB - APIValue) * 10 * preferences); //bonus points for more B, negative points for less
        return score;
    }

    /**
     * @param aPIValues [0]: saturated fats, [1]: unsaturated fats, [2]: polyunsaturated fats [3]: transfats
     * @return
     */
    public int evaluateFats(double[] aPIValues) {
        double score = 100;
        score += aPIValues[1];
        score += aPIValues[2] * 5;
        score -= aPIValues[0] * 10; //punishing score for saturated fats
        score -= aPIValues[3] * 15; //punishing score for trans fats
        if (score <= 0)
            score = 0;
        return (int) score;
    }

    /**
     * @param userMahlzeit
     * @param aPIValues    Same structure as mgVitamine, length: 13
     * @return [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     */
    public int evaluateVitamines(int userMahlzeit, double aPIValues[]) {
        double score = 100;
        int multiplier = mahlzeit[userMahlzeit][0];
        for (int i = 0; i < mgVitamine.length; i++) {
            details[2 + i] = 0;
            if (aPIValues[i] > mgVitamine[i] * multiplier / 100) {
                score += 5;
                details[2 + i] = 1;
            }
        }
        return (int) score;
    }

    public int evaluateMinerales(int userMahlzeit, double aPIValues[]) {
        double score = 100;
        int multiplier = mahlzeit[userMahlzeit][0];
        for (int i = 0; i < mgMineralStoffe.length; i++) {
            details[15 + i] = 0;
            if (aPIValues[i] > mgMineralStoffe[i] * multiplier / 100) {
                score += 5;
                if (i == 0)
                    score -= 10;
                details[15 + i] = 1;
            }
        }
        return (int) score;
    }

    public static String[] getStatistics(String foodName, double[] apiValues, int[] scores, int mealtype) {

        String[] statistic = new String[39];
        statistic[0] = Evaluator.getMealtypeString(mealtype) +":  " + foodName;
        statistic[1] = "Total Score";
        statistic[2] = "Macro Score";
        statistic[3] = "Sugar Score";
        statistic[4] = "Fiber Score";
        statistic[5] = "Fat Score";
        statistic[6] = "Vitamin Bonus";
        statistic[7] = "Mineral Bonus";

        statistic[8] = "P";
        statistic[9] = "F";
        statistic[10] = "C";
        statistic[11] = "S";

        statistic[12] = "Saturated Fats (Unhealthy)";
        statistic[13] = "Mono-unsaturated Fats (Healthy)";
        statistic[14] = "Poly-unsaturated Fats (Very Healthy)";
        statistic[15] = "Trans-Fats (Very Unhealthy)";

        statistic[16] = "Vitamin A";
        statistic[17] = "Vitamin D";
        statistic[18] = "Vitamin E";
        statistic[19] = "Vitamin K";
        statistic[20] = "Vitamin B1";
        statistic[21] = "Vitamin B2";
        statistic[22] = "Niacin (B3)";
        statistic[23] = "Vitamin B6";
        statistic[24] = "Folate (B9)";
        statistic[25] = "Pantothenic Acid (B5)";
        statistic[26] = "Biotin (B5)"; //Currently not integrated
        statistic[27] = "Vitamin B12";
        statistic[28] = "Vitamin C";

        statistic[29] = "Sodium";
        statistic[31] = "Chloride"; //currently not integrated
        statistic[30] = "Potassium";
        statistic[32] = "Calcium";
        statistic[33] = "Phosphorus";
        statistic[34] = "Magnesium";
        statistic[35] = "Iron";
        statistic[36] = "Fluoride";
        statistic[37] = "Zinc";
        statistic[38] = "Selenium";


        return statistic;
    }

    /**
     * Return detailed statistics for a dish
     *
     * @param apiValues apiValues as calculated in getNutrition(foodName)
     * @param scores    Scores as calculated in getNutrition(foodName)
     * @param mealtype  Mealtype selected by the user
     * @return Detailed statistics in this order:
     * [0]:mealtype: Breakfast (0),lunch(1),dinner(2),snack(3) [1]:total score
     * [2-7]:  macroScore,Sugar Score, Fiber Score, Fat score, Vitamin Bonus(Vitamin score-100), Minerals Bonus(Minerals score-100) (can be negative!!)
     * in this exact order
     * [8-11]: grams of Proteins, fats, carbs, sugar in this order
     * [12-15]: grams of saturated(bad),monosaturated(good),polysaturated(good) and transfats(bad) in this exact order
     * [16-28]: percentage of vitamines of daily recommendation in the following order:
     * A,D,E,K,B1,B2,Niacin ,B6, Folat, Pantothensäure, Biotin, B12,C
     * [29-38]: percantage of minerals of daily recommendation (taking too much Sodium is not good) in the following order:
     * Sodium, Chlorid, Kalium, Calcium, Phosphor,Magnesium,Eisen, Fluorid, Zink, Selen
     */
    public static double[] getStatisticsValues(double[] apiValues, int[] scores, int mealtype) {
        double[] mgVitamineL = {0.9, 20, 13, 65, 1.1, 1.3, 1.3, 1.3, 300, 6, 45, 3, 100};
        double[] mgMineralStoffeL = {1500, 2300, 4000, 1000, 700, 325, 13, 3.5, 8, 65};

        double[] statistic = new double[40];
        statistic[0] = mealtype; //Breakfast/lunch/diner
        statistic[1] = scores[0]; //total score
        for (int i = 2; i < 8; i++) //macroScore,Sugar Score, Fiber Score, Fat score, Vitamin Score, Minerals score in the same order
        {
            statistic[i] = scores[i - 1];
        }
        statistic[6] = statistic[6] - 100;
        statistic[7] = statistic[7] - 100;


        for (int i = 0; i < 4; i++) //proteins,fats,carbs,sugar
        {
            statistic[8 + i] = (int) apiValues[i];
        }

        statistic[12] = apiValues[5]; //saturated
        statistic[13] = apiValues[6]; //monounsaturated
        statistic[14] = apiValues[7]; //polyunsaturated
        statistic[15] = apiValues[31]; //transfats

        for (int i = 0; i < 13; i++) {
            statistic[i + 16] = (apiValues[i + 8] / mgVitamineL[i]); //percentage of each vitamine on daily recommendation
        }
        for (int i = 0; i < 10; i++) {
            statistic[i + 29] = (apiValues[i + 21] / mgMineralStoffeL[i]); //percentage of each mineral on daily recomendation
        }

        for (int i = 0; i < statistic.length; i++) {
            if (statistic[i] < 0 && i != 7)
                statistic[i] = -1;

        }

        return statistic;
    }

    public static String capitalize(String word) {
        String wordResult = "";
        if(word.length() > 1) {
            if(word.charAt(0) == '"')
            word = word.substring(1);
            if(word.charAt(word.length()-1)=='"')
                word = word.substring(0,word.length()-1);
            String[] word2 = word.split(" ", -1);
            for (int i = 0; i < word2.length; i++) {
                for (int j = 0; j < word2[i].length(); j++) {
                    if (j == 0 && word2[i].charAt(j) > 96 && word2[i].charAt(j) < 123)
                        wordResult += Character.toUpperCase(word2[i].charAt(j));
                    else {
                        wordResult += word2[i].charAt(j);
                    }
                }
                if (i != word2.length - 1)
                    wordResult += " ";


            }
        }
        return wordResult;
    }
    public static String getMealtypeString(int mealtype)
    {
        String[] mealtypeS = {"Breakfast", "Lunch", "Dinner", "Snack"};
        return mealtypeS[mealtype];
    }
    public static String[][] getVitaminesExplanation() {
        /** identical to the following structure
         *     statistic[16] = "Vitamin A";
         statistic[17] = "Vitamin D";
         statistic[18] = "Vitamin E";
         statistic[19] = "Vitamin K";
         statistic[20] = "Vitamin B1";
         statistic[21] = "Vitamin B2";
         statistic[22] = "Niacin (B3)";
         statistic[23] = "Vitamin B6";
         statistic[24] = "Folate (B9)";
         statistic[25] = "Pantothenic Acid (B5)";
         statistic[26] = "Biotin (B7)"; //Currently not integrated
         statistic[27] = "Vitamin B12";
         statistic[28] = "Vitamin C";
         */
        String[][] explanation = new String[13][2];
        explanation[0][0] = "Main Function: Vitamin A is important for vision. It helps the retina of the eye to function properly, particularly at night. Night blindness is an early sign of deficiency and blindness can result if preventative steps are not taken. Vitamin A also aids in maintaining healthy skin and it is sometimes used in the treatment of acne. It also plays a role in the growth of bones and it helps to regulate the immune system and fight infection.";
        explanation[0][1] = "Source: Vitamin A is found in deep orange and dark green fruits and vegetables such as carrots, broccoli, kale and spinach. Eggs also contain vitamin A.";
        explanation[1][0] = "Main Function: The sunshine vitamin plays a key role in bone health. Low levels of vitamin D are linked with a growing list of health problems, including multiple sclerosis, osteoporosis, osteoarthritis, rickets, heart disease, diabetes, depression and several kinds of cancers.";
        explanation[1][1] = "Source: Unlike other vitamins, the best way to obtain vitamin D is not from food but to simply step outdoors. The reason: our bodies develop it from sunshine. Often, about 15 minutes of exposure to sunlight a day is enough for the body to produce an adequate amount of vitamin D.";
        explanation[2][0] = "Main Function: Studies have shown that people with high dietary intakes of vitamin E and zinc are protected against age-related macular degeneration, an eye condition common among people aged 50 and older. Vitamin E also protects against Alzheimer’s disease. High in antioxidants, vitamin E helps the immune cells to produce antibodies, and it can help to reverse some of the decline in immune function that comes with ageing.";
        explanation[2][1] = "Source: Vitamin E is common in seeds and nuts such as sunflower seeds, peanuts and almonds. Whole-grain cereals and rice bran are also good sources of this vitamin.";
        explanation[3][0] = "Main Function: Vitamin K helps your blood to clot normally and it plays an important role in bone health.";
        explanation[3][1] = "Source: For almost everyone, the bacteria in our intestines makes vitamin K. It’s also found in green, leafy vegetables, including spinach, broccoli and brussels sprouts. Fruits such as grapes, kiwi fruit and avocados are also rich in vitamin K.";
        explanation[4][0] = "Main Function: All eight of the B vitamins help the body to convert fat and carbohydrates into energy. They are needed for healthy skin, hair, eyes and liver. Thiamine was the first to be discovered. It is essential for heart health and for the brain and nervous system to function properly.";
        explanation[4][1] = "Source: Whole grains are great sources of vitamin B1. You can also find it in yeast and yeast extract, brown rice, oatmeal, cauliflower and potatoes. Some processed foods also have vitamin B1 added.";
        explanation[5][0] = "Main Function: Riboflavin is an antioxidant that fights the free radicals that damage the body. It plays a key role in breaking down and distributing fats, carbohydrates and proteins throughout the body. It also aids in the absorption of iron and vitamin B6.";
        explanation[5][1] = "Source: Excellent sources of vitamin B2 include mushrooms, soybeans, yoghurt, eggs and dark, leafy greens such as brussels sprouts, broccoli and spinach. Some commercial flours and cereals are also fortified with this vitamin. Be sure to store these foods in the dark, since riboflavin is easily destroyed by light. It can also be lost in water when food is boiled or soaked.";
        explanation[6][0] = "Main Function: Vitamin B3 helps to increase the level of good cholesterol (HDL) in your body, thus improving circulation. It is also involved in the repair of DNA.";
        explanation[6][1] = "Source: Among the better sources of vitamin B3 are avocados, tomatoes, dates, asparagus and nuts.";
        explanation[7][0] = "Main Function: Like the other B vitamins, B6 helps the body to convert fat and carbohydrates into energy, and it contributes to healthy skin, hair and eyes. Vitamin B6 is also involved in brain development of the fetus during pregnancy and infancy, and it helps the immune system to function well. It is sometimes used as a treatment for morning sickness.";
        explanation[7][1] = "Source: You can obtain vitamin B6 from bananas, nuts, starchy vegetables (like potatoes) and whole-grain products.";
        explanation[8][0] = "Main Function: Vitamin B9 aids in cell division and growth, such as in infancy and during pregnancy. It also plays a part in the proper development of a baby’s nervous system. It helps our bodies to produce healthy red blood cells and to prevent anaemia.";
        explanation[8][1] = "Source: Leafy vegetables like spinach and turnip greens are a principal source of vitamin B9. Broccoli, certain fruit juices and legumes (including beans, peas and lentils) are also good sources of this vitamin. And it’s common in fortified cereals and bread.";
        explanation[9][0] = "Main Function: Vitamin B5 aids in the manufacture of red blood cells and it helps to maintain a healthy digestive tract. It also helps the body to use other vitamins, especially B2.";
        explanation[9][1] = "Source: While it’s available in a wide variety of foods, much of it can be lost if the food is cooked or processed. Among the good sources of vitamin B5 are broccoli, avocados, lentils, cauliflower and whole grains.";
        explanation[10][0] = "Main Function: Biotin is necessary for cell growth and metabolism. It aids in the transfer of carbon dioxide in the body, and it is believed to strengthen the hair and nails. It helps the body to maintain a steady blood sugar level.";
        explanation[10][1] = "Source: Fortified cereals are your best bet. You can also find vitamin B7 in barley, corn, nuts and soy.";
        explanation[11][0] = "Main Function: Vitamin B12 plays an important role in preventing a number of neurological problems, including numbness or tingling in the hands and feet, insomnia, loss of memory and depression. It helps our body’s red blood cells to mature normally and it aids in the development of DNA, our genetic material.";
        explanation[11][1] = "Source: Vegetarians and vegans tend to have lower intakes of vitamin B12 because plant foods do not naturally contain it. However, soy and rice milks commonly include B12 as an additive, as do yeast extracts and vegetarian burgers. Vitamin B12 can also be found in eggs, milk, cheese, yoghurt and other dairy products.";
        explanation[12][0] = "Main Function: Practically a celebrity in the world of vitamins, almost everybody reaches for a vitamin C pill at the first sign of a cold. Besides increasing the production of disease-fighting white blood cells and antibodies, thereby boosting the immune system, vitamin C is helpful in maintaining good eyesight.";
        explanation[12][1] = "Source: Many fruits are rich in vitamin C, including citrus, pineapple, berries and papaya.";
        return explanation;
    }
    public static String[][] getMineralsExplanation()
    {
        /**
         * identical to the following structure
         * statistic[29] = "Sodium";
         statistic[31] = "Chloride"; //currently not integrated
         statistic[30] = "Potassium";
         statistic[32] = "Calcium";
         statistic[33] = "Phosphorus";
         statistic[34] = "Magnesium";
         statistic[35] = "Iron";
         statistic[36] = "Fluoride";
         statistic[37] = "Zinc";
         statistic[38] = "Selenium";
         */
        String[][] explanation = new String[10][2];
        explanation[0][0]="What it does: Important for fluid balance. Don't get more than the recommended daily amount!";
        explanation[0][1]="Foods that have it: Foods made with added salt, such as processed and restaurant foods";
        explanation[1][0]="[Not integrated]";
        explanation[1][1]="[not integrated]";
        explanation[2][0]="What it does: Helps control blood pressure, makes kidney stones less likely";
        explanation[2][1]="Foods that have it: Potatoes, bananas, yogurt, milk, yellowfin tuna, soybeans, and a variety of fruits and vegetables.";
        explanation[3][0]="What it does: Needed for bone growth and strength, blood clotting, muscle contraction, and more";
        explanation[3][1]="Foods that have it: Milk, fortified nondairy alternatives like soy milk, yogurt, hard cheeses, fortified cereals, kale";
        explanation[4][0]="What it does: Cells need it to work normally. Helps make energy. Needed for bone growth.";
        explanation[4][1]="Foods that have it: Milk and other dairy products, peas, meat, eggs, some cereals and breads";
        explanation[5][0]="Helps with heart rhythm, muscle and nerve function, bone strength";
        explanation[5][1]="Foods that have it: Green leafy vegetables, nuts, dairy, soybeans, potatoes, whole wheat, quinoa";
        explanation[6][0]="What it does: Needed for red blood cells and many enzymes";
        explanation[6][1]="Foods that have it: Fortified cereals, beans, lentils, beef, turkey (dark meat), soy beans, spinach";
        explanation[7][0]="What it does: Prevents cavities in teeth, helps with bone growth";
        explanation[7][1]="Foods that have it: Fluoridated water, some sea fish";
        explanation[8][0]="What it does: Supports your immune system and nerve function. Also important for reproduction.";
        explanation[8][1]="Foods that have it: Red meats, some seafood, fortified cereals";
        explanation[9][0]="What it does: Protects cells from damage. Helps manage thyroid hormone.";
        explanation[9][1]="Foods that have it: Organ meats, seafood, dairy, some plants (if grown in soil with selenium), Brazil nuts";

        return explanation;
    }
    public static String getMacroExplanation(int mealtime) //for indexes 8-11
    {
        int[][] mahlzeitL = {{25, 20, 25, 30}, {30, 35, 30, 30}, {25, 30, 25, 25}, {10, 10, 10, 10}}; //erster Identifier: Frühstück, Mitagessen, Abendessen, Snack. Zweiter Identifier: Energie,Proteine,Fett,KH
//laut der DGE sollte eine Mahlzeit 10% Zucker am Kalorienanteil nicht überschreiten
        String[] mealtypeL = {"Breakfast", "Lunch", "Dinner", "Snack"};
        return ("The optimal " + mealtypeL[mealtime] + " should only consist of a maximum proportion of " + mahlzeitL[mealtime][2] + "% fat and at most " + mahlzeitL[mealtime][3] + "% carbohydrates!");
    }
    public static String getFatExplanation() //for indexes 12-15
    {
        return "Types of Fat:\n\n" +
                "Saturated fats: Saturated fats raise your LDL (bad) cholesterol level. High LDL cholesterol puts you at risk for heart attack, stroke, and other major health problems. You should avoid or limit foods that are high in saturated fats.\n" +
                "\n" +

                "Trans fats: Trans fats can raise LDL cholesterol levels in your blood. They can also lower your HDL (good) cholesterol levels. Foods with a lot of saturated fats are animal products, such as butter, cheese, whole milk, ice cream, cream, and fatty meats.\n" +
                "\n" + "Unsaturated Fats: Eating unsaturated fats instead of saturated fats can help lower your LDL cholesterol. Most vegetable oils that are liquid at room temperature have unsaturated fats. There are two kinds of unsaturated fats:" +
                "\n" +
                "Mono-unsaturated fats: Includes olive and canola oil\n" +
                "Polyunsaturated fats: Includes safflower, sunflower, corn, and soy oil\n";
                }
    }
