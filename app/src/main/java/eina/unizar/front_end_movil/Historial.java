package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import SessionManagement.GestorSesion;
import SessionManagement.Question;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Historial extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ListView listaPartidas;
    ListViewAdapterHistory adaptador;

    private GestorSesion gestorSesion;
    private RetrofitInterface retrofitInterface;
    private String emailUsuario;
    int opcion = 0;


    ArrayList<String> fechas = new ArrayList<>();
    ArrayList<String> puntuaciones = new ArrayList<>();
    ArrayList<String> numJugadores = new ArrayList<>();
    ArrayList<String> ganadores = new ArrayList<>();
    ArrayList<String> fotos = new ArrayList<>();
    ArrayList<String> puestos = new ArrayList<>();
    ArrayList<String> rondas = new ArrayList<>();

    ArrayList<String> idInd = new ArrayList<>();
    ArrayList<String> fechasInd = new ArrayList<>();
    ArrayList<String> puntuacionesInd = new ArrayList<>();
    ArrayList<String> numJugadoresInd = new ArrayList<>();
    ArrayList<String> ganadoresInd = new ArrayList<>();
    ArrayList<String> fotosInd = new ArrayList<>();
    ArrayList<String> puestosInd = new ArrayList<>();
    ArrayList<String> rondasInd = new ArrayList<>();

    ArrayList<String> fechasMulti = new ArrayList<>();
    ArrayList<String> puntuacionesMulti = new ArrayList<>();
    ArrayList<String> numJugadoresMulti = new ArrayList<>();
    ArrayList<String> ganadoresMulti = new ArrayList<>();
    ArrayList<String> fotosMulti = new ArrayList<>();
    ArrayList<String> puestosMulti = new ArrayList<>();
    ArrayList<String> rondasMulti = new ArrayList<>();


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);
        getSupportActionBar().hide();

        //Construirmos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();
        gestorSesion = new GestorSesion(Historial.this);
        emailUsuario = gestorSesion.getmailSession();

        resetear();

        Spinner spinner = (Spinner) findViewById(R.id.opciones_orden);
        spinner.setOnItemSelectedListener(this);

        listaPartidas = (ListView)findViewById(R.id.list);
        fillData(); // llamada a base de datos para rellenar la lista

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void resetear(){
        fechas.clear();
        puntuaciones.clear();
        numJugadores.clear();
        ganadores.clear();
        fotos.clear();
        idInd.clear();
        puntuacionesInd.clear();
        fechasInd.clear();
        numJugadoresInd.clear();
        ganadoresInd.clear();
        fotosInd.clear();
        puntuacionesMulti.clear();
        fechasMulti.clear();
        numJugadoresMulti.clear();
        ganadoresMulti.clear();
        fotosMulti.clear();
        puestos.clear();
        puestosInd.clear();
        puestosMulti.clear();
        rondas.clear();
        rondasInd.clear();
        rondasMulti.clear();
    }


    private void fillData() {
        // en juega --> conseguir todas las partidas de el usuario y su puntuacion
        // en cada partida conseguir todos los datos
        Call<JsonArray> call = retrofitInterface.getGames(emailUsuario);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.code() == 200){
                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject) {
                        JsonObject partida = j.getAsJsonObject();
                        String fecha = partida.get("fecha").getAsString();
                        String numJ = partida.get("maxJugadores").getAsString();
                        String puntuacionMia = "";
                        String puesto = "";
                        String rondasP = partida.get("maxRondas").getAsString();
                        JsonArray jugadores = partida.getAsJsonArray("jugadoresEnPartida");
                        int lugar = 0;
                        for(JsonElement j2 : jugadores) {
                            lugar++;
                            JsonObject jugador = j2.getAsJsonObject();
                            // si es el jugador que ha pedido el historial
                            if(jugador.get("username").getAsString().equals(gestorSesion.getSession())) {
                                puntuacionMia = jugador.get("puntos").getAsString();
                                puesto = Integer.toString(lugar);
                                break;
                            }
                        }
                        //System.out.println(jugadores);
                        String ganador = jugadores.get(0).getAsJsonObject().get("username").getAsString();
                        String imagenGanador = jugadores.get(0).getAsJsonObject().get("avatar").getAsString();

                        if(numJ.equals("1")){
                            //System.out.println("INDIVIDUAL");
                            fechasInd.add(fecha);
                            numJugadoresInd.add(numJ);
                            ganadoresInd.add(ganador);
                            puntuacionesInd.add(puntuacionMia);
                            fotosInd.add(imagenGanador);
                            rondasInd.add(rondasP);
                            puestosInd.add(puesto);
                        } else{
                            //System.out.println("MULTIJUGADOR");
                            fotosMulti.add(imagenGanador);
                            puntuacionesMulti.add(puntuacionMia);
                            fechasMulti.add(fecha);
                            numJugadoresMulti.add(numJ);
                            ganadoresMulti.add(ganador);
                            rondasMulti.add(rondasP);
                            puestosMulti.add(puesto);
                        }
                        rondas.add(rondasP);
                        fotos.add(imagenGanador);
                        puntuaciones.add(puntuacionMia);
                        fechas.add(fecha);
                        numJugadores.add(numJ);
                        ganadores.add(ganador);
                        puestos.add(puesto);
                    }

                    adaptador = new ListViewAdapterHistory(Historial.this, fotos, fechas, puntuaciones, numJugadores, ganadores, rondas, puestos);
                    listaPartidas.setAdapter(adaptador);
                    //getPuntuacion();*/
                }else if(response.code() == 400){
                    Toast.makeText( Historial.this, "No hay partidas todavía", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText( Historial.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /* para cuando clica una opción*/
        System.out.println(position);
        opcion = position;
        if(opcion == 0){
            System.out.println("TODAS 2");
            System.out.println("Numero de todas " + fechas.size());
            adaptador = new ListViewAdapterHistory(Historial.this, fotos, fechas, puntuaciones, numJugadores, ganadores, rondas, puestos);
        } else if(opcion == 1){
            System.out.println("INDIVIDUAL 2");
            System.out.println("Numero de individuales " + fechasInd.size());
            adaptador = new ListViewAdapterHistory(Historial.this, fotosInd, fechasInd, puntuacionesInd, numJugadoresInd, ganadoresInd, rondasInd, puestosInd);
        } else{
            System.out.println("MULTIJUGADOR 2");
            System.out.println("Numero de multi " + fechasMulti.size());
            adaptador = new ListViewAdapterHistory(Historial.this, fotosMulti, fechasMulti, puntuacionesMulti, numJugadoresMulti, ganadoresMulti, rondasMulti, puestosMulti);
        }
        listaPartidas.setAdapter(adaptador);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        /* */
    }

    public class ListViewAdapterHistory extends BaseAdapter {
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        ArrayList<String> imagenes;
        ArrayList<String> fechas;
        ArrayList<String> puntuaciones;
        ArrayList<String> numJugadores;
        ArrayList<String> ganadores;
        ArrayList<String> rondas;
        ArrayList<String> puestos;

        public ListViewAdapterHistory(Context context, ArrayList<String> imagenes, ArrayList<String> fechas, ArrayList<String> puntuaciones, ArrayList<String> numJugadores, ArrayList<String> ganadores, ArrayList<String> rondas, ArrayList<String> puestos) {
            this.context = context;
            this.imagenes = imagenes;
            this.fechas = fechas;
            this.puntuaciones = puntuaciones;
            this.numJugadores = numJugadores;
            this.ganadores = ganadores;
            this.rondas = rondas;
            this.puestos = puestos;
        }

        @Override
        public int getCount() {
            return fechas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Declare Variables
            TextView date;
            TextView points;
            TextView players;
            TextView winner;
            ImageView foto;
            TextView place;
            TextView rounds;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.rows_usuarios_historial, parent, false);

            // Locate the TextViews in listview_item.xml
            date = (TextView) itemView.findViewById(R.id.fecha);
            points = (TextView) itemView.findViewById(R.id.puntos);
            players = (TextView) itemView.findViewById(R.id.jugadores);
            winner = (TextView) itemView.findViewById(R.id.ganador);
            foto = (ImageView) itemView.findViewById(R.id.foto_perfil);
            place = (TextView) itemView.findViewById(R.id.puesto);
            rounds = (TextView) itemView.findViewById(R.id.rondas);

            // Capture position and set to the TextViews
            date.setText(fechas.get(position));
            points.setText(puntuaciones.get(position));
            players.setText(numJugadores.get(position));
            winner.setText(ganadores.get(position));
            cargarImagenUsuario(imagenes.get(position), foto);
            place.setText(puestos.get(position));
            rounds.setText(rondas.get(position));

            return itemView;
        }
    }

    public void cargarImagenUsuario(String url, ImageView perfilButton){
        Picasso.get().load(url).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(perfilButton);
    }
}


