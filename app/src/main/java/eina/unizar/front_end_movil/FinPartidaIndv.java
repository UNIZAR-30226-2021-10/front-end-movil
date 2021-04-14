package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import SessionManagement.GestorSesion;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinPartidaIndv extends AppCompatActivity {

    private static final int OPTION_NUEVA_PARTIDA = 0;
    private static final int OPTION_SALIR = 1;

    private int PUNTOS_TOTALES;
    private int NUM_RONDAS;
    private int MONEDAS_OBTENIDAS;

    TextView puntosTotales;
    TextView monedasTotales;

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
        setContentView(R.layout.fin_partida_indv);
        getSupportActionBar().hide();

        //Construimos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(FinPartidaIndv.this);

        Bundle extra = getIntent().getExtras();
        PUNTOS_TOTALES = extra.getInt("puntosTotales");
        NUM_RONDAS = extra.getInt("rondas");
        MONEDAS_OBTENIDAS = PUNTOS_TOTALES/2;

        handleFinish(); // llamada para insertar la partida

        puntosTotales = (TextView)findViewById(R.id.puntos_ganados);
        monedasTotales = (TextView)findViewById(R.id.monedas_ganadas);
        puntosTotales.setText(String.valueOf(PUNTOS_TOTALES));
        monedasTotales.setText(String.valueOf(MONEDAS_OBTENIDAS));

        insertPointsAndCoins();

        // Botón de empezar partida individual
        Button nuevaButton = (Button) findViewById(R.id.nueva);
        nuevaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DecisionIndividual.class);
                startActivityForResult(intent, OPTION_NUEVA_PARTIDA);
            }
        });

        // Botón de salir
        Button salirButton = (Button) findViewById(R.id.salir);
        salirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_SALIR);
            }
        });

    }

    private void handleFinish(){
        HashMap<String,String> newGame = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy--MM--dd(HH:mm:ss)", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        newGame.put("fecha",fecha);
        newGame.put("numJugadores", Integer.toString(1));
        newGame.put("rondas", Integer.toString(NUM_RONDAS));
        newGame.put("ganador", gestorSesion.getSession());
        newGame.put("email", gestorSesion.getmailSession());
        newGame.put("puntos", Integer.toString(PUNTOS_TOTALES));

        Call<JsonObject> call = retrofitInterface.insertNewGameInd(newGame);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if(response.code() == 450){
                    Toast.makeText(FinPartidaIndv.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else if(response.code() == 440) {
                    Toast.makeText(FinPartidaIndv.this, "No se ha podido insertar jugada", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(FinPartidaIndv.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(FinPartidaIndv.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void insertPointsAndCoins(){
        HashMap<String,String> newData = new HashMap<>();

        newData.put("email", gestorSesion.getmailSession());
        newData.put("puntos", Integer.toString(PUNTOS_TOTALES));
        newData.put("monedas", Integer.toString(MONEDAS_OBTENIDAS));

        Call<JsonObject> call = retrofitInterface.insertNewData(newData);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                    int monedasUsuario = Integer.parseInt(gestorSesion.getKEY_SESSION_COINS());
                    int puntosUsuario = Integer.parseInt(gestorSesion.getpointsSession());
                    int newPoints = puntosUsuario + PUNTOS_TOTALES;
                    int newCoins = monedasUsuario + MONEDAS_OBTENIDAS;
                    gestorSesion.updateCoins_points(Integer.toString(newPoints) ,Integer.toString(newCoins));
                } else{
                    Toast.makeText(FinPartidaIndv.this, "No se ha podido insertar monedas y puntos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(FinPartidaIndv.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}


