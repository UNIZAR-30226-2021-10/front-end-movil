package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DecisionIndividual extends AppCompatActivity implements OnItemSelectedListener {

    private static final int OPTION_COMENZAR = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_INSTRUCCIONES = 2;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_individual);
        getSupportActionBar().hide();

        Spinner spinner = (Spinner) findViewById(R.id.caja_opciones);
        spinner.setOnItemSelectedListener(this);

        final Bundle extras = new Bundle();
        int numRonda = Integer.parseInt(spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString());
        extras.putInt("rondas",numRonda);


        // Botón de empezar partida individual
        Button comenzarButton = (Button) findViewById(R.id.comenzar);
        comenzarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), JuegoIndividual.class);
                intent.putExtras(extras);
                startActivityForResult(intent, OPTION_COMENZAR);
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ATRAS);
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
        /* para cuando clica una opción*/
        // TODO: para la BD aquí se cogerá el valor del nº de rondas
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* */
    }
}


