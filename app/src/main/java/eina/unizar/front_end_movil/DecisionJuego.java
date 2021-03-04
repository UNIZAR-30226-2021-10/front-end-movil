package eina.unizar.front_end_movil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DecisionJuego extends AppCompatActivity {

    /**
     * Opciones de los dos botones del menú princiapl
     */


    private EditText usuario;
    private EditText password;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_juego);
        getSupportActionBar().hide();


}


