package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class JuegoIndividual extends AppCompatActivity{

    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_JUEGO = 1;

    TextView pregunta;
    TextView resp1;
    TextView resp2;
    TextView resp3;
    TextView resp4;
    TextView num_rondas;
    TextView num_puntos;
    TextView categoria;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego_individual);
        getSupportActionBar().hide();

        pregunta = (TextView)findViewById(R.id.pregunta);
        resp1 = (TextView)findViewById(R.id.respuesta1);
        resp2 = (TextView)findViewById(R.id.respuesta2);
        resp3 = (TextView)findViewById(R.id.respuesta3);
        resp4 = (TextView)findViewById(R.id.respuesta4);
        num_rondas = (TextView)findViewById(R.id.num_rondas);
        num_puntos = (TextView)findViewById(R.id.num_puntos);
        categoria = (TextView)findViewById(R.id.categoria);

        // text view de rondas --> para poner por qué ronda vas
        num_rondas.setText("1");

        // text view de puntos acumulados --> para poner cuántos puntos lleva
        num_puntos.setText("0");

        // text view de categoria
        String[] categorias = {"Arte y Literatura", "Geografía", "Historia", "Cine", "Ciencias y Naturaleza", "Deportes"};
        String[] coloresCategorías = {"#703C02", "#0398FA", "#FFDA00", "#FC57FF", "#17B009", "#FF8D00"};
        // Esto luego no se hará por que se tendrá el dado
        int valorDado = (int) Math.floor(Math.random()*6);
        categoria.setText(categorias[valorDado]);
        categoria.setTextColor((Color.parseColor(coloresCategorías[valorDado])));

        // TODO: esto luego se hará con la base de datos
        // text view de pregunta
        pregunta.setText("¿Cúal es el río más caudaloso del mundo?");

        // textview respuesta 1
        resp1.setText("Amazonas");
        resp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp1.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(v.getContext(), JuegoIndividual.class);
                        startActivityForResult(intent, OPTION_JUEGO);
                    }
                }, 2000);//wait 1000ms before doing the action
            }
        });

        // textview respuesta 2
        resp2.setText("Nilo");
        resp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // respuesta correcta
                // TODO: poner if de si es incorrecta o no para el color
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(v.getContext(), JuegoIndividual.class);
                        startActivityForResult(intent, OPTION_JUEGO);
                    }
                }, 2000);//wait 1000ms before doing the action
            }
        });

        // textview de respuesta 3
        resp3.setText("Río Amarillo");
        resp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp3.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(v.getContext(), JuegoIndividual.class);
                        startActivityForResult(intent, OPTION_JUEGO);
                    }
                }, 2000);//wait 1000ms before doing the action
            }
        });

        // textview de respuesta 4
        resp4.setText("Yangste");
        resp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp4.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(v.getContext(), JuegoIndividual.class);
                        startActivityForResult(intent, OPTION_JUEGO);
                    }
                }, 2000);//wait 1000ms before doing the action
            }
        });

        // boton de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AbandonarPartida.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });


    }
}


