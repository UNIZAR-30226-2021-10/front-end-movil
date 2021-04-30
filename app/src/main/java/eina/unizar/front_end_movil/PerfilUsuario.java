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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import SessionManagement.GestorSesion;

public class PerfilUsuario extends AppCompatActivity {

    private static final int OPTION_CERRAR_SESION = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_AJUSTES = 2;
    private static final int OPTION_PERFIL = 3;

    private ListView listaObjetos;
    private String[] nombres = {"Traje", "Médico", "Paragüas", "Estetoscopio", "Maletín",
            "Vestido", "Corbata", "Gafas", "Balon", "Sombrero"};


    private GestorSesion gestorSesion;

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

        listaObjetos = (ListView)findViewById(R.id.list);
        fillData();

        gestorSesion = new GestorSesion(PerfilUsuario.this);
        TextView usuario =  (TextView) findViewById(R.id.user_name);
        usuario.setText(gestorSesion.getSession());
        TextView email = (TextView) findViewById(R.id.user_email);
        email.setText(gestorSesion.getmailSession());
        TextView coins = (TextView) findViewById(R.id.user_coins);
        coins.setText(gestorSesion.getKEY_SESSION_COINS());
        TextView points = (TextView) findViewById(R.id.user_points);
        points.setText(gestorSesion.getpointsSession());


        ImageView perfilButton = (ImageView) findViewById(R.id.perfil_button);
        String imageUri = gestorSesion.getAvatarSession();
        Picasso.get().load(imageUri).fit()
                .error(R.drawable.ic_baseline_error_24)
                .placeholder(R.drawable.animacion_carga)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(perfilButton);


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
}


