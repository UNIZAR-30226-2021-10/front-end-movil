package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class Ranking extends AppCompatActivity {

    private static final int OPTION_ATRAS = 0;

    ListViewAdapterRank adapterRanking;

    private ListView listaUsuarios;
    private String[] puntos = {"3456", "765", "32", "20"};
    private String[] nombres = {"usuario1", "usuario2", "usuario3", "w"};
    private int[] imagenesUsuarios = {R.mipmap.imagenusr1, R.mipmap.imagenusr2, R.mipmap.imagenusr3, R.mipmap.imagenusr1};

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

        listaUsuarios = (ListView)findViewById(R.id.list);
        adapterRanking = new ListViewAdapterRank(this, nombres, imagenesUsuarios, puntos);
        listaUsuarios.setAdapter(adapterRanking);

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

    public class ListViewAdapterRank extends BaseAdapter {
        // Declare Variables
        Context context;
        LayoutInflater inflater;
        int[] imagenes;
        String[] nombres;
        String[] puntos;
        int conteo;

        public ListViewAdapterRank(Context context, String[] _nombres, int[] _imagenes, String[] _puntos) {
            this.context = context;
            this.nombres = _nombres;
            this.imagenes = _imagenes;
            this.puntos = _puntos;
            conteo = 0;
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

            // Capture position and set to the TextViews
            conteo++;
            number.setText(Integer.toString(conteo));
            img.setImageResource(imagenes[position]);
            name.setText(nombres[position]);
            points.setText(puntos[position]);

            return itemView;
        }
    }

}


