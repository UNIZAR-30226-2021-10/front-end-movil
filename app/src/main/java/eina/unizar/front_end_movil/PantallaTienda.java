package eina.unizar.front_end_movil;

import android.content.Context;
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

import java.util.ArrayList;

public class PantallaTienda extends AppCompatActivity {

    private static final int OPTION_OBJETO = 0;

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

    int[] imagenesCabeza = { R.mipmap.sombrero1icon, R.mipmap.sombrero2icon};
    int[] imagenesColores = { R.mipmap.colores1icon, R.mipmap.colores2icon, R.mipmap.colores3icon };
    int[] imagenesCara = { R.mipmap.comp1icon};
    int[] imagenesCuerpo = {};

    String[] nombresCabeza = {"Elegante", "Top hat"};
    String[] nombresColores = {"Rosa", "Verde", "Arcoiris"};
    String[] nombresCara = {"Gafas sol"};
    String[] nombresCuerpo = {};


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

        rv_colores = findViewById(R.id.recyclerview_colores);
        rv_cabeza = findViewById(R.id.recyclerview_sombreros);

        inicializar();

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

        // SOMBREROS
        for(int i = 0; i < imagenesCabeza.length; i++){
            MainModel model = new MainModel(imagenesCabeza[i], nombresCabeza[i]);
            mainmodels_cabeza.add(model);
        }

        // COLORES
        for(int i = 0; i < imagenesColores.length; i++){
            MainModel model = new MainModel(imagenesColores[i], nombresColores[i]);
            mainmodels_colores.add(model);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        rv_colores.setLayoutManager(layoutManager);
        rv_colores.setItemAnimator(new DefaultItemAnimator());

        rv_cabeza.setLayoutManager(layoutManager2);
        rv_cabeza.setItemAnimator(new DefaultItemAnimator());

        mainAdapter_colores = new MainAdapter(this, mainmodels_colores);
        mainAdapter_cabeza = new MainAdapter(this, mainmodels_cabeza);

        mainAdapter_colores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SELECCION: "+
                                mainmodels_colores.get(rv_colores.getChildAdapterPosition(v)).getNombre(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        rv_colores.setAdapter(mainAdapter_colores);

        mainAdapter_cabeza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SELECCION: "+
                                mainmodels_cabeza.get(rv_cabeza.getChildAdapterPosition(v)).getNombre(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        rv_cabeza.setAdapter(mainAdapter_cabeza);
    }


    public class MainModel {
        Integer foto;
        String nombre;

        public MainModel(Integer foto, String nombre){
            this.foto = foto;
            this.nombre = nombre;
        }

        public Integer getFoto() {
            return foto;
        }

        public String getNombre() {
            return nombre;
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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                foto = itemView.findViewById(R.id.foto_objeto);
                nombre = itemView.findViewById(R.id.nombre_objeto);
            }
        }
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


