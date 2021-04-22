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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import Avatar.DrawAVMethods;
import Avatar.ItemAvatar;
import SessionManagement.GestorSesion;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarAvatar extends AppCompatActivity {

    private static final int OPTION_OK = 0;
    private static final int OPTION_ATRAS = 1;

    private final static String COLOR_NARANJA =  "#FFA141";

    //Retrofit y gestor de Sesion
    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    //Listas y vectores que contienen los items del usuario y los items que lleva puesto
    private ArrayList<ItemAvatar> itemsUsuario;
    //TODO: Crear vector para los items equipados del usuario que posteriormente se renderizaran en el Canvas
    private ArrayList<Bitmap> itemsUsuarioEquipados;

    //RecyclerView y recyclerviewAdapter(AvatarAdapter) para hacer de puente entre listaItem y los items de la BD y el layOutManager para asignar
    //los items al layout personalizado "avatar_cardview.xml"
    private RecyclerView listaItem;
    private AvatarAdapter listAdapter;
    private RecyclerView.LayoutManager mListManager;

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
        //Construimos el gestor de Sesion y el retrofit.
        gestorSesion = new GestorSesion(CambiarAvatar.this);
        retrofitInterface = APIUtils.getAPIServiceImages();

        imagenAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        //Cargamos el Avatar actual que el usuario tenga.
        cargarAvatar();
        //Llamada al método que inicializa la recyclerView con los items del usuario.
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

    private void cargarAvatar() {
        String imageUri = gestorSesion.getAvatarSession();
        Picasso.get().load(imageUri).fit().centerCrop()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .into(imagenAvatar);
    }

    /**
     * Función que se encarga de guardar en el arraylist ItemsUsuario , los items resultantes de la consulta
     * a la base de datos.
     */
    private void fillData() {
        // TODO: Por ahora así hasta que decidamos como está la BD
        itemsUsuario = new ArrayList<>();
        HashMap<String,String> hashPerfilUsuario = new HashMap<>();
        hashPerfilUsuario.put("email",gestorSesion.getmailSession());
        //Creamos la llamada que hace la petición POST
        Call<JsonArray> call = retrofitInterface.getUserItems(hashPerfilUsuario);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.body() != null) {
                    JsonArray jsonArray = response.body().getAsJsonArray();
                    for (JsonElement json : jsonArray) {
                        JsonObject jsonObject = json.getAsJsonObject();
                        String image = jsonObject.get("Imagen").getAsString().replaceAll("http://localhost:3060", "https://trivial-images.herokuapp.com");
                        itemsUsuario.add(new ItemAvatar(jsonObject.get("Nombre").getAsString(),
                                jsonObject.get("Tipo").getAsString(), image, jsonObject.get("equipado").getAsString().equals("1")));
                    }
                }
                cargarRecyclerView();

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(CambiarAvatar.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /** Función que se encarga de cargar en el recyclerView "listaItem" todos los productos que el
     *  usuario tiene. Además gestiona los clicks en el recyclerView.
     */
    private void cargarRecyclerView() {
            listaItem = findViewById(R.id.list_items);
            listaItem.setHasFixedSize(true);
            mListManager = new LinearLayoutManager(this);
            listAdapter = new AvatarAdapter(itemsUsuario);
            //Si el usuario no tiene ningún item mostrarle que no tienen ningún item.
            if( listAdapter.getItemCount() == 0){
                Toast.makeText(this,"Todavia no tienes items",Toast.LENGTH_LONG).show();
                return;
            }
            listaItem.setLayoutManager(mListManager);
            listaItem.setAdapter(listAdapter);

            listAdapter.setOnItemClickListener(new AvatarAdapter.OnItemClickListener(){

                @Override
                public void onItemClick(int position) {
                    changeEquiparItem(position);
                }
            });
    }

    private void changeEquiparItem(int position) {
        itemsUsuario.get(position).negateEquipped();
        listAdapter.notifyItemChanged(position);
        //Toast.makeText(this,String.valueOf(itemsUsuario.get(position).isEquipped()),Toast.LENGTH_LONG).show();
        actualizarAvatarUsuario(itemsUsuario.get(position).isEquipped(),itemsUsuario.get(position).getImagen());
    }

    private void actualizarAvatarUsuario(boolean isEquipped, String urlImagen) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ALTURA_AVATAR= imagenAvatar.getHeight();
            ANCHO_AVATAR = imagenAvatar.getWidth();
        }
    }

   /* public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
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
    }*/

   /* private double checkforSimilarColors(int a, int b){
        return Math.sqrt(Math.pow(Color.red(a) - Color.red(b), 2) + Math.pow(Color.blue(a) - Color.blue(b), 2) + Math.pow(Color.green(a) - Color.green(b), 2));
    }*/

    class DrawingAvatar extends View {

        //Bitmap sobre el que se va a dibujar
         private Bitmap bmOverlay;
         //CANVAS
         private Canvas canvas ;
         //Vector con los items equipados
         private  Vector<DrawAVMethods> itemsEquipados;

        public DrawingAvatar(Context context) {
            super(context);
            bmOverlay = Bitmap.createBitmap(ANCHO_AVATAR,ALTURA_AVATAR,Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bmOverlay);
            itemsEquipados = new Vector<>();
            setWillNotDraw(false);

        }

        public void onPaint() {
            imagenAvatar.setImageBitmap(bmOverlay);
        }

        public void onUpdateItem() {

            for ( Object o : itemsEquipados){

            }
               /*
             TOP: indica desde empieza desde arriba 0 lo cogerá desde el margen incial de la ImageView
                   si se aumenta el margen top la recortara de arriba hacia abajo (la estrecha)
              BOTTOM: le indica hasta donde tiene que coger el alto de la Imageview
              RIGHT: indica donde acaba o hasta donde va el margen derecho, tendrá que el total del ancho
                     del imageview para que abarque la imageview entera.
              LEFT: indica desde empieza desde el margen izquierdo del imageview, 0 empezará desde el incio
                    y si se aumenta ira acortandola en ancho
            * */
            Bitmap c = BitmapFactory.decodeResource(getResources(),R.mipmap.color_naranja);
            Bitmap s = BitmapFactory.decodeResource(getResources(),R.mipmap.color_naranja);
            Rect dest1 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR); // left,top,right,bottom
            canvas.drawBitmap(c, null, dest1, null);
            Rect dest2 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR);
            canvas.drawBitmap(s, null, dest2, null);
            onPaint();

        }

        public void onRemoveItem() {
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
            onClearCanvas();
            Bitmap c = BitmapFactory.decodeResource(getResources(),R.mipmap.color_naranja);
            Rect dest1 = new Rect(0, 0, ANCHO_AVATAR, ALTURA_AVATAR); // left,top,right,bottom
            canvas.drawBitmap(c, null, dest1, null);
            onPaint();
        }

        public void onClearCanvas() {
            Path path = new Path();
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(0, 0, 0, 0, clearPaint);
        }
    }

    public static class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>{

        private ArrayList<ItemAvatar> mList;
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick (int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public static class AvatarViewHolder extends RecyclerView.ViewHolder{

            public ImageView imageView;
            public TextView TextNombre;
            public TextView TextTipo;
            public TextView TextEquipado;

            public AvatarViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imagen_item);
                TextNombre = itemView.findViewById(R.id.first_tview);
                TextTipo = itemView.findViewById(R.id.second_tview);
                TextEquipado = itemView.findViewById(R.id.text_equipped);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null){
                            int position = getAdapterPosition();
                            if( position != RecyclerView.NO_POSITION){
                                listener.onItemClick(position);
                            }
                        }
                    }
                });
            }
        }

        public AvatarAdapter(ArrayList<ItemAvatar> itemsAv){
            mList = itemsAv;
        }

        @NonNull
        @Override
        public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_cardview,parent,false);
            AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view, mListener);
            return avatarViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
            ItemAvatar itemAvatar = mList.get(position);
            Picasso.get().load(itemAvatar.getImagen()).into(holder.imageView);
            holder.TextNombre.setText(itemAvatar.getNombre());
            holder.TextTipo.setText(itemAvatar.getTipo());
            if(itemAvatar.isEquipped()){
                holder.TextEquipado.setText(R.string.equipado);
                holder.TextEquipado.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_check_24,0);
            }else{
                holder.TextEquipado.setText(R.string.Noequipado);
                holder.TextEquipado.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }




}


/*listaObjetos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });*/