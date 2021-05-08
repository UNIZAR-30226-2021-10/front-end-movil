package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.HashMap;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbandonarPartida extends AppCompatActivity {

    private static final int OPTION_POSPONER = 0;
    private static final int OPTION_ABANDONAR = 1;
    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    String jugadoresEnSala;
    int jugadoresActivos;
    String codigo;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abandonar_partida);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();
        gestorSesion = new GestorSesion(AbandonarPartida.this);

        Bundle extras = getIntent().getExtras();
        //NUM_RONDAS = extras.getInt("rondas");
        //NUM_JUGADORES = extras.getInt("jugadores");
        jugadoresEnSala= extras.getString("jugadoresEnSala");
        jugadoresActivos = Integer.parseInt(jugadoresEnSala);
        codigo = extras.getString("codigo");

        // Botón de cancelar
        Button cancelarButton = (Button) findViewById(R.id.cancelar);
        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Para que vuelva a donde estaba
                finish();
            }
        });

        // Botón de abandonar partida
        Button abandonarButton = (Button) findViewById(R.id.abandonar);
        abandonarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUsuario0Puntos();
                //TODO: falta anyadir: si solo quedo yo en la sala tengo que eliminarla
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ABANDONAR);
            }
        });
    }

    private void  handleUsuario0Puntos(){
        HashMap<String,String> user0points = new HashMap<>();
        user0points.put("codigo", codigo);
        user0points.put("puntuacion", String.valueOf(0));

        Call<JsonObject> call = retrofitInterface.finPartidaMultiJuega(user0points);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if(response.code() == 450){
                    Toast.makeText(AbandonarPartida.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(AbandonarPartida.this, "No se ha podido actualizar la partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AbandonarPartida.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}


