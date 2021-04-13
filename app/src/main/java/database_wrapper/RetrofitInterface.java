package database_wrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @POST("/Registrarse")
    Call<JsonObject> executeSignUp(@Body HashMap<String,String> map);

    @POST("/MenuInicio")
    Call<JsonObject> executeLogin(@Body HashMap<String,String> map);

    @GET("/ModoIndividual")
    Call<JsonObject> getQuestion(@Query("category") String cat);

    @POST("/FinalIndividual")
    Call<JsonObject> insertNewGameInd(@Body HashMap<String,String> map);

    @POST("/FinalIndividual_Usuario")
    Call<JsonObject> insertNewData(@Body HashMap<String,String> map);

    @POST("/AjustesUsuario")
    Call<JsonObject> executeSaveChanges(@Body HashMap<String,String> map);

    @POST("/EliminarCuenta")
    Call<JsonObject> executeDropUser(@Body HashMap<String,String> map);

    @POST("/CambiarContrasenya")
    Call<JsonObject> executeChangePassword(@Body HashMap<String,String> map);

    @GET("/Historial")
    Call<JsonArray> getGames(@Query("mail") String email);

    @GET("/Historial_Puntuacion")
    Call<JsonArray> getPointsFromGames(@Query("email") String email);


}
