package de.fbl.menual.utils;
import java.util.Arrays;

/**
 * @author Christopher Harth-Kitzerow
 *
 */
public class Evaluator {
    private int[] makroverteilung = {200,300,500}; //proteine, Fett Kohlenhydrate Referenzmenge (DGE)
    private double[] gMakros = {200/4, 300/9, 500/4};
    private int gballaststoffe = 13;
    private double gfettO6 = (1000*0.025)/9;
    private double gfett03 = (1000*0.005)/9;
    private double[] mgVitamine = {0.9,20, 13, 65, 1.1, 1.3, 1.3, 1.3, 300, 6, 45, 3,100}; //A,D(Microgramm),E,K(Microgramm),B1,B2,Niacin ,B6, Folat(Microgramm), Pantothensäure, Biotin, B12(Microgramm), C
    private double[] mgMineralStoffe = {1500,2300,4000,1000,700,325,13,3.5,8,65}; //Natrium, Chlorid, Kalium, Calcium, Phosphor,Magnesium,Eisen, Fluorid, Zink, Selen(Microgramm)
    private int[][] mahlzeit = {{25,20,25,30}, {30,35,30,30}, {25,30,25,25}, {10,10,10,10}}; //erster Identifier: Frühstück, Mitagessen, Abendessen, Snack. Zweiter Identifier: Energie,Proteine,Fett,KH
//laut der DGE sollte eine Mahlzeit 10% Zucker am Kalorienanteil nicht überschreiten

    private int[] details = new int[25]; //[0]: too many fats [1]: too many carbohydrates [2-14]: good amount of vitamine [15-24]: good amount of mineral

    public int[] getDetails()
    {
        return details;
    }
    public int[][] getMahlzeit()
    {
        return mahlzeit;
    }
    public int indexOf(int[] a, int value)
    {
        for(int i = 0; i<a.length;i++)
        {
            if(a[i] == value)
                return i;
        }
            return -1;
    }

