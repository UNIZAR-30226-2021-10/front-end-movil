package eina.unizar.front_end_movil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class PantallaChat extends AppCompatActivity{

    private static final int OPTION_INSTRUCCIONES = 0;

    private TextView mensajeChat;
    private ListView listaMensajes;
    ConstraintLayout pant1;
    ConstraintLayout pant2;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_chat);
        getSupportActionBar().hide();

        mensajeChat = (TextView)findViewById(R.id.mensaje_writer);
        listaMensajes = (ListView) findViewById(R.id.lista_mensajes);
        //pant1 = (ConstraintLayout) findViewById(R.id.constraint);
        //pant2 = (ConstraintLayout) findViewById(R.id.constraint2);
        //pant2.setVisibility(View.GONE);


        // Construct the data source
        ArrayList<Mensaje> arrayOfMessages = new ArrayList<Mensaje>();
        // Create the adapter to convert the array to views
        final MyAdapter adapter = new MyAdapter(this, arrayOfMessages);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lista_mensajes);
        listView.setAdapter(adapter);

        // Add item to adapter
        Mensaje newMessage = new Mensaje("Usuario1", "Hola");
        adapter.add(newMessage);
        newMessage = new Mensaje("Usuario3", "¿Preparados?");
        adapter.add(newMessage);

        // Botón de enviar el mensaje
        ImageButton enviarButton = (ImageButton) findViewById(R.id.enviar);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // poner mensaje que haya en el editText
                String m = mensajeChat.getText().toString();
                Mensaje newMessage = new Mensaje("Usuario2", m);
                adapter.add(newMessage);
                mensajeChat.setText("");
            }
        });

        // Botón de atrás
        Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //pant1.setVisibility(View.GONE);
                //pant2.setVisibility(View.VISIBLE);
            }
        });

        ImageButton instButton = (ImageButton) findViewById(R.id.instrucciones);
        instButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), InstruccionesJuego.class);
                startActivityForResult(intent, OPTION_INSTRUCCIONES);
            }
        });

    }

    public class MyAdapter extends ArrayAdapter<Mensaje> {
        public MyAdapter(Context context, ArrayList<Mensaje> msg) {
            super(context, 0, msg);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Mensaje m = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mensajes_chat, parent, false);
            }
            // Lookup view for data population
            TextView mUsr = (TextView) convertView.findViewById(R.id.nombre_usuario);
            TextView mMensaje = (TextView) convertView.findViewById(R.id.mensaje);
            // Populate the data into the template view using the data object
            mUsr.setText(m.usuario);
            mMensaje.setText(m.mensaje);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class Mensaje{
        private String usuario;
        private String mensaje;

        public Mensaje(String _usuario, String _mensaje){
            this.usuario = _usuario;
            this.mensaje = _mensaje;
        }

        String getUsuario(){
            return usuario;
        }

        String getMensaje(){
            return mensaje;
        }

        void setUsuario(String _usuario){
            usuario = _usuario;
        }

        void setMensaje(String _mensaje){
            mensaje = _mensaje;
        }
    }
}


