package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CambiarPassword extends AppCompatActivity {

    private static final int OPTION_CONFIRMAR = 0;
    private static final int OPTION_ATRAS = 1;

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
        setContentView(R.layout.cambiar_password);
        getSupportActionBar().hide();

        //EDIT TEXT DE EMAIL
        email = (EditText) findViewById(R.id.email);
        //EDIT TEXT DE CONTRASEÑA NUEVA
        password_new = (EditText) findViewById(R.id.password_new);
        // EDIT TEXT DE REPETICION DE CONTRASEÑA NUEVA
        password_new2 = (EditText) findViewById(R.id.password_new2);

        // Botón de confirmar
        Button confirmarButton = (Button) findViewById(R.id.confirmar);
        /*confirmarButton.setOnClickListener(new View.OnClickListener() {
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
        });*/

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        /*atrasButton.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }
}


