package eina.unizar.front_end_movil;

import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
/*
public class socket {
    private Socket socket;
    public void iniciarSocket(String username, int code, int firstJoin, String avatar){
        socket = IO.socket(URI.create("localhost:5000"));
        socket.emit("join", username, code, firstJoin, avatar, (error) =>{
            if(error){
                alert(error);
            }
        };
        console.log("Se ha unido el usuario");
    }

    public void disconnectSocket(){
        socket.emit("disconnection");
        socket.off();
    }

    private Emitter.Listener message = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
           /* runOnUiThread(new Runnable(){
                @Override 
                public void run(){
                    JSONObject datos = (JSONObject) args[0];
                    
                }
            }
        };
    };

    private Emitter.Listener jugador = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    }

    public void actualizarMensajes(String messages, String jugadores, falta algo ){
        socket.on("message", message);
        String mensajesActuales = messages;
        mensajesActuales.push(message);
        socket.on("newPlayer", (jugador));
        String jugadoresActuales = jugadores;
        jugadoresActuales.push(jugador);
    }

    public void enviarMensaje (Emitter.Listener message) {
        socket.emit("sendMessage", message);
        System.out.println("He enviado mensaje");
        System.out.println(message);
    }
*/
}
