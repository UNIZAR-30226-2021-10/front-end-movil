package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AbandonarPartida extends AppCompatActivity {

    private static final int OPTION_CANCELAR = 0;
    private static final int OPTION_POSPONER = 1;
    private static final int OPTION_ABANDONAR = 2;

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


        // Botón de cancelar
        Button cancelarButton = (Button) findViewById(R.id.cancelar);
        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Botón de posponer partida
        Button posponerButton = (Button) findViewById(R.id.posponer);
        posponerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuPrincipal.class);
                startActivityForResult(intent, OPTION_POSPONER);
            }
        });

        // Botón de abandonar partida
        Button abandonarButton = (Button) findViewById(R.id.abandonar);
        abandonarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ABANDONAR);
            }
        });
    }
}


