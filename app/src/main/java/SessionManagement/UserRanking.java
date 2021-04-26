package SessionManagement;

public class UserRanking {
    private String nombre;
    private int puntos;
    private String imagen;
    private int puesto;

    public UserRanking(String nombre, int puntos, String imagen, int puesto) {
        this.nombre = nombre;
        this.puntos = puntos;
        this.imagen = imagen;
        this.puesto = puesto;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getImagen() {
        return imagen;
    }

    public int getPuesto() {
        return puesto;
    }

    public void setPuesto(int puesto) {
        this.puesto = puesto;
    }
}
