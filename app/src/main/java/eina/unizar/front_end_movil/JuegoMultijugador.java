package eina.unizar.front_end_movil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import Chat.Mensaje;
import Chat.MessageAdapter;
import SessionManagement.GestorSesion;
import SessionManagement.Jugadores;
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
import retrofit2.http.Body;

public class JuegoMultijugador extends AppCompatActivity{

    private static final String ipMarta = "http://192.168.1.162:5000/";
    private static final String ipAndrea = "https://websocketstrivial.herokuapp.com/";

    private ConstraintLayout pantallaMulti;
    private ConstraintLayout pantallaChat;

    private static final String TAG = "JuegoMultijugador";
    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_ACABAR = 1;
    private static final int OPTION_INSTRUCCIONES = 2; // en el chat aparecen las instrucciones

    private Socket msocket;

    /** COSAS DEL CHAT **/
    private EditText mensajeEscribir;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

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
    private boolean haEmpezado = false;

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
    ArrayList<Jugadores> playersAux = new ArrayList<Jugadores>();
    ArrayList<Jugadores> playersOrdenados = new ArrayList<Jugadores>();
    List<String> emails = new ArrayList<String>();
    List<String> users = new ArrayList<String>();
    List<String> abandonados = new ArrayList<String>();

    private int NUM_RONDAS;
    private int NUM_JUGADORES;
    private int ID_PARTIDA;
    int numero_ronda = 1;
    int teToca = 1;
    int puntos_ganador;
    int jugadoresEnSala;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;
    private boolean firstJoin = true;

