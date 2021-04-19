package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import SessionManagement.GestorSesion;
import cn.pedant.SweetAlert.SweetAlertDialog;
import database_wrapper.APIUtils;
import database_wrapper.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjetoTienda extends AppCompatActivity {

    private static final int OPTION_COMPRAR = 0;

    private ImageView image;
    private TextView monedas_usuario;
    private TextView nombre_objeto;
    private TextView precio_objeto;

    private RetrofitInterface retrofitInterface;
    private GestorSesion gestorSesion;

    private String nombreObjeto;
    private String precioObjeto;
    private int precio;
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.objeto_tienda);
        getSupportActionBar().hide();

        gestorSesion = new GestorSesion(ObjetoTienda.this);
        retrofitInterface = APIUtils.getAPIServiceImages();

        Bundle extras = getIntent().getExtras();
        String imagen = extras.getString("Imagen");
        nombreObjeto = extras.getString("Nombre");
        precio = extras.getInt("Precio");
        String monedas = extras.getString("Monedas");
        precioObjeto = String.valueOf(precio);

        image = (ImageView) findViewById(R.id.imagenObjeto);
        monedas_usuario = (TextView) findViewById(R.id.monedas_usuario);
        nombre_objeto = (TextView) findViewById(R.id.nombre_objeto);
        precio_objeto = (TextView) findViewById(R.id.precio_objeto);

        Picasso.get().load(imagen).into(image);
        monedas_usuario.setText(monedas);
        nombre_objeto.setText(nombreObjeto);
        precio_objeto.setText(precioObjeto);

        final boolean ok = hayMonedasSuficientes();

        Button comprarButton = (Button) findViewById((R.id.comprar));
        comprarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ok) {
                        comprarObjeto();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new SweetAlertDialog(ObjetoTienda.this,SweetAlertDialog.SUCCESS_TYPE).setTitleText("¡Comprado correctamente!")
                                        .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                /*Intent intent = new Intent(ObjetoTienda.this, PantallaTienda.class);
                                                startActivityForResult(intent, OPTION_COMPRAR);*/
                                                finish();
                                            }
                                        }).show();
                            }

                        },500);
                    } else{
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new SweetAlertDialog(ObjetoTienda.this,SweetAlertDialog.ERROR_TYPE).setTitleText("¡No tiene suficientes monedas para comprar este objeto!")
                                        .setConfirmButton("Vale", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                /*Intent intent = new Intent(ObjetoTienda.this, PantallaTienda.class);
                                                startActivityForResult(intent, OPTION_COMPRAR);*/
                                                finish();
                                            }
                                        }).show();
                            }
                        },500);
                    }
                }
        });



        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void comprarObjeto(){
        HashMap<String,String> object = new HashMap<>();

        object.put("nombreObjeto",nombreObjeto);
        object.put("email",gestorSesion.getmailSession());
        Call<JsonObject> call = retrofitInterface.buyObject(object);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200){
                    System.out.println("TODO OK");
                    updateMonedas();
                }else if(response.code() == 400){
                    Toast.makeText( ObjetoTienda.this, "No se ha conseguido comprar el objeto", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText( ObjetoTienda.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateMonedas(){
        HashMap<String,String> update = new HashMap<>();

        update.put("precioObjeto",precioObjeto);
        update.put("email",gestorSesion.getmailSession());
        Call<JsonObject> call = retrofitInterface.updateCoins(update);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200){
                    System.out.println("TODO OK");
                    int monedasUsuario = Integer.parseInt(gestorSesion.getKEY_SESSION_COINS());
                    int newCoins = monedasUsuario - precio;
                    gestorSesion.updateCoins(Integer.toString(newCoins));
                    System.out.println(newCoins);
                }else if(response.code() == 410){
                    Toast.makeText( ObjetoTienda.this, "No se ha podido cambiar monedas", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText( ObjetoTienda.this, "Usuario no registrado", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText( ObjetoTienda.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean hayMonedasSuficientes(){
        int monedasActuales = Integer.parseInt(gestorSesion.getKEY_SESSION_COINS());
        if(precio > monedasActuales){
            return false;
        }
        return true;
    }
}


