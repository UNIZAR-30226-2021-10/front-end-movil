package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import SessionManagement.GestorSesion;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class enviarCodigoVerificacion extends AppCompatActivity{
    private static final int OPTION_CONFIRMAR = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText email;
    private EditText codigo_verificacion;
    private EditText new_password1;
    private EditText new_password2;

    private GestorSesion gestorSesion;
    private RetrofitInterface retrofitInterface;

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
        setContentView(R.layout.enviar_codigo_verificacion);
        getSupportActionBar().hide();

        //Construimos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(enviarCodigoVerificacion.this);

        //EDIT TEXT DEL EMAIL
        email = (EditText) findViewById(R.id.email);
        //EDIT TEXT DEL CODIGO DE VERIFICACION
        codigo_verificacion = (EditText) findViewById(R.id.codigo_verificacion);
        //EDIT TEXT DE LA NUEVA CONTRASEÑA
        new_password1 = (EditText) findViewById(R.id.password_new);
        //EDIT TEXT DE LA NUEVA CONTRASEÑA2
        new_password2 = (EditText) findViewById(R.id.password_new2);

        //Boton de confirmar
        Button confirmarButton = (Button) findViewById(R.id.confirmar);
        confirmarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobar que están todos los campos rellenados
                if(email.getText().toString().isEmpty()){
                    email.setError("El campo no puede estar vacío");
                }else if(codigo_verificacion.getText().toString().isEmpty()){
                    codigo_verificacion.setError("El campo no puede estar vacío");
                } else if(new_password1.getText().toString().isEmpty()){
                    new_password1.setError("El campo no puede estar vacío");
                } else if(new_password2.getText().toString().isEmpty()){
                    new_password2.setError("El campo no puede estar vacío");
                } else {
                    if(comprobarEmail(email.getText().toString())){
                        // COMPROBAR CONTRASEÑA es igual en ambos campos
                        if (new_password1.getText().toString().equals(new_password2.getText().toString())) {
                            handleCambiarContrasenya();
                            Intent intent = new Intent (v.getContext(), MenuPrincipal.class);
                            startActivityForResult(intent, OPTION_CONFIRMAR);
                        } else {
                            // mensaje de error
                            new_password2.setError("Las contraseñas no son iguales");
                        }
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
                Intent intent = new Intent(v.getContext(), CambiarPassword.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }

    private void handleCambiarContrasenya() {

        HashMap<String, String> changePassword = new HashMap<>();

        changePassword.put("email", email.getText().toString());
        changePassword.put("password", new_password1.getText().toString());

        Call<JsonObject> call = retrofitInterface.executeChangePassword(changePassword);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(enviarCodigoVerificacion.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Contraseña modificada exitosamente!")
                                    .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent intent = new Intent(enviarCodigoVerificacion.this, MenuPrincipal.class);
                                            startActivityForResult(intent, OPTION_CONFIRMAR);
                                        }
                                    }).show();
                        }

                    }, 500);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(enviarCodigoVerificacion.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean comprobarEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

}
