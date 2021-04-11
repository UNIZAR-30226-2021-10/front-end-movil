package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjustesUsuario extends AppCompatActivity {

    private static final int OPTION_GUARDAR = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText nombre_usuario;
    private EditText email;
    private EditText password_new;
    private EditText password_new2;

    //MARTA
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
    ); //HASTA AQUI

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_usuario);
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
        Button signUpButton = (Button) findViewById(R.id.signup);
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
                        Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
                        startActivityForResult(intent, OPTION_GUARDAR);
                        //editado por Marta
                        //Comprobar email válido
                        if(comprobarEmail(email.getText().toString())){
                            handleSaveChanges();
                        }else {
                            email.setError("El email es invalido, introduzca un email valido por ejemplo: pedro@gmail.com");
                        }//hasta aquí
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
                Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
                startActivityForResult(intent, OPTION_ATRAS);
            }
        });
    }

    private void handleSaveChanges() {

        HashMap<String, String> updateUser = new HashMap<>();

        updateUser.put("email", email.getText().toString());
        updateUser.put("nickname", nombre_usuario.getText().toString());
        updateUser.put("password", password_new.getText().toString());

        Call<JsonObject> call = retrofitInterface.executeSaveChanges(updateUser);
        call.enqueue(new Callback<JsonObject>() {
            //Gestionamos la respuesta de la llamada a post
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.code() == 200) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(AjustesUsuario.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Datos guardados exitosamente!")
                                    .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent intent = new Intent(AjustesUsuario.this, PerfilUsuario.class);
                                            startActivityForResult(intent, OPTION_GUARDAR);
                                        }
                                    }).show();
                        }

                    }, 500);
                } else if (response.code() == 410) {
                    Toast.makeText(AjustesUsuario.this, "Ya existe un usuario con ese email, compruebe las creedenciales.", Toast.LENGTH_LONG).show();
                    email.getText().clear();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AjustesUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean comprobarEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}


