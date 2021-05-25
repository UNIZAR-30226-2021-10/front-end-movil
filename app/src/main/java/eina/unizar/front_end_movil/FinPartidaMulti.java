package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

public class FinPartidaMulti extends AppCompatActivity {

    private static final int OPTION_SALIR = 0;

    private TextView ganador;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_partida_multi);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        ganador = (TextView)findViewById(R.id.ganador);
        System.out.println("DENTRO DE FINAL PARTIDA " + ganador);
        ganador.setText(extras.getString("ganador"));

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


