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

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

//import java.net.URI;
import java.util.HashMap;
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

    //String CODIGO_PARTIDA = "ABCD123";
    String codigoInsertado;
    //int NUM_RONDAS;
    //int NUM_JUGADORES;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;
    private Socket msocket;


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

        final Emitter.Listener unirseMultijugador = new Emitter.Listener(){
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String user;
                        String code;
                        try{
                            user = data.getString("user");
                            code = data.getString("code");
                        } catch (JSONException e){
                            return;
                        }
                    }
                });
            }
        };

        try {
            //This address is the way you can connect to localhost with AVD(Android Virtual Device)
            msocket = IO.socket("http://10.0.2.2:3050");
            Log.d("success", msocket.id());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("fail", "Failed to connect");
        }

        msocket.connect();


        // Botón de unirse a una partida ya creada
        Button accederButton = (Button) findViewById(R.id.acceder);
        accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertado = codigoPartida.getText().toString();
                msocket.on("unirseMulti", unirseMultijugador);
                Intent intent = new Intent (v.getContext(), JuegoMultijugador.class);
                Bundle extras = new Bundle();
                extras.putString("codigo",codigoInsertado);
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
}


