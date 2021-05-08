package eina.unizar.front_end_movil;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.pedant.SweetAlert.*;

import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegistroUsuario extends AppCompatActivity {

    private static final int OPTION_SIGN_UP = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText nombre_usuario;
    private EditText email;
    private EditText password_new;
    private EditText password_new2;
    private ImageView avatar;

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
        setContentView(R.layout.registro_usuario);
        getSupportActionBar().hide();
        //Construimos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        //Edit text de nombre usuario
        nombre_usuario = (EditText) findViewById(R.id.nombre_usuario);
        //EDIT TEXT DE EMAIL
        email = (EditText) findViewById(R.id.email);
        //EDIT TEXT DE CONTRASEÑA NUEVA
        password_new = (EditText) findViewById(R.id.password_new);
        // EDIT TEXT DE REPETICION DE CONTRASEÑA NUEVA
        password_new2 = (EditText) findViewById(R.id.password_new2);
        avatar = (ImageView) findViewById(R.id.usuario_imagen);

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
                        //Comprobar email válido
                        if(comprobarEmail(email.getText().toString())){
                            handleRegister();
                        }else {
                            email.setError("El email es invalido, introduzca un email valido por ejemplo: pedro@gmail.com");
                        }
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

    /* TODO: coger foto del color naranja, renombrarla, subirla al servidor de imagenes en carpeta usuarios
       y meter el link en la base de datos
     */

    private void handleRegister() {

        HashMap<String,String> newUser = new HashMap<>();

        newUser.put("email",email.getText().toString());
        newUser.put("nickname",nombre_usuario.getText().toString());
        newUser.put("password",password_new.getText().toString());
        newUser.put("imagen", "http://localhost:3060/tienda/color_naranja.png");

        Call<JsonObject> call = retrofitInterface.executeSignUp(newUser);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    comprarNaranja();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(RegistroUsuario.this,SweetAlertDialog.SUCCESS_TYPE).setTitleText("Registrado Exitosamente!")
                                    .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent intent = new Intent (RegistroUsuario.this, MenuPrincipal.class);
                                            startActivityForResult(intent,OPTION_SIGN_UP);
                                        }
                                    }).show();
                        }

                    },500);
                } else if (response.code() == 400 ) {
                    Toast.makeText(RegistroUsuario.this, "Ya existe un usuario con ese nickname, introduzca otro.", Toast.LENGTH_LONG).show();
                    nombre_usuario.getText().clear();
                } else if (response.code() == 410) {
                    Toast.makeText(RegistroUsuario.this, "Ya existe un usuario con ese email, compruebe las creedenciales.", Toast.LENGTH_LONG).show();
                    email.getText().clear();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegistroUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean comprobarEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private void comprarNaranja() {

        HashMap<String,String> buyOrange = new HashMap<>();

        buyOrange.put("email",email.getText().toString());
        buyOrange.put("nombreObjeto","Naranja");

        Call<JsonObject> call = retrofitInterface.comprarNaranja(buyOrange);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                    System.out.println("TODO OK");
                } else{
                    Toast.makeText(RegistroUsuario.this, "No se ha podido insertar en tabla tiene", Toast.LENGTH_LONG).show();
                    nombre_usuario.getText().clear();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegistroUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}


