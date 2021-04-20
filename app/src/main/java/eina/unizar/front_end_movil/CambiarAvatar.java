package eina.unizar.front_end_movil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CambiarAvatar extends AppCompatActivity {

    private static final int OPTION_OK = 0;
    private static final int OPTION_ATRAS = 1;

    private final static String COLOR_NARANJA =  "#FFA141";
    private ListView listaObjetos;
    private String[] nombres = {"Quitar Traje", "Poner traje", "Pirata"};


    private  Bitmap avatarDefinitivo;
    private static Bitmap bitmapAvatarStandar;
    protected int ALTURA_AVATAR;
    protected int ANCHO_AVATAR;

    protected Context c;

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
        c = this;
        getSupportActionBar().hide();
        imagenAvatar = (ImageView) findViewById(R.id.imageViewAvatar);


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

        Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos el bitmap del imageview (el que tendrá el usuario)
                BitmapDrawable bitmapOriginal = (BitmapDrawable) imagenAvatar.getDrawable();
                bitmapAvatarStandar = bitmapOriginal.getBitmap();

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

        listaObjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String)parent.getItemAtPosition(position);

                Toast.makeText(CambiarAvatar.this,clickedItem, Toast.LENGTH_SHORT).show();
                if(clickedItem.equals(nombres[0])){ //quitar traje -> debe crear avatar con traje y color
                    //Obtenemos el bitmap del imageview (el que tendrá el usuario)
                    BitmapDrawable bitmapOriginal = (BitmapDrawable) imagenAvatar.getDrawable();
                    Bitmap bitmapAvatarStandar = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.color_naranja);
                    Bitmap bitmapTraje = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.disfraz_traje);

                    DrawingAvatar drawingAvatar = new DrawingAvatar(c);
                    drawingAvatar.equiparItem(bitmapAvatarStandar,bitmapTraje,null);


                }else if(clickedItem.equals(nombres[1])){ //poner traje
                    Bitmap bitmapAvatarStandar = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.color_naranja);
                    Bitmap bitmapTraje = BitmapFactory.decodeResource(getBaseContext().getResources(),R.mipmap.disfraz_traje);

                    DrawingAvatar drawingAvatar = new DrawingAvatar(getBaseContext());
                    drawingAvatar.quitarItem(bitmapAvatarStandar,bitmapTraje);

                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ALTURA_AVATAR= imagenAvatar.getHeight();
            ANCHO_AVATAR = imagenAvatar.getWidth();
        }
    }

    public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {
            //pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
            System.out.println("Rojo: " + Color.red(pixels[x]) + " Verde:" + Color.green(pixels[x]) + " Azul:" + Color.blue(pixels[x]));
            if (checkforSimilarColors(Color.parseColor(COLOR_NARANJA),pixels[x]) < 120){
                pixels[x] = Color.argb(255,99,216,39);
            }

        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

    private double checkforSimilarColors(int a, int b){
        return Math.sqrt(Math.pow(Color.red(a) - Color.red(b), 2) + Math.pow(Color.blue(a) - Color.blue(b), 2) + Math.pow(Color.green(a) - Color.green(b), 2));
    }

      class DrawingAvatar extends View {

        //Bitmap sobre el que se va a dibujar
         private Bitmap bmOverlay;
         //CANVAS
         private Canvas canvas ;
         //Imageview

        public DrawingAvatar(Context context) {
            super(context);
            bmOverlay = Bitmap.createBitmap(ANCHO_AVATAR,ALTURA_AVATAR,Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bmOverlay);
            setWillNotDraw(false);

        }

        protected void onPoner() {
            System.err.println("Me llaman aqui");
            imagenAvatar.setImageBitmap(bmOverlay);
        }

         public void equiparItem(Bitmap c, Bitmap s, Bitmap r) {

            /*
             TOP: indica desde empieza desde arriba 0 lo cogerá desde el margen incial de la ImageView
                   si se aumenta el margen top la recortara de arriba hacia abajo (la estrecha)
              BOTTOM: le indica hasta donde tiene que coger el alto de la Imageview
              RIGHT: indica donde acaba o hasta donde va el margen derecho, tendrá que el total del ancho
                     del imageview para que abarque la imageview entera.
              LEFT: indica desde empieza desde el margen izquierdo del imageview, 0 empezará desde el incio
                    y si se aumenta ira acortandola en ancho
            * */
             System.err.println(c);
             Rect dest1 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR); // left,top,right,bottom
             canvas.drawBitmap(c, null, dest1, null);
             Rect dest2 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR);
             canvas.drawBitmap(s, null, dest2, null);
             onPoner();
                /*  Rect dest3 = new Rect(0, 0, 274, 290);
            comboImage.drawBitmap(r, null, dest3, null);*/


         }

         public void quitarItem(Bitmap c, Bitmap s) {

        /*
         TOP: indica desde empieza desde arriba 0 lo cogerá desde el margen incial de la ImageView
               si se aumenta el margen top la recortara de arriba hacia abajo (la estrecha)
          BOTTOM: le indica hasta donde tiene que coger el alto de la Imageview
          RIGHT: indica donde acaba o hasta donde va el margen derecho, tendrá que el total del ancho
                 del imageview para que abarque la imageview entera.
          LEFT: indica desde empieza desde el margen izquierdo del imageview, 0 empezará desde el incio
                y si se aumenta ira acortandola en ancho
        * */
             //comboImage.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
             limpiarCanvas();
             Rect dest1 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR); // left,top,right,bottom
             canvas.drawBitmap(c, null, dest1, null);
             onPoner();

         }

         public void limpiarCanvas(){
             Path path = new Path();
             Paint clearPaint = new Paint();
             clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
             canvas.drawRect(0, 0, 0, 0, clearPaint);
         }
    }

}


