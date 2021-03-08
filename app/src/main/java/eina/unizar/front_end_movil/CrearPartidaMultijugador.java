package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class CrearPartidaMultijugador extends AppCompatActivity implements OnItemSelectedListener {

    private static final int OPTION_CREAR = 0;
    private static final int OPTION_ATRAS = 1;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_partida_multijugador);
        getSupportActionBar().hide();

        Spinner numJugadores = (Spinner) findViewById(R.id.numero_jugadores);
        numJugadores.setOnItemSelectedListener(this);

        Spinner numRondas = (Spinner) findViewById(R.id.numero_rondas);
        numRondas.setOnItemSelectedListener(this);

        final Bundle extras = new Bundle();
        int numPlayers = Integer.parseInt(numJugadores.getItemAtPosition(numJugadores.getSelectedItemPosition()).toString());
        extras.putInt("jugadores",numPlayers);
        int numRounds = Integer.parseInt(numRondas.getItemAtPosition(numRondas.getSelectedItemPosition()).toString());
        extras.putInt("rondas",numRounds);


        // Botón de empezar partida individual
        Button crearButton = (Button) findViewById(R.id.crear);
        crearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), JuegoMultijugador.class);
                intent.putExtras(extras);
                startActivityForResult(intent, OPTION_CREAR);
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionMultijugador.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /* para cuando clica una opción*/
        // TODO: para la BD aquí se cogerá el valor del nº de rondas
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* */
    }
}


