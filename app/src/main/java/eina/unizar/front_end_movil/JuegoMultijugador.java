package eina.unizar.front_end_movil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import SessionManagement.JugadoresFinal;
import SessionManagement.Question;
import cn.pedant.SweetAlert.SweetAlertDialog;
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

    private static final String ipAndrea = "https://websocketstrivial.herokuapp.com/";

    //private static final String ipAndrea = "http://192.168.0.26:5000/";

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

    //private Button empezar;
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
    JSONArray playersOrdenados = new JSONArray();
    List<String> emails = new ArrayList<String>();
    List<String> users = new ArrayList<String>();

    private int NUM_RONDAS;
    private int NUM_JUGADORES;
    private int ID_PARTIDA;
    int numero_ronda = 1;
    int teToca = 0;
    int jugadoresEnSala;
    boolean meTocaYa;

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
                    String nickname = "";
                    String avatar = "";
                    try {
                        nickname = datos.getString("username");
                        avatar = datos.getString("avatar");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int entrada = playersAux.get(playersAux.size() - 1).getOrden();
                    Jugadores jugador = new Jugadores(nickname, 0, avatar, entrada + 1, true);
                    //players.add(jugador);
                    playersAux.add(jugador);
                    jugadoresEnSala = playersAux.size();
                    asignarJugadores();
                    // EMPEZAR PARTIDA SI ERES EL PRIMERO!!!
                    //es mi turno de jugar
                    // Si están todos y es mi turno porque soy el primero
                    if(playersAux.size() == NUM_JUGADORES && playersAux.get(teToca).getUsername().equals(gestorSesion.getSession())){
                        siguiente.setVisibility(View.VISIBLE);
                        siguiente.setClickable(true);
                        rollDice();
                        meTocaYa = true;
                    }
                    num_rondas.setText(String.valueOf(numero_ronda));
                    turno_jugador.setText(playersAux.get(teToca).getUsername());
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
                    // Actualizar los puntos
                    if (teToca == 0) {
                        // el que juega antes del primero es el ultimo jugador
                        boolean teHeEncontrado = false;
                        for(int i = 1; i <= playersAux.size() && !teHeEncontrado; i++){
                            if(playersAux.get(NUM_JUGADORES-i).isEstaJugando()){
                                playersAux.get(NUM_JUGADORES - i).setPuntos(puntosActualizar);
                                teHeEncontrado = true;
                            }
                        }

                    } else {
                        boolean teHeEncontrado = false;
                        for(int i = 1; i <= playersAux.size() && !teHeEncontrado; i++){
                            if(playersAux.get(teToca-i).isEstaJugando()){
                                playersAux.get(teToca - i).setPuntos(puntosActualizar); // el que ha jugado antes de ti
                                teHeEncontrado = true;
                            }
                        }
                    }
                    asignarPuntos();
                    //es mi turno de jugar
                    if(playersAux.get(teToca).getUsername().equals(gestorSesion.getSession())){
                        siguiente.setVisibility(View.VISIBLE);
                        siguiente.setClickable(true);
                        rollDice();
                    }
                    num_rondas.setText(String.valueOf(numero_ronda));
                    turno_jugador.setText(playersAux.get(teToca).getUsername());
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
                    // LLEGA EL JSON !!!!!
                    playersOrdenados = (JSONArray) args[0];
                    try {
                        JSONObject j  = playersOrdenados.getJSONObject(0);
                        ganador = j.get("username").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    int puntosGuardar = 0;
                    for(int i = 0; i < playersAux.size(); i++){
                        if((playersAux.get(i).getUsername()).equals(gestorSesion.getSession())){
                            puntosGuardar = playersAux.get(i).getPuntos();
                        }
                    }
                    System.out.println("Se acaba la partida " + gestorSesion.getmailSession());
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
                    int indice = 0;
                    for(int i = 0; i < playersAux.size(); i++){
                        if((playersAux.get(i).getUsername()).equals(userOut)){
                            playersAux.get(i).setEstaJugando(false);
                            playersAux.get(i).setPuntos(0);
                            indice = i;
                        }
                    }
                    eliminarJugador(userOut);
                    //tiene que comprobar que hay dos jugadores o mas para poder continuar la partida y sino
                    jugadoresEnSala--;
                    if(jugadoresEnSala == 1){
                        //finalizar la partida para todos y poner al jugador como ganador
                        msocket.emit("disconnection");
                        int puntosGuardar = 0;
                        for(int i = 0; i < playersAux.size(); i++){
                            if((playersAux.get(i).getUsername()).equals(gestorSesion.getSession())){
                                puntosGuardar = playersAux.get(i).getPuntos();
                                ganador = playersAux.get(i).getUsername();
                            }
                        }
                        //Registra los puntos y monedas del jugador que se queda en la partida
                        System.out.println("Soy el unico que queda");
                        handleRegistrarPuntos(gestorSesion.getmailSession(), puntosGuardar);
                        //pone al usuario como ganador de la partida
                        handleFinPartidaMulti();
                        if(!gestorSesion.getSession().equals(userOut)){
                            //lo manda a la pantalla de fin de partida
                            finalizarPartida();
                        }
                    }
                    else{
                        // borra al usuario del struct
                        /*for(int i = 0; i < playersAux.size(); i++){
                            if((playersAux.get(i).getUsername()).equals(userOut)){
                                // para saber quién se ha ido
                                indice = i;
                                playersAux.remove(i);
                            }
                        }*/
                        //actualizarTeToca(indice);
                    }
                }
            });
        }
    };

    public void actualizarTeToca(int seHaIdo){
        if(teToca == 1){
            if(seHaIdo == 0){
                teToca--;
            }
        } else if(teToca == 2){
            if(seHaIdo == 0 || seHaIdo == 1){
                teToca--;
            }
        } else if(teToca == 3){
            if(seHaIdo == 0 ||seHaIdo == 1 || seHaIdo == 2){
                teToca--;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        msocket.disconnect();
        msocket.emit("disconnection");
        msocket.off();
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
        //if(type == 1) {
            //handleUnirseJuega();
        //}
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{WebSocket.NAME};
            msocket = IO.socket(ipAndrea, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        msocket.on("message",newMessage)
                .on("newPlayer", nuevoJugador)
                .on("recibirTurno", nuevoTurno)
                .on("finalizarPartida", jugadoresOrdenados)
                .on("desconexion", userDesconectado);

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
                String callback = (String) args[0];
                if(!callback.equals("ok")){
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(JuegoMultijugador.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Ya estás jugando esta partida en otro dispositivo")
                                    .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            finish();
                                        }
                                    }).show();
                        }
                    },500);
                } else {
                    handleUnirseJuega();
                }

            }
        });

        //llamar a la bd para conseguir el numRondas y numJugadores
        //handleObtenerInfo();

        /*if(type == 1){
            empezar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(players.size() == NUM_JUGADORES){
                        resetear();
                        num_rondas.setText(String.valueOf(numero_ronda));
                        turno_jugador.setText(players.get(teToca).getUsername());
                        rollDice();
                        empezar.setVisibility(View.GONE);
                        empezar.setClickable(false);
                        siguiente.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(JuegoMultijugador.this, "Faltan jugadores por unirse a la partida", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            empezar.setVisibility(View.GONE);
            empezar.setClickable(false);
        }*/


        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meTocaYa = false;
                resetear();
                desactivar();
                siguiente.setVisibility(View.GONE);
                siguiente.setClickable(false);
                int indice = teToca;
                // Pasa el turno una vez
                teToca = teToca + 1;
                if(teToca == NUM_JUGADORES){
                    teToca = 0;
                    indice = NUM_JUGADORES - 1;
                }
                if(teToca == 0){
                    numero_ronda++;
                }
                // mirar si el siguiente está jugando --> 3 jugadores
                if(playersAux.size() >= 3){
                    if(!playersAux.get(teToca).isEstaJugando()) { // si no esta jugando pasar el turno
                        //indice = teToca;
                        // Pasa el turno una vez
                        teToca = teToca + 1;
                        if (teToca == NUM_JUGADORES) {
                            teToca = 0;
                            //indice = NUM_JUGADORES - 1;
                        }
                        if (teToca == 0) {
                            numero_ronda++;
                        }

                        if (playersAux.size() == 4) { // si son cuatro y el siguiente no esta jugando
                            if (!playersAux.get(teToca).isEstaJugando()) { // si no esta jugando pasar el turno
                                //indice = teToca;
                                // Pasa el turno una vez
                                teToca = teToca + 1;
                                if (teToca == NUM_JUGADORES) {
                                    teToca = 0;
                                    //indice = NUM_JUGADORES - 1;
                                }
                                if (teToca == 0) {
                                    numero_ronda++;
                                }
                            }
                        }
                    }
                }
                //pasas los puntos actuales del jugador
                msocket.emit("pasarTurno", teToca, numero_ronda, playersAux.get(indice).getPuntos());
                num_rondas.setText(String.valueOf(numero_ronda));
                turno_jugador.setText(playersAux.get(teToca).getUsername()); // pone el siguiente jugador
                asignarPuntos();
            }
        });

        desactivar();

        // boton de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(!meTocaYa){
                    if(playersAux.size() != NUM_JUGADORES){
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
                                if (playersAux.get(teToca).getUsername().equals(gestorSesion.getSession())) {
                                    // es mi turno actualmente
                                    teToca = teToca + 1;
                                    if (teToca == NUM_JUGADORES) {
                                        teToca = 0;
                                    }
                                    // pasas el turno al siguiente con 0 puntos
                                    msocket.emit("pasarTurno", teToca, numero_ronda, 0);
                                }

                                System.out.println("ANTES DE DISCONNECTION no es su turno");
                                msocket.emit("disconnection");
                                msocket.disconnect();
                                msocket.off();


                                //confirma que quiere salir de la partida
                                //tiene que hacer el emit de desconection

                                //tiene que ser eliminado de la tabla juega
                                //handleAbandonarPartida(); !!!! AQUÍ
                                //tiene que continuar la partida con un jugador menos
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
                }
                else{
                    Toast.makeText(JuegoMultijugador.this, "Debes contestar para poder marcharte de la partida", Toast.LENGTH_LONG).show();
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

        //empezar = (Button) findViewById(R.id.empezar);

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

    public void comprobarRondas(){
        if (numero_ronda == NUM_RONDAS && playersAux.get(teToca).getUsername().equals(gestorSesion.getSession()) && teToca == jugadoresEnSala-1) {
            int puntosGuardar = 0;
            for(int i = 0; i < playersAux.size(); i++){
                if((playersAux.get(i).getUsername()).equals(gestorSesion.getSession())){
                    puntosGuardar = playersAux.get(i).getPuntos();
                }
            }
            System.out.println("Soy el ultimo que juega y llamo a handle registrar puntos");
            handleRegistrarPuntos(gestorSesion.getmailSession(), puntosGuardar);
            handleFinPartidaMultiJuega(gestorSesion.getmailSession(), puntosGuardar);
            playersOrdenados = ordenarJugadores();
            Bundle extra = new Bundle();
            try {
                JSONObject j = playersOrdenados.getJSONObject(0);
                ganador = j.get("username").toString();
            }catch (JSONException e){
                e.printStackTrace();
            }
            extra.putString("ganador", ganador);
            System.out.println(ganador);
            handleFinPartidaMulti();
            msocket.emit("sendFinPartida",playersOrdenados);
            Intent intent = new Intent(this, FinPartidaMulti.class);
            System.out.println("GANADOR DE VERDAD ES " + ganador);
            intent.putExtras(extra);
            startActivityForResult(intent, OPTION_ACABAR);
        }
    }

    public JSONArray ordenarJugadores(){
        JSONArray jugadores = new JSONArray();
        ArrayList<JugadoresFinal> p = new ArrayList<>();
        for(int i = 0; i < playersAux.size(); i++){
            JugadoresFinal newP = new JugadoresFinal(playersAux.get(i).getUsername(), playersAux.get(i).getPuntos(), playersAux.get(i).getImagen());
            p.add(newP);
        }
        // los ordena según puntos
        Collections.sort(p);
        for(int j = 0; j < p.size(); j++){
            JSONObject aux = new JSONObject();
            try{
                aux.put("username", p.get(j).getUsername());
                aux.put("puntos", p.get(j).getPuntos());
                aux.put("avatar", p.get(j).getImagen());
            } catch (JSONException e){
                e.printStackTrace();
            }
            jugadores.put(aux);
        }
        return jugadores;
    }

    public void creadorPartida(){
        if (type == 1) { //ha creado la partida
            usuario1_nombre.setText(gestorSesion.getSession());
            usuario1_puntos.setText("0");
            cargarImagenUsuario(gestorSesion.getAvatarSession(), imagenUsuario1);
            Jugadores jugador = new Jugadores(gestorSesion.getSession(), 0, gestorSesion.getAvatarSession(), 0, true);
            //players.add(jugador);
            playersAux.add(jugador);
            jugadoresEnSala = playersAux.size();
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
        for(int i = 0; i < playersAux.size(); i++){
            if(i == 0){
                usuario1_nombre.setText(playersAux.get(i).getUsername());
                usuario1_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(playersAux.get(i).getImagen(), imagenUsuario1);
            }
            else if(i == 1){
                usuario2_nombre.setText(playersAux.get(i).getUsername());
                usuario2_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(playersAux.get(i).getImagen(), imagenUsuario2);
                texto_puntos2.setText("puntos");
            }
            else if(i == 2){
                usuario3_nombre.setText(playersAux.get(i).getUsername());
                usuario3_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(playersAux.get(i).getImagen(), imagenUsuario3);
                texto_puntos3.setText("puntos");
            }
            else if(i == 3){
                usuario4_nombre.setText(playersAux.get(i).getUsername());
                usuario4_puntos.setText(String.valueOf(playersAux.get(i).getPuntos()));
                cargarImagenUsuario(playersAux.get(i).getImagen(), imagenUsuario4);
                texto_puntos4.setText("puntos");
            }
        }
    }

    public void eliminarJugador(String usuario){
        if(playersAux.size() == 2){
            if(usuario1_nombre.getText().equals(usuario)){
                usuario1_nombre.setText("Desconectado");
                usuario1_puntos.setText("0");
            }
            else if(usuario2_nombre.getText().equals(usuario)){
                usuario2_nombre.setText("Desconectado");
                usuario2_puntos.setText("0");
            }
        }
        else if(playersAux.size() == 3){
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
        /*for(int i = 0; i < playersAux.size(); i++){
            if(usuario.equals(playersAux.get(i).getUsername())){
                playersAux.get(i).setPuntos(0);
            }
        }*/
        /*for(int i = 0; i < players.size(); i++){
            if(usuario.equals(players.get(i).getUsername())){
                players.get(i).setPuntos(0);
            }
        }*/
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
                    num_rondas.setText(String.valueOf(1));
                    //obtener jugadores de la partida
                    if(type != 1){
                        handleObtenerJugadores();
                    } else{
                        turno_jugador.setText(playersAux.get(0).getUsername());
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
                        //players.add(jugador2);
                        playersAux.add(jugador2);
                        emails.add(email);
                        users.add(nickname);
                    }
                    jugadoresEnSala = playersAux.size();
                    Collections.sort(playersAux);
                    turno_jugador.setText(playersAux.get(0).getUsername());
                    // Poner el primer jugador y la ronda
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
        unirseJuega.put("email", gestorSesion.getmailSession());
        unirseJuega.put("puntos",String.valueOf(0));

        Call<JsonObject> call = retrofitInterface.UnirseMultijugadorJuega(unirseJuega);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    handleObtenerInfo();
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
        System.out.println("GANADOR DENTRO DE FIN PARTIDA MULTI " + ganador);
        finPartidaMulti.put("codigo",codigo);

        Call<JsonObject> call = retrofitInterface.FinalPartidaMultijugador(finPartidaMulti);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {

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
        System.out.println("DENTRO DE HANDLE REGISTRAR PUNTOS");

        Call<JsonObject> call = retrofitInterface.insertNewData(ganarMonedas);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
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
                int puntos;
                int puntosCat1;
                int suma;
                if(teToca == 0){
                    puntos = playersAux.get(0).getPuntos();
                    puntosCat1 = puntosCat[cat];
                    suma = puntos + puntosCat1;
                    //players.get(0).setPuntos(suma);
                    playersAux.get(0).setPuntos(suma);
                } else if(teToca == 1){
                    puntos = playersAux.get(1).getPuntos();
                    puntosCat1 = puntosCat[cat];
                    suma = puntos + puntosCat1;
                    //players.get(1).setPuntos(suma);
                    playersAux.get(1).setPuntos(suma);
                } else if(teToca == 2){
                    puntos = playersAux.get(2).getPuntos();
                    puntosCat1 = puntosCat[cat];
                    suma = puntos + puntosCat1;
                    //players.get(2).setPuntos(suma);
                    playersAux.get(2).setPuntos(suma);
                } else {
                    puntos = playersAux.get(3).getPuntos();
                    puntosCat1 = puntosCat[cat];
                    suma = puntos + puntosCat1;
                    //players.get(3).setPuntos(suma);
                    playersAux.get(3).setPuntos(suma);
                }
                siguiente.setClickable(true);
                comprobarRondas();
            }
        });
    }


    public void obtenerPregunta(final int random){
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy--MM--dd(HH:mm:ss)", Locale.getDefault());
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


