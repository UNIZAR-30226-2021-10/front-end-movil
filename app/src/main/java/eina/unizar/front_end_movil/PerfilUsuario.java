package eina.unizar.front_end_movil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    private String ip = "192.168.0.26"; //TODO: cambiar para poder usarlo todos

    private static final int OPTION_CERRAR_SESION = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_AJUSTES = 2;
    private static final int OPTION_PERFIL = 3;

    RecyclerView recycler_view;

    ArrayList<MainModel> mainmodel;
    MainAdapter mainAdapter;
    ArrayList<String> imagenesCosas = new ArrayList<>();
    ArrayList<String> nombreCosas = new ArrayList<>();
    ArrayList<Integer> precioCosas = new ArrayList<>();

    private ListView listaObjetos;
    private String[] nombres = {"Traje", "Médico", "Paragüas", "Estetoscopio", "Maletín",
            "Vestido", "Corbata", "Gafas", "Balon", "Sombrero"};

    private GestorSesion gestorSesion;
    private RetrofitInterface retrofitInterface;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);
        getSupportActionBar().hide();

        retrofitInterface = APIUtils.getAPIService();

        //listaObjetos = (ListView)findViewById(R.id.list);
        //fillData();
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

        obtenerImagenes();
        //inicializar();

        Button ajustesButton = (Button) findViewById((R.id.ajustes));
        ajustesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AjustesUsuario.class);
                startActivityForResult(intent, OPTION_AJUSTES);
            }
        });

        // Botón de sign out
        Button perfilButton = (Button) findViewById(R.id.perfil_button);
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

    public void inicializar(){
        mainmodel = new ArrayList<>();
        for(int i = 0; i < nombreCosas.size(); i++){
            MainModel model = new MainModel(imagenesCosas.get(i), nombreCosas.get(i), precioCosas.get(i));
            mainmodel.add(model);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false);

        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        mainAdapter = new MainAdapter(this, mainmodel);
        recycler_view.setAdapter(mainAdapter);
    }

    private void obtenerImagenes() {
        HashMap<String,String> prueba = new HashMap<>();
        prueba.put("email",gestorSesion.getmailSession());
        Call<JsonArray> call = retrofitInterface.getObjectsUser(prueba);
        call.enqueue(new Callback<JsonArray>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200) {

                    JsonArray jsonObject = response.body().getAsJsonArray();
                    for(JsonElement j : jsonObject){
                        //System.out.println(j);
                        JsonObject prueba = j.getAsJsonObject();
                        String imagen = prueba.get("Imagen").getAsString();
                        imagen = imagen.replaceAll("localhost", ip);
                        precioCosas.add(prueba.get("Precio").getAsInt());
                        nombreCosas.add(prueba.get("Nombre").getAsString());
                        imagenesCosas.add(imagen);
                        //System.out.println(prueba);
                    }
                    inicializar();
                } else{
                    Toast.makeText(PerfilUsuario.this, "No se han podido obtener los objetos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Toast.makeText(PerfilUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}


