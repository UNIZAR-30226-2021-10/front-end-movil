package eina.unizar.front_end_movil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import database_wrapper.RetrofitInterface;

public class CambiarPassword extends AppCompatActivity {

    private static final int OPTION_ENVIAR_CORREO = 0;
    private static final int OPTION_ATRAS = 1;

    private EditText email;
    String correo;
    String password;
    Session sesion;
    String mensaje;

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
        setContentView(R.layout.cambiar_password);
        getSupportActionBar().hide();

        //EDIT TEXT DE EMAIL
        email = (EditText) findViewById(R.id.email);
        correo = "trivialProyectoSoftware@gmail.com";
        password = "trivial1234";

        // Botón de confirmar
        Button confirmarButton = (Button) findViewById(R.id.confirmar);
        confirmarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // COMPROBAR que el campo del email es no vacio
                if(email.getText().toString().isEmpty()) {
                    email.setError("El campo no puede estar vacío");
                }
                else{
                    if(comprobarEmail(email.getText().toString())){
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                       Properties properties = new Properties();
                       properties.put("mail.smtp.host","smtp.googlemail.com");
                       properties.put("mail.smtp.socketFactory.port","465");
                       properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                       properties.put("mail.smtp.auth","true");
                       properties.put("mail.smtp.port","465");

                       try{
                           sesion = Session.getDefaultInstance(properties, new Authenticator() {
                               @Override
                               protected PasswordAuthentication getPasswordAuthentication() {
                                   return new PasswordAuthentication(correo, password);
                               }
                           });

                           if(sesion != null){
                               Message message = new MimeMessage(sesion);
                               message.setFrom(new InternetAddress(correo));
                               message.setSubject("codigo de verificacion");
                               String destinatario = email.getText().toString().trim();
                               message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(destinatario));
                               mensaje = "Tu codigo es 1234560";
                               message.setContent(mensaje, "text/html; charset=utf-8");

                               Transport.send(message);

                               Intent intent = new Intent (v.getContext(), enviarCodigoVerificacion.class);
                               startActivityForResult(intent, OPTION_ENVIAR_CORREO);
                           }

                       }catch(Exception e){
                           e.printStackTrace();
                       }

                    }else {
                        email.setError("El email es invalido, introduzca un email valido por ejemplo: pedro@gmail.com");
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
    private boolean comprobarEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}


