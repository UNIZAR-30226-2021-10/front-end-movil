package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import SessionManagement.GestorSesion;
import SessionManagement.Question;
import SessionManagement.User;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JuegoMultijugador extends AppCompatActivity{

    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_ACABAR = 1;
    private static final int OPTION_CHAT = 2;

    private Socket msocket;

    private TextView texto_puntos3;
    private TextView texto_puntos4;
    private ImageView imagenUsuario1;
    private ImageView imagenUsuario2;
    private ImageView imagenUsuario3;
    private ImageView imagenUsuario4;

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
    private Random rndQuestion = new Random();

    private Button empezar;
    private Button siguiente;

    String[] categorias = {"Art and Literature", "Geography", "History", "Film and TV", "Science", "Sport and Leisure"};
    String[] coloresCategorías = {"#703C02", "#0398FA", "#FFDA00", "#FC57FF", "#17B009", "#FF8D00"};
    int[] puntosCat = {20, 30, 25, 15, 5, 10};
    int[] preguntasCogidas = new int[60]; // pueden llegar a ser 60 preguntas
    int indice;
    String codigo;
    String ganador;
    String tipo;
    int type;

    private int NUM_RONDAS;
    private int NUM_JUGADORES;
    int numero_ronda = 0;
    int numero_puntos_p1 = 0;
    int numero_puntos_p2 = 0;
    int numero_puntos_p3 = 0;
    int numero_puntos_p4 = 0;
    int teToca = 0;
    int puntos_ganador;
    int jugadoresEnSala;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;
    private boolean firstJoin = true;

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

        //Construirmos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(JuegoMultijugador.this);


        indice = 0;
        for(int i = 0; i< 60; i++){
            preguntasCogidas[i] = 0;
        }

        Bundle extras = getIntent().getExtras();
        //NUM_RONDAS = extras.getInt("rondas");
        //NUM_JUGADORES = extras.getInt("jugadores");
        codigo = extras.getString("codigo");
        tipo = extras.getString("tipo");
        type = Integer.parseInt(tipo);
        /*
        msocket = IO.socket(URI.create("http://localhost:5000"));
        msocket.connect();
        JSONObject aux = new JSONObject();
        try{
            aux.put("username", gestorSesion.getSession()); //username
            aux.put("code", codigo); //code
            aux.put("firstJoin",firstJoin); //firstJoin
           // aux.put("avatar",); //avatar
            //aux.put("history",); //history

        } catch (JSONException e){
            e.printStackTrace();
        }

        Ack ack = new Ack(){
            @Override
            public void call(Object... args){

            }
        };

        msocket.emit("join", aux, ack);
         */


        //llamar a la bd para conseguir el numRondas y numJugadores
        handleObtenerInfo();
        //asigna los jugadores donde corresponde en la pantalla del juego


        pregunta = (TextView)findViewById(R.id.pregunta);
        resp1 = (TextView)findViewById(R.id.respuesta1);
        resp2 = (TextView)findViewById(R.id.respuesta2);
        resp3 = (TextView)findViewById(R.id.respuesta3);
        resp4 = (TextView)findViewById(R.id.respuesta4);
        num_rondas = (TextView)findViewById(R.id.num_rondas);
        codigo_partida = (TextView)findViewById(R.id.partida_code);
        turno_jugador = (TextView)findViewById(R.id.turno_jugador);
        categoria = (TextView)findViewById(R.id.categoria);
        codigo_partida.setText(String.valueOf(codigo));

        usuario1_nombre = (TextView)findViewById(R.id.usuario1_nombre);
        usuario2_nombre = (TextView)findViewById(R.id.usuario2_nombre);
        usuario3_nombre = (TextView)findViewById(R.id.usuario3_nombre);
        usuario4_nombre = (TextView)findViewById(R.id.usuario4_nombre);
        usuario1_puntos = (TextView)findViewById(R.id.usuario1_puntos);
        usuario2_puntos = (TextView)findViewById(R.id.usuario2_puntos);
        usuario3_puntos = (TextView)findViewById(R.id.usuario3_puntos);
        usuario4_puntos = (TextView)findViewById(R.id.usuario4_puntos);
        imagenUsuario1 = (ImageView) findViewById(R.id.usuario1_imagen);
        imagenUsuario2 = (ImageView) findViewById(R.id.usuario2_imagen);
        imagenUsuario3 = (ImageView) findViewById(R.id.usuario3_imagen);
        imagenUsuario4 = (ImageView) findViewById(R.id.usuario4_imagen);

        texto_puntos3 = (TextView)findViewById(R.id.text_puntos3);
        texto_puntos4 = (TextView)findViewById(R.id.text_puntos4);

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
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("jugadoresEnSala",String.valueOf(jugadoresEnSala));
                extras.putString("codigo", codigo);
                Intent intent = new Intent(v.getContext(), AbandonarPartida.class);
                intent.putExtras(extras);
                startActivityForResult(intent, OPTION_ATRAS);
                System.out.println("TODO OK");
            }
        });

        // boton de chat
        ImageButton chatButton = (ImageButton) findViewById(R.id.chat);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PantallaChat.class);
                startActivityForResult(intent, OPTION_CHAT);
            }
        });


    }
    /*
    public void asignarJugadores(){
        // TODO: CUANDO SE VAYAN UNIENDO LOS JUGADORES.... ir poniendo sus datos
        if(NUM_JUGADORES == 4){
            usuario4_nombre.setText("usuario4");
            usuario4_puntos.setText("0");
            usuario3_nombre.setText("usuario3");
            usuario3_puntos.setText("0");
            usuario2_nombre.setText("usuario2");
            usuario2_puntos.setText("0");
            imagenUsuario4.setImageResource(R.mipmap.imagenusr3);
            imagenUsuario3.setImageResource(R.mipmap.imagenusr1);
            // TODO faltarian las imagenes de los demas
        }
        if(NUM_JUGADORES == 3){
            usuario3_nombre.setText("usuario3");
            usuario3_puntos.setText("0");
            usuario2_nombre.setText("usuario2");
            usuario2_puntos.setText("0");
            texto_puntos4.setText("");
            imagenUsuario3.setImageResource(R.mipmap.imagenusr1);
            // TODO faltarian las imagenes de los demas
        }
        if(NUM_JUGADORES == 2){
            texto_puntos3.setText("");
            texto_puntos4.setText("");
            usuario2_nombre.setText("usuario2");
            usuario2_puntos.setText("0");
        }
        usuario1_nombre.setText(gestorSesion.getSession());
        usuario1_puntos.setText("0");
    }*/

    public String quienEsGanador() {
        if (numero_puntos_p1 > numero_puntos_p2 && numero_puntos_p1 > numero_puntos_p3 && numero_puntos_p1 > numero_puntos_p4) {
            ganador = usuario1_nombre.getText().toString();
            puntos_ganador = numero_puntos_p1;
            return usuario1_nombre.getText().toString();
        } else if (numero_puntos_p2 > numero_puntos_p1 && numero_puntos_p2 > numero_puntos_p3 && numero_puntos_p2 > numero_puntos_p4) {
            ganador = usuario2_nombre.getText().toString();
            puntos_ganador = numero_puntos_p2;
            return usuario2_nombre.getText().toString();
        } else if (numero_puntos_p3 > numero_puntos_p2 && numero_puntos_p3 > numero_puntos_p1 && numero_puntos_p3 > numero_puntos_p4) {
            ganador = usuario3_nombre.getText().toString();
            puntos_ganador = numero_puntos_p3;
            return usuario3_nombre.getText().toString();
        } else {
            ganador = usuario4_nombre.getText().toString();
            puntos_ganador = numero_puntos_p4;
            return usuario4_nombre.getText().toString();
        }
    }

    public void comprobarRondas(){
        if (numero_ronda == NUM_RONDAS*NUM_JUGADORES) {
            Bundle extra = new Bundle();
            String ganador = quienEsGanador();
            extra.putString("ganador", ganador);
            handleFinPartidaMulti();
            //handleFinPartidaMultiJuega();
            Intent intent = new Intent(this, FinPartidaMulti.class);
            intent.putExtras(extra);
            startActivityForResult(intent, OPTION_ACABAR);
        }
    }

    public void asignarJugadores(int tipo){
        if (tipo == 1) { //ha creado la partida
            usuario1_nombre.setText(gestorSesion.getSession());
            usuario1_puntos.setText("0");
            imagenUsuario1.setImageResource(R.mipmap.imagenusr1);
            //imagenUsuario1 = gestorSesion.getImage();
            texto_puntos3.setText("");
            texto_puntos4.setText("");
            usuario2_nombre.setText("");
            usuario2_puntos.setText("");
            jugadoresEnSala = 1;
            handleUnirseJuega();
        }
        else{ //se ha unido a la partida
            if(jugadoresEnSala < NUM_JUGADORES){
                if(jugadoresEnSala == 1){ //es el usuario2
                    usuario2_nombre.setText(gestorSesion.getSession());
                    usuario2_puntos.setText("0");
                    //imagenUsuario2 = gestorSesion.getImage();
                    jugadoresEnSala ++;
                }
                else if(jugadoresEnSala == 2){ //es el usuario 3
                    usuario3_nombre.setText(gestorSesion.getSession());
                    usuario3_puntos.setText("0");
                    //imagenUsuario3 = gestorSesion.getImage();
                    jugadoresEnSala ++;
                }
                else if(jugadoresEnSala == 3){ //es el usuario 4
                    usuario4_nombre.setText(gestorSesion.getSession());
                    usuario4_puntos.setText("0");
                    //imagenUsuario4 = gestorSesion.getImage();
                    jugadoresEnSala ++;
                }
                handleUnirseJuega();
            }
        }
    }


    private void  handleObtenerInfo(){
        HashMap<String,String> obtener = new HashMap<>();
        obtener.put("codigo", codigo);

        Call<JsonObject> call = retrofitInterface.obtenerInfo(obtener);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonObject jsonObject = response.body().getAsJsonObject("idpartida");
                    NUM_JUGADORES = jsonObject.get("numJugadores").getAsInt();
                    NUM_RONDAS = jsonObject.get("rondas").getAsInt();
                    //System.out.println(NUM_JUGADORES);
                    //System.out.println(NUM_RONDAS);
                    System.out.println("TODO OK");
                    asignarJugadores(type);
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void  handleUnirseJuega(){
        HashMap<String,String> unirseJuega = new HashMap<>();

        unirseJuega.put("codigo",codigo);
        System.out.println(gestorSesion.getmailSession());
        System.out.println(String.valueOf(0));
        unirseJuega.put("email", gestorSesion.getmailSession());
        unirseJuega.put("puntos",String.valueOf(0));


        Call<JsonObject> call = retrofitInterface.UnirseMultijugadorJuega(unirseJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if(response.code() == 450){
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }



    private void  handleFinPartidaMulti(){
        HashMap<String,String> finPartidaMulti = new HashMap<>();

        finPartidaMulti.put("ganador", ganador);
        finPartidaMulti.put("codigo",codigo);

        Call<JsonObject> call = retrofitInterface.FinalPartidaMultijugador(finPartidaMulti);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if(response.code() == 450){
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
    private void  handleFinPartidaMultiJuega() {
        HashMap<String, String> finMultiJuega = new HashMap<>();

        finMultiJuega.put("codigo", codigo);
        finMultiJuega.put("usuario_email", ganador);
        finMultiJuega.put("puntuacion", Integer.toString(puntos_ganador));

        Call<JsonObject> call = retrofitInterface.finPartidaMultiJuega(finMultiJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else if (response.code() == 450) {
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido encontrar partida", Toast.LENGTH_LONG).show();
                } else if (response.code() == 440) {
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido insertar jugada", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido insertar partida", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/

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
                //ponerPregunta(pregunta1[0],pregunta1[1],pregunta1[2],pregunta1[3], pregunta1[4]);
                break;
            case 2:
                imagenDados.setBackgroundResource(R.drawable.dado2icon);
                //ponerPregunta(pregunta1[0],pregunta1[1],pregunta1[2],pregunta1[3], pregunta1[4]);
                break;
            case 3:
                imagenDados.setBackgroundResource(R.drawable.dado3icon);
                //ponerPregunta(pregunta2[0],pregunta2[1],pregunta2[2],pregunta2[3], pregunta2[4]);
                break;
            case 4:
                imagenDados.setBackgroundResource(R.drawable.dado4icon);
                //ponerPregunta(pregunta3[0],pregunta3[1],pregunta3[2],pregunta3[3], pregunta3[4]);
                break;
            case 5:
                imagenDados.setBackgroundResource(R.drawable.dado5icon);
                //ponerPregunta(pregunta3[0],pregunta3[1],pregunta3[2],pregunta3[3], pregunta3[4]);
                break;
            case 6:
                imagenDados.setBackgroundResource(R.drawable.dado6icon);
                //ponerPregunta(pregunta2[0],pregunta2[1],pregunta2[2],pregunta2[3], pregunta2[4]);
                break;
        }
        obtenerPregunta(random);
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
        if(NUM_JUGADORES >= 3){
            usuario3_puntos.setText(String.valueOf(numero_puntos_p3));
        } else if(NUM_JUGADORES >= 4){
            usuario4_puntos.setText(String.valueOf(numero_puntos_p4));
        }
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
                if(teToca == 1){
                    numero_puntos_p1 += puntosCat[cat];
                } else if(teToca == 2){
                    numero_puntos_p2 += puntosCat[cat];
                } else if(teToca == 3){
                    numero_puntos_p3 += puntosCat[cat];
                } else {
                    numero_puntos_p4 += puntosCat[cat];
                }

                siguiente.setClickable(true);
                comprobarRondas();
            }
        });
    }


    public void obtenerPregunta(final int random){
        System.out.println(categorias[random-1]);
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
                    for(int i = 0; i< 60; i++){
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
                    Toast.makeText( JuegoMultijugador.this, "No se ha conseguido la pregunta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText( JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


