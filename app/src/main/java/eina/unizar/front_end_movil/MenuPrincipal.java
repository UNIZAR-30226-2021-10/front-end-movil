package eina.unizar.front_end_movil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;

import SessionManagement.GestorSesion;
import SessionManagement.User;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuPrincipal extends AppCompatActivity {

    /**
     * Opciones de los dos botones del menú princiapl
     */
    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_PASSWORD = 1;
    private static final int OPTION_REGISTRO = 2;

    private EditText usuario;
    private EditText password;

    private RetrofitInterface retrofitInterface;


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

        //Construirmos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();
        //EDIT TEXT DE USUARIO
        usuario = (EditText) findViewById(R.id.nombre_usuario);
        //EDIT TEXT DE CONTRASEÑA
        password = (EditText) findViewById(R.id.texto_contasenya);

        // Probar mensajes de error para contraseña -- antes de base de datos
        //final String contrasenyaCorrecta = "1234";
        //final String usuarioCorrecto = "w";

        // Botón de acceder/iniciar sesión
        Button accederButton = (Button) findViewById(R.id.acceder);
        accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validamos el login del usuario
                handleLogin();
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

    @Override
    protected void onStart() {
        super.onStart();
        GestorSesion gestorSesion = new GestorSesion(MenuPrincipal.this);
        //Si el usuario ya está loggeado lo llevamos directamente a la pantalla de juego.
        if (!gestorSesion.getSession().equals(String.valueOf(1))){
            Intent intent = new Intent (MenuPrincipal.this, DecisionJuego.class);
            startActivityForResult(intent, OPTION_ACCEDER);
        }
    }

    private void handleLogin() {

        HashMap<String, String> newUser = new HashMap<>();
        newUser.put("nickname",usuario.getText().toString());
        newUser.put("password",password.getText().toString());

        Call<JsonObject> call = retrofitInterface.executeLogin(newUser);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200){

                    JsonObject jsonObject = response.body().getAsJsonObject("email");
                    // String test = jsonObject.get("email").getAsString();
                    //Creamos un usuario con la información procedente de la base de datos
                    User usuario =  new User(jsonObject.get("email").getAsString(),jsonObject.get("nickname").getAsString()
                                            ,jsonObject.get("puntos").getAsString(),jsonObject.get("monedas").getAsString());

                    GestorSesion gestorSesion = new GestorSesion(MenuPrincipal.this);
                    gestorSesion.saveSession(usuario);
                    Intent intent = new Intent (MenuPrincipal.this, DecisionJuego.class);
                    startActivityForResult(intent, OPTION_ACCEDER);

                }else if(response.code() == 400){
                    Toast.makeText( MenuPrincipal.this, "Contraseña o usuario incorrecto.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText( MenuPrincipal.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}


