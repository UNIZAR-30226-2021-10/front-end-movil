package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ObjetoTienda extends AppCompatActivity {

    private static final int OPTION_COMPRAR = 0;
    private static final int OPTION_ATRAS = 1;

    private ImageView image;
    private TextView monedas;
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.objeto_tienda);
        getSupportActionBar().hide();

        image = (ImageView) findViewById(R.id.imagenObjeto);
        monedas = (TextView) findViewById(R.id.monedas_usuario);

        Bundle extras = getIntent().getExtras();
        int imagen = extras.getInt("imagen");

        image.setImageResource(imagen);

        Button comprarButton = (Button) findViewById((R.id.comprar));
        comprarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PantallaTienda.class);
                startActivityForResult(intent, OPTION_COMPRAR);
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PantallaTienda.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }
}


