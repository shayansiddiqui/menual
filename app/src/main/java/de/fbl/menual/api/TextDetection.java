package de.fbl.menual.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TextDetection {

    @Headers("Content-Type: application/json")
    @POST("./images:annotate?key=AIzaSyDPiH5XLmBEBD8b-PJneHS23mchGjbeldk")
    Call<JsonObject> detectText(@Body String body);
}
