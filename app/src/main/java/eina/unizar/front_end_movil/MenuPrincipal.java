package eina.unizar.front_end_movil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MenuPrincipal extends AppCompatActivity {

    /**
     * Opciones de los dos botones del menú princiapl
     */
    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_PASSWORD = 1;
    private static final int OPTION_REGISTRO = 2;

    private EditText usuario;
    private EditText password;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);
        getSupportActionBar().hide();

        //EDIT TEXT DE USUARIO
        usuario = (EditText) findViewById(R.id.nombre_usuario);
        //EDIT TEXT DE CONTRASEÑA
        password = (EditText) findViewById(R.id.texto_contasenya);


        // Probar mensajes de error para contraseña
        final String contrasenyaCorrecta = "1234";
        final String usuarioCorrecto = "w";

        // Botón de acceder/iniciar sesión
        Button accederButton = (Button) findViewById(R.id.acceder);
        accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPROBAR CONTRASEÑA
                if( usuarioCorrecto.equals(usuario.getText().toString()) && contrasenyaCorrecta.equals(password.getText().toString())){
                        Intent intent = new Intent (v.getContext(), DecisionJuego.class);
                        startActivityForResult(intent, OPTION_ACCEDER);
                }else{
                    // mensaje de error
                    password.setError("El usuario o contraseña son incorrectos");
                }
            }
        });

        // Botón de olvidar la contraseña
        Button passwordButton = (Button) findViewById(R.id.password);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), CambiarPassword.class);
                startActivityForResult(intent, OPTION_PASSWORD);
            }
        });

        // Botón de registrarse
        Button registroButton = (Button) findViewById(R.id.registro);
        registroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RegistroUsuario.class);
                startActivityForResult(intent, OPTION_REGISTRO);
            }
        });
    }
}


