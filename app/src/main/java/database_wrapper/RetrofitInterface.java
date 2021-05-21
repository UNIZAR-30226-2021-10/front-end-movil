package database_wrapper;


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

    @GET("/Historial_Completo")
    Call<JsonArray> getGames(@Query("email") String email);

    @POST("/PantallaTienda")
    Call<JsonArray> getObjectsShop(@Body HashMap<String,String> map);

    @POST("/ObjetoTienda")
    Call<JsonObject> buyObject(@Body HashMap<String,String> map);

    @POST("/ObjetoTienda_RestarMonedas")
    Call<JsonObject> updateCoins(@Body HashMap<String,String> map);

    @POST("CrearMultijugador_Partida")
    Call<JsonObject> crearPartidaMultijugador(@Body HashMap<String,String> map);

    @POST("FinalMultijugador_Partida")
    Call<JsonObject> FinalPartidaMultijugador(@Body HashMap<String,String> map);

    @POST("UnirseMultijugador_Juega")
    Call<JsonObject> UnirseMultijugadorJuega(@Body HashMap<String,String> map);

    @POST("/Ranking")
    Call<JsonArray> getRanking();

    @POST("/PartidaMulti_info")
    Call<JsonObject> obtenerInfo(@Body HashMap<String,String> map);

    @POST("/BuscarPartidaMulti")
    Call<JsonObject> buscarPartidaCreada(@Body HashMap<String,String> map);

    @POST("/Multijugador_PartidaCode")
    Call<JsonObject> multiPartidaCode(@Body HashMap<String,String> map);

    @FormUrlEncoded
    @POST("/UpdateItemsUsuario")
    Call<Void> updateItemsFromUser(@Field("email") String email, @Field("equipados")ArrayList<Integer> equipados
            , @Field("nombre") ArrayList<String> nombres);

    @POST("/UpdateAvatarUsuario")
    Call<JsonObject> updateAvatarFromUser(@Body HashMap<String,String> map);

    @POST("/PerfilUsuario")
    Call<JsonArray> getUserItems(@Body HashMap<String,String> map);

    @GET("/Multijugador_PartidaJugadoresUsuario")
    Call<JsonArray> obtenerJugadores(@Query("idpartida") String idpartida);

    @POST("/AbandonarPartidaMulti")
    Call<JsonObject> salirPartidaJuega(@Body HashMap<String,String> map);

    @POST("/FinalMultijugador_Juega2")
    Call<JsonObject> finMultiJuega(@Body HashMap<String,String> map);

}
