package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class JuegoMultijugador extends AppCompatActivity{

    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_ACABAR = 1;
    private static final int OPTION_CHAT = 2;

    private TextView pregunta;
    private TextView resp1;
    private TextView resp2;
    private TextView resp3;
    private TextView resp4;
    private TextView num_rondas;
    private TextView codigo_partida;
    private TextView turno_jugador;
    private TextView usuario1_nombre;
    private TextView usuario2_nombre;
    private TextView usuario3_nombre;
    private TextView usuario4_nombre;
    private TextView usuario1_puntos;
    private TextView usuario2_puntos;
    private TextView usuario3_puntos;
    private TextView usuario4_puntos;
    private TextView categoria;
    private ImageButton imagenDados;
    private Random rndNumber = new Random();

    String[] categorias = {"Arte y Literatura", "Geografía", "Historia", "Cine", "Ciencias y Naturaleza", "Deportes"};
    String[] coloresCategorías = {"#703C02", "#0398FA", "#FFDA00", "#FC57FF", "#17B009", "#FF8D00"};
    String[] pregunta1 = {"Georgia shares a land border with which of these countries?", "Syria", "Armenia", "Iraq", "Lebanon"};
    String[] pregunta2 = {"Two rats can become the progenitors of 15,000 rats in less than..", "1 month", "1 week", "1 day", "1 year"};
    String[] pregunta3 = {"What Separates Spain From Morocco?", "The North African Strait", "The Bering Strait", "The Strait Of Gibralter" , "The Strait Of Casablanca"};

    private int NUM_RONDAS;
    private int NUM_JUGADORES;
    int numero_ronda = 0;
    int numero_puntos_p1 = 0;
    int numero_puntos_p2 = 0;
    int numero_puntos_p3 = 0;
    int numero_puntos_p4 = 0;
    int teToca = 0;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego_multijugador);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        NUM_RONDAS = extras.getInt("rondas");
        NUM_JUGADORES = extras.getInt("jugadores");

        pregunta = (TextView)findViewById(R.id.pregunta);
        resp1 = (TextView)findViewById(R.id.respuesta1);
        resp2 = (TextView)findViewById(R.id.respuesta2);
        resp3 = (TextView)findViewById(R.id.respuesta3);
        resp4 = (TextView)findViewById(R.id.respuesta4);
        num_rondas = (TextView)findViewById(R.id.num_rondas);
        codigo_partida = (TextView)findViewById(R.id.codigo_partida);
        turno_jugador = (TextView)findViewById(R.id.turno_jugador);
        categoria = (TextView)findViewById(R.id.categoria);

        usuario1_nombre = (TextView)findViewById(R.id.usuario1_nombre);
        usuario2_nombre = (TextView)findViewById(R.id.usuario2_nombre);
        usuario3_nombre = (TextView)findViewById(R.id.usuario3_nombre);
        usuario4_nombre = (TextView)findViewById(R.id.usuario4_nombre);
        usuario1_puntos = (TextView)findViewById(R.id.usuario1_puntos);
        usuario2_puntos = (TextView)findViewById(R.id.usuario2_puntos);
        usuario3_puntos = (TextView)findViewById(R.id.usuario3_puntos);
        usuario4_puntos = (TextView)findViewById(R.id.usuario4_puntos);

        // TODO: aquí se elegirán los jugadores
        if(NUM_JUGADORES == 4){
            usuario1_nombre.setText("usuario1");
            usuario2_nombre.setText("usuario2");
            usuario3_nombre.setText("usuario3");
            usuario4_nombre.setText("usuario4");
            usuario1_puntos.setText("0");
            usuario2_puntos.setText("0");
            usuario3_puntos.setText("0");
            usuario4_puntos.setText("0");
        }

        desactivar();

        imagenDados = (ImageButton) findViewById(R.id.dado);
        imagenDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetear();
                rollDice();
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

        // boton de atrás
        ImageButton chatButton = (ImageButton) findViewById(R.id.chat);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PantallaChat.class);
                startActivityForResult(intent, OPTION_CHAT);
            }
        });


    }

    String quienEsGanador() {
        if (numero_puntos_p1 > numero_puntos_p2 && numero_puntos_p1 > numero_puntos_p3 && numero_puntos_p1 > numero_puntos_p4) {
            return usuario1_nombre.getText().toString();
        } else if (numero_puntos_p2 > numero_puntos_p1 && numero_puntos_p2 > numero_puntos_p3 && numero_puntos_p2 > numero_puntos_p4) {
            return usuario2_nombre.getText().toString();
        } else if (numero_puntos_p3 > numero_puntos_p2 && numero_puntos_p3 > numero_puntos_p1 && numero_puntos_p3 > numero_puntos_p4) {
            return usuario3_nombre.getText().toString();
        } else {
            return usuario4_nombre.getText().toString();
        }
    }

    void comprobarRondas(){
        if (numero_ronda == NUM_RONDAS*NUM_JUGADORES) {
            Bundle extra = new Bundle();
            String ganador = quienEsGanador();
            extra.putString("ganador", ganador);
            Intent intent = new Intent(this, FinPartidaMulti.class);
            intent.putExtras(extra);
            startActivityForResult(intent, OPTION_ACABAR);
        }
    }

    void activar(){
        pregunta.setClickable(true);
        resp1.setClickable(true);
        resp2.setClickable(true);
        resp3.setClickable(true);
        resp4.setClickable(true);
    }

    void desactivar(){
        pregunta.setClickable(false);
        resp1.setClickable(false);
        resp2.setClickable(false);
        resp3.setClickable(false);
        resp4.setClickable(false);
    }

    void resetear(){
        activar();
        pregunta.setText("");
        resp1.setText("");
        resp2.setText("");
        resp3.setText("");
        resp4.setText("");
        resp1.setBackgroundColor((Color.parseColor("#ffffffff")));
        resp2.setBackgroundColor((Color.parseColor("#ffffffff")));
        resp3.setBackgroundColor((Color.parseColor("#ffffffff")));
        resp4.setBackgroundColor((Color.parseColor("#ffffffff")));
    }

    void rollDice(){
        int random = rndNumber.nextInt(6) + 1;
        switch(random){
            case 1:
                imagenDados.setBackgroundResource(R.drawable.dado1icon);
                ponerPregunta(pregunta1[0],pregunta1[1],pregunta1[2],pregunta1[3], pregunta1[4]);
                break;
            case 2:
                imagenDados.setBackgroundResource(R.drawable.dado2icon);
                ponerPregunta(pregunta1[0],pregunta1[1],pregunta1[2],pregunta1[3], pregunta1[4]);
                break;
            case 3:
                imagenDados.setBackgroundResource(R.drawable.dado3icon);
                ponerPregunta(pregunta2[0],pregunta2[1],pregunta2[2],pregunta2[3], pregunta2[4]);
                break;
            case 4:
                imagenDados.setBackgroundResource(R.drawable.dado4icon);
                ponerPregunta(pregunta3[0],pregunta3[1],pregunta3[2],pregunta3[3], pregunta3[4]);
                break;
            case 5:
                imagenDados.setBackgroundResource(R.drawable.dado5icon);
                ponerPregunta(pregunta3[0],pregunta3[1],pregunta3[2],pregunta3[3], pregunta3[4]);
                break;
            case 6:
                imagenDados.setBackgroundResource(R.drawable.dado6icon);
                ponerPregunta(pregunta2[0],pregunta2[1],pregunta2[2],pregunta2[3], pregunta2[4]);
                break;
        }
        categoria.setText(categorias[random-1]);
        categoria.setTextColor((Color.parseColor(coloresCategorías[random-1])));
        // text view de rondas --> para poner por qué ronda vas
        numero_ronda++;
        num_rondas.setText(String.valueOf(numero_ronda));
        teToca++;
        if(teToca == NUM_JUGADORES+1){
            teToca = 1;
        }
        turno_jugador.setText(String.valueOf(teToca));

        // asignar puntos respectivos
        usuario1_puntos.setText(String.valueOf(numero_puntos_p1));
        usuario2_puntos.setText(String.valueOf(numero_puntos_p2));
        usuario3_puntos.setText(String.valueOf(numero_puntos_p3));
        usuario4_puntos.setText(String.valueOf(numero_puntos_p4));
    }

    void ponerPregunta(String preguntaN, String resp1N, String resp2N, String resp3N, String resp4N){

        // TODO: esto luego se hará con la base de datos
        // text view de pregunta
        imagenDados.setClickable(false);
        pregunta.setText(preguntaN);

        // textview respuesta 1
        resp1.setText(resp1N);
        resp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp1.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                imagenDados.setClickable(true);
                comprobarRondas();
            }
        });
        // textview respuesta 2
        resp2.setText(resp2N);
        resp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // respuesta correcta
                // TODO: poner if de si es incorrecta o no para el color
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                if(teToca == 1){
                    numero_puntos_p1+= 50;
                } else if(teToca == 2){
                    numero_puntos_p2+= 50;
                } else if(teToca == 3){
                    numero_puntos_p3+= 50;
                } else{
                    numero_puntos_p4+= 50;
                }
                imagenDados.setClickable(true);
                comprobarRondas();
            }
        });
        // textview de respuesta 3
        resp3.setText(resp3N);
        resp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp3.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                imagenDados.setClickable(true);
                comprobarRondas();
            }
        });
        // textview de respuesta 4
        resp4.setText(resp4N);
        resp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // respuesta incorrecta
                // TODO: poner if de si es incorrecta o no para el color
                resp4.setBackgroundColor((Color.parseColor("#E35252")));
                resp2.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                imagenDados.setClickable(true);
                comprobarRondas();
            }
        });
    }
}


