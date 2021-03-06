package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearPartidaMultijugador extends AppCompatActivity implements OnItemSelectedListener {

    private static final int OPTION_CREAR = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_INSTRUCCIONES = 2;

    private int NUM_RONDAS;
    private int NUM_JUGADORES;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    Spinner numJugadores;
    Spinner numRondas;
    Bundle extras;
    int code;
    String codigo;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_partida_multijugador);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(CrearPartidaMultijugador.this);

        NUM_JUGADORES = 2; // por defecto
        NUM_RONDAS = 5; // por defecto

        extras = new Bundle();

        numJugadores = (Spinner) findViewById(R.id.numero_jugadores);
        numJugadores.setOnItemSelectedListener(this);

        numRondas = (Spinner) findViewById(R.id.numero_rondas);
        numRondas.setOnItemSelectedListener(this);


        // Botón de empezar partida multijugador
        Button crearButton = (Button) findViewById(R.id.crear);
        crearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarCodigo();
                handleRegistrarPartida();
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        NUM_RONDAS = Integer.parseInt(numRondas.getItemAtPosition(numRondas.getSelectedItemPosition()).toString());
        NUM_JUGADORES = Integer.parseInt(numJugadores.getItemAtPosition(numJugadores.getSelectedItemPosition()).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* */
    }

    public void generarCodigo(){
        Random r = new Random();
        code = r.nextInt(100000 - 10000 + 1) + 10000;
        codigo = Integer.toString(code);
        handleBuscarPartida();
    }

    private void  handleRegistrarPartida(){
        HashMap<String,String> nuevaPartida = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy--MM--dd(HH:mm:ss)", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        nuevaPartida.put("fecha",fecha);
        nuevaPartida.put("numJugadores", Integer.toString(NUM_JUGADORES));
        nuevaPartida.put("rondas", Integer.toString(NUM_RONDAS));
        nuevaPartida.put("ganador", gestorSesion.getSession());
        nuevaPartida.put("codigo", String.valueOf(codigo));

        Call<JsonObject> call = retrofitInterface.crearPartidaMultijugador(nuevaPartida);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    extras.putString("codigo", codigo);
                    extras.putString("tipo", String.valueOf(1));
                    Intent intent = new Intent (CrearPartidaMultijugador.this, JuegoMultijugador.class);
                    intent.putExtras(extras);
                    startActivityForResult(intent, OPTION_CREAR);
                } else{
                    Toast.makeText(CrearPartidaMultijugador.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(CrearPartidaMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void  handleBuscarPartida(){
        HashMap<String,String> yaPartida = new HashMap<>();

        yaPartida.put("codigo", String.valueOf(codigo));

        Call<JsonObject> call = retrofitInterface.multiPartidaCode(yaPartida);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    generarCodigo();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(CrearPartidaMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}


