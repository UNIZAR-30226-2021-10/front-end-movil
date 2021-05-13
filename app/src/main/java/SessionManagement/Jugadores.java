package SessionManagement;

public class Jugadores implements Comparable<Jugadores>{
    private String username;
    private int puntos;
    private String imagen;
    private int orden;

    public  Jugadores(String username, int puntos, String _imagen, int orden){
        this.username = username;
        this.puntos = puntos;
        this.imagen = _imagen;
        this.orden = orden;
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

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    @Override
    public int compareTo(Jugadores o) {
        if (orden < o.getOrden()) {
            return -1;
        }
        if (orden > o.getOrden()) {
            return 1;
        }
        return 0;
    }
}
