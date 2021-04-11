package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import database_wrapper.RetrofitInterface;

public class CambiarPassword extends AppCompatActivity {

    private static final int OPTION_ENVIAR_CORREO = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText email;

    private RetrofitInterface retrofitInterface;
    //REGEX para comprobar el email
    private  final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_password);
        getSupportActionBar().hide();

        //EDIT TEXT DE EMAIL
        email = (EditText) findViewById(R.id.email);

        // Botón de confirmar
        Button confirmarButton = (Button) findViewById(R.id.confirmar);
        confirmarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPROBAR que el campo del email es no vacio
                if(email.getText().toString().isEmpty()) {
                    email.setError("El campo no puede estar vacío");
                }
                else{
                    if(comprobarEmail(email.getText().toString())){
                        //handleEnviarCorreo();
                        Intent intent = new Intent (v.getContext(), enviarCodigoVerificacion.class);
                        startActivityForResult(intent, OPTION_ENVIAR_CORREO);
                    }else {
                        email.setError("El email es invalido, introduzca un email valido por ejemplo: pedro@gmail.com");
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
    private boolean comprobarEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}