    public static double[] nutritionXgetCorrectUnits(double[] apiValues)
    {
        apiValues[8] = apiValues[8]/3/1000; //Calculating from IU to µg to mg
        apiValues[9] = apiValues[9]/3; //Calculating from IU to µg
        //Vitamine E,B1,B2,Niacin, B6, Pantothenic acid, B12, C is already in mg, Vitamin K, Folat is already in µg
        apiValues[28] = apiValues[28]/1000; //Fluoride from µg to mg
        for(int i =8;i<30;i++)
        {
            if(apiValues[i] < 0)
            {
                apiValues[i] = -1; //Reset unjustified calculations
            }
        }
        return apiValues;
    }
    /**
     * Evaluates a Health Score for a Dish. Standard score is 100. >100 = green, >90 = yellow, <90 = red, -1 = not enough information available
     * @param userMahlzeit [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     * @param preferences Importance Multiplier (standard: 1) for [0]: High amount of Proteins [1]: Low amount of fats
     * [2]: Low amount of Carbs [3]: Low amount of sugar [4] High amount of Ballaststoffe [5-...]: certain eating habits
     * @param aPiValues Information on the dish provided by the API (-1 for no Information) [0]: Grams of Proteins [1]: Grams of Fats [2]: Grams of Carbohydrates
     * [3]: Grams of Sugar [4]: Grams of Ballaststoffe [5]: Grams of saturated Fats [6]: Grams of mono-unsaturated Fats [7]: Grams of poly-unsaturated Fat
     * [8-20]: Grams of each Vitamines in the same order as above [21-30]: Grams of Mineralstoffe in the same order as above  [31]: Trans fats(added with later version),
     * [32]:Fructose (added with later version) [33]: Food Category //currently not integrated
     * @return [0]: Total score [1]: Makroscore [2]: sugarscore [3]:ballaststoffscore [4]: Fatscore [5]: Vitaminscore [6]: mineralscore
     */
    public int[] evaluateDish(int userMahlzeit, int[] preferences, double[] aPiValues)
    {

        double score = 0;
        int counter[] = new int[4];

        //The following lines evaluate the macronutrients-ratio score (*2)
        double[] aPIgMakros = {aPiValues[0], aPiValues[1], aPiValues[2]};
        int[] makropreferences = {preferences[0], preferences[1], preferences[2]};
        int makroscore = eveluateMakros(userMahlzeit, makropreferences, aPIgMakros);
        for(int i=0; i<3; i++)
        {
            if(aPiValues[i] == -1)
            {
                makroscore = 0;
                counter[0] = 1;
            }
        }


        if(aPiValues[32]<0)
            aPiValues[32] = 0; //Adjust invalid Fructose value
        //The following lines evaluate the sugar score (*3)
        int sugarscore = evaluateSugar(preferences[3], aPIgMakros, aPiValues[3],aPiValues[32]);
        int highSugar= 0;
        for(int i=0; i<4; i++)
        {
            if(aPiValues[i] == -1)
            {
                sugarscore = 0;
                counter[1] = 1;
            }
        }
        if(sugarscore < 75 && counter[1] == 0)
            highSugar++;

        //The following lines evaluate the fiber score
        int ballaststoffscore = evaluateBallaststoffe(userMahlzeit, preferences[4], aPiValues[4]);
        if(aPiValues[4] == -1)
        {
            ballaststoffscore = 0;
            counter[2] = 1;
        }

        if(aPiValues[31] < 0) //Adjust invalid Transfat value
        {
            aPiValues[31] = 0;
        }
        //Evaluates the healthyness of fats contained in the meal (*2)
        double[] aPIFatValues = {aPiValues[5], aPiValues[6], aPiValues[7],aPiValues[31]};
        int fatscore = evaluateFats(aPIFatValues);
        counter[3] = 3;
        for(int i=5; i<8; i++)
        {
            if(aPiValues[i] == -1)
            {
                aPiValues[i] = 0;
                if(counter[3] > 0)
                    counter[3] --;
            }

        }

        //Evaluates all Vitamins contained in the dish
        double[] aPIVitaminValues = Arrays.copyOfRange(aPiValues, 8, 21);
        for(int i=8; i<22;i++)
        {
            if(aPiValues[i] < 0)
                aPiValues[i]= 0;
        }
        int vitaminscore = evaluateVitamines(userMahlzeit, aPIVitaminValues);

        double[] aPIMineralValues = Arrays.copyOfRange(aPiValues,21,31);
        for(int i=21; i<30;i++)
        {
            if(aPiValues[i] < 0)
                aPiValues[i]= 0;
        }
        int mineralScore = evaluateMinerales(userMahlzeit, aPIMineralValues);

        //The followoing evaluations will be added here: Mineralstoffe, eating preferences/disorders


        score = makroscore*2*(1-counter[0]) + sugarscore*(1+(3*highSugar))*(1-counter[1]) + ballaststoffscore*1*(1-counter[2]) + fatscore*counter[3] + (vitaminscore-100)+ (mineralScore-100);
        if(counter[0]*2 - counter[1]*(2+(2*highSugar)) - counter[2] - (3-counter[3]) == -10)
        {
            System.out.println("Not enough information on dish to evaluate");
            int a[] = {-1};
            return a;

        }
        score = score/(10- (3*(1-highSugar)) - counter[0]*2 - counter[1]*1 - counter[2] - (3-counter[3]));
        int[] scores = {(int)score, makroscore, sugarscore, ballaststoffscore, fatscore, vitaminscore, mineralScore};
        System.out.println("total score, makros, sugar, ballast, fat, vitamins, minerals");
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
     * @param userMahlzeit [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     * @param preferences Importance Multiplier (standard: 1) for: [0]:P [1]: F [2]: C
     * @param aPIgMakros returned Makros for the meals from API call
     * @return
     */
    public int eveluateMakros(int userMahlzeit, int[] preferences,double[] aPIgMakros)
    {
        double score = 100;
        double APIgMakros[] = aPIgMakros.clone();
        double[] optimalgMakroverteilung = {gMakros[0]*mahlzeit[userMahlzeit][1]/100, gMakros[1]*mahlzeit[userMahlzeit][2]/100,gMakros[2]*mahlzeit[userMahlzeit][3]/100};
        double[] optimalpMakroverteilung = optimalgMakroverteilung;
        double sum = 0;
        for(int i=0; i< optimalgMakroverteilung.length; i++)
            sum += optimalgMakroverteilung[i];
        for(int i=0; i< optimalgMakroverteilung.length; i++)
            optimalpMakroverteilung[i] =  optimalgMakroverteilung[i]/sum; // optimales prozentuales Makroverhältnis

        double[] actualMakroverteilung = APIgMakros;
        sum = 0;
        for(int i=0; i< APIgMakros.length; i++)
            sum += APIgMakros[i];
        for(int i=0; i< APIgMakros.length; i++)
            actualMakroverteilung[i] =  APIgMakros[i]/sum; // tatsächliches prozentuales Makroverhältnis

        double punishFats = (actualMakroverteilung[1] - optimalpMakroverteilung[1]) * 100 * preferences[0];
        double punishCarbs = (actualMakroverteilung[2] - optimalpMakroverteilung[2]) * 100 * preferences[0];

        if(actualMakroverteilung[0] > optimalpMakroverteilung[0])
            score = score + 0.5*(actualMakroverteilung[0] - optimalpMakroverteilung[0])*100*preferences[0]; //Pluspunkte für proteinreiche Mahlzeit
        if(actualMakroverteilung[1] > optimalpMakroverteilung[1])
            score = score - punishFats; //Strafpunkte für zu viele Fette
        if(actualMakroverteilung[2] > optimalpMakroverteilung[2])
                score = score - punishCarbs; //Strafpunkte für zu viele Kohlenhydrate

        if(punishFats > 20)
            details[0] = 1;
        if(punishCarbs > 20)
            details[1] = 1;

        return (int) score;
    }
    /**
     *
     * @param preferences preferences for sugar, Standard is 1
     * @param aPIgMakros grams of each macronutrient in the dish
     * @param aPIgSugar grams of sugar in the dish
     * @return
     */
    public int evaluateSugar(int preferences,double[] aPIgMakros, double aPIgSugar,double apigFructose)
    {
        double score = 100;
        double sum = 0;
        for(int i = 0; i < aPIgMakros.length; i++)
        {
            sum+= aPIgMakros[i];
        }
        double sugarRatio = ((aPIgSugar-apigFructose)/sum) - 0.1; //Up to 10% is ok. Everything bigger than 10 percent of sugar is punished. Fructose is not punished
        if(sugarRatio <= 0)
            return 100;
        score = 100 - (100*sugarRatio)*3*preferences;
        if(score < 0)
            return 0;

        return (int) score;
    }
    public int evaluateBallaststoffe(int userMahlzeit, int preferences,double APIValue)
    {
        int score = 100;
        double recommendedB = gballaststoffe* mahlzeit[userMahlzeit][1]/100; //recommended amount in grams of B for specific meal
        score = (int) (score - (recommendedB - APIValue)*10*preferences); //bonus points for more B, negative points for less
        return score;
    }
    /**
     *
     * @param aPIValues [0]: saturated fats, [1]: unsaturated fats, [2]: polyunsaturated fats [3]: transfats
     * @return
     */
    public int evaluateFats(double[] aPIValues)
    {
        double score = 100;
        score += aPIValues[1];
        score += aPIValues[2]*5;
        score -= aPIValues[0]*10; //punishing score for saturated fats
        score -= aPIValues[3]*15; //punishing score for trans fats
        if(score <= 0)
            score = 0;
        return (int) score;
    }

    /**
     *
     * @param userMahlzeit
     * @param aPIValues Same structure as mgVitamine, length: 13
     * @return [0]: breakfast [1]: lunch, [2]: dinner [3]: Snack
     */
    public int evaluateVitamines(int userMahlzeit, double aPIValues[])
    {
        double score = 100;
        int multiplier = mahlzeit[userMahlzeit][0];
        for(int i = 0; i < mgVitamine.length;i++)
        {
            details[2+i] = 0;
            if(aPIValues[i] > mgVitamine[i]*multiplier/100) {
                score += 5;
                details[2+i] = 1;
            }
        }
        return (int) score;
    }

    public int evaluateMinerales(int userMahlzeit, double aPIValues[])
    {
        double score = 100;
        int multiplier = mahlzeit[userMahlzeit][0];
        for(int i = 0; i < mgMineralStoffe.length;i++)
        {
            details[15+i] = 0;
            if(aPIValues[i] > mgMineralStoffe[i]*multiplier/100) {
                score += 5;
                if(i == 0)
                    score -= 10;
                details[15+i] = 1;
            }
        }
        return (int) score;
    }

}
