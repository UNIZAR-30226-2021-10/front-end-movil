package SessionManagement;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorSesion {

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String SHARED_SESION_ID = "session";
    private String KEY_SESSION_ID = "session_user";
    private String KEY_SESSION_MAIL = "session_mail";
    private String KEY_SESSION_POINTS = "session_points";
    private String KEY_SESSION_COINS = "session_coins";

    public GestorSesion(Context context){
        sharedPreferences =  context.getSharedPreferences(SHARED_SESION_ID,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User usuario){
        editor.putString(KEY_SESSION_ID,usuario.getUsername()).commit();
        editor.putString(KEY_SESSION_MAIL,usuario.getEmail()).commit();
        editor.putString(KEY_SESSION_COINS,usuario.getCoins()).commit();
        editor.putString(KEY_SESSION_POINTS,usuario.getPoints()).commit();

    }

    public String getSession(){
        return  sharedPreferences.getString(KEY_SESSION_ID, String.valueOf(1));
    }
    //HACER BASE DE DATOS SQLITE para guardar toda esta info.
    public String getmailSession(){
        return sharedPreferences.getString(KEY_SESSION_MAIL,String.valueOf(1));
    }

    public String getpointsSession(){
        return sharedPreferences.getString(KEY_SESSION_POINTS,String.valueOf(1));
    }

    public String getKEY_SESSION_COINS(){
        return sharedPreferences.getString(KEY_SESSION_COINS,String.valueOf(1));
    }

    public void removeSession(){
        editor.putString(KEY_SESSION_ID,String.valueOf(1)).commit();
    }
}
