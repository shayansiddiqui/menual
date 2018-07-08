package de.fbl.menual;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.List;

import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.api.RetrofitInstance;
import de.fbl.menual.utils.CameraPreview;
import de.fbl.menual.utils.Config;
import de.fbl.menual.utils.Constants;
import de.fbl.menual.utils.FileUtils;
import de.fbl.menual.Services.NotificationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private int rotation;
    private ApiInterface apiInterface;
    private int mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMealType(1);
            }
        });

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMealType(2);
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMealType(3);
            }
        });

        startService(new Intent(MainActivity.this, NotificationService.class));


        // Create an instance of Camera
        mCamera = getCameraInstance();

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size desiredSize = getPictureSize(params.getSupportedPictureSizes());
        System.out.println(desiredSize.width);
        params.setPictureSize(desiredSize.width, desiredSize.height);
        mCamera.setParameters(params);

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        rotation = CameraPreview.correctCameraDisplayOrientation(MainActivity.this, mCamera);
        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
               (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


       // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         //   searchView.getSuggestionsAdapter().setDropDownViewTheme(this.getTheme());
       // }

        return true;
    }

    private Camera.Size getPictureSize(List<Camera.Size> sizes) {

        for (Camera.Size size : sizes) {
            if ((size.width * size.height) / 1024000 <= 2.5) {
                return size;
            }
        }

        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

         //if (id == R.id.action_search) {
         //  Intent myIntent = new Intent(MainActivity.this, SearchableActivity.class);
         //  MainActivity.this.startActivity(myIntent);
          // return true;
        // }

       // SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
         //       HelloSuggestionProvider.AUTHORITY, HelloSuggestionProvider.MODE);
        //suggestions.clearHistory();

        if (id == R.id.action_diet_preferences) {
            Intent myIntent = new Intent(MainActivity.this, DietPreferences.class);
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
        private ProgressDialog mDialog;
        Context context = MainActivity.this;
        boolean newCacheFileNeeded = false;
        Bitmap previewImage = null;
        byte[] imgData = null;

        @Override
        public void onPictureTaken(byte[] data, final Camera camera) {
            mDialog = ProgressDialog.show(MainActivity.this, "In progress", "Detecting text...", true);

            String encoded = null;
            JsonObject response = null;

            if (Config.USE_CACHED_MENUS) {
                String menuToUse = Config.CACHED_MENU_TO_USE + ".json";
                imgData = FileUtils.readCachedImage(context, Config.CACHED_MENU_TO_USE + ".jpg");
                previewImage = FileUtils.bytesToBitmap(imgData);
                newCacheFileNeeded = !FileUtils.isCachedFileAvailable(context, menuToUse);
                if (newCacheFileNeeded) {
                    encoded = FileUtils.convertToBase64(previewImage);
                    doDetectionAPICall(encoded);
                } else {
                    response = FileUtils.readCachedResponseFile(context, menuToUse);
                    endImageCapture(response);
                }
            } else {
                imgData = data;
                previewImage = correctImageOrientation(data);
                encoded = FileUtils.convertToBase64(previewImage);
                doDetectionAPICall(encoded);
            }


        }

        private void endImageCapture(JsonObject response) {
            File previewImageFile = FileUtils.createPreviewImageFile(context, previewImage, imgData);
            FileUtils.writePreviewResponseFile(context, response.toString().getBytes());
            showResponse(previewImageFile);
            mDialog.dismiss();
        }

        private String createDetectionAPIRequest(String encoded) {
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
            return httpQuery.toString();
        }

        private void doDetectionAPICall(String encoded) {
            String jsonRequest = createDetectionAPIRequest(encoded);
            Call<JsonObject> callTextDetection = apiInterface.detectText(jsonRequest);

            callTextDetection.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (newCacheFileNeeded) {
                        FileUtils.createCacheFile(context, Config.CACHED_MENU_TO_USE + ".json");
                        FileUtils.writeCacheResponseFile(context, response.body().toString().getBytes());
                    }
                    endImageCapture(response.body());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

        private void showResponse(File previewImageFile) {
            Intent myIntent = new Intent(MainActivity.this, TextSelection.class);
            myIntent.putExtra(Constants.PREVIEW_IMAGE_KEY, previewImageFile); //Optional parameters
            myIntent.putExtra(Constants.DETECTION_RESPONSE_KEY, Config.PREVIEW_RESPONSE_FILE_NAME); //Optional parameters
            myIntent.putExtra(Constants.MEAL_TYPE_KEY, getMealType());
            MainActivity.this.startActivity(myIntent);
        }


        private Bitmap correctImageOrientation(byte[] data) {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, bounds);

            Bitmap bm = FileUtils.bytesToBitmap(data);
            Matrix matrix = new Matrix();

            matrix.postRotate(rotation, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            final Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            return rotatedBitmap;
        }

    };

    private void setMealType(int meal_type){
        mealType = meal_type;
    }

    private int getMealType(){
        return mealType;
    }

}