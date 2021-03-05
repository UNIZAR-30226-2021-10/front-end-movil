package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuario extends AppCompatActivity {

    private static final int OPTION_CERRAR_SESION = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_AJUSTES = 2;

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

        Button ajustesButton = (Button) findViewById((R.id.ajustes));
        ajustesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AjustesUsuario.class);
                startActivityForResult(intent, OPTION_AJUSTES);
            }
        });

        // Botón de sign out
       /* Button signOutButton = (Button) findViewById(R.id.signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuPrincipal.class);
                startActivityForResult(intent, OPTION_CERRAR_SESION);
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
        });*/
    }
}


