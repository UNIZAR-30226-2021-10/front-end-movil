package eina.unizar.front_end_movil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity {

    /**
     * Opciones de los dos botones del menú princiapl
     */
    private static final int OPTION_ACCEDER = 0;
    private static final int OPTION_PASSWORD = 1;
    private static final int OPTION_REGISTRO = 2;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        // Botón de acceder/iniciar sesión
        Button accederButton = (Button) findViewById(R.id.acceder);
        /*accederButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), DecisionJuego.class);
                startActivityForResult(intent, OPTION_ACCEDER);
            }
        });*/

        // Botón de mostrar listas de compra
        Button categoriasButton = (Button) findViewById(R.id.categorias);
        /*categoriasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), CategoriaPad.class);
                startActivityForResult(intent, OPTION_CATEGORIAS);
            }
        });*/

        // Botón de mostrar listas de compra
        Button categoriasButton = (Button) findViewById(R.id.categorias);
        /*categoriasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), CategoriaPad.class);
                startActivityForResult(intent, OPTION_CATEGORIAS);
            }
        });*/
    }
}


