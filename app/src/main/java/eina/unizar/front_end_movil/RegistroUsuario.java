package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroUsuario extends AppCompatActivity {

    private static final int OPTION_SIGN_UP = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText nombre_usuario;
    private EditText email;
    private EditText password_new;
    private EditText password_new2;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_usuario);
        getSupportActionBar().hide();

        //Edit text de nombre usuario
        nombre_usuario = (EditText) findViewById(R.id.nombre_usuario);
        //EDIT TEXT DE EMAIL
        email = (EditText) findViewById(R.id.email);
        //EDIT TEXT DE CONTRASEÑA NUEVA
        password_new = (EditText) findViewById(R.id.password_new);
        // EDIT TEXT DE REPETICION DE CONTRASEÑA NUEVA
        password_new2 = (EditText) findViewById(R.id.password_new2);

        // Botón de sign up
        Button signUpButton = (Button) findViewById(R.id.confirmar);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobar que están todos los campos rellenados
                if(nombre_usuario.getText().toString().isEmpty()){
                    nombre_usuario.setError("El campo no puede estar vacío");
                } else if(email.getText().toString().isEmpty()){
                    email.setError("El campo no puede estar vacío");
                } else if(password_new.getText().toString().isEmpty()){
                    password_new.setError("El campo no puede estar vacío");
                } else if(password_new2.getText().toString().isEmpty()){
                    password_new2.setError("El campo no puede estar vacío");
                } else {
                    // COMPROBAR CONTRASEÑA es igual en ambos campos
                    if (password_new.getText().toString().equals(password_new2.getText().toString())) {
                        Intent intent = new Intent(v.getContext(), MenuPrincipal.class);
                        startActivityForResult(intent, OPTION_SIGN_UP);
                    } else {
                        // mensaje de error
                        password_new2.setError("Las contraseñas no son iguales");
                    }
                }
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MenuPrincipal.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }
}


