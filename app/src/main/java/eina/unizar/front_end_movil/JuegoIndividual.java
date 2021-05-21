package eina.unizar.front_end_movil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import java.util.HashMap;

import SessionManagement.Question;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Random;



public class JuegoIndividual extends AppCompatActivity{

    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_ACABAR = 1;
    private static final int OPTION_INSTRUCCIONES = 2;

    private TextView pregunta;
    private TextView resp1;
    private TextView resp2;
    private TextView resp3;
    private TextView resp4;
    private TextView num_rondas;
    private TextView num_puntos;
    private TextView categoria;
    private ImageButton imagenDados;
    private Button empezar;
    private Button siguiente;
    private Random rndNumber = new Random();
    private Random rndQuestion = new Random();

    String[] categorias = {"Art and Literature", "Geography", "History", "Film and TV", "Science", "Sport and Leisure"};
    String[] coloresCategorías = {"#703C02", "#0398FA", "#FFDA00", "#FC57FF", "#17B009", "#FF8D00"};
    int[] puntosCat = {20, 30, 25, 15, 5, 10};

    int[] preguntasCogidas = new int[15];
    int indice;

    private int NUM_RONDAS;
    int numero_ronda = 0;
    int numero_puntos = 0;

    private RetrofitInterface retrofitInterface;

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

        //Construirmos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        indice = 0;
        for(int i = 0; i< 15; i++){
            preguntasCogidas[i] = 0;
        }

        Bundle extras = getIntent().getExtras();
        NUM_RONDAS = extras.getInt("rondas");