    private Emitter.Listener nuevoJugador = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    JSONObject datos = (JSONObject) args[0];
                    System.out.println(datos);
                    String nickname = "";
                    String avatar = "";
                    try {
                        nickname = datos.getString("username");
                        avatar = datos.getString("avatar");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int entrada = players.get(players.size() - 1).getOrden();
                    Jugadores jugador = new Jugadores(nickname, 0, avatar, entrada + 1, true);
                    players.add(jugador);
                    playersAux.add(jugador);
                    asignarJugadores();
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
                    System.out.println("Este es el TETOCA que me llega: ");
                    System.out.println(teToca);
                    // Actualizar los puntos
                    if (teToca == 1) {
                        // el que juega antes del primero es el ultimo jugador
                        players.get(NUM_JUGADORES-1).setPuntos(puntosActualizar);
                    } else {
                        players.get(teToca - 2).setPuntos(puntosActualizar); // el que ha jugado antes de ti
                    }
                    asignarPuntos();
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

    private Emitter.Listener jugadoresOrdenados = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    String ganadorPartida = (String) args[0];
                    System.out.println("El ganador que me ha llegado es: ");
                    System.out.println(ganadorPartida);
                    ganador = ganadorPartida;
                    int puntosGuardar = 0;
                    for(int i = 0; i < players.size(); i++){
                        if((players.get(i).getUsername()).equals(gestorSesion.getSession())){
                            puntosGuardar = players.get(i).getPuntos();
                        }
                    }
                    handleRegistrarPuntos(gestorSesion.getmailSession(), puntosGuardar);
                    handleFinPartidaMultiJuega(gestorSesion.getmailSession(),puntosGuardar);
                    finalizarPartida();
                }
            });
        }
    };

    // const mensajeUserJoin = {sender: 'admin', avatar: admin, text: "Bienvenido al chat "+ user.username, date: "admin" };
    private Emitter.Listener newMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    JSONObject datos = (JSONObject) args[0];
                    String sender = "";
                    String texto = "";
                    String avatar = "";
                    try {
                        sender = datos.getString("sender");
                        texto = datos.getString("text");
                        avatar = datos.getString("avatar");
                        System.out.println("NICKNAME EN EMITTER: " + sender);
                        System.out.println("AVATAR EN EMITTER: " + texto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int orden = 0;
                    if(!sender.equals(gestorSesion.getSession())) {
                        for(int i = 0; i < playersAux.size(); i++){
                            // guardar el orden en el que está para el chat
                            if(playersAux.get(i).getUsername().equals(sender)){
                                orden = i;
                            }
                        }
                        final Mensaje m = new Mensaje(sender, texto, false, sender.equals("admin"), avatar, orden);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(m);
                                // scroll the ListView to the last added element
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });
                    }
                }
            });
        }
    };

    private Emitter.Listener userDesconectado = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    String userOut = (String) args[0];
                    System.out.println("El usuario que ha abandonado la partida es ");
                    System.out.println(userOut);
                    for(int i = 0; i < playersAux.size(); i++){
                        if((playersAux.get(i).getUsername()).equals(userOut)){
                            playersAux.get(i).setEstaJugando(false);
                        }
                    }
                    eliminarJugador(userOut);
                    //tiene que comprobar que hay dos jugadores o mas para poder continuar la partida y sino
                    NUM_JUGADORES--;
                    //abandonados.add("");
                    //int quedan = players.size() - abandonados.size();
                    if(NUM_JUGADORES == 1){
                        //finalizar la partida para todos y poner al jugador como ganador
                        msocket.emit("disconnection");
                        int puntosGuardar = 0;
                        for(int i = 0; i < players.size(); i++){
                            if((players.get(i).getUsername()).equals(gestorSesion.getSession())){
                                puntosGuardar = players.get(i).getPuntos();
                                ganador = players.get(i).getUsername();
                                System.out.println("El ganador soy yo porque no abandono:");
                                System.out.println(ganador);
                            }
                        }
                        //Registra los puntos y monedas del jugador que se queda en la partida
                        handleRegistrarPuntos(gestorSesion.getmailSession(), puntosGuardar);
                        //pone al usuario como ganador de la partida
                        handleFinPartidaMulti();
                        if(!gestorSesion.getSession().equals(userOut)){
                            //lo manda a la pantalla de fin de partida
                            finalizarPartida();
                        }
                    }
                    else{
                        System.out.println("Tetoca " + teToca);
                        int indice = 0;
                        // borra al usuario del struct
                        for(int i = 0; i < players.size(); i++){
                            if((players.get(i).getUsername()).equals(userOut)){
                                // para saber quién se ha ido
                                indice = i;
                                players.remove(i);
                            }
                        }
                        actualizarTeToca(indice);
                        System.out.println("Tetoca actualizado " + teToca);
                        System.out.println("Se ha ido " + indice);
                        System.out.println("El orden de jugadores ahora queda así:");
                        for(int i = 0; i < players.size(); i++){
                            players.get(i).setOrden(i);
                            System.out.println(players.get(i).getUsername());
                        }
                    }
                }
            });
        }
    };

    public void actualizarTeToca(int seHaIdo){
        if(teToca == 1){
            if(seHaIdo == 0){
                // pasar turno
            }
        } else if(teToca == 2){
            if(seHaIdo == 0){
                teToca--;
            } else if(seHaIdo == 1){
                //pasar turno --> SE PUEDE HACER CUANDO TE VAS
            }
        } else if(teToca == 3){
            if(seHaIdo == 0 || seHaIdo == 1){
                teToca--;
            } else if(seHaIdo == 3){
                // pasar turno
            }
        } else if(teToca == 4){
            if(seHaIdo == 0 ||seHaIdo == 1 || seHaIdo == 2){
                teToca--;
            } else{
                // pasar turno
            }
        }
    }


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

        pantallaChat = (ConstraintLayout) findViewById(R.id.constraintChat);
        pantallaMulti = (ConstraintLayout) findViewById(R.id.constraintMulti);
        pantallaChat.setVisibility(View.GONE);

        // PARA CHAT
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.lista_mensajes);
        messagesView.setAdapter(messageAdapter);

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

        msocket.on("message",newMessage)
                .on("newPlayer", nuevoJugador)
                .on("recibirTurno", nuevoTurno)
                .on("finalizarPartida", jugadoresOrdenados)
                .on("desconexion", userDesconectado)
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
                        haEmpezado = true;
                        num_rondas.setText(String.valueOf(numero_ronda));
                        turno_jugador.setText(players.get(teToca - 1).getUsername());
                        rollDice();
                        empezar.setVisibility(View.GONE);
                        empezar.setClickable(false);
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
                //pasas los puntos actuales del jugador
                msocket.emit("pasarTurno", teToca, numero_ronda, players.get(indice).getPuntos());
                num_rondas.setText(String.valueOf(numero_ronda));
                turno_jugador.setText(players.get(teToca - 1).getUsername()); // pone el siguiente jugador
                asignarPuntos();
            }
        });

        desactivar();

        // boton de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(players.size() != NUM_JUGADORES){
                    // si es el único jugador
                    Toast.makeText(JuegoMultijugador.this, "No te puedes marchar hasta que no estén todos los jugadores", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder quiereSalir = new AlertDialog.Builder(JuegoMultijugador.this);
                    quiereSalir.setMessage("¿Desea abandonar la partida?");
                    quiereSalir.setCancelable(false);
                    quiereSalir.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // tiene que saltar su turno si le tocaba
                            if (players.get(teToca - 1).getUsername().equals(gestorSesion.getSession())) {
                                // es mi turno actualmente
                                teToca = teToca + 1;
                                if (teToca == NUM_JUGADORES + 1) {
                                    teToca = 1;
                                }
                                // pasas el turno al siguiente con 0 puntos
                                msocket.emit("pasarTurno", teToca, numero_ronda, 0);
                            }

                            //confirma que quiere salir de la partida
                            //tiene que hacer el emit de desconection
                            msocket.emit("disconnection");
                            msocket.disconnect();
                            msocket.off();
                            //tiene que ser eliminado de la tabla juega
                            handleAbandonarPartida();
                            //tiene que continuar la partida con un jugador menos
                            Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                            startActivityForResult(intent, OPTION_ATRAS);
                            //finish();

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
            }
        });

        // Botón atrás del botón
        Button atrasChatButton = (Button) findViewById(R.id.atrasChat);
        atrasChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantallaChat.setVisibility(View.GONE);
                pantallaMulti.setVisibility(View.VISIBLE);
            }
        });

        // boton de chat
        ImageButton chatButton = (ImageButton) findViewById(R.id.chat);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(v.getContext(), PantallaChat.class);
                //startActivityForResult(intent, OPTION_CHAT);
                pantallaMulti.setVisibility(View.GONE);
                pantallaChat.setVisibility(View.VISIBLE);
            }
        });

        // VOTON DE INSTRUCCIONES DEL CHAT
        ImageButton instButton = (ImageButton) findViewById(R.id.instrucciones);
        instButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), InstruccionesJuego.class);
                startActivityForResult(intent, OPTION_INSTRUCCIONES);
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
        // PARA CHAT
        mensajeEscribir = (EditText) findViewById(R.id.mensaje_writer);

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

    public void finalizarPartida(){
        Bundle extra = new Bundle();
        extra.putString("ganador", ganador);
        Intent intent = new Intent(this, FinPartidaMulti.class);
        intent.putExtras(extra);
        startActivityForResult(intent, OPTION_ACABAR);
    }

    public String calcularGanador(){
        int user1 = 0;
        int user2 = 0;
        int user3 = -1;
        int user4 = -2;
        if(players.size() == 2){
            user1 = players.get(0).getPuntos();
            user2 = players.get(1).getPuntos();

        }else if(players.size() == 3){
            user1 = players.get(0).getPuntos();
            user2 = players.get(1).getPuntos();
            user3 = players.get(2).getPuntos();
        }else if(players.size() == 4){
            user1 = players.get(0).getPuntos();
            user2 = players.get(1).getPuntos();
            user3 = players.get(2).getPuntos();
            user4 = players.get(3).getPuntos();
        }

        if (user1 > user2 && user1 > user3 && user1 > user4) {
            ganador = players.get(0).getUsername();
            puntos_ganador = user1;
        }else if (user2 > user1 && user2 > user3 && user2 > user4) {
            ganador = players.get(1).getUsername();
            puntos_ganador = user2;
        }else if (user3 > user1 && user3 > user2 && user3 > user4) {
            ganador = players.get(2).getUsername();
            puntos_ganador = user3;
        }else if (user4 > user1 && user4 > user3 && user4 > user2) {
            ganador = players.get(3).getUsername();
            puntos_ganador = user4;
        }
        return ganador;
    }

    public void ordenarJugadores(){
        ArrayList<Jugadores> aux = new ArrayList<Jugadores>();
        ArrayList<Jugadores> aux2 = new ArrayList<Jugadores>();
        if(players.size() == 2){
            if(players.get(0).getPuntos() > players.get(1).getPuntos()){
                playersOrdenados.add(players.get(0));
                playersOrdenados.add(players.get(1));
            }
            else{
                playersOrdenados.add(players.get(1));
                playersOrdenados.add(players.get(2));
            }
        }
        else if(players.size() == 3){
            if(players.get(0).getPuntos() > players.get(1).getPuntos() && players.get(0).getPuntos() > players.get(2).getPuntos()){
                playersOrdenados.add(players.get(0));
                aux.add(players.get(1));
                aux.add(players.get(2));
            } else if(players.get(1).getPuntos() > players.get(0).getPuntos() && players.get(1).getPuntos() > players.get(2).getPuntos()){
                playersOrdenados.add(players.get(1));
                aux.add(players.get(0));
                aux.add(players.get(2));
            } else{
                playersOrdenados.add(players.get(2));
                aux.add(players.get(0));
                aux.add(players.get(1));
            }

            if(aux.get(0).getPuntos() > aux.get(1).getPuntos() ){
                playersOrdenados.add(aux.get(0));
                playersOrdenados.add(aux.get(1));
            }
            else{
                playersOrdenados.add(aux.get(1));
                playersOrdenados.add(aux.get(0));
            }

        }
        else{
            if(players.get(0).getPuntos() > players.get(1).getPuntos() && players.get(0).getPuntos() > players.get(2).getPuntos() && players.get(0).getPuntos() > players.get(3).getPuntos()){
                playersOrdenados.add(players.get(0));
                aux.add(players.get(1));
                aux.add(players.get(2));
                aux.add(players.get(3));
            } else if(players.get(1).getPuntos() > players.get(0).getPuntos() && players.get(1).getPuntos() > players.get(2).getPuntos() && players.get(1).getPuntos() > players.get(3).getPuntos()){
                playersOrdenados.add(players.get(1));
                aux.add(players.get(0));
                aux.add(players.get(2));
                aux.add(players.get(3));
            }else if(players.get(2).getPuntos() > players.get(0).getPuntos() && players.get(2).getPuntos() > players.get(1).getPuntos() && players.get(2).getPuntos() > players.get(3).getPuntos()){
                playersOrdenados.add(players.get(2));
                aux.add(players.get(0));
                aux.add(players.get(1));
                aux.add(players.get(3));
            }
            else{
                playersOrdenados.add(players.get(3));
                aux.add(players.get(0));
                aux.add(players.get(1));
                aux.add(players.get(2));
            }

            if(aux.get(0).getPuntos() > aux.get(1).getPuntos() && aux.get(0).getPuntos() > aux.get(2).getPuntos()){
                playersOrdenados.add(aux.get(0));
                aux2.add(players.get(1));
                aux2.add(players.get(2));
            }
            else if(aux.get(1).getPuntos() > aux.get(0).getPuntos() && aux.get(1).getPuntos() > aux.get(2).getPuntos()){
                playersOrdenados.add(aux.get(1));
                aux2.add(players.get(0));
                aux2.add(players.get(2));
            }
            else{
                playersOrdenados.add(aux.get(2));
                aux2.add(players.get(0));
                aux2.add(players.get(1));
            }

            if(aux2.get(0).getPuntos() > aux2.get(1).getPuntos()){
                playersOrdenados.add(aux2.get(0));
                playersOrdenados.add(aux2.get(1));
            }
            else{
                playersOrdenados.add(aux2.get(1));
                playersOrdenados.add(aux2.get(0));
            }
        }
    }

    public void comprobarRondas(){
        if (numero_ronda == NUM_RONDAS*NUM_JUGADORES) {
            int puntosGuardar = 0;
            for(int i = 0; i < players.size(); i++){
                if((players.get(i).getUsername()).equals(gestorSesion.getSession())){
                    puntosGuardar = players.get(i).getPuntos();
                }
            }
            handleRegistrarPuntos(gestorSesion.getmailSession(), puntosGuardar);
            handleFinPartidaMultiJuega(gestorSesion.getmailSession(), puntosGuardar);
            Bundle extra = new Bundle();
            String ganador = calcularGanador();
            extra.putString("ganador", ganador);
            handleFinPartidaMulti();
            //msocket.emit("sendFinPartida",playersOrdenados);
            msocket.emit("sendFinPartida",ganador);
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
            Jugadores jugador = new Jugadores(gestorSesion.getSession(), 0, gestorSesion.getAvatarSession(), 0, true);
            players.add(jugador);
            playersAux.add(jugador);
            jugadoresEnSala = players.size();
        }
    }

    public void asignarPuntos(){
        for(int i = 0; i < playersAux.size(); i++) {
            if(i == 0 && playersAux.get(i).isEstaJugando()){
                usuario1_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
            } else if(i == 0 && !playersAux.get(i).isEstaJugando()){
                usuario1_puntos.setText(String.valueOf(0));
            }
            else if(i == 1 && playersAux.get(i).isEstaJugando()){
                usuario2_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
            } else if(i == 1 && !playersAux.get(i).isEstaJugando()){
                usuario2_puntos.setText(String.valueOf(0));
            }
            else if(i == 2 && playersAux.get(i).isEstaJugando()){
                usuario3_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
            } else if(i == 2 && !playersAux.get(i).isEstaJugando()){
                usuario3_puntos.setText(String.valueOf(0));
            }
            else if(i == 3 && playersAux.get(i).isEstaJugando()){
                usuario4_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
            }else if(i == 3 && !playersAux.get(i).isEstaJugando()){
                usuario4_puntos.setText(String.valueOf(0));
            }
        }
    }

    public void asignarJugadores(){
        for(int i = 0; i < players.size(); i++){
            if(i == 0){
                usuario1_nombre.setText(players.get(i).getUsername());
                usuario1_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario1);
            }
            else if(i == 1){
                usuario2_nombre.setText(players.get(i).getUsername());
                usuario2_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario2);
                texto_puntos2.setText("puntos");
            }
            else if(i == 2){
                usuario3_nombre.setText(players.get(i).getUsername());
                usuario3_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario3);
                texto_puntos3.setText("puntos");
            }
            else if(i == 3){
                usuario4_nombre.setText(players.get(i).getUsername());
                usuario4_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(players.get(i).getImagen(), imagenUsuario4);
                texto_puntos4.setText("puntos");
            }
        }
    }

    public void eliminarJugador(String usuario){
        if(players.size() == 2){
            if(usuario1_nombre.getText().equals(usuario)){
                usuario1_nombre.setText("Desconectado");
                usuario1_puntos.setText("0");
            }
            else if(usuario2_nombre.getText().equals(usuario)){
                usuario2_nombre.setText("Desconectado");
                usuario2_puntos.setText("0");
            }
        }
        else if(players.size() == 3){
            if(usuario1_nombre.getText().equals(usuario)){
                usuario1_nombre.setText("Desconectado");
                usuario1_puntos.setText("0");
            }
            else if(usuario2_nombre.getText().equals(usuario)) {
                usuario2_nombre.setText("Desconectado");
                usuario2_puntos.setText("0");
            }
            else if(usuario3_nombre.getText().equals(usuario)) {
                usuario3_nombre.setText("Desconectado");
                usuario3_puntos.setText("0");
            }
        }
        else{
            if(usuario1_nombre.getText().equals(usuario)){
                usuario1_nombre.setText("Desconectado");
                usuario1_puntos.setText("0");
            }
            else if(usuario2_nombre.getText().equals(usuario)) {
                usuario2_nombre.setText("Desconectado");
                usuario2_puntos.setText("0");
            }
            else if(usuario3_nombre.getText().equals(usuario)) {
                usuario3_nombre.setText("Desconectado");
                usuario3_puntos.setText("0");
            }
            else if(usuario4_nombre.getText().equals(usuario)) {
                usuario4_nombre.setText("Desconectado");
                usuario4_puntos.setText("0");
            }
        }
        for(int i = 0; i < playersAux.size(); i++){
            if(usuario.equals(playersAux.get(i).getUsername())){
                playersAux.get(i).setPuntos(0);
            }
        }
        for(int i = 0; i < players.size(); i++){
            if(usuario.equals(players.get(i).getUsername())){
                players.get(i).setPuntos(0);
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
                    //System.out.println("TODO OK");
                    //System.out.println("Este es el handleObtenerInfo");
                    //System.out.println(ID_PARTIDA);
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
        //System.out.println("Este es el handleObtenerJugadores");
        //System.out.println(ID_PARTIDA);

        Call<JsonArray> call = retrofitInterface.obtenerJugadores(String.valueOf(ID_PARTIDA));
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {
                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        JsonObject prueba = j.getAsJsonObject();
                        String email = prueba.get("email").getAsString();
                        String nickname = prueba.get("nickname").getAsString();
                        String image = prueba.get("imagen").getAsString(); //coger el avatar
                        int entrada = prueba.get("orden_entrada").getAsInt(); //coger el avatar
                        Jugadores jugador2 = new Jugadores(nickname,0, image, entrada, true);
                        players.add(jugador2);
                        playersAux.add(jugador2);
                        emails.add(email);
                        users.add(nickname);
                        //System.out.println("El usuario añadido es:");
                        //System.out.println(email);
                        System.out.println("JUGADOR AÑADIDO DESDE JOIN " + jugador2.getUsername());
                    }
                    //System.out.println("Los usuarios que hay son:");
                    //System.out.println(emails);
                    jugadoresEnSala = players.size();
                    /*System.out.println("ANTES SORT");
                    for(int i = 0; i< players.size(); i++){
                        System.out.println(players.get(i).getUsername());
                    }*/
                    Collections.sort(players);
                    /*System.out.println("DESPUES SORT");
                    for(int i = 0; i< players.size(); i++){
                        System.out.println(players.get(i).getUsername());
                    }
                    System.out.println("TODO OK obtener jugadores");*/
                    // Poner el primer jugador y la ronda
                    num_rondas.setText(String.valueOf(1));
                    turno_jugador.setText(players.get(0).getUsername());
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
        //System.out.println(gestorSesion.getmailSession());
        //System.out.println(String.valueOf(0));
        unirseJuega.put("email", gestorSesion.getmailSession());
        unirseJuega.put("puntos",String.valueOf(0));


        Call<JsonObject> call = retrofitInterface.UnirseMultijugadorJuega(unirseJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    //System.out.println("TODO OK");
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
                    System.out.println("TODO OK al actualizar el ganador");
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

    private void  handleRegistrarPuntos(String correo, int puntosJugador){
        int monedasInsertar = puntosJugador/2;
        HashMap<String,String> ganarMonedas = new HashMap<>();
        ganarMonedas.put("email", correo);
        ganarMonedas.put("monedas", String.valueOf(monedasInsertar));
        ganarMonedas.put("puntos", String.valueOf(puntosJugador));
       /* System.out.println("Los puntos que tengo que registrar son:");
        System.out.println(puntosJugador);
        System.out.println("Las monedas que tengo que registrar son:");
        System.out.println(0);*/

        Call<JsonObject> call = retrofitInterface.insertNewData(ganarMonedas);
        //Call<JsonObject> call = retrofitInterface.guardarMonedas(ganarMonedas);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK al registrar las monedas");
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha actualizado al ganador", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void  handleAbandonarPartida(){
        HashMap<String,String> salirDePartida = new HashMap<>();
        salirDePartida.put("codigo", codigo);
        salirDePartida.put("email", gestorSesion.getmailSession());

        Call<JsonObject> call = retrofitInterface.salirPartidaJuega(salirDePartida);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    System.out.println("TODO OK, se ha abandonado la partida correctamente");
                } else{
                    Toast.makeText(JuegoMultijugador.this, "No se ha podido eliminar al usuario de juega", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(JuegoMultijugador.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    private void  handleFinPartidaMultiJuega(String user, int points) {
        HashMap<String, String> finMultiJuega = new HashMap<>();

        finMultiJuega.put("codigo", codigo);
        finMultiJuega.put("puntos", Integer.toString(points));
        finMultiJuega.put("email", user);

        Call<JsonObject> call = retrofitInterface.finMultiJuega(finMultiJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    //System.out.println("TODO OK");
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
                    playersAux.get(0).setPuntos(playersAux.get(0).getPuntos() + puntosCat[cat]);
                    System.out.println("Soy el jugador 1");
                    System.out.println(playersAux.get(0).getPuntos());
                } else if(teToca == 2){
                    players.get(1).setPuntos(players.get(1).getPuntos() + puntosCat[cat]);
                    playersAux.get(1).setPuntos(playersAux.get(1).getPuntos() + puntosCat[cat]);
                    System.out.println("Soy el jugador 2");
                    System.out.println(players.get(1).getPuntos());
                } else if(teToca == 3){
                    players.get(2).setPuntos(players.get(2).getPuntos() + puntosCat[cat]);
                    playersAux.get(2).setPuntos(playersAux.get(2).getPuntos() + puntosCat[cat]);
                } else {
                    players.get(3).setPuntos(players.get(3).getPuntos() + puntosCat[cat]);
                    playersAux.get(3).setPuntos(playersAux.get(3).getPuntos() + puntosCat[cat]);
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

    // PARA CHAT
    public void sendMessage(View view) {
        final String m = mensajeEscribir.getText().toString();
        if (m.length() > 0) {
            // mandar mensaje
            int orden = 0;
            for(int i = 0; i < playersAux.size(); i++){
                // guardar el orden en el que está para el chat
                if(playersAux.get(i).getUsername().equals(gestorSesion.getSession())){
                    orden = i;
                }
            }
            final Mensaje message = new Mensaje(gestorSesion.getSession(), m, true, false, gestorSesion.getAvatarSession(), orden);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    Date date = new Date();
                    String fecha = dateFormat.format(date);
                    // const mensajeUserJoin = {sender: 'admin', avatar: admin, text: "Bienvenido al chat "+ user.username, date: "admin" };
                    JSONObject aux = new JSONObject();
                    try{
                        aux.put("sender", gestorSesion.getSession()); //sender
                        aux.put("avatar", gestorSesion.getAvatarSession()); //avatar
                        aux.put("text",m); //texto
                        aux.put("date", fecha); //fecha

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    msocket.emit("sendMessage", aux, new Ack(){
                        @Override
                        public void call(Object... args){
                            //JSONObject response = (JSONObject) args[0];
                            //System.out.println(response);
                        }
                    });
                }
            });
            mensajeEscribir.getText().clear();
            hideSoftKeyboard();
        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}


