package de.fbl.menual;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fbl.menual.adapters.FoodListAdapter;
import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.api.RetrofitInstance;
import de.fbl.menual.models.FoodItem;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.DishRecognizer;
import de.fbl.menual.utils.EvaluatorUtils;
import retrofit2.Call;
import retrofit2.Response;

public class TextSelectionActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private ProgressDialog mDialog;
    private boolean isFromSearch;
    String searchedMeal="";
    ArrayList<FoodItem> foodItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_selection);

        mDialog = ProgressDialog.show(TextSelectionActivity.this, "In progress", "Getting nutrition info...", true);

        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filename = (String) extras.get(Constants.DETECTION_RESPONSE_KEY);

            if(extras.get(Constants.SEARCH_QUERY)!=null){
                searchedMeal = extras.get(Constants.SEARCH_QUERY).toString();
            }
            GetNutritionTask getNutritionTask = new GetNutritionTask();
            if (!searchedMeal.isEmpty()) {
                isFromSearch = true;
                showMockScreen();
//                getNutritionTask.execute(searchedMeal);
            } else {
                isFromSearch = false;
                StringBuffer fileContent = new StringBuffer();
                byte[] buffer = new byte[1024];
                int n;
                try {
                    FileInputStream fis = TextSelectionActivity.this.openFileInput(filename);
                    while ((n = fis.read(buffer)) != -1) {
                        fileContent.append(new String(buffer, 0, n));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonElement element = new JsonParser().parse(fileContent.toString());
                String[] dishes = fetchBlocks(element);
                showMockScreen();
//                getNutritionTask.execute(dishes);
            }

        }
        else{
            foodItems = (ArrayList<FoodItem>) savedInstanceState.getSerializable("foodItems");
            searchedMeal = savedInstanceState.getString("searchedMeal");
            showList(foodItems);
        }
    }


    private void showMockScreen(){
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(FoodItem.getMockFoodItem());
        showList(foodItems);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("searchedMeal", searchedMeal);
        outState.putSerializable("foodItems", foodItems);
    }

    private void showList(final List<FoodItem> foodItems) {
        FoodListAdapter adapter = new FoodListAdapter(foodItems, TextSelectionActivity.this);
        //handle listview and assign adapter
        ListView lView = (ListView) findViewById(R.id.food_list);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent myIntent = new Intent(TextSelectionActivity.this, StatisticsActivity.class);
                myIntent.putExtra(Constants.FOOD_ITEM_KEY, foodItems.get(i)); //Optional parameters
//                myIntent.putExtra(Constants.DETECTION_RESPONSE_KEY, Config.PREVIEW_RESPONSE_FILE_NAME); //Optional parameters
//                myIntent.putExtra(Constants.MEAL_TYPE_KEY, getMealType());
                TextSelectionActivity.this.startActivity(myIntent);

//                showResultAlert(foodItems.get(i));
            }
        });
        lView.setAdapter(adapter);
        mDialog.dismiss();
    }

    private class GetNutritionTask extends AsyncTask<String, Integer, ArrayList<FoodItem>> {
        protected ArrayList<FoodItem> doInBackground(String... dishes) {
            ArrayList<FoodItem> foodItems = new ArrayList<>();
            for (String dish : dishes) {
                Response<JsonObject> response = getNutrition(dish);
                FoodItem foodItem = EvaluatorUtils.evaluateResponse(response, dish);
                if (foodItem != null) {
                    foodItems.add(foodItem);
                }
            }
            return foodItems;
        }

        protected void onPostExecute(ArrayList<FoodItem> result) {
            foodItems=result;
            showList(result);
        }

        private Response<JsonObject> getNutrition(final String foodName) {
            try {
                JsonObject httpQuery = new JsonParser().parse(
                        "{" +
                                "\"query\":" + "\"" + foodName + "\"" +
                                ", " +
                                "\"timezone\":\"US/Eastern\"" +
                                "}").getAsJsonObject();


                Call<JsonObject> callGetNutrition = apiInterface.getNutrition(httpQuery.toString());
                return callGetNutrition.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private String[] fetchBlocks(JsonElement element) {
        if (element != null) {
            JsonElement responses = element.getAsJsonObject().get("responses");
            /**
             String one = responses.toString().substring(0,3000);
             String two = responses.toString().substring(3000,6000);
             System.out.println(one);
             System.out.println(two);
             */

            if (responses != null) {
                String dishCandidates = responses.getAsJsonArray().get(0).getAsJsonObject().get("textAnnotations").getAsJsonArray().get(0).getAsJsonObject().get("description").toString();
                String[] dishes = DishRecognizer.getDishes(dishCandidates); //This output are our dishes. These have to get passed one by one to the evaluation algorithm
                return dishes;
            }
        }
        return new String[0];
    }

    private void showResultAlert(FoodItem foodItem) {
        String result = foodItem.getResult();
        Map<String, String> comments = foodItem.getComments();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TextSelectionActivity.this);
        LayoutInflater factory = LayoutInflater.from(TextSelectionActivity.this);
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
