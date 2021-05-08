package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Avatar.ItemAvatar;
import Avatar.MiddleWareAV;
import SessionManagement.GestorSesion;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarAvatar extends AppCompatActivity {

    private static final int OPTION_OK = 0;
    private static final int OPTION_ATRAS = 1;

    //Retrofit y gestor de Sesion
    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    //Listas y vectores que contienen los items del usuario y los items que lleva puesto
    private ArrayList<ItemAvatar> itemsUsuario;
    //Vector para los items equipados del usuario que posteriormente se renderizaran en el Canvas
    private Bitmap []itemsUsuarioEquipados = new Bitmap[4];

    //RecyclerView y recyclerviewAdapter(AvatarAdapter) para hacer de puente entre listaItem y los items de la BD y el layOutManager para asignar
    //los items al layout personalizado "avatar_cardview.xml"
    private RecyclerView listaItem;
    private AvatarAdapter listAdapter;
    private RecyclerView.LayoutManager mListManager;

    //Traduccion
    //Componentes 0:color 1:cabeza 2:cara 3:cuerpo
    private final HashMap<String,Integer> traduccion = new HashMap<String, Integer>(){{
        put("color",0);
        put("cabeza",1);
        put("cara",2);
        put("cuerpo",3);
    }};
    //Alto y Ancho del ImageView del Avatar
    protected int ALTURA_AVATAR;
    protected int ANCHO_AVATAR;
    //Variable para guardar el context (equivalente a getAplicationContext())
    protected Context c;
    //ImageView del Avatar
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
        retrofitInterface = APIUtils.getAPIService();

        imagenAvatar = (ImageView) findViewById(R.id.imageViewAvatar);

        //Llamada al método que inicializa la recyclerView con los items del usuario.
        fillData();
        //Cargamos el Avatar actual que el usuario tenga.
        cargarAvatar();


        Button okButton = (Button) findViewById((R.id.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiddleWareAV middleWareAV = MiddleWareAV.createMiddleWareAV(c);
                ArrayList<Integer> arraybooleanos = new ArrayList<>();
                ArrayList<String> arrayNombre = new ArrayList<>();
                for(int i=0 ; i < itemsUsuario.size(); i++){
                    arraybooleanos.add(itemsUsuario.get(i).isEquipped() ? 1 : 0);
                    arrayNombre.add(itemsUsuario.get(i).getIdItem());
                }
                middleWareAV.updateItemsEquipados(arraybooleanos,arrayNombre);
                BitmapDrawable drawable = (BitmapDrawable) imagenAvatar.getDrawable();
                Bitmap bitmapAVTR = drawable.getBitmap();
                middleWareAV.saveAvatar(bitmapAVTR);
                mostrarMensajeCarga();

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

    private void mostrarMensajeCarga() {
        // SweetAler para mostrar el usuario que está procesando la carga.
        final SweetAlertDialog pDialog = new SweetAlertDialog(CambiarAvatar.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setCancelable(false);
        pDialog.setTitleText("Guardando Cambios...");
        pDialog.show();
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
               pDialog.setTitleText("Listo");
               pDialog.setConfirmText("Ok").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                   @Override
                   public void onClick(SweetAlertDialog sweetAlertDialog) {
                       Arrays.fill(itemsUsuarioEquipados, null);
                       Intent intent = new Intent(c, PerfilUsuario.class);
                       startActivityForResult(intent, OPTION_OK);
                   }
               });


           }
       },2500);


    }

    private void loadItemsEquipped()  {

        for (final ItemAvatar i : itemsUsuario) {
                if (i.isEquipped()) {
                    Glide.with(c)
                            .asBitmap().load(i.getImagen())
                            .listener(new RequestListener<Bitmap>() {
                                          @Override
                                          public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, boolean isFirstResource) {
                                              return false;
                                          }

                                          @Override
                                          public boolean onResourceReady(Bitmap resource, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                              itemsUsuarioEquipados[traduccion.get(i.getTipo())] = resource;
                                              return false;
                                          }
                                      }
                            ).submit();
                }
            }
    }

    /**
     * Función que carga en el imageview la imagen que tenga el usuario.
     */
    private void cargarAvatar() {
      String imageUri = gestorSesion.getAvatarSession();
        Picasso.get().load(imageUri).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imagenAvatar);

    }

    /**
     * Función que se encarga de guardar en el arraylist ItemsUsuario , los items resultantes de la consulta
     * a la base de datos.
     */
    private void fillData() {

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
                        //Si se está usando la API en la nube pasarle image
                        String image = jsonObject.get("Imagen").getAsString().replaceAll("http://localhost:3060", "https://trivial-images.herokuapp.com");
                        //Si se está usando la API en local pasarle imagelocal
                        String imageLocal = jsonObject.get("Imagen").getAsString().replaceAll("localhost", "192.168.1.34");
                        itemsUsuario.add(new ItemAvatar(jsonObject.get("Nombre").getAsString(),
                                jsonObject.get("Tipo").getAsString(), image, jsonObject.get("equipado").getAsString().equals("1"),
                                jsonObject.get("iditem").getAsString()));
                    }
                    loadItemsEquipped();

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
            //Si el usuario no tiene ningún item mostrarle que no tienen ningún item. No debería entrar nunca ya que siempre tendrá la skin naranja, pero si se producen fallos en la base de datos
            // prevendrá bugs.
            if( listAdapter.getItemCount() == 0){
                Toast.makeText(this,"Todavia no tienes ningun item",Toast.LENGTH_LONG).show();
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
        boolean cambiarColor = itemsUsuario.get(position).getTipo().equals("color");
        boolean equipado = itemsUsuario.get(position).isEquipped();
        if(cambiarColor && equipado) {
            Toast.makeText(this,"No puedes desequiparte un color, si quieres cambiarlo selecciona otro diferente",Toast.LENGTH_SHORT).show();
            return;
        }else if(!equipado){
            for (ItemAvatar i : itemsUsuario){
                if(i.isEquipped() && i.getTipo().equals(itemsUsuario.get(position).getTipo())){
                    i.negateEquipped();
                    listAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        itemsUsuario.get(position).negateEquipped();
        listAdapter.notifyItemChanged(position);
        actualizarAvatarUsuario(itemsUsuario.get(position).getImagen(), itemsUsuario.get(position).getTipo(), itemsUsuario.get(position).isEquipped());
    }

    private void actualizarAvatarUsuario( final String urlImagen, final String tipo, final boolean equipped) {
        DrawingAvatar drawingAvatar = new DrawingAvatar(c);
        drawingAvatar.onUpdateItem(urlImagen,tipo,equipped);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ALTURA_AVATAR= imagenAvatar.getHeight();
            ANCHO_AVATAR = imagenAvatar.getWidth();
        }
    }

    class DrawingAvatar extends View {

        //Bitmap sobre el que se va a dibujar
         private Bitmap bmOverlay;
         //CANVAS
         private Canvas canvas ;


        public DrawingAvatar(Context context) {
            super(context);
            bmOverlay = Bitmap.createBitmap(ANCHO_AVATAR,ALTURA_AVATAR,Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bmOverlay);
            setWillNotDraw(false);

        }

        public void onPaint() {
            imagenAvatar.setImageBitmap(bmOverlay);
        }


        public void onUpdateItem(String urlSrc, final String tipo, final Boolean equipped) {

             Picasso.get().load(urlSrc).into(new Target() {
                  @Override
                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                      //Actualizamos el bitmap en funcion de si esta o no equipado
                      if(equipped) {
                          itemsUsuarioEquipados[traduccion.get(tipo)] = bitmap;
                      }else{
                          itemsUsuarioEquipados[traduccion.get(tipo)] = null;
                      }
                      //System.out.println(Arrays.toString(itemsUsuarioEquipados));
                      onUpdateCanvas();
                  }

                  @Override
                  public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        e.printStackTrace();
                  }

                  @Override
                  public void onPrepareLoad(Drawable placeHolderDrawable) {

                  }
              });



        }

        public void onUpdateCanvas() {
            onClearCanvas();
            for (int i = 0; i<itemsUsuarioEquipados.length; i++) {
                   /*
                     TOP: indica desde empieza desde arriba 0 lo cogerá desde el margen incial de la ImageView
                           si se aumenta el margen top la recortara de arriba hacia abajo (la estrecha)
                      BOTTOM: le indica hasta donde tiene que coger el alto de la Imageview
                      RIGHT: indica donde acaba o hasta donde va el margen derecho, tendrá que el total del ancho
                             del imageview para que abarque la imageview entera.
                      LEFT: indica desde empieza desde el margen izquierdo del imageview, 0 empezará desde el incio
                            y si se aumenta ira acortandola en ancho
                   **/
                if(itemsUsuarioEquipados[i] == null) continue;
                Rect rect = new Rect(0, 0, ANCHO_AVATAR
                        , ALTURA_AVATAR); //left,top,right,bottom
                canvas.drawBitmap(itemsUsuarioEquipados[i], null, rect, null);
            }
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
            Picasso.get().load(itemAvatar.getImagen()).placeholder(R.drawable.animacion_carga).into(holder.imageView);
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