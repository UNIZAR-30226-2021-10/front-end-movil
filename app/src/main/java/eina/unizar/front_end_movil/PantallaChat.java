package eina.unizar.front_end_movil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import SessionManagement.GestorSesion;
import SessionManagement.Jugadores;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class PantallaChat extends AppCompatActivity{

    //private static final String ipMarta = "http://192.168.1.162:5000/";
    //private static final String ipAndrea = "http://192.168.0.26:5000/";

    //private static final int OPTION_INSTRUCCIONES = 0;

    //private Socket msocket;
    //private GestorSesion gestorSesion;

    //private EditText editText;
    //private MessageAdapter messageAdapter;
    //private ListView messagesView;


    // const mensajeUserJoin = {sender: 'admin', avatar: admin, text: "Bienvenido al chat "+ user.username, date: "admin" };
    /*private Emitter.Listener newMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    JSONObject datos = (JSONObject) args[0];
                    String sender = "";
                    String texto = "";
                    try {
                        sender = datos.getString("sender");
                        texto = datos.getString("text");
                        System.out.println("NICKNAME EN EMITTER: " + sender);
                        System.out.println("AVATAR EN EMITTER: " + texto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!sender.equals(gestorSesion.getSession())) {
                        Mensaje m = new Mensaje(sender, texto, false, sender.equals("admin"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageAdapter.add(m);
                                // scroll the ListView to the last added element
                                messagesView.setSelection(messagesView.getCount() - 1);
                            }
                        });
                    }
                }
            });
        }
    };*/

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.pantalla_chat);
        //getSupportActionBar().hide();
        //gestorSesion = new GestorSesion(PantallaChat.this);

        //editText = (EditText) findViewById(R.id.mensaje_writer);

        //messageAdapter = new MessageAdapter(this);
        //messagesView = (ListView) findViewById(R.id.lista_mensajes);
        //messagesView.setAdapter(messageAdapter);
        //pant1 = (ConstraintLayout) findViewById(R.id.constraint);
        //pant2 = (ConstraintLayout) findViewById(R.id.constraint2);
        //pant2.setVisibility(View.GONE);

        // conexión con sockets
        /*try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{WebSocket.NAME};
            msocket = IO.socket(ipAndrea, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }*/

        /*msocket.on("message",newMessage)
                .on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        for (Object obj : args) {
                            System.out.println("POR FIN!!!!!!");
                            Log.d("chat"," NOT Errors :: " + obj);
                        }
                    }
                })
                .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        for (Object obj : args) {
                            Log.d("chat","Errors :: " + obj);
                        }
                    }
                });


        msocket.connect();

        // hacer primer join
        JSONObject aux = new JSONObject();
        try{
            aux.put("username", gestorSesion.getSession()); //username
            aux.put("code", 12345); //code
            aux.put("firstJoin",true); //firstJoin
            aux.put("avatar", gestorSesion.getAvatarSession()); //avatar

        } catch (JSONException e){
            e.printStackTrace();
        }

        msocket.emit("join", aux, new Ack(){
            @Override
            public void call(Object... args){
                //JSONObject response = (JSONObject) args[0];
                //System.out.println(response);
            }
        });*/

        // Botón de atrás
        /*Button atrasButton = (Button) findViewById(R.id.atras);
        atrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //pant1.setVisibility(View.GONE);
                //pant2.setVisibility(View.VISIBLE);
            }
        });*/

        /*ImageButton instButton = (ImageButton) findViewById(R.id.instrucciones);
        instButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), InstruccionesJuego.class);
                startActivityForResult(intent, OPTION_INSTRUCCIONES);
            }
        });*/

    }

    /*public void sendMessage(View view) {
        String m = editText.getText().toString();
        if (m.length() > 0) {
            // mandar mensaje
            final Mensaje message = new Mensaje(gestorSesion.getSession(), m, true, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    Date date = new Date();
                    String fecha = dateFormat.format(date);
                    // const mensajeUserJoin = {sender: 'admin', avatar: admin, text: "Bienvenido al chat "+ user.username, date: "admin" };
                    JSONObject aux = new JSONObject();
                    try{
                        aux.put("sender", gestorSesion.getSession()); //sender
                        aux.put("avatar", gestorSesion.getAvatarSession()); //avatar
                        aux.put("text",m); //texto
                        aux.put("date", fecha); //fecha

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    msocket.emit("sendMessage", aux, new Ack(){
                        @Override
                        public void call(Object... args){
                            //JSONObject response = (JSONObject) args[0];
                            //System.out.println(response);
                        }
                    });
                }
            });
            editText.getText().clear();
        }
    }*/

    // MessageAdapter.java
    /*public class MessageAdapter extends BaseAdapter {

        List<Mensaje> messages = new ArrayList<Mensaje>();
        Context context;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        public void add(Mensaje message) {
            this.messages.add(message);
            notifyDataSetChanged(); // to render the list we need to notify
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            MessageViewHolder holder = new MessageViewHolder();
            LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            Mensaje message = messages.get(i);

            if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getMensaje());
            } else if(!message.isBelongsToCurrentUser() && !message.isAdmin()){ // this message was sent by someone else so let's create an advanced chat bubble on the left
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.name.setText(message.getUsuario());
                holder.messageBody.setText(message.getMensaje());
            } else{
                convertView = messageInflater.inflate(R.layout.admin_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getMensaje());
            }

            return convertView;
        }

    }

    class MessageViewHolder {
        public TextView name;
        public TextView messageBody;
    }*/


}


