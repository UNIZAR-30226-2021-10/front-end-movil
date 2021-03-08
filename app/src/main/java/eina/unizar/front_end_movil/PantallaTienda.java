package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PantallaTienda extends AppCompatActivity {

    private static final int OPTION_ATRAS = 0;
    private static final int OPTION_OBJETO = 0;

    ListViewAdapter adapterSombreros;
    ListViewAdapter adapterColores;
    ListViewAdapter adapterVehiculos;
    ListViewAdapter adapterComp;

    private ListView listaSombreros;
    private ListView listaComp;
    private ListView listaColores;
    private ListView listaVehiculos;

    int[] imagenesSombreros = { R.mipmap.sombrero1icon, R.mipmap.sombrero2icon};
    int[] imagenesColores = { R.mipmap.colores1icon, R.mipmap.colores2icon, R.mipmap.colores3icon };
    int[] imagenesComp = { R.mipmap.comp1icon, R.mipmap.comp2icon };
    int[] imagenesVehiculos = { R.mipmap.vehiculos1icon, R.mipmap.vehiculos2icon, R.mipmap.vehiculos3icon };

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

        // boton de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });

        listaSombreros = (ListView) findViewById(R.id.listaSombreros);
        adapterSombreros = new ListViewAdapter(this, imagenesSombreros);
        listaSombreros.setAdapter(adapterSombreros);

        listaSombreros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Intent intent = new Intent(adapterView.getContext(), ObjetoTienda.class);
                startActivityForResult(intent, OPTION_OBJETO);
            }
        });

        listaColores = (ListView) findViewById(R.id.listaColores);
        adapterColores = new ListViewAdapter(this, imagenesColores);
        listaColores.setAdapter(adapterColores);

        listaVehiculos = (ListView) findViewById(R.id.listaVehículos);
        adapterVehiculos = new ListViewAdapter(this, imagenesVehiculos);
        listaVehiculos.setAdapter(adapterVehiculos);

        listaComp = (ListView) findViewById(R.id.listaComplementos);
        adapterComp = new ListViewAdapter(this, imagenesComp);
        listaComp.setAdapter(adapterComp);
    }

    public class ListViewAdapter extends BaseAdapter {
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        int[] imagenes;

        public ListViewAdapter(Context context, int[] imagenes) {
            this.context = context;
            this.imagenes = imagenes;
        }

        @Override
        public int getCount() {
            return imagenes.length;
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

            //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.objetos_tienda, parent, false);

            // Locate the TextViews in listview_item.xml
            img = (ImageView) itemView.findViewById(R.id.foto_objeto);

            // Capture position and set to the TextViews
            img.setImageResource(imagenes[position]);

            return itemView;
        }
    }
}


