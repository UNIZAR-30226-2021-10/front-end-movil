package database_wrapper;

import android.widget.ArrayAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @POST("/Registrarse")
    Call<JsonObject> executeSignUp(@Body HashMap<String,String> map);

    @POST("/Registrarse_Foto")
    Call<JsonObject> comprarNaranja(@Body HashMap<String,String> map);

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

    @POST("/PantallaTienda")
    Call<JsonArray> getObjectsShop(@Body HashMap<String,String> map);

    @POST("/ObjetoTienda")
    Call<JsonObject> buyObject(@Body HashMap<String,String> map);

    @POST("/ObjetoTienda_RestarMonedas")
    Call<JsonObject> updateCoins(@Body HashMap<String,String> map);

    @POST("/PerfilUsuario")
    Call<JsonArray> getUserItems(@Body HashMap<String,String> map);

    @FormUrlEncoded
    @POST("/UpdateItemsUsuario")
    Call<Void> updateItemsFromUser(@Field("email") String email, @Field("equipados")ArrayList<Integer> equipados
                                    , @Field("nombre") ArrayList<String> nombres);


}
