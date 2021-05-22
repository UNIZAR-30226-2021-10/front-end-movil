package SessionManagement;

public class JugadoresFinal implements Comparable<JugadoresFinal>{
    private String username;
    private int puntos;
    private String imagen;

    public JugadoresFinal(String username, int puntos, String _imagen){
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




    @Override
    public int compareTo(JugadoresFinal o) {
        if (puntos > o.getPuntos()) {
            return -1;
        }
        if (puntos < o.getPuntos()) {
            return 1;
        }
        return 0;
    }
}
