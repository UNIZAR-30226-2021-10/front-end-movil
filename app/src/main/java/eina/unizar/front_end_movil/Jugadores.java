package eina.unizar.front_end_movil;

public class Jugadores {
    private String username;
    private int puntos;
    private String imagen;

    public  Jugadores(String username, int puntos, String _imagen){
        this.username = username;
        this.puntos = puntos;
        this.imagen = _imagen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
