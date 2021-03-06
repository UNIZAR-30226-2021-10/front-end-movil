package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DecisionJuego extends AppCompatActivity {

    private static final int OPTION_MUTLIJUGADOR = 0;
    private static final int OPTION_INDIVIDUAL = 1;
    private static final int OPTION_TIENDA = 2;
    private static final int OPTION_PERFIL = 3;
    private static final int OPTION_INSTRUCCIONES = 4;
    private static final int OPTION_RANKING = 5;
    private static final int OPTION_HISTORIAL = 6;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_juego);
        getSupportActionBar().hide();

        // Botón de empezar partida multijugador
        Button multiButton = (Button) findViewById(R.id.multijugador);
        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DecisionMultijugador.class);
                startActivityForResult(intent, OPTION_MUTLIJUGADOR);
            }
        });

        // Botón de empezar partida individual
        Button indvButton = (Button) findViewById(R.id.individual);
        indvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DecisionIndividual.class);
                startActivityForResult(intent, OPTION_INDIVIDUAL);
            }
        });

        ImageButton tiendaButton = (ImageButton) findViewById(R.id.tienda);
        tiendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), PantallaTienda.class);
                startActivityForResult(intent, OPTION_TIENDA);
            }
        });

        ImageButton perfilButton = (ImageButton) findViewById(R.id.perfil);
        perfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), PerfilUsuario.class);
                startActivityForResult(intent, OPTION_PERFIL);
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

        ImageButton rankingButton = (ImageButton) findViewById(R.id.ranking);
        rankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Ranking.class);
                startActivityForResult(intent, OPTION_RANKING);
            }
        });

        ImageButton historialButton = (ImageButton) findViewById(R.id.historial);
        historialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Historial.class);
                startActivityForResult(intent, OPTION_HISTORIAL);
            }
        });

    }
}


