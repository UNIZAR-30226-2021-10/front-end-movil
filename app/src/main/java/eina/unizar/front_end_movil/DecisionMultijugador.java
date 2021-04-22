package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class DecisionMultijugador extends AppCompatActivity {

    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_CREAR_NUEVA = 1;
    private static final int OPTION_INSTRUCCIONES = 2;
    private static final int OPTION_ATRAS = 2;

    EditText codigoPartida;

    String CODIGO_PARTIDA = "ABCD123";
    String codigoInsertado;
    //int NUM_RONDAS;
    //int NUM_JUGADORES;

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
                handleUnirsePartida();
                Intent intent = new Intent (v.getContext(), JuegoMultijugador.class);
                Bundle extras = new Bundle();
                extras.putInt("jugadores",4);
                extras.putInt("rondas",5);
                intent.putExtras(extras);
                startActivityForResult(intent, OPTION_ACCEDER);
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

    private void  handleUnirsePartida() {
        HashMap<String, String> unirsePartida = new HashMap<>();

        unirsePartida.put("codigo", codigoInsertado);
        unirsePartida.put("usuario_email", gestorSesion.getSession());
        unirsePartida.put("puntuacion", Integer.toString(0));

        Call<JsonObject> call = retrofitInterface.UnirseMultijugadorJuega(unirsePartida);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if (response.code() == 450) {
                    Toast.makeText(DecisionMultijugador.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else if (response.code() == 440) {
                    Toast.makeText(DecisionMultijugador.this, "No se ha podido insertar jugada", Toast.LENGTH_LONG).show();
                } else {
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


