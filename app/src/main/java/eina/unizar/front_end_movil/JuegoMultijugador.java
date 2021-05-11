package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import SessionManagement.GestorSesion;
import SessionManagement.Question;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JuegoMultijugador extends AppCompatActivity{

    private static final String ipMarta = "http://192.168.1.162:5000/";
    private static final String ipAndrea = "http://192.168.0.26:5000/";

    private static final String TAG = "JuegoMultijugador";
    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_ACABAR = 1;
    private static final int OPTION_CHAT = 2;

    private Socket msocket;

    private TextView texto_puntos2;
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
    String[] coloresCategorias = {"#703C02", "#0398FA", "#FFDA00", "#FC57FF", "#17B009", "#FF8D00"};
    int[] puntosCat = {20, 30, 25, 15, 5, 10};
    String codigo;
    String ganador;
    String tipo;
    int type;
    ArrayList<Jugadores> players = new ArrayList<Jugadores>();

    private int NUM_RONDAS;
    private int NUM_JUGADORES;
    private int ID_PARTIDA;
    private String EMAIL;
    int numero_ronda = 1;
    int numero_puntos_p1 = 0;
    int numero_puntos_p2 = 0;
    int numero_puntos_p3 = 0;
    int numero_puntos_p4 = 0;
    int teToca = 1;
    int puntos_ganador;
    int jugadoresEnSala;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;
    private boolean firstJoin = true;

    private Emitter.Listener message = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    JSONObject datos = (JSONObject) args[0];
                    System.out.println(datos);
                    String nickname = "";
                    String avatar = "";
                    String mensaje = "";
                    try {
                        nickname = datos.getString("nombreUsr");
                        avatar = datos.getString("avatarUsr");
                        mensaje = datos.getString("text");
                        System.out.println("NICKNAME EN EMITTER: " + nickname);
                        System.out.println("AVATAR EN EMITTER: " + avatar);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(mensaje.equals("Se ha unido " + nickname)) {
                        Jugadores jugador = new Jugadores(nickname, 0, avatar);
                        players.add(jugador);
                        asignarJugadores();
                    }
                    for (Object obj : args) {
                        Log.d(TAG," NOT Errors :: " + obj);
                    }

                }
            });
        }
    };

    //turno actualizado despues de que el jugador pase el turno
    private Emitter.Listener nuevoTurno = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    int turnoActual = (int) args[0];
                    int rondaActual = (int) args[1];
                    int puntosActualizar = (int) args[2];
                    teToca = turnoActual;
                    numero_ronda = rondaActual;
                    System.out.println("Estos son los puntos que me llegan: ");
                    System.out.println(puntosActualizar);
                    players.get(teToca-1).setPuntos(puntosActualizar);
                    asignarJugadores();
                    //players.get(teToca-1).getUsername();
                    //es mi turno de jugar
                    if(players.get(teToca-1).getUsername().equals(gestorSesion.getSession())){
                        siguiente.setVisibility(View.VISIBLE);
                        siguiente.setClickable(true);
                        rollDice();
                    }
                    num_rondas.setText(String.valueOf(numero_ronda));
                    turno_jugador.setText(players.get(teToca - 1).getUsername());
                }
            });
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();

        msocket.disconnect();
        msocket.emit("disconnection");
        msocket.off();
        //mSocket.off("message", onNewMessage);
    }

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

        Bundle extras = getIntent().getExtras();
        codigo = extras.getString("codigo");
        tipo = extras.getString("tipo");
        type = Integer.parseInt(tipo);

        unirConXML();

        creadorPartida();
        if(type == 1) {
            handleUnirseJuega();
        }

        {
            try {
                IO.Options options = new IO.Options();
                options.transports = new String[]{WebSocket.NAME};
                msocket = IO.socket(ipAndrea, options);
                System.out.println("SOS");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        msocket.on("message",message)
                .on("recibirTurno", nuevoTurno)
                .on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        for (Object obj : args) {
                            System.out.println("POR FIN!!!!!!");
                            Log.d(TAG," NOT Errors :: " + obj);
                        }
                    }
                })
                .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        for (Object obj : args) {
                            Log.d(TAG,"Errors :: " + obj);
                        }
                    }
                });


        msocket.connect();

        JSONObject aux = new JSONObject();
        try{
            aux.put("username", gestorSesion.getSession()); //username
            aux.put("code", codigo); //code
            aux.put("firstJoin",firstJoin); //firstJoin
            aux.put("avatar", gestorSesion.getAvatarSession()); //avatar

        } catch (JSONException e){
            e.printStackTrace();
        }

        msocket.emit("join", aux, new Ack(){
            @Override
            public void call(Object... args){
                //JSONObject response = (JSONObject) args[0];
                //System.out.println(response);
            }
        });

        //llamar a la bd para conseguir el numRondas y numJugadores
        handleObtenerInfo();

        if(type == 1){
            empezar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(players.size() == NUM_JUGADORES){
                        resetear();
                        num_rondas.setText(String.valueOf(numero_ronda));
                        turno_jugador.setText(players.get(teToca - 1).getUsername());
                        rollDice();
                        empezar.setVisibility(View.GONE);
                        empezar.setClickable(true);
                        siguiente.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(JuegoMultijugador.this, "Faltan jugadores por unirse a la partida", Toast.LENGTH_LONG).show();
                    }
                }
            }); // para quitar el botón
        }
        else{
            empezar.setVisibility(View.GONE);
            empezar.setClickable(false);
        }


        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetear();
                desactivar();
                //int indice = teToca - 1;
                siguiente.setVisibility(View.GONE);
                siguiente.setClickable(false);
                System.out.println("El teToca antes del if es: ");
                System.out.println(teToca);
                int indice = teToca - 1;
                System.out.println("El indice antes del if es: ");
                System.out.println(indice);
                teToca = teToca + 1;
                if(teToca == NUM_JUGADORES+1){
                    teToca = 1;
                    //indice = 0;
                }
                System.out.println("El teToca despues del if es: ");
                System.out.println(teToca);
                numero_ronda++;
                //Aqui he cambiado el parámetro final
                System.out.println("Los puntos que envio son estos:");
                System.out.println(players.get(teToca-1).getPuntos());
                System.out.println("Los puntos del jugador 1, indice 0 son:");
                System.out.println(players.get(0).getPuntos());
                System.out.println("Los puntos del jugador 2, indice 1 son:");
                System.out.println(players.get(1).getPuntos());
                msocket.emit("pasarTurno", teToca, numero_ronda, players.get(indice).getPuntos());
                num_rondas.setText(String.valueOf(numero_ronda));
                turno_jugador.setText(players.get(teToca - 1).getUsername());
                asignarJugadores();
                //rollDice();
            }
        });

        desactivar();

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

    public void cargarImagenUsuario(String url, ImageView perfilButton){
        Picasso.get().load(url).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(perfilButton);
    }

    public void unirConXML(){
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

        texto_puntos2 = (TextView)findViewById(R.id.text_puntos2);
        texto_puntos3 = (TextView)findViewById(R.id.text_puntos3);
        texto_puntos4 = (TextView)findViewById(R.id.text_puntos4);

        siguiente = (Button) findViewById(R.id.siguiente);
        siguiente.setVisibility(View.INVISIBLE); // quitarlo hasta que le den a empezar
        siguiente.setClickable(false);

        empezar = (Button) findViewById(R.id.empezar);

        imagenDados = (ImageButton) findViewById(R.id.dado);
        imagenDados.setClickable(false);
    }

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

    public void creadorPartida(){
        if (type == 1) { //ha creado la partida
            usuario1_nombre.setText(gestorSesion.getSession());
            usuario1_puntos.setText("0");
            cargarImagenUsuario(gestorSesion.getAvatarSession(), imagenUsuario1);
            Jugadores jugador = new Jugadores(gestorSesion.getSession(), 0, gestorSesion.getAvatarSession());
            players.add(jugador);
        }
    }

    public void asignarJugadores(){
        for(int i = 0; i < players.size(); i++){
            if(i == 0){
                usuario1_nombre.setText(players.get(i).getUsername());
                usuario1_puntos.setText(String.valueOf(players.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario1);
            }
            else if(i == 1){
                usuario2_nombre.setText(players.get(i).getUsername());
                usuario2_puntos.setText(String.valueOf(players.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario2);
                texto_puntos2.setText("puntos");
            }
            else if(i == 2){
                usuario3_nombre.setText(players.get(i).getUsername());
                usuario3_puntos.setText(String.valueOf(players.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario3);
                texto_puntos3.setText("puntos");
            }
            else if(i == 3){
                usuario4_nombre.setText(players.get(i).getUsername());
                usuario4_puntos.setText(String.valueOf(players.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario4);
                texto_puntos4.setText("puntos");
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
                    ID_PARTIDA = jsonObject.get("idpartida").getAsInt();
                    NUM_JUGADORES = jsonObject.get("numJugadores").getAsInt();
                    NUM_RONDAS = jsonObject.get("rondas").getAsInt();
                    System.out.println("TODO OK");
                    System.out.println("Este es el handleObtenerInfo");
                    System.out.println(ID_PARTIDA);
                    //obtener jugadores de la partida
                    if(type != 1){
                        handleObtenerJugadores();
                    }
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido obtener informacion", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void handleObtenerJugadores(){
        System.out.println("Este es el handleObtenerJugadores");
        System.out.println(ID_PARTIDA);

        Call<JsonArray> call = retrofitInterface.obtenerJugadores(String.valueOf(ID_PARTIDA));
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {
                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        String email = prueba.get("email").getAsString();
                        String nickname = prueba.get("nickname").getAsString();
                        String image = prueba.get("imagen").getAsString(); //coger el avatar
                        Jugadores jugador2 = new Jugadores(nickname,0, image);
                        players.add(jugador2);
                    }
                    System.out.println("TODO OK obtener jugadores");
                    if(type != 1) {
                        asignarJugadores();
                    }
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se han podido obtener jugadores", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void handleUnirseJuega(){
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
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido unir a la partida", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido finalizar la partida", Toast.LENGTH_LONG).show();
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
        categoria.setText("");
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
        categoria.setTextColor((Color.parseColor(coloresCategorias[random-1])));
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
                    players.get(0).setPuntos(players.get(0).getPuntos() + puntosCat[cat]);
                    System.out.println("Soy el jugador 1");
                    System.out.println(players.get(0).getPuntos());
                } else if(teToca == 2){
                    players.get(1).setPuntos(players.get(1).getPuntos() + puntosCat[cat]);
                    System.out.println("Soy el jugador 2");
                    System.out.println(players.get(1).getPuntos());
                } else if(teToca == 3){
                    players.get(2).setPuntos(players.get(2).getPuntos() + puntosCat[cat]);
                } else {
                    players.get(3).setPuntos(players.get(3).getPuntos() + puntosCat[cat]);
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
                    int randomQuestion = rndQuestion.nextInt(4) + 1;
                    ponerPregunta(q.getStatement(), q.getCorrect(), q.getIncorrect1(), q.getIncorrect2(), q.getIncorrect3(), randomQuestion, random-1);

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


