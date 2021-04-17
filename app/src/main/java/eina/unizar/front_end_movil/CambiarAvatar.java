package eina.unizar.front_end_movil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

            Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.disfraz_traje);
            //Obtenemos el bitmap del imageview
            BitmapDrawable bitmapOriginal = (BitmapDrawable) imagenAvatar.getDrawable();
            Bitmap bitmapAvatarStandar = bitmapOriginal.getBitmap();
            //Height 93 Weight 93 para el traje
            System.err.println(bitmap.getHeight() + "," + bitmap.getWidth());
            Bitmap bmOverlay = Bitmap.createBitmap(imagenAvatar.getWidth(),imagenAvatar.getHeight(),Bitmap.Config.RGB_565);
            Bitmap definitivo = finalcombieimage(bitmapAvatarStandar,bitmap);
            imagenAvatar.setImageBitmap(definitivo);
        }
    }

    public Bitmap finalcombieimage(Bitmap c, Bitmap s) {

        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Bitmap bmOverlay = Bitmap.createBitmap(imagenAvatar.getWidth(),imagenAvatar.getHeight(),Bitmap.Config.RGB_565);
        Canvas comboImage = new Canvas(bmOverlay);
        /*
         TOP: indica desde empieza desde arriba 0 lo cogerá desde el margen incial de la ImageView
               si se aumenta el margen top la recortara de arriba hacia abajo (la estrecha)
          BOTTOM: le indica hasta donde tiene que coger el alto de la Imageview
          RIGHT: indica donde acaba o hasta donde va el margen derecho, tendrá que el total del ancho
                 del imageview para que abarque la imageview entera.
          LEFT: indica desde empieza desde el margen izquierdo del imageview, 0 empezará desde el incio
                y si se aumenta ira acortandola en ancho
        * */
        Rect dest1 = new Rect(0, 0, 274, 290); // left,top,right,bottom
        comboImage.drawBitmap(c, null, dest1, null);
        Rect dest2 = new Rect(0, 0, 274, 290);
        comboImage.drawBitmap(s, null, dest2, null);
        return bmOverlay;
    }
}


//Bitmap bitmap = Bitmap.createBitmap(imagenAvatar.getWidth(),imagenAvatar.getHeight()/4,Bitmap.Config.RGB_565);