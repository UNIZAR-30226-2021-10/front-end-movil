package Chat;

public class Mensaje {

    private String usuario;
    private String mensaje;
    private boolean belongsToCurrentUser; // is this message sent by us?
    private boolean isAdmin; // is this admin

    public Mensaje(String usuario, String mensaje, boolean belongsToCurrentUser, boolean isAdmin) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.isAdmin = isAdmin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
