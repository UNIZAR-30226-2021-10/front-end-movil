package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
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

    ArrayList<String> idInd = new ArrayList<>();
    ArrayList<String> fechasInd = new ArrayList<>();
    ArrayList<String> puntuacionesInd = new ArrayList<>();
    ArrayList<String> numJugadoresInd = new ArrayList<>();
    ArrayList<String> ganadoresInd = new ArrayList<>();

    ArrayList<String> fechasMulti = new ArrayList<>();
    ArrayList<String> puntuacionesMulti = new ArrayList<>();
    ArrayList<String> numJugadoresMulti = new ArrayList<>();
    ArrayList<String> ganadoresMulti = new ArrayList<>();


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
        idInd.clear();
        puntuacionesInd.clear();
        fechasInd.clear();
        numJugadoresInd.clear();
        ganadoresInd.clear();
        puntuacionesMulti.clear();
        fechasMulti.clear();
        numJugadoresMulti.clear();
        ganadoresMulti.clear();
    }


    /**
     * Rellena la lista.
     */
    private void fillData() {
        // en juega --> conseguir todas las partidas de el usuario y su puntuacion
        // en cada partida conseguir todos los datos
        Call<JsonArray> call = retrofitInterface.getGames(emailUsuario);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.code() == 200){

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        numJugadores.add(prueba.get("numJugadores").getAsString());
                        numJugadoresInd.add(prueba.get("numJugadores").getAsString());
                        numJugadoresMulti.add(prueba.get("numJugadores").getAsString());
                        if(prueba.get("numJugadores").getAsString() == "1"){
                            idInd.add(prueba.get("idpartida").getAsString());
                            fechasInd.add(prueba.get("fecha").getAsString().substring(0,10));
                            numJugadoresInd.add(prueba.get("numJugadores").getAsString());
                            ganadoresInd.add(prueba.get("ganador").getAsString());
                        } else{
                            fechasMulti.add(prueba.get("fecha").getAsString().substring(0,10));
                            numJugadoresMulti.add(prueba.get("numJugadores").getAsString());
                            ganadoresMulti.add(prueba.get("ganador").getAsString());
                        }
                        fechas.add(prueba.get("fecha").getAsString().substring(0,10));
                        numJugadores.add(prueba.get("numJugadores").getAsString());
                        ganadores.add(prueba.get("ganador").getAsString());
                        System.out.println(prueba);
                    }
                    getPuntuacion();
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

    private void getPuntuacion(){
        Call<JsonArray> call = retrofitInterface.getPointsFromGames(emailUsuario);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.code() == 200){

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        String id = prueba.get("id_partida").getAsString();
                        if(idInd.contains(id)){
                            puntuacionesInd.add(prueba.get("puntuacion").getAsString());
                        } else{
                            puntuacionesMulti.add(prueba.get("puntuacion").getAsString());
                        }
                        puntuaciones.add(prueba.get("puntuacion").getAsString());
                    }

                    adaptador = new ListViewAdapterHistory(Historial.this, fechas, puntuaciones, numJugadores, ganadores);
                    listaPartidas.setAdapter(adaptador);

                }else if(response.code() == 400){
                    Toast.makeText( Historial.this, "No se ha conseguido el listado", Toast.LENGTH_LONG).show();
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
            adaptador = new ListViewAdapterHistory(Historial.this, fechas, puntuaciones, numJugadores, ganadores);
        } else if(opcion == 1){
            adaptador = new ListViewAdapterHistory(Historial.this, fechasInd, puntuacionesInd, numJugadoresInd, ganadoresInd);
        } else{
            adaptador = new ListViewAdapterHistory(Historial.this, fechasMulti, puntuacionesMulti, numJugadoresMulti, ganadoresMulti);
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
        ArrayList<String> fechas;
        ArrayList<String> puntuaciones;
        ArrayList<String> numJugadores;
        ArrayList<String> ganadores;

        public ListViewAdapterHistory(Context context, ArrayList<String> fechas, ArrayList<String> puntuaciones, ArrayList<String> numJugadores, ArrayList<String> ganadores) {
            this.context = context;
            this.fechas = fechas;
            this.puntuaciones = puntuaciones;
            this.numJugadores = numJugadores;
            this.ganadores = ganadores;
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

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.rows_usuarios_historial, parent, false);

            // Locate the TextViews in listview_item.xml
            date = (TextView) itemView.findViewById(R.id.fecha);
            points = (TextView) itemView.findViewById(R.id.puntos);
            players = (TextView) itemView.findViewById(R.id.jugadores);
            winner = (TextView) itemView.findViewById(R.id.ganador);

            // Capture position and set to the TextViews
            date.setText(fechas.get(position));
            points.setText(puntuaciones.get(position));
            players.setText(numJugadores.get(position));
            winner.setText(ganadores.get(position));

            return itemView;
        }
    }
}


