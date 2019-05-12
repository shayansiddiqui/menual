package de.fbl.menual.tasks;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import de.fbl.menual.api.ApiInterface;
import de.fbl.menual.api.RetrofitInstance;
import de.fbl.menual.utils.Constants;
import retrofit2.Call;
import retrofit2.Response;

public class TextDetectionTask extends AsyncTask<String, Void, Response<JsonObject>> {

    private ApiInterface apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);

    protected Response<JsonObject> doInBackground(String... encoded) {
        String jsonRequest = createDetectionAPIRequest(encoded[0]);

        Call<JsonObject> callTextDetection = apiInterface.detectText(jsonRequest);
        try {
            return callTextDetection.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(JsonObject response) {
        // TODO: check this.exception
        // TODO: do something with the feed
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
}
