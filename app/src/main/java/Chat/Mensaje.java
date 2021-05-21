package Chat;

public class Mensaje {

    private String usuario;
    private String mensaje;
    private String imagen;
    private boolean belongsToCurrentUser; // is this message sent by us?
    private boolean isAdmin; // is this admin
    private int numJugador;

    public Mensaje(String usuario, String mensaje, boolean belongsToCurrentUser, boolean isAdmin, String imagen, int numjugador) {
        this.usuario = usuario;
        this.mensaje = mensaje;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.isAdmin = isAdmin;
        this.imagen = imagen;
        this.numJugador = numjugador;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getNumJugador() {
        return numJugador;
    }
}
