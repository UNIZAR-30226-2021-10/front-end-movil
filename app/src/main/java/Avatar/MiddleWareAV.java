package Avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import eina.unizar.front_end_movil.CambiarAvatar;
import eina.unizar.front_end_movil.MenuPrincipal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MiddleWareAV {

    private RetrofitInterface retrofit, retrofitImagen;
    private GestorSesion gestorSesion;

    private MiddleWareAV(Context context) {
        retrofit = APIUtils.getAPIService();
        retrofitImagen  = APIUtils.getAPIServiceImages();
        gestorSesion = new GestorSesion(context);
    }


    public void updateItemsEquipados(ArrayList<Integer> equipados, ArrayList<String> nombres){
        String emailUsuario = gestorSesion.getmailSession();
        Call<Void> call = retrofit.updateItemsFromUser(emailUsuario,equipados,nombres);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    System.out.println("Good");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ;
            }
        });


    }

    public void saveAvatar(Bitmap Imagen){
        //Convertimos el bitmap en un array de bytes y luego la codificamos en Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Imagen.compress(Bitmap.CompressFormat.PNG, 100, baos); //bitmap is required image which have to send  in Bitmap form
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        //Creamos el hashmap clave valor.
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("imagen",encodedImage);
        hashMap.put("nombre",gestorSesion.getmailSession());
        //Creamos la llamada
        Call<JsonObject> call = retrofitImagen.updateAvatarFromUser(hashMap);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.code() == 200){
                    JsonPrimitive jsonObject = response.body().getAsJsonPrimitive("imagenAv");
                    gestorSesion.setAvatarSession(jsonObject.getAsString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.err.println(t.getMessage());
            }
        });

    }

    public static MiddleWareAV createMiddleWareAV(Context context) {
        return new MiddleWareAV(context);
    }
}
