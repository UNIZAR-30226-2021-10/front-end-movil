package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import SessionManagement.UserRanking;

public class Ranking extends AppCompatActivity {

    private static final int OPTION_ATRAS = 0;

    ListViewAdapterRank adapterRanking;

    ArrayList<String> imagenes = new ArrayList<>();
    ArrayList<String> nombres = new ArrayList<>();
    ArrayList<String> puntos = new ArrayList<>();

    ArrayList<String> imagenesNew = new ArrayList<>();
    ArrayList<String> nombresNew = new ArrayList<>();
    ArrayList<String> puntosNew = new ArrayList<>();

    ArrayList<UserRanking> lista = new ArrayList<>();

    private ListView listaUsuarios;
    private RetrofitInterface retrofitInterface;

    ImageView img1;
    TextView name1;
    TextView points1;
    ImageView img2;
    TextView name2;
    TextView points2;
    ImageView img3;
    TextView name3;
    TextView points3;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();

        resetear();

        listaUsuarios = (ListView)findViewById(R.id.list);
        img1 = (ImageView) findViewById(R.id.puesto1);
        name1 = (TextView) findViewById(R.id.nombre1);
        points1 = (TextView) findViewById(R.id.puntos1);
        img2 = (ImageView) findViewById(R.id.puesto2);
        name2 = (TextView) findViewById(R.id.nombre2);
        points2 = (TextView) findViewById(R.id.puntos2);
        img3 = (ImageView) findViewById(R.id.puesto3);
        name3 = (TextView) findViewById(R.id.nombre3);
        points3 = (TextView) findViewById(R.id.puntos3);
        fillData();

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }

    private void resetear(){
        imagenes.clear();
        nombres.clear();
        puntos.clear();
        imagenesNew.clear();
        nombresNew.clear();
        puntosNew.clear();
    }

    /**
     * Rellena la lista.
     */
    private void fillData() {
        // de cada usuario conseguir sus puntos, nombre, e imagen (avatar)
        Call<JsonArray> call = retrofitInterface.getRanking();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.code() == 200){
                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        String imagen_url = prueba.get("imagen").getAsString();
                        imagen_url = imagen_url.replaceAll("http://localhost:3060", "https://trivial-images.herokuapp.com");
                        UserRanking user = new UserRanking(prueba.get("nickname").getAsString(), prueba.get("puntos").getAsInt(), imagen_url, 0);
                        lista.add(user);
                    }
                    Collections.sort(lista, new Comparator<UserRanking>() {
                        @Override
                        public int compare(UserRanking p1, UserRanking p2) {
                            // Aqui esta el truco, ahora comparamos p2 con p1 y no al reves como antes
                            return new Integer(p2.getPuntos()).compareTo(new Integer(p1.getPuntos()));
                        }
                    });
                    int puesto = 1;
                    for(UserRanking u : lista){
                        u.setPuesto(puesto);
                        puesto++;
                    }

                    Picasso.get().load(lista.get(0).getImagen()).into(img1);
                    name1.setText(lista.get(0).getNombre());
                    points1.setText(String.valueOf(lista.get(0).getPuntos()));
                    Picasso.get().load(lista.get(1).getImagen()).into(img2);
                    name2.setText(lista.get(1).getNombre());
                    points2.setText(String.valueOf(lista.get(1).getPuntos()));
                    Picasso.get().load(lista.get(2).getImagen()).into(img3);
                    name3.setText(lista.get(2).getNombre());
                    points3.setText(String.valueOf(lista.get(2).getPuntos()));

                    lista.remove(0);
                    lista.remove(0);
                    lista.remove(0);

                    adapterRanking = new ListViewAdapterRank(Ranking.this, lista);
                    listaUsuarios.setAdapter(adapterRanking);

                }else if(response.code() == 400){
                    //System.out.println("AQUI 4");
                    Toast.makeText( Ranking.this, "No hay usuarios?", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText( Ranking.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class ListViewAdapterRank extends BaseAdapter {
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        ArrayList<UserRanking> listaUsuarios;

        public ListViewAdapterRank(Context context, ArrayList<UserRanking> lista) {
            this.context = context;
            this.listaUsuarios = lista;
        }

        @Override
        public int getCount() {
            return listaUsuarios.size();
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
            ImageView img;
            TextView name;
            TextView points;
            TextView number;

            //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.rows_usuarios_ranking, parent, false);

            // Locate the TextViews in listview_item.xml
            img = (ImageView) itemView.findViewById(R.id.foto_usuario);
            name = (TextView) itemView.findViewById(R.id.nombre_usuario);
            points = (TextView) itemView.findViewById(R.id.puntos_usuario);
            number = (TextView) itemView.findViewById(R.id.numero_rank);

            number.setText(String.valueOf(listaUsuarios.get(position).getPuesto()));
            Picasso.get().load(listaUsuarios.get(position).getImagen()).into(img);
            name.setText(listaUsuarios.get(position).getNombre());
            points.setText(String.valueOf(listaUsuarios.get(position).getPuntos()));


            return itemView;
        }
    }

}


