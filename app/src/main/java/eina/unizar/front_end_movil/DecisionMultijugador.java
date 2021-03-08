package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DecisionMultijugador extends AppCompatActivity {

    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_CREAR_NUEVA = 1;

    EditText codigoPartida;

    String CODIGO_PARTIDA = "ABCD123";

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

        codigoPartida = (EditText) findViewById(R.id.code);

        // Botón de unirse a una partida ya creada
        Button accederButton = (Button) findViewById(R.id.acceder);
        accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigoInsertado = codigoPartida.getText().toString();
                if(codigoInsertado.equals(CODIGO_PARTIDA)){
                    Intent intent = new Intent (v.getContext(), JuegoMultijugador.class);
                    startActivityForResult(intent, OPTION_ACCEDER);
                }else{
                    codigoPartida.setError("El código de partida es incorrecto");
                }

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

    }

}


