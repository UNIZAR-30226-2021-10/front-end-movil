package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CambiarAvatar extends AppCompatActivity {

    private static final int OPTION_OK = 0;
    private static final int OPTION_ATRAS = 1;

    private ListView listaObjetos;
    private String[] nombres = {"Traje", "Médico", "Paragüas", "Estetoscopio", "Maletín",
            "Vestido", "Corbata", "Gafas", "Balon", "Sombrero"};

    private  ImageView imagenAvatar;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_avatar);
        getSupportActionBar().hide();
        imagenAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        //Bitmap bitmap = Bitmap.createBitmap(imagenAvatar.getWidth(),imagenAvatar.getHeight(),Bitmap.Config.RGB_565);
        //imagenAvatar.setImageBitmap(bitmap);

        listaObjetos = (ListView)findViewById(R.id.list);
        fillData();

        Button okButton = (Button) findViewById((R.id.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
                startActivityForResult(intent, OPTION_OK);
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }

    /**
     * Rellena la lista.
     */
    private void fillData() {
        // TODO: Por ahora así hasta que decidamos como está la BD
        // Cuando hagamos base de datos hay que cambiar esto
        listaObjetos = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombres);
        listaObjetos.setAdapter(adaptador);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            System.err.println(imagenAvatar.getWidth()  +  "," + imagenAvatar.getHeight());
            //Bitmap bitmap = Bitmap.createBitmap(imagenAvatar.getWidth(),imagenAvatar.getHeight()/4,Bitmap.Config.RGB_565);
            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.disfraz_traje);

            imagenAvatar.setImageBitmap(bitmap);
        }
    }
}


