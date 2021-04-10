package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FinPartidaIndv extends AppCompatActivity {

    private static final int OPTION_NUEVA_PARTIDA = 0;
    private static final int OPTION_SALIR = 1;

    private int PUNTOS_TOTALES;
    private int NUM_RONDAS;

    TextView puntosTotales;
    TextView monedasTotales;

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

        Bundle extra = getIntent().getExtras();
        PUNTOS_TOTALES = extra.getInt("puntosTotales");
        NUM_RONDAS = extra.getInt("rondas");

        puntosTotales = (TextView)findViewById(R.id.puntos_ganados);
        monedasTotales = (TextView)findViewById(R.id.monedas_ganadas);
        puntosTotales.setText(String.valueOf(PUNTOS_TOTALES));
        monedasTotales.setText(String.valueOf(PUNTOS_TOTALES/2));

        // TODO: METER DATOS A BASE DE DATOS
        // meter num rondas
        // meter numero de monedas del usuario
        // meter numero de puntos obtenidos
        // en general meter la partida entera

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

}


