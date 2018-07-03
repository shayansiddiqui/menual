package de.fbl.menual;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.api.RetrofitInstance;
import de.fbl.menual.models.BoundingBox;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.DishRecognizer;
import de.fbl.menual.utils.Evaluator;
import de.fbl.menual.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextSelection extends AppCompatActivity {

    Paint paint = new Paint();
    Canvas cnvs = null;
    ImageView previewImageView;
    List<BoundingBox> boundingBoxes = new ArrayList<>();
    Rect selectedBox = null;
    Bitmap bitmap = null;
    JsonArray responseBlocks = null;
    private ApiInterface apiInterface;
    private String foodName;
    private MenuItem sendFoodMenuItem;


    private JsonArray fetchBlocks(JsonElement element){
        if(element!=null){
            JsonElement responses = element.getAsJsonObject().get("responses");



            /**
            String one = responses.toString().substring(0,3000);
            String two = responses.toString().substring(3000,6000);
            System.out.println(one);
            System.out.println(two);
            */

            if(responses !=null){
                System.out.println(responses.toString().length());
                System.out.println(responses.getAsJsonArray().get(0).getAsJsonObject().get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").toString());
                String dishCandidates = responses.getAsJsonArray().get(0).getAsJsonObject().get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").toString();
                String[] dishes = DishRecognizer.getDishes(dishCandidates); //This output are our dishes. These have to get passed one by one to the evaluation algorithm
                JsonElement fullTextAnnotation = responses.getAsJsonArray().get(0).getAsJsonObject().get("fullTextAnnotation");
                if(fullTextAnnotation!=null){
                    JsonElement pages = fullTextAnnotation.getAsJsonObject().get("pages");
                    if(pages!=null){
                        JsonElement blocks = pages.getAsJsonArray().get(0).getAsJsonObject().get("blocks");
                        if(blocks!=null){
                            return blocks.getAsJsonArray();
                        }
                    }

                }
            }
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_selection);

        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);

        Bundle extras = getIntent().getExtras();
        File previewImage = (File) extras.get(Constants.PREVIEW_IMAGE_KEY);
        String filename = (String) extras.get(Constants.DETECTION_RESPONSE_KEY);

        StringBuffer fileContent = new StringBuffer();
        byte[] buffer =new byte[1024];
        int n;
        try {
            FileInputStream fis = TextSelection.this.openFileInput(filename);
            while ((n = fis.read(buffer)) != -1)
            {
//                buffer = IOUtils.toByteArray(fis);
                fileContent.append(new String(buffer, 0, n));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonElement element = new JsonParser().parse(fileContent.toString());
        responseBlocks = fetchBlocks(element);

        previewImageView = findViewById(R.id.preview_image);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;


        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(previewImage), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
//            Paint paint = new Paint();
            paint.setColor(Color.RED);
//            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);

            cnvs = new Canvas(bitmap);


            try {
                for (JsonElement block : responseBlocks) {
                    JsonArray vertices = block.getAsJsonObject().get("boundingBox").getAsJsonObject().get("vertices").getAsJsonArray();
                    System.out.println(vertices.toString());
                    JsonObject leftTop = vertices.get(0).getAsJsonObject();
                    JsonObject rightBottom = vertices.get(2).getAsJsonObject();
                    if(leftTop.get("x")!=null && leftTop.get("y")!=null && rightBottom.get("x")!=null && rightBottom.get("y")!=null){
                        int left = leftTop.get("x").getAsInt();
                        int top = leftTop.get("y").getAsInt();
                        int right = rightBottom.get("x").getAsInt();
                        int bottom = rightBottom.get("y").getAsInt();
                        System.out.println(left + " " + top + " " + right + " " + bottom);
                        Rect rect = new Rect(left, top, right, bottom);
                        cnvs.drawRect(left, top, right, bottom, paint);
                        boundingBoxes.add(new BoundingBox(rect, block.getAsJsonObject()));
                    }

//                cnvs.drawBitmap(bitmap, 0, 0, null);

                }
                previewImageView.setImageBitmap(bitmap);
                System.out.println("Canvas Height");
                System.out.println(cnvs.getWidth() + " " + cnvs.getHeight());

                System.out.println("Bitmap Height");
                System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
            } catch (Exception e) {
                System.out.println("Error while creating bounding boxes");
                e.printStackTrace();
            }
        }

        previewImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int[] viewCoords = new int[2];
                previewImageView.getLocationOnScreen(viewCoords);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    float yRatio = (float) cnvs.getHeight() / (float) previewImageView.getHeight();
                    y = (int) (yRatio * (float) y);

                    System.out.println(x + " " + y);
                    for (BoundingBox boundingBox : boundingBoxes) {
                        if (boundingBox.getRect().contains(x, y)) {
                            if (selectedBox != null) {
                                paint.setColor(Color.RED);
                                cnvs.drawRect(selectedBox, paint);
                            } else {
                                sendFoodMenuItem.setVisible(false);
                            }
                            selectedBox = boundingBox.getRect();
                            paint.setColor(Color.GREEN);
                            cnvs.drawRect(boundingBox.getRect(), paint);
                            foodName = getBoundingBoxText(boundingBox.getBoxElement());
                            sendFoodMenuItem.setVisible(true);
                            Toast.makeText(TextSelection.this, foodName,
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                    previewImageView.invalidate();
                    // check if (x,y) is on chair and do other staff
                }
                return true;
            }
        });

    }

    private String getBoundingBoxText(JsonObject boxElement) {
        StringBuilder sb = new StringBuilder();
        JsonArray words = boxElement.get("paragraphs").getAsJsonArray().get(0).getAsJsonObject().get("words").getAsJsonArray();
        for (JsonElement word : words) {
            JsonArray symbols = word.getAsJsonObject().get("symbols").getAsJsonArray();
            for (JsonElement symbol : symbols) {
                JsonObject symbolObj = symbol.getAsJsonObject();
                String text = symbolObj.get("text").getAsString();
                sb.append(text);
                if (symbolObj.get("property").getAsJsonObject().has("detectedBreak")) {
                    sb.append(" ");
                }
            }

        }
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_selection_menu, menu);
        sendFoodMenuItem = menu.findItem(R.id.action_send_food);
        if (foodName == null || foodName.isEmpty()) {
            sendFoodMenuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_food) {
           // foodName = "xnsadfjndsf";
            getNutrition(foodName);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getNutrition(final String foodName) {
        final ProgressDialog mDialog = ProgressDialog.show(TextSelection.this, "In progress", "Loading nutrition...", true);
        JsonObject httpQuery = new JsonParser().parse(
                "{" +
                        "\"query\":" + "\"" + foodName + "\"" +
                        ", " +
                        "\"timezone\":\"US/Eastern\"" +
                        "}").getAsJsonObject();

        Callback<JsonObject> callbackGetNutrition = new Callback<JsonObject>() {


            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String temp = response.body().toString();
                if(!temp.contains("match any of your food")) //In this would be true, the API didn't find the food
                {

                    JsonObject lresponse = response.body().getAsJsonArray("foods").get(0).getAsJsonObject();
                    String foodQuery = lresponse.get("food_name").toString();
                    String foodQueryLow = foodQuery.toLowerCase();
                    String foodNameLow = foodName.toLowerCase();
                    if (foodQueryLow.contains(foodNameLow)) //only outputs food that is matches/is contained in the query
                    {
                        System.out.println(response.body().toString());
                        String sApiValues = response.body().toString();

                        //if(sApiValues.contains(foodName)) {
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
                        mDialog.dismiss();
                        showResultAlert(result, comments);
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

    public Map<String, String> getComment(int[] scores, Evaluator e) {
        int[] sortedScores = scores.clone();
        Arrays.sort(sortedScores);

        String[] ingredients = {"", "proteins", "sugar", "fiber", "healthy fats", "vitamins"};
        String[] ingredientsUnhealthy = {"", "proteins", "sugar", "fiber", "saturated fats", "vitamins"};


        String comment1 = "";
        String comment2 = "";
        if (scores[0] > 100) {
            System.out.println("green");
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

    private void showResultAlert(String result, Map<String, String> comments) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TextSelection.this);
        LayoutInflater factory = LayoutInflater.from(TextSelection.this);
        final View view = factory.inflate(R.layout.food_result, null);
        ImageView foodResultIcon = view.findViewById(R.id.food_result_icon);

        int color = R.color.yellow;
        int background = R.drawable.yellow;
        switch (result) {
            case "green":
                background = R.drawable.green;
                color = R.color.green;
                break;
            case "red":
                background = R.drawable.red;
                color = R.color.red;
                break;
            case "yellow":
                background = R.drawable.yellow;
                color = R.color.yellow;
                break;
        }

        foodResultIcon.setBackgroundResource(background);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Your food result");

        String msg = "";
        if (!comments.get("comment1").isEmpty()) {
            TextView comment1_textView = view.findViewById(R.id.food_result_comment1);
            comment1_textView.setText(comments.get("comment1"));
            comment1_textView.setTextColor(getResources().getColor(color));

        }
        if (!comments.get("comment2").isEmpty()) {
            TextView commet1_textView = view.findViewById(R.id.food_result_comment2);
            commet1_textView.setText(comments.get("comment2"));
        }
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.show();

    }
}