        pregunta = (TextView)findViewById(R.id.pregunta);
        resp1 = (TextView)findViewById(R.id.respuesta1);
        resp2 = (TextView)findViewById(R.id.respuesta2);
        resp3 = (TextView)findViewById(R.id.respuesta3);
        resp4 = (TextView)findViewById(R.id.respuesta4);
        num_rondas = (TextView)findViewById(R.id.num_rondas);
        num_puntos = (TextView)findViewById(R.id.num_puntos);
        categoria = (TextView)findViewById(R.id.categoria);
        siguiente = (Button) findViewById(R.id.siguiente);
        siguiente.setVisibility(View.INVISIBLE); // quitarlo hasta que le den a empezar
        siguiente.setClickable(false);
        empezar = (Button) findViewById(R.id.empezar);
        empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetear();
                rollDice();
                empezar.setVisibility(View.GONE);
                empezar.setClickable(true);
                siguiente.setVisibility(View.VISIBLE);
            }
        }); // para quitar el botón

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetear();
                rollDice();
            }
        });

        desactivar();

        imagenDados = (ImageButton) findViewById(R.id.dado);
        imagenDados.setClickable(false);

        // boton de atrás

        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                    AlertDialog.Builder quiereSalir = new AlertDialog.Builder(JuegoIndividual.this);
                    quiereSalir.setMessage("¿Desea abandonar la partida?");
                    quiereSalir.setCancelable(false);
                    quiereSalir.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                            startActivityForResult(intent, OPTION_ATRAS);
                        }
                    });
                    quiereSalir.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog titulo = quiereSalir.create();
                    titulo.setTitle("Salir");
                    titulo.show();
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

    void comprobarRondas(){
        if (numero_ronda == NUM_RONDAS) {
            Bundle extra = new Bundle();
            extra.putInt("puntosTotales", numero_puntos);
            extra.putInt("rondas", NUM_RONDAS);
            Intent intent = new Intent(this, FinPartidaIndv.class);
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
                break;
            case 2:
                imagenDados.setBackgroundResource(R.drawable.dado2icon);
                break;
            case 3:
                imagenDados.setBackgroundResource(R.drawable.dado3icon);
                break;
            case 4:
                imagenDados.setBackgroundResource(R.drawable.dado4icon);
                break;
            case 5:
                imagenDados.setBackgroundResource(R.drawable.dado5icon);
                break;
            case 6:
                imagenDados.setBackgroundResource(R.drawable.dado6icon);
                break;
        }
        obtenerPregunta(random);
        categoria.setText(categorias[random-1]);
        categoria.setTextColor((Color.parseColor(coloresCategorías[random-1])));

        // text view de rondas --> para poner por qué ronda vas
        numero_ronda++;
        num_rondas.setText(String.valueOf(numero_ronda));

        // text view de puntos acumulados --> para poner cuántos puntos lleva
        num_puntos.setText(String.valueOf(numero_puntos));

    }

    void ponerPregunta(String enunciado, String correcta, String incorrecta1, String incorrecta2, String incorrecta3, int posicion, int indCategoria){
        siguiente.setClickable(false);
        pregunta.setText(enunciado); // text view de pregunta

        if(posicion == 1){
            ponerBien(resp1, correcta, indCategoria);
            ponerMal(resp2, incorrecta1, resp1);
            ponerMal(resp3, incorrecta2, resp1);
            ponerMal(resp4, incorrecta3, resp1);
        } else if(posicion == 2){
            ponerMal(resp1, incorrecta1, resp2);
            ponerBien(resp2, correcta, indCategoria);
            ponerMal(resp3, incorrecta2, resp2);
            ponerMal(resp4, incorrecta3, resp2);
        } else if(posicion == 3){
            ponerMal(resp1, incorrecta1, resp3);
            ponerMal(resp2, incorrecta2, resp3);
            ponerBien(resp3, correcta, indCategoria);
            ponerMal(resp4, incorrecta3, resp3);
        } else{
            ponerMal(resp1, incorrecta1, resp4);
            ponerMal(resp2, incorrecta2, resp4);
            ponerMal(resp3, incorrecta3, resp4);
            ponerBien(resp4, correcta, indCategoria);
        }
    }

    void ponerMal(final TextView respMal, String texto, final TextView respBien){
        respMal.setText(texto);
        respMal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // respuesta incorrecta
                respMal.setBackgroundColor((Color.parseColor("#E35252")));
                respBien.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                siguiente.setClickable(true);
                comprobarRondas();
            }
        });
    }

    void ponerBien(final TextView respBien, String texto, final int cat){
        respBien.setText(texto);
        respBien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respBien.setBackgroundColor((Color.parseColor("#87e352")));
                desactivar();
                numero_puntos += puntosCat[cat];
                siguiente.setClickable(true);
                comprobarRondas();
            }
        });
    }


    public void obtenerPregunta(final int random){
        final Question[] q = new Question[1];
        Call<JsonObject> call = retrofitInterface.getQuestion(categorias[random-1]);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200){
                    boolean yaEsta = false;
                    JsonObject jsonObject = response.body().getAsJsonObject("idpregunta");
                    //Creamos una pregunta
                    Question q =  new Question(jsonObject.get("incorrecta1").getAsString(),
                            jsonObject.get("incorrecta2").getAsString(),
                            jsonObject.get("incorrecta3").getAsString(),
                            jsonObject.get("correcta").getAsString(),
                            jsonObject.get("enunciado").getAsString(),
                            categorias[random-1]);
                    for(int i = 0; i< 15; i++){
                        if(preguntasCogidas[i] == jsonObject.get("idpregunta").getAsInt()){
                            obtenerPregunta(random);
                            yaEsta = true;
                        }
                    }
                    if(!yaEsta){
                        int randomQuestion = rndQuestion.nextInt(4) + 1;
                        preguntasCogidas[indice] = jsonObject.get("idpregunta").getAsInt();
                        //System.out.println(preguntasCogidas[indice]);
                        indice++;
                        ponerPregunta(q.getStatement(), q.getCorrect(), q.getIncorrect1(), q.getIncorrect2(), q.getIncorrect3(), randomQuestion, random-1);
                    }

                }else if(response.code() == 400){
                    Toast.makeText( JuegoIndividual.this, "No se ha conseguido la pregunta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText( JuegoIndividual.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


