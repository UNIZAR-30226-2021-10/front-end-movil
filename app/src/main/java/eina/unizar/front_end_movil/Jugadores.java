package eina.unizar.front_end_movil;

public class Jugadores {
    private String username;
    private int puntos;

    public  Jugadores(String username, int puntos){
        this.username = username;
        this.puntos = puntos;
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
}
