package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaTienda extends AppCompatActivity {

    private static final int OPTION_OBJETO = 0;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    private String MONEDAS_USUARIO;

    TextView numMonedas;
    RecyclerView rv_colores;
    RecyclerView rv_cabeza;
    RecyclerView rv_cara;
    RecyclerView rv_cuerpo;

    ArrayList<MainModel> mainmodels_colores;
    ArrayList<MainModel> mainmodels_cabeza;
    ArrayList<MainModel> mainmodels_cara;
    ArrayList<MainModel> mainmodels_cuerpo;

    MainAdapter mainAdapter_colores;
    MainAdapter mainAdapter_cabeza;
    MainAdapter mainAdapter_cara;
    MainAdapter mainAdapter_cuerpo;

    int[] imagenesCabeza = { R.mipmap.complemento_gorrito_santa};
    int[] imagenesColores = {  R.mipmap.color_rojo, R.mipmap.color_amarillo, R.mipmap.color_azul, R.mipmap.color_naranja,
           R.mipmap.color_rosa, R.mipmap.color_verde};
    int[] imagenesCara = { R.mipmap.complemento_gafas_boss};
    int[] imagenesCuerpo = {R.mipmap.disfraz_traje};

    ArrayList<Integer> precioCabeza = new ArrayList<>();
    ArrayList<Integer> precioColores = new ArrayList<>();
    ArrayList<Integer> precioCara = new ArrayList<>();
    ArrayList<Integer> precioCuerpo = new ArrayList<>();

    ArrayList<String> nombresCabeza = new ArrayList<>();
    ArrayList<String> nombresColores = new ArrayList<>();
    ArrayList<String> nombresCara = new ArrayList<>();
    ArrayList<String> nombresCuerpo = new ArrayList<>();

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_tienda);
        getSupportActionBar().hide();

        gestorSesion = new GestorSesion(PantallaTienda.this);
        numMonedas = findViewById(R.id.monedas_usuario);
        MONEDAS_USUARIO = gestorSesion.getKEY_SESSION_COINS();
        numMonedas.setText(MONEDAS_USUARIO);

        retrofitInterface = APIUtils.getAPIService();

        rv_colores = findViewById(R.id.recyclerview_colores);
        rv_cabeza = findViewById(R.id.recyclerview_cabeza);
        rv_cara = findViewById(R.id.recyclerview_cara);
        rv_cuerpo = findViewById(R.id.recyclerview_cuerpo);

        obtenerImagenes();

        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void inicializar(){
        mainmodels_colores = new ArrayList<>();
        mainmodels_cabeza = new ArrayList<>();
        mainmodels_cuerpo = new ArrayList<>();
        mainmodels_cara = new ArrayList<>();

        poblarListas();
        ponerLayouts();
        ponerAdaptadores();
    }

    public void poblarListas(){
        // CABEZA
        for(int i = 0; i < imagenesCabeza.length; i++){
            MainModel model = new MainModel(imagenesCabeza[i], nombresCabeza.get(i), precioCabeza.get(i));
            mainmodels_cabeza.add(model);
        }

        // COLORES
        for(int i = 0; i < imagenesColores.length; i++){
            MainModel model = new MainModel(imagenesColores[i], nombresColores.get(i), precioColores.get(i));
            mainmodels_colores.add(model);
        }

        // CUERPO
        for(int i = 0; i < imagenesCuerpo.length; i++){
            MainModel model = new MainModel(imagenesCuerpo[i], nombresCuerpo.get(i), precioCuerpo.get(i));
            mainmodels_cuerpo.add(model);
        }

        // CARA
        for(int i = 0; i < imagenesCara.length; i++){
            MainModel model = new MainModel(imagenesCara[i], nombresCara.get(i), precioCara.get(i));
            mainmodels_cara.add(model);
        }
    }

    public void ponerLayouts(){
        LinearLayoutManager layoutManagerCara = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        LinearLayoutManager layoutManagerCuerpo = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        LinearLayoutManager layoutManagerColor = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        LinearLayoutManager layoutManagerCabeza = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        rv_colores.setLayoutManager(layoutManagerColor);
        rv_colores.setItemAnimator(new DefaultItemAnimator());

        rv_cabeza.setLayoutManager(layoutManagerCabeza);
        rv_cabeza.setItemAnimator(new DefaultItemAnimator());

        rv_cara.setLayoutManager(layoutManagerCara);
        rv_cara.setItemAnimator(new DefaultItemAnimator());

        rv_cuerpo.setLayoutManager(layoutManagerCuerpo);
        rv_cuerpo.setItemAnimator(new DefaultItemAnimator());
    }

    public void ponerAdaptadores(){
        mainAdapter_colores = new MainAdapter(this, mainmodels_colores);
        mainAdapter_cabeza = new MainAdapter(this, mainmodels_cabeza);
        mainAdapter_cara = new MainAdapter(this, mainmodels_cara);
        mainAdapter_cuerpo = new MainAdapter(this, mainmodels_cuerpo);

        mainAdapter_colores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                extra.putString("Nombre", mainmodels_colores.get(rv_colores.getChildAdapterPosition(v)).getNombre());
                extra.putInt("Precio", mainmodels_colores.get(rv_colores.getChildAdapterPosition(v)).getPrecio());
                extra.putInt("Imagen", mainmodels_colores.get(rv_colores.getChildAdapterPosition(v)).getFoto());
                extra.putString("Monedas", MONEDAS_USUARIO);
                Intent intent = new Intent(v.getContext(), ObjetoTienda.class);
                intent.putExtras(extra);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });
        rv_colores.setAdapter(mainAdapter_colores);

        mainAdapter_cabeza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                extra.putString("Nombre", mainmodels_cabeza.get(rv_cabeza.getChildAdapterPosition(v)).getNombre());
                extra.putInt("Precio", mainmodels_cabeza.get(rv_cabeza.getChildAdapterPosition(v)).getPrecio());
                extra.putInt("Imagen", mainmodels_cabeza.get(rv_cabeza.getChildAdapterPosition(v)).getFoto());
                extra.putString("Monedas", MONEDAS_USUARIO);
                Intent intent = new Intent(v.getContext(), ObjetoTienda.class);
                intent.putExtras(extra);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });
        rv_cabeza.setAdapter(mainAdapter_cabeza);

        mainAdapter_cara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                extra.putString("Nombre", mainmodels_cara.get(rv_cara.getChildAdapterPosition(v)).getNombre());
                extra.putInt("Precio", mainmodels_cara.get(rv_cara.getChildAdapterPosition(v)).getPrecio());
                extra.putInt("Imagen", mainmodels_cara.get(rv_cara.getChildAdapterPosition(v)).getFoto());
                extra.putString("Monedas", MONEDAS_USUARIO);
                Intent intent = new Intent(v.getContext(), ObjetoTienda.class);
                intent.putExtras(extra);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });
        rv_cara.setAdapter(mainAdapter_cara);

        mainAdapter_cuerpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                extra.putString("Nombre", mainmodels_cuerpo.get(rv_cuerpo.getChildAdapterPosition(v)).getNombre());
                extra.putInt("Precio", mainmodels_cuerpo.get(rv_cuerpo.getChildAdapterPosition(v)).getPrecio());
                extra.putInt("Imagen", mainmodels_cuerpo.get(rv_cuerpo.getChildAdapterPosition(v)).getFoto());
                extra.putString("Monedas", MONEDAS_USUARIO);
                Intent intent = new Intent(v.getContext(), ObjetoTienda.class);
                intent.putExtras(extra);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });
        rv_cuerpo.setAdapter(mainAdapter_cuerpo);
    }

    public class MainModel {
        Integer foto;
        String nombre;
        Integer precio;

        public MainModel(Integer foto, String nombre, Integer precio){
            this.foto = foto;
            this.nombre = nombre;
            this.precio = precio;
        }

        public Integer getFoto() {
            return foto;
        }

        public String getNombre() {
            return nombre;
        }

        public Integer getPrecio() {
            return precio;
        }
    }

    public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> implements View.OnClickListener {

        ArrayList<MainModel> mainModels;
        Context context;
        private View.OnClickListener listener;

        public MainAdapter(Context context, ArrayList<MainModel> mainModels) {
            this.mainModels = mainModels;
            this.context = context;
        }

        @NonNull
        @Override
        public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rows_objetos_tienda,
                    parent, false);
            view.setOnClickListener(this);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
            holder.foto.setImageResource(mainModels.get(position).getFoto());
            holder.nombre.setText(mainModels.get(position).getNombre());
            holder.precio.setText(mainModels.get(position).getPrecio().toString());
        }

        @Override
        public int getItemCount() {
            return mainModels.size();
        }

        public void setOnClickListener(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if(listener!=null){
                listener.onClick(v);
            }
        }

        public class ViewHolder  extends RecyclerView.ViewHolder{

            ImageView foto;
            TextView nombre;
            TextView precio;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                foto = itemView.findViewById(R.id.foto_objeto);
                nombre = itemView.findViewById(R.id.nombre_objeto);
                precio = itemView.findViewById(R.id.precio_objeto);
            }
        }
    }


    private void obtenerImagenes() {
        Call<JsonArray> call = retrofitInterface.getObjectsShop();
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        if(prueba.get("Tipo").getAsString().equals("color")){
                            precioColores.add(prueba.get("Precio").getAsInt());
                            nombresColores.add(prueba.get("Nombre").getAsString());

                            // TODO: coger la imagen aquí
                        } else if(prueba.get("Tipo").getAsString().equals("cara")){
                            precioCara.add(prueba.get("Precio").getAsInt());
                            nombresCara.add(prueba.get("Nombre").getAsString());
                            // TODO: coger la imagen aquí
                        } else if(prueba.get("Tipo").getAsString().equals("cuerpo")){
                            precioCuerpo.add(prueba.get("Precio").getAsInt());
                            nombresCuerpo.add(prueba.get("Nombre").getAsString());
                            // TODO: coger la imagen aquí
                        } else if(prueba.get("Tipo").getAsString().equals("cabeza")){
                            precioCabeza.add(prueba.get("Precio").getAsInt());
                            nombresCabeza.add(prueba.get("Nombre").getAsString());
                            // TODO: coger la imagen aquí
                            System.out.println(precioCabeza);
                        }
                        //System.out.println(prueba);
                    }
                    inicializar();
                } else{
                    Toast.makeText(PantallaTienda.this, "No se han podido obtener los objetos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(PantallaTienda.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    /*

        listaComp = (ListView) findViewById(R.id.listaComplementos);
        adapterComp = new ListViewAdapter(this, imagenesComp);
        listaComp.setAdapter(adapterComp);
    }

    public class ListViewAdapter extends BaseAdapter {
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        String[] nombres;
        RecyclerView[] listas;

        public ListViewAdapter(Context context, String[] nombres, RecyclerView[] listas) {
            this.context = context;
            this.nombres = nombres;
            this.listas = listas;
        }

        @Override
        public int getCount() {
            return nombres.length;
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
            TextView texto;
            RecyclerView rv;

            //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.rows_lista_tienda, parent, false);

            // Locate the TextViews in listview_item.xml
            texto = (TextView) itemView.findViewById(R.id.titulo_categoria);
            rv = (RecyclerView) itemView.findViewById(R.id.recyclerview);

            // Capture position and set to the TextViews
            texto.setText(nombres[position]);

            mainmodels_colores = new ArrayList<>();

            return itemView;
        }
    }*/
}


