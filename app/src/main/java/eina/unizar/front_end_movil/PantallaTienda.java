package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import models.model.MainAdapter;
import models.model.MainModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaTienda extends AppCompatActivity {

    private String ip = "192.168.0.26"; //TODO: cambiar para poder usarlo todos

    private static final int OPTION_OBJETO = 0;

    private boolean esPrimeraVez = true;

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

    ArrayList<String> imagenCabeza = new ArrayList<>();
    ArrayList<String> imagenColores = new ArrayList<>();
    ArrayList<String> imagenCara = new ArrayList<>();
    ArrayList<String> imagenCuerpo = new ArrayList<>();

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

        // Drive = "https://drive.google.com/file/d/1yBy5abUvTuvAMk5aeKDnlljuWMcBG0Ur/view?usp=sharing"
        // Imagen prueba = "http://192.168.0.26:3060/tienda/color_amarillo.png"
        //String url = generateUrl("https://drive.google.com/file/d/1yBy5abUvTuvAMk5aeKDnlljuWMcBG0Ur/view?usp=sharing");
        //Picasso.get().load("http://192.168.0.26:3060/tienda/color_amarillo.png").into(imageView);

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

    /*public String generateUrl(String s){
        String[] p=s.split("/");
        String imageLink="https://drive.google.com/uc?export=download&id="+p[5];
        return imageLink;
    }*/


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
        Bitmap bitMap;
        // CABEZA
        for(int i = 0; i < nombresCabeza.size(); i++){
            //bitMap = getBitmapFromURL(imagenCabeza.get(i));
            MainModel model = new MainModel(imagenCabeza.get(i), nombresCabeza.get(i), precioCabeza.get(i));
            mainmodels_cabeza.add(model);
        }

        // COLORES
        for(int i = 0; i < nombresColores.size(); i++){
            //bitMap = getBitmapFromURL(imagenColores.get(i));
            MainModel model = new MainModel(imagenColores.get(i), nombresColores.get(i), precioColores.get(i));
            mainmodels_colores.add(model);
        }

        // CUERPO
        for(int i = 0; i < nombresCuerpo.size(); i++){
            //bitMap = getBitmapFromURL(imagenCuerpo.get(i));
            MainModel model = new MainModel(imagenCuerpo.get(i), nombresCuerpo.get(i), precioCuerpo.get(i));
            mainmodels_cuerpo.add(model);
        }

        // CARA
        for(int i = 0; i < nombresCara.size(); i++){
            //bitMap = getBitmapFromURL(imagenCara.get(i));
            MainModel model = new MainModel(imagenCara.get(i), nombresCara.get(i), precioCara.get(i));
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
                extra.putString("Imagen", mainmodels_colores.get(rv_colores.getChildAdapterPosition(v)).getFoto());
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
                extra.putString("Imagen", mainmodels_cabeza.get(rv_cabeza.getChildAdapterPosition(v)).getFoto());
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
                extra.putString("Imagen", mainmodels_cara.get(rv_cara.getChildAdapterPosition(v)).getFoto());
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
                extra.putString("Imagen", mainmodels_cuerpo.get(rv_cuerpo.getChildAdapterPosition(v)).getFoto());
                extra.putString("Monedas", MONEDAS_USUARIO);
                Intent intent = new Intent(v.getContext(), ObjetoTienda.class);
                intent.putExtras(extra);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });
        rv_cuerpo.setAdapter(mainAdapter_cuerpo);
    }

    private void obtenerImagenes() {
        HashMap<String,String> prueba = new HashMap<>();
        prueba.put("email",gestorSesion.getmailSession());
        Call<JsonArray> call = retrofitInterface.getObjectsShop(prueba);
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (response.code() == 200) {

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        String imagen = prueba.get("Imagen").getAsString();
                        imagen = imagen.replaceAll("http://localhost:3060", "https://trivial-images.herokuapp.com");
                        if(prueba.get("Tipo").getAsString().equals("color")){
                            precioColores.add(prueba.get("Precio").getAsInt());
                            nombresColores.add(prueba.get("Nombre").getAsString());
                            imagenColores.add(imagen);
                        } else if(prueba.get("Tipo").getAsString().equals("cara")){
                            precioCara.add(prueba.get("Precio").getAsInt());
                            nombresCara.add(prueba.get("Nombre").getAsString());
                            imagenCara.add(imagen);
                        } else if(prueba.get("Tipo").getAsString().equals("cuerpo")){
                            precioCuerpo.add(prueba.get("Precio").getAsInt());
                            nombresCuerpo.add(prueba.get("Nombre").getAsString());
                            imagenCuerpo.add(imagen);
                        } else if(prueba.get("Tipo").getAsString().equals("cabeza")){
                            precioCabeza.add(prueba.get("Precio").getAsInt());
                            nombresCabeza.add(prueba.get("Nombre").getAsString());
                            imagenCabeza.add(imagen);
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

    public void resetear(){
        int sizeCabeza = imagenCabeza.size();
        int sizeCuerpo = imagenCuerpo.size();
        int sizeCara = imagenCara.size();
        int sizeColor = imagenColores.size();
        imagenCabeza.clear();
        imagenColores.clear();
        imagenCara.clear();
        imagenCuerpo.clear();
        precioCabeza.clear();
        precioColores.clear();
        precioCara.clear();
        precioCuerpo.clear();
        nombresCabeza.clear();
        nombresColores.clear();
        nombresCara.clear();
        nombresCuerpo.clear();

        mainAdapter_colores.notifyItemRangeRemoved(0,sizeColor);
        mainAdapter_cara.notifyItemRangeRemoved(0,sizeCara);
        mainAdapter_cabeza.notifyItemRangeRemoved(0,sizeCabeza);
        mainAdapter_cuerpo.notifyItemRangeRemoved(0,sizeCuerpo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!esPrimeraVez){
            resetear();
            obtenerImagenes();
            MONEDAS_USUARIO = gestorSesion.getKEY_SESSION_COINS();
            numMonedas.setText(MONEDAS_USUARIO);
        }
        esPrimeraVez = false;
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

    /*public static Bitmap getBitmapFromURL(String url_image){
        HttpURLConnection connection = null;
        try {
            /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //connection.setDoInput(true);
            //connection.connect();
            URL url = new URL(url_image);
            connection = (HttpURLConnection) url.openConnection();
            //InputStream input = new BufferedInputStream(connection.getInputStream());
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        } finally{
            connection.disconnect();
        }
    }*/
}


