package de.fbl.menual;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;


import org.json.JSONObject;

import java.util.Date;

import de.fbl.menual.api.RetrofitInstance;
import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.utils.CameraPreview;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.Evaluator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private int rotation;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);

//                TODO: Just a sample call. Need to move from here

              //getNutrition("Big mac");
               getNutrition("Salmon salad");
               getNutrition("Pizza");
               //getNutrition("Spaghetti bolognese");


            }
        });


        // Create an instance of Camera
        mCamera = getCameraInstance();

        Camera.Parameters params = mCamera.getParameters();
        //*EDIT*//params.setFocusMode("continuous-picture");
        //It is better to use defined constraints as opposed to String, thanks to AbdelHady
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);


        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        rotation = CameraPreview.correctCameraDisplayOrientation(MainActivity.this, mCamera);
        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_diet_preferences) {
            Intent myIntent = new Intent(MainActivity.this, DietPreferences.class);
//            myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "Error while opening camera, check permissions: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, bounds);

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();

            matrix.postRotate(rotation, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {

                FileOutputStream fos = new FileOutputStream(pictureFile);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.write(data);
                fos.close();
                String encoded = Base64.encodeToString(data, Base64.DEFAULT);

                JsonObject httpQuery = new JsonParser().parse(
                        "{" +
                                "\"requests\": [" +
                                "{" +
                                "\"image\":{" +
                                "\"content\":" + "\"" + encoded + "\"" +
                                "}, " +
                                "\"features\":[" +
                                "{" +
                                "\"type\":\"" + Constants.OCR_TYPE + "\"" +
                                "}" +
                                "]" +
                                "}" +
                                "]" +
                                "}").getAsJsonObject();

                Callback<JsonObject> callbackTextDetection = new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        System.out.println(response.body());
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                };

                Call<JsonObject> callTextDetection = apiInterface.detectText(httpQuery.toString());
                callTextDetection.enqueue(callbackTextDetection);

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Menual");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Menual", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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


                //System.out.println(b);
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
                int[] sortedScores = scores.clone();
                Arrays.sort(sortedScores);

                String[] ingredients = {"", "proteins", "sugar", "fiber", "healthy fats", "vitamins"};
                String[] ingredientsUnhealthy = {"", "proteins", "sugar", "fiber", "saturated fats", "vitamins"};

                if (scores[0] > 100) {
                    System.out.println("green");
                    int max = sortedScores[sortedScores.length - 1];
                    int max2 = sortedScores[sortedScores.length - 2];

                    if (max > 110) {
                        int result1st = e.indexOf(scores,max);
                        String kriterium1 = ingredients[result1st];
                        if (result1st != 0)
                            System.out.println("Awesome, this meal contains a lot of " + kriterium1 + "!");
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
                            System.out.println("Great, this meal contains a lot of " + kriterium2 + "!");
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
                                System.out.println("Boo, this meal contains too much " + kriterium1 + "!");
                            if (result1st == 4)
                                System.out.println("Oh no, this meal contains too many " + kriterium1 + "!");
                            if (result1st == 3 || result1st == 5)
                                System.out.println("This meal contains not enough " + kriterium1 + "!");
                            if (result1st == 1) {
                                if (e.getDetails()[0] == 1) {
                                    System.out.println("This meal has too many carbohydrates!");
                                }
                                if (e.getDetails()[1] == 1) {
                                    System.out.println("This meal has too many fats!");
                                }
                                int[][] mahlzeit = e.getMahlzeit();
                                int mealtime = 1; //Has to be replaced with actual mealtime
                                String[] mealtype = {"breakfast", "lunch", "dinner", "snack"};
                                System.out.println("The optimal " + mealtype[mealtime] + " should only consist of a maximum proportion of " + mahlzeit[mealtime][3] + "% fat and at most " + mahlzeit[mealtime][4] + "% carbohydrates!");
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
                                    System.out.println("Bad news, this meal contains too much " + kriterium2 + "!");
                                if (result2nd == 4)
                                    System.out.println("Bad news, this meal contains too many " + kriterium2 + "!");
                                if (result2nd == 3 || result2nd == 5)
                                    System.out.println("Unfortunately, this meal contains not enough " + kriterium2 + "!");
                            }

                        if (result2nd == 1) {
                            if (e.getDetails()[0] == 1) {
                                System.out.println("This meal has too many carbohydrates!");
                            }
                            if (e.getDetails()[1] == 1) {
                                System.out.println("This meal has too many fats!");
                            }
                            int[][] mahlzeit = e.getMahlzeit();
                            int mealtime = 1; //Has to be replaced with actual mealtime
                            String[] mealtype = {"breakfast", "lunch", "dinner", "snack"};
                            System.out.println("The optimal " + mealtype[mealtime] + " should only consist of a maximum proportion of " + mahlzeit[mealtime][3] + "% fat and at most " + mahlzeit[mealtime][4] + "% carbohydrates!");
                        }


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
}
