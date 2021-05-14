package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

//import java.net.URI;
import java.net.URI;
import java.util.HashMap;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DecisionMultijugador extends AppCompatActivity {

    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_CREAR_NUEVA = 1;
    private static final int OPTION_INSTRUCCIONES = 2;
    private static final int OPTION_ATRAS = 2;

    EditText codigoPartida;
    String codigoInsertado;
    int jugadoresEnSala;
    int ID_PARTIDA;
    int NUM_JUGADORES;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_multijugador);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(DecisionMultijugador.this);

        codigoPartida = (EditText) findViewById(R.id.code);


        // Botón de unirse a una partida ya creada
        Button accederButton = (Button) findViewById(R.id.acceder);
        accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertado = codigoPartida.getText().toString();
                handleBuscarPartida();
            }
        });

        // Botón de crear partida
        Button crearButton = (Button) findViewById(R.id.crear);
        crearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CrearPartidaMultijugador.class);
                startActivityForResult(intent, OPTION_CREAR_NUEVA);
            }
        });

        ImageButton instButton = (ImageButton) findViewById(R.id.instrucciones);
        instButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), InstruccionesJuego.class);
                startActivityForResult(intent, OPTION_INSTRUCCIONES);
            }
        });

        // Botón de atras
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });

    }

    private void  handleBuscarPartida(){
        HashMap<String,String> buscarPartida = new HashMap<>();
        buscarPartida.put("codigo", codigoInsertado);

        Call<JsonObject> call = retrofitInterface.buscarPartidaCreada(buscarPartida);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    handleObtenerInfo();
                    //handleUnirseJuega();
                    System.out.println("TODO OK en obtener partida");
                } else if(response.code() == 400){
                    Toast.makeText(DecisionMultijugador.this, "La partida introducida no existe", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(DecisionMultijugador.this, "La partida no ha sido encontrada", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DecisionMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void  handleObtenerInfo(){
        HashMap<String,String> obtener = new HashMap<>();
        obtener.put("codigo", codigoInsertado);

        Call<JsonObject> call = retrofitInterface.obtenerInfo(obtener);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonObject jsonObject = response.body().getAsJsonObject("idpartida");
                    ID_PARTIDA = jsonObject.get("idpartida").getAsInt();
                    NUM_JUGADORES = jsonObject.get("numJugadores").getAsInt();
                    System.out.println(NUM_JUGADORES);
                    System.out.println("TODO OK");
                    System.out.println("Este es el handleObtenerInfo");
                    System.out.println(ID_PARTIDA);
                    handleContarJugadores();
                } else{
                    Toast.makeText(DecisionMultijugador.this, "No se ha podido obtener informacion", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DecisionMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void handleContarJugadores(){
        System.out.println("Este es el handleContarJugadores");
        System.out.println(ID_PARTIDA);

        Call<JsonArray> call = retrofitInterface.obtenerJugadores(String.valueOf(ID_PARTIDA));
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {
                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        JsonObject prueba = j.getAsJsonObject();
                        String email = prueba.get("email").getAsString();
                        jugadoresEnSala++;
                    }
                    System.out.println("TODO OK contar jugadores");
                    System.out.println(jugadoresEnSala);
                    //falta saber cuantos jugadores hay en la sala para poder entrar
                    if(jugadoresEnSala < NUM_JUGADORES){
                        handleUnirseJuega();
                    }
                    else{
                        Toast.makeText(DecisionMultijugador.this, "La partida ya está llena", Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(DecisionMultijugador.this, "No se han podido contar jugadores", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(DecisionMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void handleUnirseJuega(){
        HashMap<String,String> unirseJuega = new HashMap<>();

        unirseJuega.put("codigo",codigoInsertado);
        System.out.println(gestorSesion.getmailSession());
        System.out.println(String.valueOf(0));
        unirseJuega.put("email", gestorSesion.getmailSession());
        unirseJuega.put("puntos",String.valueOf(0));


        Call<JsonObject> call = retrofitInterface.UnirseMultijugadorJuega(unirseJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    Intent intent = new Intent (DecisionMultijugador.this, JuegoMultijugador.class);
                    Bundle extras = new Bundle();
                    extras.putString("codigo",codigoInsertado);
                    extras.putString("tipo", String.valueOf(2)); //cambiar a valor 2
                    intent.putExtras(extras);
                    startActivityForResult(intent, OPTION_ACCEDER);
                    System.out.println("TODO OK");
                } else if(response.code() == 450){
                    Toast.makeText(DecisionMultijugador.this, "No se ha podido unir a la partida", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(DecisionMultijugador.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DecisionMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}


