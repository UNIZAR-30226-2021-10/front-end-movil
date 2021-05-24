package eina.unizar.front_end_movil;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InstruccionesJuego extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrucciones_juego);
        getSupportActionBar().hide();

        TextView inst = (TextView) findViewById(R.id.instrucciones);
        inst.setMovementMethod(new ScrollingMovementMethod());
        String texto = "Modos de juego: individual y multijugador. \n\n" +
                "Al empezar una partida individual le deberás dar al botón \"Empezar\" y eso lanzará el dado e iniciará " +
                "la partida. Si estás en modo multijugador la partida empezará automáticamente cuando " +
                "todos los jugadores estén en la partida. \n\n" +
                "Deberás responder a la pregunta que te toque y el propio juego te indicará si es la " +
                "correcta en cuanto cliques en ella. Debrás darle al boton \"Siguiente\" para pasar a la siguiente ronda. \n\n" +
                "El dado consta de seis colores. Cada uno de ellos " +
                "está asociado a una categoria diferente. Las preguntas son multirespuesta y solo una de las opciones " +
                "es la correcta. Las categorías son las siguientes y te darán los siguientes puntos:\n" +
                "Geografía (azul): 30 puntos.\n" +
                "Arte y Literatura (marrón): 20 puntos.\n" +
                "HIstoria (amarillo): 25 puntos.\n" +
                "Cine (rosa): 15 puntos.\n" +
                "Ciencias y Naturaleza (verde): 5 puntos.\n" +
                "Deportes y Pasatiempos (naranja): 10 puntos.\n\n" +
                "El juego consiste en sumar puntos, los cuales se consiguen respondiendo correctamente a " +
                "las preguntas. Cada tema tiene un " +
                "valor de puntos asociados. Conforme vayas obteniendo puntos en las partidas, obtendrás " +
                "monedas que más tarde puedes usar para comprar objetos para tu avatar en la tienda. \n\n" +
                "Puedes abandonar la partida individual cuando desees, simplemente dándole al botón \"Atras\". Si " +
                "abandonas la partida, esta no se registrará y será como si nunca hubieras jugado. En las partidas multijugador no " +
                "podrás irte hasta que no estén todos los jugadores. Una vez estén todos puedes irte dandole al msimo botón. ¡AVISO! " +
                "Si te vas de la partida, se te registraran 0 puntos en esa partida.\n\n" +
                "Las partidas multijugador tienen un mínimo de dos jugadores y un máximo de cuatro " +
                "jugadores. Gana el jugador que más puntos obtiene. Puedes crear una " +
                "partida o unirte a una ya existente con un código (todas las partidas lo tienen, pregúntale " +
                "a tus amigos por el código de la partida que han creado). Además, el modo multijugador " +
                "consta de un chat que te permite hablar con tus rivales. \n\n ¡A JUGAR!";

        inst.setText(texto);

        // Botón de atras
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}


