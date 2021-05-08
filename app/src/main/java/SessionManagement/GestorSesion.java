package SessionManagement;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorSesion {

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String SHARED_SESION_ID = "session";
    private final String KEY_SESSION_ID = "session_user";
    private final String KEY_SESSION_MAIL = "session_mail";
    private final String KEY_SESSION_POINTS = "session_points";
    private final String KEY_SESSION_COINS = "session_coins";
    private final String KEY_SESSION_AVATAR = "session_avatar";

    public GestorSesion(Context context){
        sharedPreferences =  context.getSharedPreferences(SHARED_SESION_ID,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User usuario){
        editor.putString(KEY_SESSION_ID,usuario.getUsername()).commit();
        editor.putString(KEY_SESSION_MAIL,usuario.getEmail()).commit();
        editor.putString(KEY_SESSION_COINS,usuario.getCoins()).commit();
        editor.putString(KEY_SESSION_POINTS,usuario.getPoints()).commit();
        editor.putString(KEY_SESSION_AVATAR,usuario.getAvatar()).commit();

    }

    public String getSession(){
        return  sharedPreferences.getString(KEY_SESSION_ID, String.valueOf(1));
    }

    public String getmailSession(){
        return sharedPreferences.getString(KEY_SESSION_MAIL,String.valueOf(1));
    }

    public String getpointsSession(){
        return sharedPreferences.getString(KEY_SESSION_POINTS,String.valueOf(1));
    }

    public void updateCoins(String coins){  editor.putString(KEY_SESSION_COINS,coins).commit(); }

    public void updatePoints(String points){ editor.putString(KEY_SESSION_POINTS,points).commit(); }

    public void updateNickname(String nickname){ editor.putString(KEY_SESSION_ID,nickname).commit(); }

    public String getKEY_SESSION_COINS(){ return sharedPreferences.getString(KEY_SESSION_COINS,String.valueOf(1)); }

    public String getAvatarSession(){ return sharedPreferences.getString(KEY_SESSION_AVATAR,String.valueOf(1)); }

    public void setAvatarSession(String nuevoAvatar){ editor.putString(KEY_SESSION_AVATAR,nuevoAvatar).commit(); }

    public void removeSession(){
        editor.putString(KEY_SESSION_ID,String.valueOf(1)).commit();
        editor.putString(KEY_SESSION_MAIL,String.valueOf(1)).commit();
        editor.putString(KEY_SESSION_POINTS,String.valueOf(1)).commit();
        editor.putString(KEY_SESSION_COINS,String.valueOf(1)).commit();
        editor.putString(KEY_SESSION_AVATAR,String.valueOf(1)).commit();
    }
}
