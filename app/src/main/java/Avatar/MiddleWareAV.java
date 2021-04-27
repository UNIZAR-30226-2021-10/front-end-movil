package Avatar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import eina.unizar.front_end_movil.CambiarAvatar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class MiddleWareAV {

    private RetrofitInterface retrofit;
    private GestorSesion gestorSesion;

    private MiddleWareAV(Context context) {
        retrofit = APIUtils.getAPIService();
        gestorSesion = new GestorSesion(context);
    }


    public void updateItemsEquipados(ArrayList<Integer> equipados, ArrayList<String> nombres){
        String emailUsuario = gestorSesion.getmailSession();
        Call<Void> call = retrofit.updateItemsFromUser(emailUsuario,equipados,nombres);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code() == 200){
                    System.err.println("EXITO");
                    Log.d("Exito","Llamada exitosa");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.err.println("ERROR");
                t.printStackTrace();
               call.cancel();
            }
        });


    }

    public static MiddleWareAV createMiddleWareAV(Context context) {
        return new MiddleWareAV(context);
    }
}
