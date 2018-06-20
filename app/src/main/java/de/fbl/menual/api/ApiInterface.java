package de.fbl.menual.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

//    @Headers("Content-Type: application/json")
    @POST("/detectText")
    Call<JsonObject> detectText(@Body String body);

    @Headers("Content-Type: application/javascript")
    @POST("/getNutrition")
    Call<JsonObject> getNutrition(@Body String body);

}
