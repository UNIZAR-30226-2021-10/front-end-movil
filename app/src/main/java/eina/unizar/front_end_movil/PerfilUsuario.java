package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
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

public class PerfilUsuario extends AppCompatActivity {

    private static final int OPTION_CERRAR_SESION = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_AJUSTES = 2;
    private static final int OPTION_PERFIL = 3;

    private RecyclerView recycler_view;

    private ArrayList<MainModel> mainmodel;
    private MainAdapter mainAdapter;
    private ArrayList<String> imagenesCosas = new ArrayList<>();
    private ArrayList<String> nombreCosas = new ArrayList<>();

    private GestorSesion gestorSesion;
    private RetrofitInterface retrofitInterface;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();

        //listaObjetos = (ListView)findViewById(R.id.list);
        recycler_view = findViewById(R.id.recyclerview);

        gestorSesion = new GestorSesion(PerfilUsuario.this);
        TextView usuario =  (TextView) findViewById(R.id.user_name);
        usuario.setText(gestorSesion.getSession());
        TextView email = (TextView) findViewById(R.id.user_email);
        email.setText(gestorSesion.getmailSession());
        TextView coins = (TextView) findViewById(R.id.user_coins);
        coins.setText(gestorSesion.getKEY_SESSION_COINS());
        TextView points = (TextView) findViewById(R.id.user_points);
        points.setText(gestorSesion.getpointsSession());
        //Cargamos los items del usuario
        obtenerImagenes();
        //Cargamos la foto del usuario
        ImageView perfilButton = (ImageView) findViewById(R.id.perfil_button);
        cargarImagenUsuario(perfilButton);



        Button ajustesButton = (Button) findViewById((R.id.ajustes));
        ajustesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AjustesUsuario.class);
                startActivityForResult(intent, OPTION_AJUSTES);
            }
        });


        perfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CambiarAvatar.class);
                startActivityForResult(intent, OPTION_PERFIL);
            }
        });

        // Botón de sign out
       Button signOutButton = (Button) findViewById(R.id.signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogOut();
            }
        });

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

    private void handleLogOut() {

        gestorSesion.removeSession();
        Intent intent = new Intent(PerfilUsuario.this, MenuPrincipal.class);
        startActivityForResult(intent, OPTION_CERRAR_SESION);
    }


    public void inicializar(){
        mainmodel = new ArrayList<>();
        for(int i = 0; i < nombreCosas.size(); i++){
            MainModel model = new MainModel(imagenesCosas.get(i), nombreCosas.get(i), 0);
            mainmodel.add(model);
        }
        LinearLayoutManager layoutManager = new GridLayoutManager(this,4,
                GridLayoutManager.VERTICAL,false);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        mainAdapter = new MainAdapter(this, mainmodel);
        recycler_view.setAdapter(mainAdapter);
    }

    private void obtenerImagenes() {
        HashMap<String,String> prueba = new HashMap<>();
        prueba.put("email",gestorSesion.getmailSession());
        Call<JsonArray> call = retrofitInterface.getUserItems(prueba);
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        JsonObject prueba = j.getAsJsonObject();
                        String imagen = prueba.get("Imagen").getAsString();
                        String imagenNube = imagen.replaceAll("http://localhost:3060", "https://trivial-images.herokuapp.com");
                        nombreCosas.add(prueba.get("Nombre").getAsString());
                        imagenesCosas.add(imagenNube);
                    }
                    inicializar();
                } else{
                    Toast.makeText(PerfilUsuario.this, "No tienes objetos comprados", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(PerfilUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void cargarImagenUsuario(ImageView perfilButton){
        String imageUri = gestorSesion.getAvatarSession();
        Picasso.get().load(imageUri).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(perfilButton);
    }
}


