package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import java.util.HashMap;
import SessionManagement.GestorSesion;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjustesUsuario extends AppCompatActivity {

    private static final int OPTION_GUARDAR = 0;
    private static final int OPTION_ATRAS = 1;
    private static final int OPTION_ELIMINARCUENTA = 2;


    private EditText nombre_usuario;
    private TextView email;
    private EditText password_new;
    private EditText password_new2;

    private GestorSesion gestorSesion;
    
    private RetrofitInterface retrofitInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_usuario);
        getSupportActionBar().hide();

        //Construimos el objeto retrofit
        retrofitInterface = APIUtils.getAPIService();

        gestorSesion = new GestorSesion(AjustesUsuario.this);
        //Edit text de nombre usuario
        nombre_usuario = (EditText) findViewById(R.id.nombre_usuario);
        nombre_usuario.setText(gestorSesion.getSession());
        //TEXT VIEW DEL EMAIL
        email = (TextView) findViewById(R.id.email);
        email.setText(gestorSesion.getmailSession());
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
                } else if(password_new.getText().toString().isEmpty()){
                    password_new.setError("El campo no puede estar vacío");
                } else if(password_new2.getText().toString().isEmpty()){
                    password_new2.setError("El campo no puede estar vacío");
                } else {
                    // COMPROBAR CONTRASEÑA es igual en ambos campos
                    if (password_new.getText().toString().equals(password_new2.getText().toString())) {
                            handleSaveChanges();
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

        //Botón de eliminar cuenta
        Button eliminarCuentaButton = (Button) findViewById(R.id.eliminarCuenta);
        eliminarCuentaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEliminarCuenta();

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
                    new SweetAlertDialog(AjustesUsuario.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Datos guardados exitosamente!").setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            gestorSesion.updateNickname(nombre_usuario.getText().toString());
                            Intent intent = new Intent(AjustesUsuario.this, PerfilUsuario.class);
                            startActivityForResult(intent, OPTION_GUARDAR);
                        }
                    }).show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AjustesUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleEliminarCuenta() {

       new SweetAlertDialog(AjustesUsuario.this,SweetAlertDialog.WARNING_TYPE)
               .setTitleText("¿Estás seguro?")
               .setContentText("Tu cuenta será borrada permanentemente.")
               .setConfirmText("Sí, borrarla")
               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                   @Override
                   public void onClick(SweetAlertDialog sweetAlertDialog) {
                      eliminarUsuario();
                   }
               })
               .setCancelButton("Cancelar", new SweetAlertDialog.OnSweetClickListener() {
                   @Override
                   public void onClick(SweetAlertDialog sweetAlertDialog) {
                       sweetAlertDialog.dismissWithAnimation();
                   }
               }).show();
    }

    private void eliminarUsuario(){
        HashMap<String, String> dropUser = new HashMap<>();
        dropUser.put("email", email.getText().toString());
        Call<JsonObject> call = retrofitInterface.executeDropUser(dropUser);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200){
                    gestorSesion.removeSession();
                    Intent intent = new Intent(AjustesUsuario.this,MenuPrincipal.class);
                    startActivityForResult(intent,OPTION_ELIMINARCUENTA);
                }else{
                    Toast.makeText(AjustesUsuario.this,"Se ha producido una situación inesperada.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AjustesUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}


